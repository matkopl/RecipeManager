package hr.algebra.recipe.factory

import hr.algebra.recipe.api.RecipeApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RecipeApiFactory {
    private const val BASE_URL = "https://api.spoonacular.com/"
    private const val API_KEY = "7aa49f4c39c34761bb2cd6f3ef0ff788"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url

            val updatedUrl = originalUrl.newBuilder()
                .addQueryParameter("apiKey", API_KEY)
                .build()

            val updatedRequest = originalRequest.newBuilder()
                .url(updatedUrl)
                .build()

            chain.proceed(updatedRequest)
        }
        .build()

    val apiService: RecipeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeApiService::class.java)
    }
}
