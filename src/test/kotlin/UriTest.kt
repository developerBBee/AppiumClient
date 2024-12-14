import util.Constant.DEFAULT_HOST
import util.Constant.DEFAULT_PATH
import util.Constant.DEFAULT_PORT
import util.Constant.DEFAULT_SCHEME
import util.Constant.DEFAULT_SSL_ENABLED
import java.net.URI
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

private val DEFAULT_URI = URI(
    "$DEFAULT_SCHEME${if (DEFAULT_SSL_ENABLED) "s" else ""}",
    null,
    DEFAULT_HOST,
    DEFAULT_PORT,
    DEFAULT_PATH,
    null,
    null
)
