package velord.brng.sunset

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
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

    private var isNight: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        sceneView.setOnClickListener {
            if (isNight) toDown()
            else toNight()
        }
    }

    private fun initViews() {
        sceneView = findViewById(R.id.scene)
        sunView = findViewById(R.id.sun)
        skyView = findViewById(R.id.sky)
    }

    private fun toNight() {
        nightAnim()
    }

    private fun toDown() {
        downAnim()
    }

    private fun downAnim() {
        val sunStartY = sceneView.bottom.toFloat()
        val sunEndY = sunView.top.toFloat()

        val heightAnimator = sunAnimator(sunStartY, sunEndY)

        val with = nightToSunsetSkyAnimator()

        val before = sunsetToBlueSkyAnimator()

        animationQueue(
            heightAnimator,
            with, heatSunAnimator(),
            beforeAnim = before
        )
    }

    private fun nightAnim() {
        val sunStartY = sunView.top.toFloat()
        val sunEndY = sceneView.bottom.toFloat()

        val heightAnimator = sunAnimator(sunStartY, sunEndY)

        val with = blueToSunsetSkyAnimator()

        val before = sunsetToNightSkyAnimator()

        animationQueue(
            heightAnimator,
            with, heatSunAnimator(),
            beforeAnim = before
        )
    }

    private fun blueToSunsetSkyAnimator(duration: Long = 3000) =
        skyAnimator(blueSkyColor, sunsetSkyColor, duration)

    private fun sunsetToNightSkyAnimator(duration: Long = 3000) =
        skyAnimator(sunsetSkyColor, nightSkyColor, duration)

    private fun nightToSunsetSkyAnimator(duration: Long = 3000) =
        skyAnimator(nightSkyColor, sunsetSkyColor, duration)

    private fun sunsetToBlueSkyAnimator(duration: Long = 3000) =
        skyAnimator(sunsetSkyColor, blueSkyColor, duration)

    private fun sunAnimator(sunStartY: Float,
                            sunEndY: Float,
                            duration: Long = 4000) =
        ObjectAnimator
            .ofFloat(sunView, "y", sunStartY,  sunEndY)
            .setDuration(duration)
            .apply {
                interpolator = AccelerateInterpolator()
            }

    private fun heatSunAnimator(duration: Long = 800,
                                scaleX: Float = 1.3f,
                                scaleY: Float = 1.3f,
                                repeat: Int = 5) =
        ObjectAnimator.ofPropertyValuesHolder(
            sunView,
            PropertyValuesHolder.ofFloat("scaleX", scaleX),
            PropertyValuesHolder.ofFloat("scaleY", scaleY))
            .setDuration(duration)
            .apply {
                repeatCount = repeat
                repeatMode = ObjectAnimator.REVERSE
                interpolator = AccelerateInterpolator()
            }

    private fun skyAnimator(from: Int,
                            to: Int,
                            duration: Long = 3000) =
        ObjectAnimator
            .ofInt(skyView, "backgroundColor", from, to)
            .setDuration(duration)
            .apply { setEvaluator(ArgbEvaluator()) }

    private fun animationQueue(heightAnimator: ObjectAnimator,
                               vararg withAnim: ObjectAnimator,
                               beforeAnim: ObjectAnimator) {
        AnimatorSet().apply {
            doOnEnd {
                if (isNight) isNight = false
                else isNight = true
            }
            play(heightAnimator).apply {
                withAnim.forEach { with(it) }
            }.before(beforeAnim)
            start()
        }
    }
}
