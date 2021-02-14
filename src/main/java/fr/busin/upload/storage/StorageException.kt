package fr.busin.upload.storage

import java.lang.RuntimeException

open class StorageException : RuntimeException {
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
}