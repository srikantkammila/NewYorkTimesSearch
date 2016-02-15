package com.ks.newyorktimessearch.network;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by skammila on 2/8/16.
 */
public class RestClient {
    public static String BASE_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    public static String API_KEY = "58f62141d2febdd794b7b5ef14de4ee2:19:74340471";
    private RequestParams searchCriteria = new RequestParams();
    private JsonHttpResponseHandler responseHandler;
    public String searchQuery = null;

    public RestClient(JsonHttpResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    public void performSearch(String query, int page, HashMap<String, String> extraParams, boolean clearSearch) {
        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams reqParams = new RequestParams();
        if (clearSearch) {
            searchCriteria = new RequestParams();
            searchCriteria.put("api-key", API_KEY);
        }
        searchCriteria.remove("fq"); //reset filterquery everytime
        searchCriteria.put("api-key", API_KEY);
        searchCriteria.put("page", page);
        if (query != null) {
            searchCriteria.put("q", query);
            searchQuery = query;
        } else if (searchQuery != null) {
            searchCriteria.put("q", searchQuery);
        } else {
            searchCriteria.remove("q");
        }
        if (extraParams != null) {
            Set<String> keys = extraParams.keySet();
            for (String key :
                    keys) {
                Log.d("DEBUG", "KEY: "+key + " Val: "+extraParams.get(key));
                searchCriteria.put(key, extraParams.get(key));
            }
        }
        RequestHandle handle = client.get(BASE_URL, searchCriteria, responseHandler);
    }

    public void clearSearchCriteria() {
        //remove all stored daa
        searchCriteria = new RequestParams();
        searchCriteria.put("api-key", API_KEY);
    }

    public void clearSearch() {
        RequestParams searchCriteria = new RequestParams();
    }

}
