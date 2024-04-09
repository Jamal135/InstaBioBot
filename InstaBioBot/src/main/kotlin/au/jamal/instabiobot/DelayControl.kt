package au.jamal.instabiobot

import kotlin.random.Random

object DelayControl {

    private val randomGenerator = Random.Default

    fun sleep(minDuration: Int = 5, maxDuration: Int = 10) {
        val durationSeconds = randomGenerator.nextInt(minDuration, maxDuration + 1)
        val durationMillis = durationSeconds * 1000L
        Thread.sleep(durationMillis)
    }
}