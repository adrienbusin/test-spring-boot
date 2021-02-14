package fr.busin.upload

import fr.busin.upload.storage.StorageFileNotFoundException
import fr.busin.upload.storage.StorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class FileUploadController @Autowired constructor(private val storageService: StorageService) {

    @GetMapping("/")
    fun listUploadedFiles(model: Model): String {
        model.addAttribute("projects", storageService.loadAll())
        return "uploadForm"
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    fun serveFile(@PathVariable filename: String): ResponseEntity<Resource> {
        val file = storageService.loadAsResource(filename)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.filename + "\"")
            .body(file)
    }

    @GetMapping("/files/{directory:.+}/{filename:.+}")
    @ResponseBody
    fun getFileFullPath(@PathVariable filename: String, @PathVariable directory: String): ResponseEntity<Resource> {
        return serveFile("$directory/$filename")
    }

    @PostMapping("/")
    fun handleFileUpload(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("email") email: String,
        @RequestParam("prenom") prenom: String,
        @RequestParam("nom") nom: String,
        redirectAttributes: RedirectAttributes
    ): String {
        val path = storageService.store(file, "$prenom $nom")

        val serverPath = "https://android.busin.fr/${path.parent.fileName}/${path.fileName}"

        storageService.sendEmail(email, prenom, serverPath)
        redirectAttributes.addFlashAttribute("message", "file uploaded")
        return "redirect:/"
    }

    @ExceptionHandler(StorageFileNotFoundException::class)
    fun handleStorageFileNotFound(): String {
        return "404"
    }
}