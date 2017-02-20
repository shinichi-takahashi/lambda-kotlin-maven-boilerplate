package boilerplate

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import org.apache.log4j.Logger
import java.util.Collections

class Handler : RequestHandler<Map<String, Any>, ApiGatewayResponse> {

    override fun handleRequest(input: Map<String, Any>, context: Context): ApiGatewayResponse {
        LOG.info("received: " + input)
        val responseBody = Response("Go Serverless v1.x! Your function executed successfully!", input)
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(responseBody)
                .setHeaders(Collections.singletonMap<String, String>("X-Powered-By", "AWS Lambda & serverless"))
                .build()
    }

    companion object {
        private val LOG = Logger.getLogger(Handler::class.java)
    }
}
