package fr.busin.upload

import fr.busin.upload.storage.StorageProperties
import fr.busin.upload.storage.StorageService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties::class)
open class UploadingFilesApplication {
    @Bean
    open fun init(storageService: StorageService): CommandLineRunner {
        return CommandLineRunner {
            storageService.deleteAll()
            storageService.init()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<UploadingFilesApplication>(*args)
}
