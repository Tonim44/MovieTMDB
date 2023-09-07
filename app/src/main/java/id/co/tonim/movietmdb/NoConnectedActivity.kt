package id.co.tonim.movietmdb

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import id.co.tonim.movietmdb.utils.LoadingDialog

class NoConnectedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noconnect)

        val refreshButton: CardView = findViewById(R.id.muat_ulang)
        refreshButton.setOnClickListener {
            val loading = LoadingDialog(this)
            loading.startLoading()
            if (isConnectedToNetwork()) {
                startActivity(Intent(this@NoConnectedActivity, MainActivity::class.java))
                (object : Runnable {
                    override fun run() {
                        finish()
                        loading.isDismiss()
                    }
                })
            }else{
                Toast.makeText(this@NoConnectedActivity, "Koneksi gagal", Toast.LENGTH_LONG).show()
                loading.isDismiss()
            }
        }
    }

    private fun isConnectedToNetwork(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        var isConnected = false

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            isConnected =
                capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        } else {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            isConnected = networkInfo?.isConnected ?: false
        }

        return isConnected
    }
}


