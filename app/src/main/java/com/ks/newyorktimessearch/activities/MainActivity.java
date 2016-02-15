package com.ks.newyorktimessearch.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ks.newyorktimessearch.R;
import com.ks.newyorktimessearch.adapter.ArticleAdapter;
import com.ks.newyorktimessearch.adapter.InfiniteScrollListener;
import com.ks.newyorktimessearch.fragments.FilterFragment;
import com.ks.newyorktimessearch.model.Article;
import com.ks.newyorktimessearch.network.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
//    @Bind(R.id.etQuery)EditText searchQuery;
//    @Bind(R.id.btnSearch)Button searchBtn;
    @Bind(R.id.rvSearchResult) RecyclerView searchResult;
    @Bind(R.id.tvErrorMsg) TextView errorMsg;
    @Bind(R.id.tvNoResultMsg) TextView noResultMsg;

    List<Article> articles = new ArrayList<Article>();
    ArticleAdapter articleAdapter;
    RestClient restClient;
    JsonHttpResponseHandler responseHandler;
    FragmentManager fm = getSupportFragmentManager();
    FilterFragment filterDialog;

    public Map<String, Object> savedFilter = new HashMap<String, Object>();
    public Map<String, Object> filter = new HashMap<String, Object>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        articleAdapter = new ArticleAdapter(articles);
        searchResult.setAdapter(articleAdapter);
        StaggeredGridLayoutManager stglManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        searchResult.setLayoutManager(stglManager);
        searchResult.addOnScrollListener(new InfiniteScrollListener(stglManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMoreDataFromApi(page);
            }
        });

        responseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                try {
                    JSONArray articleJsonArray = response.getJSONObject("response").getJSONArray("docs");
                    List<Article> arcls = Article.articlesFromJsonArray(articleJsonArray);
                    if (arcls.size() > 0) {
                        noResultMsg.setVisibility(View.INVISIBLE);
                        articles.addAll(arcls);
                        articleAdapter.notifyDataSetChanged();
                    } else {
                        noResultMsg.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    Log.e("ERROR", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        };

        restClient = new RestClient(responseHandler);
        if (!isNetworkAvailable() || !isOnline()) {
            //Network not available. Show error
            errorMsg.setVisibility(View.VISIBLE);
        } else {
            clearSearch(null);
        }

//        restClient.performSearch(null, 0, null);
    }

    public void loadMoreDataFromApi(int page) {
        //fetch next page of results and add to adapter
        restClient.performSearch(null, page, null, false);
    }

    public void clearSearch(String query) {
        articles.clear();
        articleAdapter.notifyDataSetChanged();
        restClient.performSearch(query, 0, null, true);
    }

    public void doSearch(String query) {
        articles.clear();
        articleAdapter.notifyDataSetChanged();
        restClient.performSearch(query, 0, null, false);
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                doSearch(query);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().equals("")) {
                    restClient.searchQuery = null;
                    doSearch(null);
                    return true;
                }
                return false;
            }
        });

        MenuItem filterItem = menu.findItem(R.id.action_filter);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showFilterDialog();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void showFilterDialog() {
        filterDialog = FilterFragment.newInstance("Filter");
        filterDialog.show(fm, "fragment_filter");
    }

    private void hideFilterDialog() {
        filterDialog.dismiss();
    }



    public void showDatepicker(View view){
        System.out.println("Show DatePicker");
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (checked) {
            filter.put(Integer.toString(view.getId()), true);
        } else {
            filter.put(Integer.toString(view.getId()), false);
        }
    }

    public void onNewsDeskChange(View view) {
        System.out.println("news desk change" + view.getId());
        // Is the button now checked?
        boolean checked = ((CheckBox) view).isChecked();
        if (checked) {
            filter.put(Integer.toString(view.getId()), true);
        } else {
            filter.put(Integer.toString(view.getId()), false);
        }
    }

    public void onSaveClicked(View view) {
        //copy the data from filter to savedfilter and apply it
        savedFilter = filter;
        Set<String> keys = savedFilter.keySet();
        Iterator<String> itr = keys.iterator();
        HashMap<String, String> urlParts = new HashMap<>();
        StringBuilder nsdURL = new StringBuilder();
        while(itr.hasNext()) {
            String key = itr.next();
            Object value = savedFilter.get(key);
            switch (Integer.parseInt(key)) {
                case R.id.radio_latest:
                    if (value  instanceof Boolean && (Boolean)value == true){
                        urlParts.put("sort", "newest");
                    }
                    break;
                case R.id.radio_oldest:
                    if (value  instanceof Boolean && (Boolean)value == true){
                        urlParts.put("sort", "oldest");
                    }
                    break;
                case R.id.cbArts:
                    if (value  instanceof Boolean && (Boolean)value == true){
                        nsdURL.append(" \"Arts\" ");
                    }
                    break;
                case R.id.cbFashin:
                    if (value  instanceof Boolean && (Boolean)value == true){
                        nsdURL.append(" \"Fashion & Style\" ");
                    }
                    break;
                case R.id.cbSports:
                    if (value  instanceof Boolean && (Boolean)value == true){
                        nsdURL.append(" \"Sports\" ");
                    }
                    break;
                case R.id.etBeginDate:
                    if (value  instanceof Long && ((Long) value) > 0){
                        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
                        String displayDate = (Long)value > 0 ? ft.format(new Date((Long)value)) : "";
                        if (displayDate.length() > 0) {
                            urlParts.put("begin_date", displayDate);
                        }
                    }
                    break;

                default:
                    break;
            }
        }
        if (nsdURL.toString().trim().length() > 0) {
            urlParts.put("fq", "news_desk:(" + nsdURL.toString().trim() + ")");
        }
        articles.clear();
        articleAdapter.notifyDataSetChanged();
        restClient.performSearch(null, 0, urlParts, false);
        hideFilterDialog();
    }

    public void onCancelClicked(View view) {
        //clear the filter
        filter.clear();
        hideFilterDialog();
    }

}
