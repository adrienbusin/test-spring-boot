package fr.busin.upload.storage

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

interface StorageService {
    fun init()
    fun store(file: MultipartFile, directory: String): Path
    fun loadAll(): MutableList<FileUploaded>
    fun load(filename: String): Path
    fun loadAsResource(filename: String): Resource
    fun deleteAll()
    fun sendEmail(to: String, name: String, path: String)
}