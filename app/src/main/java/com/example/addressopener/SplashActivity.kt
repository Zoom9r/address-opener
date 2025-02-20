package com.example.addressopener

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {
    private lateinit var splashText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var shift = 0f  // Для анімації градієнта

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Ховаємо ActionBar (якщо є)
        supportActionBar?.hide()

        splashText = findViewById(R.id.splashText)

        // Запускаємо ефекти
        startAnimations()

        // Перехід до головного екрану з затримкою
        splashText.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }

    private fun startAnimations() {
        fadeInAndScaleUp()
        animateGradient()
        fillTextFromBottom()
    }

    private fun fadeInAndScaleUp() {
        splashText.animate()
            .alpha(1f) // Збільшуємо прозорість до 100%
            .scaleX(1.2f) // Робимо текст трохи більшим
            .scaleY(1.2f)
            .setDuration(1000) // Тривалість анімації (1000 мс)
            .setInterpolator(DecelerateInterpolator()) // Плавне уповільнення
            .start()
    }

    private fun animateGradient() {
        val colors = intArrayOf(
            0xFFFF0000.toInt(), // Червоний
            0xFFFF9900.toInt(), // Оранжевий
            0xFFFFFF00.toInt(), // Жовтий
            0xFF00FF00.toInt(), // Зелений
            0xFF0099FF.toInt(), // Блакитний
            0xFF0000FF.toInt(), // Синій
            0xFFFF00FF.toInt()  // Фіолетовий
        )

        val positions = floatArrayOf(0f, 0.15f, 0.3f, 0.5f, 0.7f, 0.85f, 1f) // Плавні переходи

        val updateGradient = object : Runnable {
            override fun run() {
                shift += 10f // Кут зміщення (можна змінювати для швидшого чи повільнішого ефекту)
                val textWidth = splashText.paint.measureText(splashText.text.toString())
                val textHeight = splashText.textSize

                // Додаємо зміщення, щоб градієнт обертався
                val gradient = LinearGradient(
                    -shift, textHeight + shift, // Початкова точка (зміщуємо для ефекту обертання)
                    textWidth + shift, -shift, // Кінцева точка (рухаємо в протилежному напрямку)
                    colors, positions, Shader.TileMode.MIRROR
                )

                splashText.paint.shader = gradient
                splashText.invalidate() // Оновлення

                handler.postDelayed(this, 20) // Частота оновлення (20 мс)
            }
        }

        handler.post(updateGradient)
    }

    private fun fillTextFromBottom() {
        splashText.alpha = 0f
        splashText.translationY = 100f

        splashText.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(1200)
            .setInterpolator(AnticipateOvershootInterpolator()) // Плавний ефект заповнення
            .start()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Очищаємо хендлер
    }
}
