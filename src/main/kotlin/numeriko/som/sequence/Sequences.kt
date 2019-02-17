package numeriko.som.sequence

import kotlin.math.pow

fun linearSequence(
    first: Double,
    last: Double,
    count: Int
) = sequence {

    val step = (last - first) / (count-1)

    var current = first
    while(true) {
        yield(current)
        current += step
    }

}

fun exponentialSequence(
    first: Double,
    last: Double,
    count: Int
) = sequence {

    val factor = (last / first).pow(1.0 / (count-1))

    var current = first
    while(true) {
        yield(current)
        current *= factor
    }

}

fun <T> List<T>.asShuffledSequence() = sequence {
    while(true) {
        yieldAll(this@asShuffledSequence.shuffled())
    }
}
