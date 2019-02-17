package numeriko.som.grid

import numeriko.som.topology.GaussianTopology
import numeriko.som.topology.Topology
import tomasvolker.numeriko.core.primitives.squared
import kotlin.math.absoluteValue
import kotlin.math.sqrt

class Grid1DGaussianTopology(
    override val size: Int,
    override var deviation: Double = 1.0
): GaussianTopology, Topology {


    override fun distanceSquared(from: Int, to: Int): Double =
        (to - from).squared().toDouble()

    override fun distance(from: Int, to: Int): Double = sqrt(distanceSquared(from, to))

    override fun areConnected(from: Int, to: Int): Boolean =
        (to - from).absoluteValue == 1

    override fun neighbors(nodeIndex: Int): Iterable<Int> =
        mutableListOf<Int>().apply {

            if (0 < nodeIndex) add(nodeIndex-1)
            if (nodeIndex < size-1) add(nodeIndex+1)

        }

}