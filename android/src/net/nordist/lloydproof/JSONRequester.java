// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.json.JSONException;
import org.json.JSONObject;

public interface JSONRequester
{
    void sendRequest(JSONObject json) throws IOException, JSONException;
    boolean requestFailed();
    String failureMessage();
    JSONObject parseResponse() throws UnsupportedEncodingException, JSONException, IOException;
}
