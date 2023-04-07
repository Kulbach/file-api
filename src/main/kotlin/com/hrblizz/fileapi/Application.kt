package com.hrblizz.fileapi

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.io.File
import java.util.TimeZone
import javax.annotation.PostConstruct

@SpringBootApplication
@EnableMongoRepositories(basePackages = ["com.hrblizz.fileapi.data.repository"])
@EnableMongoAuditing
class Application : SpringBootServletInitializer() {

    @Value("\${file.upload.dir}")
    private lateinit var fileUploadDir: String

    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(Application::class.java)
    }

    @PostConstruct
    fun setDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @PostConstruct
    fun createFileDir() {
        val uploadDir = File(fileUploadDir)
        if (!uploadDir.exists()) {
            uploadDir.mkdir()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
}
