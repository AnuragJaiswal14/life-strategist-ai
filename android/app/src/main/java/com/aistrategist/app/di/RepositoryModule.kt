package com.aistrategist.app.di

import com.aistrategist.app.data.repository.LogRepositoryImpl
import com.aistrategist.app.data.repository.AuthRepositoryImpl
import com.aistrategist.app.domain.repository.LogRepository
import com.aistrategist.app.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    abstract fun bindLogRepository(
        logRepositoryImpl: LogRepositoryImpl
    ): LogRepository

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}
