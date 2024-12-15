import data.DEFAULT_URI
import kotlin.test.Test
import kotlin.test.assertEquals

class UriTest {

    @Test
    fun defaultUriTest() {
        val scheme = "http"
        val host = "127.0.0.1"
        val port = 4723
        val path = "/"
        val uriString = "$scheme://$host:$port$path"
        assertEquals(scheme, DEFAULT_URI.scheme)
        assertEquals(host, DEFAULT_URI.host)
        assertEquals(port, DEFAULT_URI.port)
        assertEquals(path, DEFAULT_URI.path)
        assertEquals(uriString, DEFAULT_URI.toString())
    }
}
