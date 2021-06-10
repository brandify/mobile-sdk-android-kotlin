package com.brandify.brandifySDKDemo.about

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import com.brandify.brandifySDKDemo.R

class AboutDetailsActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_details)

        val titleText = findViewById<View>(R.id.view_title) as TextView
        var urlToLoad = ""
        val bfURL = intent.getStringExtra("AboutURL")
        if (bfURL != null) {
            val termsURL = "https://www.brandify.com/company/terms"
            val privacyURL = "https://www.brandify.com/company/privacy-policy"
            when (bfURL) {
                "terms" -> {
                    urlToLoad = termsURL
                    titleText.text = "Terms of Service"
                }
                "privacy" -> {
                    urlToLoad = privacyURL
                    titleText.text = "Privacy Policy"
                }
            }
        }
        val webView = findViewById<WebView>(R.id.aboutWebView)
        webView.loadUrl(urlToLoad)

        val backButton = findViewById<Button>(R.id.backButtonId)
        backButton.setOnClickListener { finish() }
    }
}