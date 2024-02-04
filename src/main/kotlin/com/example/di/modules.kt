package com.example.di

import com.example.repository.ToolRepository
import com.example.service.ToolService
import com.example.repository.UserRepository
import com.example.service.UserService
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::ToolRepository) { bind<ToolRepository>() }
    singleOf(::ToolService)

    singleOf(::UserRepository) { bind<UserRepository>() }
    singleOf(::UserService)
}