// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpJSONClient implements JSONRequester
{
    HttpClient httpClient;
    HttpPost httpRequest;
    HttpResponse httpResponse;

    public HttpJSONClient(String url) {
        super();
        httpClient = new DefaultHttpClient();
        httpRequest = new HttpPost(url);
    }

    public void sendRequest(JSONObject json) throws IOException, JSONException {
        StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
        entity.setContentType("application/json");
        httpRequest.setEntity(entity);
        httpResponse = httpClient.execute(httpRequest);
    }

    public boolean requestFailed() {
        return httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK;
    }

    public String failureMessage() {
        StatusLine statusLine = httpResponse.getStatusLine();
        return "HTTP " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase();
    }

    public JSONObject parseResponse()
            throws UnsupportedEncodingException, JSONException, IOException {
        byte[] body = EntityUtils.toByteArray(httpResponse.getEntity());
        return new JSONObject(new String(body, "UTF-8"));
    }
}
