package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.repositories.IToyotaRepository
import org.delcom.repositories.ToyotaRepository
import org.delcom.services.PlantService
import org.delcom.services.ProfileService
import org.delcom.services.ToyotaService
import org.koin.dsl.module

val appModule = module {
    // Plant Repository
    single<IPlantRepository> {
        PlantRepository()
    }

    // Plant Service
    single {
        PlantService(get())
    }

    // Toyota Repository
    single<IToyotaRepository> {
        ToyotaRepository()
    }

    // Toyota Service
    single {
        ToyotaService(get())
    }

    // Profile Service
    single {
        ProfileService()
    }
}