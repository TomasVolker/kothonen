package volkerandreasen.som.grid

import volkerandreasen.som.topology.GaussianTopology
import volkerandreasen.som.topology.Topology
import tomasvolker.numeriko.core.primitives.squared
import kotlin.math.absoluteValue
import kotlin.math.sqrt

class Grid3DGaussianTopology(
    val width: Int,
    val height: Int,
    val depth: Int,
    override var deviation: Double = 1.0
): GaussianTopology, Topology {

    override val size: Int
        get() = width * height * depth

    fun xOf(index: Int) = index % width
    fun yOf(index: Int) = (index % (width * height)) / width
    fun zOf(index: Int) = index / (width * height)

    fun toLinear(x: Int, y: Int, z: Int) = width * height * z + width * y + x

    fun deltaX(from: Int, to: Int): Int = xOf(to) - xOf(from)
    fun deltaY(from: Int, to: Int): Int = yOf(to) - yOf(from)
    fun deltaZ(from: Int, to: Int): Int = zOf(to) - zOf(from)

    override fun distanceSquared(from: Int, to: Int): Double =
        (deltaX(from, to).squared() +
         deltaY(from, to).squared() +
         deltaZ(from, to).squared()
        ).toDouble()

    override fun distance(from: Int, to: Int): Double = sqrt(distanceSquared(from, to))

    override fun areConnected(from: Int, to: Int): Boolean =
        (deltaX(from, to).absoluteValue + deltaY(from, to).absoluteValue + deltaZ(from, to).absoluteValue) == 1

    override fun neighbors(nodeIndex: Int): Iterable<Int> =
        mutableListOf<Int>().apply {
            val x = xOf(nodeIndex)
            val y = yOf(nodeIndex)
            val z = zOf(nodeIndex)

            if (0 < x) add(toLinear(x-1, y, z))
            if (x < width-1) add(toLinear(x+1, y, z))

            if (0 < y) add(toLinear(x, y-1, z))
            if (y < height-1) add(toLinear(x, y+1, z))

            if (0 < z) add(toLinear(x, y, z-1))
            if (z < depth-1) add(toLinear(x, y, z+1))

        }

}