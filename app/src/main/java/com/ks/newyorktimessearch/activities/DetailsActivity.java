package com.ks.newyorktimessearch.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.ks.newyorktimessearch.R;

public class DetailsActivity extends AppCompatActivity {
//    @Bind(R.id.wvArticle)WebView articleWv;

    private WebView articleWv ;
    private ShareActionProvider miShareAction;
    private Intent shareIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        FacebookSdk.sdkInitialize(getApplicationContext());
//        ButterKnife.bind(this);

        articleWv = new WebView(this);
        articleWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
                Toast.makeText(DetailsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        String webUrl = getIntent().getExtras().getString("web_url");
        articleWv.loadUrl(webUrl);
        setContentView(articleWv);



//        setupShareIntent();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_article, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.item_share);
        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (miShareAction != null) {
            setupShareIntent();
            miShareAction.setShareIntent(shareIntent);
        }

        return true;
    }

    public void setupShareIntent() {
        // Create share intent
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, articleWv.getUrl());
        shareIntent.setType("text/plain");
//        startActivity(Intent.createChooser(shareIntent, "Share link using"));


//        shareIntent.setType("text/html");
//        shareIntent.putExtra(Intent.EXTRA_EMAIL, "srikanth1234@gmail.com");
//        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//        shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");

//        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    public void setupFacebookShareIntent() {
        ShareDialog shareDialog;
        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("Title")
                .setContentDescription(
                        "\"Body Of Test Post\"")
                .setContentUrl(Uri.parse("http://someurl.com/here"))
                .build();

        shareDialog.show(linkContent);
    }



}
