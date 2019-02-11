package numeriko.som


import org.openrndr.*
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.math.Matrix44
import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.functions.normalized
import tomasvolker.numeriko.core.functions.times
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextGaussianArray1D
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class SelfOrganizingMap<out T: WeightedTopology>(
    val topology: T,
    val dimension: Int,
    var learningRate: Double = 1.0
) {

    val graph = List(topology.size) { i ->
        Node(
            index = i,
            position = Random.nextGaussianArray1D(dimension)
        )
    }

    fun learn(input: DoubleArray1D) {

        val closest = graph.minBy { distanceSquared(input, it.position) } ?: error("empty graph")

        val delta = input - closest.position

        for(index in topology.support(closest.index)) {
            graph[index].position += learningRate * topology.weight(closest.index, index) * delta
        }

    }

    data class Node(
        val index: Int,
        var position: DoubleArray1D
    )

}


