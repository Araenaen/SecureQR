package com.secureqr.di

import android.app.Application
import androidx.room.Room
import com.secureqr.BuildConfig
import com.secureqr.data.local.AppDatabase
import com.secureqr.data.local.ScanDao
import com.secureqr.data.remote.GsbApiService
import com.secureqr.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- CAPA DE DATOS LOCAL (Base de Datos) ---

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            // 3. Usa la importación directa
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideScanDao(db: AppDatabase): ScanDao {
        return db.scanDao()
    }

    // --- CAPA DE DATOS REMOTA (Red/API) ---

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val apiKeyInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val originalHttpUrl = originalRequest.url

            val newUrl = originalHttpUrl.newBuilder()
                .addQueryParameter("key", BuildConfig.GSB_API_KEY)
                .build()

            val newRequest = originalRequest.newBuilder().url(newUrl).build()
            chain.proceed(newRequest)
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(apiKeyInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGsbApiService(client: OkHttpClient): GsbApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.GSB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(GsbApiService::class.java)
    }
}
