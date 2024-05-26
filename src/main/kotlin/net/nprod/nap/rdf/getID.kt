fun String.getRef(): String {
    // Gross
    return split("https://nap.nprod.net/").last()
}
