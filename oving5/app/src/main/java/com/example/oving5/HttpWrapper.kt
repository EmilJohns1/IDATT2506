import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder

enum class HTTP { GET, POST, GET_WITH_HEADER }
const val ENCODING = "UTF-8"

class HttpWrapper(private val URL: String) {
    init {
        CookieHandler.setDefault(CookieManager(null, CookiePolicy.ACCEPT_ALL))
    }

    private fun openConnection(url: String): HttpURLConnection {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.setRequestProperty("Accept-Charset", ENCODING)

        val cookieManager = CookieHandler.getDefault() as CookieManager
        val cookies = cookieManager.cookieStore.cookies
        if (cookies.isNotEmpty()) {
            connection.setRequestProperty("Cookie", cookies.joinToString("; ") { it.toString() })
        }

        return connection
    }

    fun get(parameterList: Map<String, String>): String {
        val connection = openConnection(URL + encodeParameters(parameterList))
        connection.inputStream.use { response ->
            updateCookies(connection)
            return readResponseBody(response, getCharSet(connection))
        }
    }

    fun post(parameterList: Map<String, String>): String {
        val connection = openConnection(URL)
        connection.doOutput = true
        val outputType = "application/x-www-form-urlencoded; charset=$ENCODING"
        connection.setRequestProperty("Content-Type", outputType)
        connection.outputStream.use { outputStream ->
            val postString = encodeParameters(parameterList)
            outputStream.write(postString.toByteArray(charset(ENCODING)))
        }
        connection.inputStream.use { inputStream ->
            updateCookies(connection)
            return readResponseBody(inputStream, getCharSet(connection))
        }
    }

    private fun updateCookies(connection: HttpURLConnection) {
        val cookieManager = CookieHandler.getDefault() as CookieManager
        val headers = connection.headerFields["Set-Cookie"]
        headers?.forEach { cookieManager.cookieStore.add(null, HttpCookie.parse(it)[0]) }
    }

    fun getWithHeader(parameterList: Map<String, String>): String {
        val connection = openConnection(URL + encodeParameters(parameterList))

        var response = ""

        for ((key, value) in connection.headerFields) {
            response += "$key: $value\n"
        }

        connection.inputStream.use { inputStream ->
            response += readResponseBody(inputStream, getCharSet(connection))
        }

        return response
    }

    private fun encodeParameters(parameterList: Map<String, String>): String {
        var parameterString = "?"
        for ((key, value) in parameterList) {
            try {
                parameterString += "${URLEncoder.encode(key, ENCODING)}=${URLEncoder.encode(value, ENCODING)}&"
            } catch (e: UnsupportedEncodingException) {
                Log.e("encodeParameters()", e.toString())
            }
        }
        return parameterString
    }

    private fun readResponseBody(inputStream: InputStream, charset: String?): String {
        val body = StringBuilder()
        try {
            BufferedReader(InputStreamReader(inputStream, charset)).use { bufferedReader ->
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    body.append(line).append("\n")
                }
            }
        } catch (e: Exception) {
            Log.e("readResponseBody()", e.toString())
            body.append("******* Problem reading from server *******\n$e")
        }
        return body.toString().trim()
    }


    private fun getCharSet(connection: URLConnection): String? {
        var charset: String? = ENCODING
        val contentType = connection.contentType
        val contentInfo = contentType.replace(" ", "").split(";").toTypedArray()
        for (param in contentInfo) {
            if (param.startsWith("charset=")) charset = param.split("=").toTypedArray()[1]
        }
        Log.i("getCharSet()", "contentType = $contentType")
        Log.i("getCharSet()", "Encoding/charset = $charset")
        return charset
    }
}
