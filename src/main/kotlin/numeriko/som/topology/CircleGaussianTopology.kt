package numeriko.som.topology

import tomasvolker.numeriko.core.primitives.modulo
import tomasvolker.numeriko.core.primitives.squared
import kotlin.math.*

class CircleGaussianTopology(
    override val size: Int,
    override var deviation: Double
): GaussianTopology, Topology {

    override fun distanceSquared(from: Int, to: Int): Double {
        val angleFrom = 2 * PI * from.toDouble() / size
        val angleTo = 2 * PI * to.toDouble() / size

        val deltaX = size * (cos(angleFrom) - cos(angleTo))
        val deltaY = size * (sin(angleFrom) - sin(angleTo))
        return deltaX.squared() + deltaY.squared()
    }

    override fun distance(from: Int, to: Int): Double = sqrt(distanceSquared(from, to))

    override fun areConnected(from: Int, to: Int): Boolean =
        ((from - to) modulo size).absoluteValue < 2

    override fun neighbors(nodeIndex: Int): Iterable<Int> =
        listOf(
            (nodeIndex-1) modulo size,
            (nodeIndex+1) modulo size
        )

}