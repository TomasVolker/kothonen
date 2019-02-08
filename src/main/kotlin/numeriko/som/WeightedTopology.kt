package numeriko.som

import tomasvolker.numeriko.core.primitives.modulo
import tomasvolker.numeriko.core.primitives.squared
import kotlin.math.*

interface WeightedTopology {

    val size: Int

    fun support(nodeIndex: Int): Iterable<Int> = 0 until size

    fun weight(from: Int, to: Int): Double

}

interface Topology {

    val size: Int

    fun areConnected(from: Int, to: Int): Boolean = to in neighbors(from)

    fun neighbors(nodeIndex: Int): Iterable<Int>

}

class CircleGaussianTopology(
    override val size: Int,
    var deviation: Double
): WeightedTopology, Topology {

    override fun weight(from: Int, to: Int): Double {
        val angleFrom = 2 * PI * from.toDouble() / size
        val angleTo = 2 * PI * to.toDouble() / size

        val deltaX = size * (cos(angleFrom) - cos(angleTo))
        val deltaY = size * (sin(angleFrom) - sin(angleTo))

        return exp(-(deltaX.squared() + deltaY.squared()) / (2 * deviation.squared()))
    }

    override fun areConnected(from: Int, to: Int): Boolean =
        ((from - to) modulo size).absoluteValue < 2

    override fun neighbors(nodeIndex: Int): Iterable<Int> =
            listOf(
                (nodeIndex-1) modulo size,
                (nodeIndex+1) modulo size
            )

}


class Grid2DGaussianTopology(
    val width: Int,
    val height: Int,
    var deviation: Double
): WeightedTopology, Topology {

    override val size: Int
        get() = width * height

    fun deltaX(from: Int, to: Int): Int {
        val fromX = from % width
        val toX = to % width
        return toX - fromX
    }

    fun deltaY(from: Int, to: Int): Int {
        val fromY = from / width
        val toY = to / width
        return toY - fromY
    }

    override fun weight(from: Int, to: Int): Double {

        val deltaX = deltaX(from, to)
        val deltaY = deltaY(from, to)

        return exp(-(deltaX.squared() + deltaY.squared()) / (2 * deviation.squared()))
    }

    override fun areConnected(from: Int, to: Int): Boolean =
        (deltaX(from, to).absoluteValue + deltaY(from, to).absoluteValue) == 1

    // TODO beri inefishient
    override fun neighbors(nodeIndex: Int): Iterable<Int> =
        (0 until size).filter { areConnected(nodeIndex, it) }

    fun toLinear(x: Int, y: Int) = width * y + x

}