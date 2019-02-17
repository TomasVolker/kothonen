package numeriko.som.grid

import numeriko.som.topology.GaussianTopology
import numeriko.som.topology.Topology
import tomasvolker.numeriko.core.primitives.squared
import kotlin.math.absoluteValue
import kotlin.math.sqrt

class Grid2DGaussianTopology(
    val width: Int,
    val height: Int,
    override var deviation: Double = 1.0
): GaussianTopology, Topology {

    override val size: Int
        get() = width * height

    fun xOf(index: Int) = index % width
    fun yOf(index: Int) = index / width

    fun deltaX(from: Int, to: Int): Int = xOf(to) - xOf(from)
    fun deltaY(from: Int, to: Int): Int = yOf(to) - yOf(from)

    override fun distanceSquared(from: Int, to: Int): Double =
        (deltaX(from, to).squared() + deltaY(from, to).squared()).toDouble()

    override fun distance(from: Int, to: Int): Double = sqrt(distanceSquared(from, to))

    override fun areConnected(from: Int, to: Int): Boolean =
        (deltaX(from, to).absoluteValue + deltaY(from, to).absoluteValue) == 1

    override fun neighbors(nodeIndex: Int): Iterable<Int> =
        mutableListOf<Int>().apply {
            val x = xOf(nodeIndex)
            val y = yOf(nodeIndex)

            if (0 < x) add(toLinear(x-1, y))
            if (x < width-1) add(toLinear(x+1, y))
            if (0 < y) add(toLinear(x, y-1))
            if (y < height-1) add(toLinear(x, y+1))

        }

    fun toLinear(x: Int, y: Int) = width * y + x

}