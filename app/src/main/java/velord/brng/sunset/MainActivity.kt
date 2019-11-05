package velord.brng.sunset

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: View
    private lateinit var sunView: View
    private lateinit var skyView: View
    private lateinit var seaView: View
    private lateinit var sunReflectionView: View

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
        seaView = findViewById(R.id.sea)
        sunReflectionView = findViewById(R.id.sunReflection)
    }

    private fun toNight() {
        nightAnim()
    }

    private fun toDown() {
        downAnim()
    }

    private fun downAnim() {
        val sunAnim = getSunAnimDownAnimator()

        val withSunReflectAnim = getSunReflectionDownAnimator()
        val withSunReflectionHeatAnim = getHeatSunReflectionAnimator()
        val withSunHeatAnim = getHeatSunAnimator()
        val withSkyAnim = getNightToSunsetSkyAnimator()

        val beforeSkyAnim = getSunsetToBlueSkyAnimator()

        animationBuilder(
            sunAnim,
            withSunReflectAnim, withSunReflectionHeatAnim,
            withSunHeatAnim, withSkyAnim,
            beforeAnim = beforeSkyAnim
        )
    }

    private fun nightAnim() {
        val sunAnim = getSunAnimNightAnimator()

        val withSunReflectAnim = getSunReflectionNightAnimator()
        val withSunReflectionHeatAnim = getHeatSunReflectionAnimator()
        val withSunHeatAnim = getHeatSunAnimator()
        val withSkyAnim = getBlueToSunsetSkyAnimator()

        val beforeSkyAnim = getSunsetToNightSkyAnimator()

        animationBuilder(
            sunAnim,
            withSunReflectAnim, withSunReflectionHeatAnim,
            withSunHeatAnim, withSkyAnim,
            beforeAnim = beforeSkyAnim
        )
    }

    private fun getSunAnimNightAnimator(): ObjectAnimator {
        val sunStartY = sunView.top.toFloat()
        val sunEndY = sceneView.bottom.toFloat()
        val view = sunView
        return sunAnimatorBuilder(view, sunStartY, sunEndY)
    }

    private fun getSunAnimDownAnimator(): ObjectAnimator {
        val sunStartY = sceneView.bottom.toFloat()
        val sunEndY = sunView.top.toFloat()
        val view = sunView
        return sunAnimatorBuilder(view, sunStartY, sunEndY)
    }

    private fun getSunReflectionNightAnimator(): ObjectAnimator {
        val sunReflectFromY = (sunReflectionView.getHeight()).toFloat()
        val sunReflectToY = (sunReflectionView.getY() - seaView.getHeight())
        val view = sunReflectionView
        return sunAnimatorBuilder(view, sunReflectFromY, sunReflectToY)
    }

    private fun getSunReflectionDownAnimator(): ObjectAnimator {
        val sunReflectFromY = (sunReflectionView.getY() - seaView.getHeight())
        val sunReflectToY = (sunReflectionView.getHeight()).toFloat()
        val view = sunReflectionView
        return sunAnimatorBuilder(view, sunReflectFromY, sunReflectToY)
    }

    private fun getBlueToSunsetSkyAnimator(duration: Long = 3000) =
        skyAnimatorBuilder(blueSkyColor, sunsetSkyColor, duration)

    private fun getSunsetToNightSkyAnimator(duration: Long = 3000) =
        skyAnimatorBuilder(sunsetSkyColor, nightSkyColor, duration)

    private fun getNightToSunsetSkyAnimator(duration: Long = 3000) =
        skyAnimatorBuilder(nightSkyColor, sunsetSkyColor, duration)

    private fun getSunsetToBlueSkyAnimator(duration: Long = 3000) =
        skyAnimatorBuilder(sunsetSkyColor, blueSkyColor, duration)

    private fun getHeatSunReflectionAnimator(duration: Long = 800,
                                             scaleX: Float = 1.3f,
                                             scaleY: Float = 1.3f,
                                             repeat: Int = 5) =
        sunHeatBuilder(sunReflectionView)

    private fun getHeatSunAnimator(duration: Long = 800,
                                   scaleX: Float = 1.3f,
                                   scaleY: Float = 1.3f,
                                   repeat: Int = 5) =
        sunHeatBuilder(sunView)

    private fun sunHeatBuilder(view: View,
                               duration: Long = 800,
                               scaleX: Float = 1.3f,
                               scaleY: Float = 1.3f,
                               repeat: Int = 5) =
        ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("scaleX", scaleX),
            PropertyValuesHolder.ofFloat("scaleY", scaleY))
            .setDuration(duration)
            .apply {
                repeatCount = repeat
                repeatMode = ObjectAnimator.REVERSE
                interpolator = AccelerateInterpolator()
            }

    private fun sunAnimatorBuilder(view: View,
                                   sunStartY: Float,
                                   sunEndY: Float,
                                   duration: Long = 4000) =
        ObjectAnimator
            .ofFloat(view, "y", sunStartY,  sunEndY)
            .setDuration(duration)
            .apply {
                interpolator = AccelerateDecelerateInterpolator()
            }

    private fun skyAnimatorBuilder(from: Int,
                                   to: Int,
                                   duration: Long = 3000) =
        ObjectAnimator
            .ofInt(skyView, "backgroundColor", from, to)
            .setDuration(duration)
            .apply { setEvaluator(ArgbEvaluator()) }

    private fun animationBuilder(heightAnimator: ObjectAnimator,
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
