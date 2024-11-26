
val String.as_local_link_if_dev: String
    get() {
    return if (System.getenv("ENVIRONMENT") == "development") {
        this.replace("https://nap.nprod.net", "http://localhost:8080")
    } else {
        this
    }
}
