package org.example;

import kong.unirest.HttpResponse;

public class MongoConnection extends HttpConnection {

    public static final String MONGO_PREFIX = "/mongo";

    public static HttpResponse<String> executeGetResponse(String mongoQl, String database) {
        return execute(MONGO_PREFIX, mongoQl, database);
    }

}
