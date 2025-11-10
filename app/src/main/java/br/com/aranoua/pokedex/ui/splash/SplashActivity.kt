package br.com.aranoua.pokedex.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import br.com.aranoua.pokedex.MainActivity
import br.com.aranoua.pokedex.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Handler para aguardar um tempo e depois iniciar a MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza a SplashActivity para que o usuário não possa voltar para ela
        }, 3000) // 3 segundos de delay
    }
}
