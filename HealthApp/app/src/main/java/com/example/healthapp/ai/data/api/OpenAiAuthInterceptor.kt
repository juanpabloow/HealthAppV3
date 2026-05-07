package com.example.healthapp.ai.data.api

import com.example.healthapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Adds the `Authorization: Bearer <key>` header to OpenAI requests.
 * The key is read from [BuildConfig.OPENAI_API_KEY], which is populated by
 * `app/build.gradle.kts` from `local.properties` (gitignored).
 */
class OpenAiAuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
            .header("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}
