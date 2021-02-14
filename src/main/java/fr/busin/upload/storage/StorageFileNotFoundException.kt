package fr.busin.upload.storage

import fr.busin.upload.storage.StorageException

class StorageFileNotFoundException : StorageException {
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
}