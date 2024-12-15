package org.example;

import com.google.gson.JsonObject;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public abstract class HttpConnection {

    static {
        Unirest.config()
                .socketTimeout(0)
                .connectTimeout(0);
    }


    public static HttpRequest<?> buildQuery(String route, String query, String database ) {
        JsonObject data = new JsonObject();
        data.addProperty( "query", query );
        data.addProperty( "namespace", database );

        return Unirest.post( "{protocol}://{host}:{port}" + route )
                .header( "Content-ExpressionType", "application/json" )
                .body( data );

    }


    protected static HttpResponse<String> execute(String prefix, String query, String database ) {
        HttpRequest<?> request = buildQuery( prefix, query, database );
        request.basicAuth( "pa", "" );
        request.routeParam( "protocol", "http" );
        request.routeParam( "host", "127.0.0.1" );
        request.routeParam( "port", "13137" );
        return request.asString();
    }

}
