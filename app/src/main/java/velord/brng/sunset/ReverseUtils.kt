import android.animation.AnimatorSet
import android.os.Build
import android.view.animation.Interpolator
import java.util.*

//possible solution for revert animation
class ReverseInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float
            =  Math.abs(input - 1f)
}

fun reverseSequentialAnimatorSet(animatorSet: AnimatorSet): AnimatorSet {
    val animators = animatorSet.childAnimations
    Collections.reverse(animators)

    val reversedAnimatorSet = AnimatorSet()
    reversedAnimatorSet.playSequentially(animators)
    reversedAnimatorSet.duration = 4000

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        // getInterpolator() requires API 18
        reversedAnimatorSet.interpolator = animatorSet.interpolator
    }
    return reversedAnimatorSet
}