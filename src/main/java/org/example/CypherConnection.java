package org.example;

import kong.unirest.HttpResponse;

public class CypherConnection extends HttpConnection {

    public static HttpResponse<String> executeGetResponse(String query, String database) {
        return execute("/cypher", query, database);
    }

}
