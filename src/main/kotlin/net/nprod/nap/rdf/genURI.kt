fun genURI(type: String, id: String): String {
    if (type.contains("/") || id.contains("/"))
        throw IllegalArgumentException("Type and id should not contain /")

    return "https://nap.nprod.net/$type/$id"
}
