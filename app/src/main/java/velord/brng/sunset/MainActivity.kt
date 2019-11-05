package velord.brng.sunset

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: View
    private lateinit var sunView: View
    private lateinit var skyView: View

    private val blueSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.blue_sky)
    }
    private val sunsetSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.sunset_sky)
    }
    private val nightSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.night_sky)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        sceneView.setOnClickListener {
            startAnimation()
        }
    }

    private fun initViews() {
        sceneView = findViewById(R.id.scene)
        sunView = findViewById(R.id.sun)
        skyView = findViewById(R.id.sky)
    }


    private fun startAnimation() {
        val sunStartY = sunView.top.toFloat()
        val sunEndY = skyView.height.toFloat()

        val heightAnimator = ObjectAnimator
            .ofFloat(sunView, "y", sunStartY,  sunEndY)
            .setDuration(3000)
            .apply {
                interpolator = AccelerateInterpolator()
            }

        val sunsetSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor",
                blueSkyColor, sunsetSkyColor)
            .setDuration(3000)
            .apply {
                setEvaluator(ArgbEvaluator())
            }

        val nightSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor",
                sunsetSkyColor, nightSkyColor)
            .setDuration(3000)
            .apply {
                setEvaluator(ArgbEvaluator())
            }

        AnimatorSet().apply {
            play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator)
            start()
        }
    }
}
