package com.brandify.brandifySDKDemo.about

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import com.brandify.brandifySDKDemo.R

class AboutActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val webView = findViewById<WebView>(R.id.aboutWebView)
        val bfURL = "https://www.brandify.com/about/company"
        webView.loadUrl(bfURL)

        val backButton = findViewById<Button>(R.id.backButtonId)
        backButton.setOnClickListener { finish() }

        val termsButton = findViewById<Button>(R.id.termsButton)
        termsButton.setOnClickListener {
            val intent = Intent(applicationContext, AboutDetailsActivity::class.java)
            intent.putExtra("AboutURL", "terms")
            startActivity(intent)
        }

        val privacyButton = findViewById<Button>(R.id.privacyButton)
        privacyButton.setOnClickListener {
            val intent = Intent(applicationContext, AboutDetailsActivity::class.java)
            intent.putExtra("AboutURL", "privacy")
            startActivity(intent)
        }
    }
}