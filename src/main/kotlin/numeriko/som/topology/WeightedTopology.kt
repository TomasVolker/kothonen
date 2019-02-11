package numeriko.som.topology

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

