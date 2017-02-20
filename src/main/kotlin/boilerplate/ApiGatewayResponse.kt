package boilerplate

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.log4j.Logger
import java.nio.charset.StandardCharsets
import java.util.Base64

class ApiGatewayResponse(
        val statusCode: Int,
        val body: String,
        val headers: Map<String, String>,
        // API Gateway expects the property to be called "isBase64Encoded" => isIs
        val isIsBase64Encoded: Boolean
) {

    class Builder {

        private var statusCode = 200
        private var headers = emptyMap<String, String>()
        private var rawBody: String? = null
        private var objectBody: Any? = null
        private var binaryBody: ByteArray? = null
        private var base64Encoded: Boolean = false

        fun setStatusCode(statusCode: Int): Builder {
            this.statusCode = statusCode
            return this
        }

        fun setHeaders(headers: Map<String, String>): Builder {
            this.headers = headers
            return this
        }

        /**
         * Builds the [ApiGatewayResponse] using the passed raw body string.
         */
        fun setRawBody(rawBody: String): Builder {
            this.rawBody = rawBody
            return this
        }

        /**
         * Builds the [ApiGatewayResponse] using the passed object body
         * converted to JSON.
         */
        fun setObjectBody(objectBody: Any): Builder {
            this.objectBody = objectBody
            return this
        }

        /**
         * Builds the [ApiGatewayResponse] using the passed binary body
         * encoded as base64. [ setBase64Encoded(true)][.setBase64Encoded] will be in invoked automatically.
         */
        fun setBinaryBody(binaryBody: ByteArray): Builder {
            this.binaryBody = binaryBody
            setBase64Encoded(true)
            return this
        }

        /**
         * A binary or rather a base64encoded responses requires
         *
         *  1. "Binary Media Types" to be configured in API Gateway
         *  1. a request with an "Accept" header set to one of the "Binary Media
         * Types"
         *
         */
        fun setBase64Encoded(base64Encoded: Boolean): Builder {
            this.base64Encoded = base64Encoded
            return this
        }

        fun build(): ApiGatewayResponse {
            var body: String = ""
            if (rawBody != null) {
                body = rawBody as String
            } else if (objectBody != null) {
                try {
                    body = objectMapper.writeValueAsString(objectBody)
                } catch (e: JsonProcessingException) {
                    LOG.error("failed to serialize object", e)
                    throw RuntimeException(e)
                }

            } else if (binaryBody != null) {
                body = String(Base64.getEncoder().encode(binaryBody!!), StandardCharsets.UTF_8)
            }
            return ApiGatewayResponse(statusCode, body, headers, base64Encoded)
        }

        companion object {

            private val LOG = Logger.getLogger(ApiGatewayResponse.Builder::class.java)

            private val objectMapper = ObjectMapper()
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
