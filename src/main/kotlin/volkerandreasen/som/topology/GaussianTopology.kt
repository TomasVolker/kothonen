package volkerandreasen.som.topology

import tomasvolker.numeriko.core.primitives.squared
import kotlin.math.*

interface GaussianTopology: WeightedTopology {

    var deviation: Double

    fun distanceSquared(from: Int, to: Int): Double = distance(from, to).squared()

    fun distance(from: Int, to: Int): Double

    override fun weight(from: Int, to: Int): Double =
        exp(-distanceSquared(from, to) / (2 * deviation.squared()))

}


