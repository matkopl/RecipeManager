package hr.algebra.recipe

import android.content.*
import android.database.Cursor
import android.net.Uri
import hr.algebra.recipe.repository.RecipeSqlHelper

private const val AUTHORITY = "hr.algebra.recipe"
private const val PATH = "recipes"
val RECIPE_PROVIDER_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH")

class RecipeProvider : ContentProvider() {

    private lateinit var recipeDb: RecipeSqlHelper

    override fun onCreate(): Boolean {
        recipeDb = RecipeSqlHelper(context)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return recipeDb.readableDatabase.query(
            "recipes", projection, selection, selectionArgs, null, null, sortOrder
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = recipeDb.writableDatabase.insert("recipes", null, values)
        context?.contentResolver?.notifyChange(uri, null)
        return Uri.withAppendedPath(RECIPE_PROVIDER_CONTENT_URI, id.toString())
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val count = recipeDb.writableDatabase.delete("recipes", selection, selectionArgs)
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val count = recipeDb.writableDatabase.update("recipes", values, selection, selectionArgs)
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun getType(uri: Uri): String? = "vnd.android.cursor.dir/$AUTHORITY.$PATH"
}
