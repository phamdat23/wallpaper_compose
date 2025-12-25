package com.itsol.vn.wallpaper.live.parallax.di


import com.itsol.vn.wallpaper.live.parallax.netWorking.ApiService
import com.itsol.vn.wallpaper.live.parallax.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
class Network {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logging
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(5, TimeUnit.MINUTES) // connection timeout
            .readTimeout(5, TimeUnit.MINUTES)    // read timeout
            .writeTimeout(5, TimeUnit.MINUTES)   // write timeout
            .callTimeout(5, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    @Named("Networking1")
    fun providerNetWorking(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providerApiService(@Named("Networking1") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }




}