package hr.algebra.recipe.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hr.algebra.recipe.model.Ingredient
import hr.algebra.recipe.model.Instruction
import hr.algebra.recipe.model.Recipe

private const val DB_NAME = "recipes.db"
private const val DB_VERSION = 1
private const val TABLE_NAME = "recipes"

private val CREATE_TABLE = """
    CREATE TABLE $TABLE_NAME (
        id INTEGER PRIMARY KEY,
        title TEXT NOT NULL,
        summary TEXT NOT NULL,
        image TEXT NOT NULL,
        recipeJson TEXT NOT NULL,
        ingredientsJson TEXT NOT NULL,
        instructionsJson TEXT NOT NULL
    )
""".trimIndent()

private const val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

class RecipeSqlHelper(context: Context?) : SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    DB_VERSION
) {
    private val gson = Gson()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_TABLE)
        onCreate(db)
    }

    fun addRecipe(recipe: Recipe) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", recipe.id)
            put("title", recipe.title)
            put("summary", recipe.summary)
            put("image", recipe.imageUrl ?: "")
            put("recipeJson", gson.toJson(recipe))
            put("ingredientsJson", gson.toJson(recipe.ingredients))

            val instructionsJson = gson.toJson(recipe.analyzedInstructions)
            put("instructionsJson", instructionsJson)
        }
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun deleteRecipe(recipeId: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "id=?", arrayOf(recipeId.toString()))
        db.close()
    }

    fun isRecipeFavorite(recipeId: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM $TABLE_NAME WHERE id=?", arrayOf(recipeId.toString()))
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    fun getAllRecipes(): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT recipeJson, ingredientsJson, instructionsJson FROM $TABLE_NAME", null)

        while (cursor.moveToNext()) {
            val recipeJson = cursor.getString(cursor.getColumnIndexOrThrow("recipeJson"))
            val recipe = gson.fromJson(recipeJson, Recipe::class.java)

            val ingredientsJson = cursor.getString(cursor.getColumnIndexOrThrow("ingredientsJson"))
            val typeIngredients = object : TypeToken<List<Ingredient>>() {}.type
            val ingredients: List<Ingredient> = try {
                gson.fromJson(ingredientsJson, typeIngredients) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            val instructionsJson = cursor.getString(cursor.getColumnIndexOrThrow("instructionsJson"))
            val typeInstructions = object : TypeToken<List<Instruction>>() {}.type
            val instructions: List<Instruction> = try {
                gson.fromJson(instructionsJson, typeInstructions) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            val updatedRecipe = recipe.copy(ingredients = ingredients, analyzedInstructions = instructions)
            recipes.add(updatedRecipe)
        }
        cursor.close()
        db.close()
        return recipes
    }

    fun deleteAllRecipes() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
        db.close()
    }

}
