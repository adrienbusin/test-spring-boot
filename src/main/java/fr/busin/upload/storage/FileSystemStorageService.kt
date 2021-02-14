package fr.busin.upload.storage

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.streams.toList

@Service
class FileSystemStorageService @Autowired constructor(
    properties: StorageProperties,
    private var mailSender: JavaMailSender
) : StorageService {

    private val rootLocation: Path = Paths.get(properties.location)

    @Value("\${email.sender}")
    lateinit var sender: String

    override fun store(file: MultipartFile, directory: String): Path {
        try {
            if (file.isEmpty) {
                throw StorageException("Failed to store empty file.")
            }
            val destinationDir =
                rootLocation.resolve(Paths.get(directory)).normalize().toAbsolutePath().toString()

            val dir = File(destinationDir)
            if (!dir.exists()) {
                dir.mkdir()
            }

            val destinationFile =
                rootLocation.resolve(Paths.get(directory + "/" + file.originalFilename)).normalize().toAbsolutePath()

            if (destinationFile.parent.toString() != rootLocation.toAbsolutePath().toString() + "/" + directory) {
                // This is a security check
                throw StorageException(
                    "Cannot store file outside current directory."
                )
            }
            file.inputStream.use { inputStream ->
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING)
            }
            return destinationFile
        } catch (e: IOException) {
            throw StorageException("Failed to store file.", e)
        }
    }

    override fun loadAll(): MutableList<FileUploaded> {
        return try {
            val files: MutableList<FileUploaded> = mutableListOf()
            Files.walk(rootLocation, 2)
                .filter { path: Path -> path != rootLocation }
                .filter { path: Path -> !Files.isDirectory(path) }
                .map { path: Path ->
                    files.add(FileUploaded(path.parent.fileName.toString(), path.fileName.toString()))
                }.toList()
            files
        } catch (e: IOException) {
            throw StorageException("Failed to read stored files", e)
        }
    }

    override fun load(filename: String): Path {
        return rootLocation.resolve(filename)
    }

    override fun loadAsResource(filename: String): Resource {
        return try {
            val file = load(filename)
            val resource: Resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable) {
                resource
            } else {
                throw StorageFileNotFoundException(
                    "Could not read file: $filename"
                )
            }
        } catch (e: MalformedURLException) {
            throw StorageFileNotFoundException("Could not read file: $filename", e)
        }
    }

    override fun deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile())
    }

    override fun sendEmail(to: String, name: String, path: String) {
        val text =
            "Bonjour $name, <br><br> Ton projet a bien été enregistré tu peux le récuperer à l'adresse <a href='$path' >$path</a>"

        val message = mailSender.createMimeMessage()
        MimeMessageHelper(message, true).apply {
            setFrom(sender)
            setTo(arrayOf(to, sender))
            setSubject("PARTIEL EILCO 2021")
            setText(text, true)
        }
        mailSender.send(message)
    }

    override fun init() {
        try {
            Files.createDirectories(rootLocation)
        } catch (e: IOException) {
            throw StorageException("Could not initialize storage", e)
        }
    }

}