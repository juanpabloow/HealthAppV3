package com.example.healthapp.di

import com.example.healthapp.auth.data.repository.FirebaseAuthRepositoryImpl
import com.example.healthapp.auth.domain.repository.AuthRepository
import com.example.healthapp.dashboard.data.repository.ScreenTimeRepositoryImpl
import com.example.healthapp.dashboard.domain.repository.ScreenTimeRepository
import com.example.healthapp.emotion.data.repository.FirebaseEmotionRepositoryImpl
import com.example.healthapp.emotion.domain.repository.EmotionRepository
import com.example.healthapp.plans.data.repository.FirebasePlanRepositoryImpl
import com.example.healthapp.plans.domain.repository.PlanRepository
import com.example.healthapp.survey.data.repository.FirebaseSurveyRepositoryImpl
import com.example.healthapp.survey.domain.repository.SurveyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSurveyRepository(impl: FirebaseSurveyRepositoryImpl): SurveyRepository

    @Binds
    @Singleton
    abstract fun bindScreenTimeRepository(impl: ScreenTimeRepositoryImpl): ScreenTimeRepository

    @Binds
    @Singleton
    abstract fun bindPlanRepository(impl: FirebasePlanRepositoryImpl): PlanRepository

    @Binds
    @Singleton
    abstract fun bindEmotionRepository(impl: FirebaseEmotionRepositoryImpl): EmotionRepository
}
