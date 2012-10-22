// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

public class JSONRequesterFactory
{
    public static JSONRequester uploadRequester() {
        String url = Settings.getString(Settings.SERVER_URL) + "corrections/upload.json";
        return new HttpJSONClient(url);
    }
}
