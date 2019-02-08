package numeriko.som


import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.LineCap
import org.openrndr.math.Vector2
import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.functions.times
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.array1d.generic.indices
import tomasvolker.numeriko.core.interfaces.array2d.double.DoubleArray2D
import tomasvolker.numeriko.core.interfaces.array2d.generic.Array2D
import tomasvolker.numeriko.core.interfaces.array2d.generic.forEachIndex
import tomasvolker.numeriko.core.interfaces.array2d.generic.get
import tomasvolker.numeriko.core.interfaces.factory.*
import tomasvolker.numeriko.core.primitives.squared
import tomasvolker.numeriko.core.primitives.sumDouble
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

fun <T> Array2D<T>.getOrNull(i0: Int, i1: Int): T? =
        if (i0 in 0 until shape0 && i1 in 0 until shape1)
            this[i0, i1]
        else
            null

interface Topology{

    val size: Int

    fun support(nodeIndex: Int): Iterable<Int> = 0 until size

    fun weight(from: Int, to: Int): Double

}


//interface GridTopology2D

class CircleGaussianTopology(
    override val size: Int,
    val deviation: Double
): Topology {

    override fun weight(from: Int, to: Int): Double {
        val angleFrom = 2 * PI * from.toDouble() / size
        val angleTo = 2 * PI * to.toDouble() / size

        val deltaX = cos(angleFrom)- cos(angleTo)
        val deltaY = sin(angleFrom) - sin(angleTo)

        return exp(-(deltaX.squared() + deltaY.squared()) / (2 * deviation))
    }


}







fun build2DMap(
    width: Int,
    height: Int,
    dimension: Int
): SelfOrganizingMap {

    val nodes = array2D(width, height) { i0, i1 ->

        val x = 200.0 * i0.toDouble() / width
        val y = 200.0 * i1.toDouble() / height

        SelfOrganizingMap.Node(
            inputVector = doubleArray1D(dimension) { i ->
                when (i) {
                    0 -> x
                    1 -> y
                    else -> 0.0
                }
            }
        )
    }

    nodes.forEachIndex { i0, i1 ->
        val node = nodes[i0, i1]

        node.addNeighbor(
            nodes.getOrNull(i0-1, i1),
            weight = 0.5
        )

        node.addNeighbor(
            nodes.getOrNull(i0, i1-1),
            weight = 0.5
        )

        node.addNeighbor(
            nodes.getOrNull(i0+1, i1),
            weight = 0.5
        )

        node.addNeighbor(
            nodes.getOrNull(i0, i1+1),
            weight = 0.5
        )

    }

    return SelfOrganizingMap(
        graph = nodes.toList()
    )
}

fun distanceSquared(vector1: DoubleArray1D, vector2: DoubleArray1D) =
        sumDouble(vector1.indices) { i -> (vector1[i] - vector2[i]).squared() }

class SelfOrganizingMap(
    val graph: List<Node>,
    var learningRate: Double = 0.1,
    val topology: Topology
) {

    fun learn(input: DoubleArray1D) {

        val closest = graph.minBy { distanceSquared(input, it.inputVector) } ?: error("empty graph")

        val delta = input - closest.inputVector

        closest.inputVector += learningRate * delta

        closest.neighborList.forEach { (node, weight) ->
            node.inputVector += weight * learningRate * delta
        }

    }


    data class Node(
        var inputVector: DoubleArray1D,
        val neighborList: MutableList<NodeWeight> = mutableListOf()
    ) {

        fun addNeighbor(node: Node?, weight: Double) {
            node?.let { neighborList.add(NodeWeight(it, weight)) }
        }

    }

    data class NodeWeight(
        val node: Node,
        val weight: Double
    )

}

fun main() {

    val map = build2DMap(
        width = 10,
        height = 10,
        dimension = 2
    )

    application {

        configure {
            windowResizable = true
        }

        program {

            extend(PanZoom())

            extend(Grid2D())

            extend {

                repeat(20) {
                    val radius = Random.nextDouble(100.0, 200.0)
                    val angle = Random.nextDouble(0.0, 2 * PI)

                    map.learn(D[100.0 + radius * cos(angle), 100.0 + radius * sin(angle)])
                }

                drawer.stroke = ColorRGBa.WHITE
                drawer.fill = ColorRGBa.WHITE
                drawer.strokeWeight = 1.0
                drawer.lineCap = LineCap.BUTT

                map.graph.forEach { node ->
                    drawer.circle(x = node.inputVector[0], y = node.inputVector[1], radius = 10.0)

                    node.neighborList.forEach { neighbor ->

                        drawer.lineSegment(
                            x0 = node.inputVector[0],
                            y0 = node.inputVector[1],
                            x1 = neighbor.node.inputVector[0],
                            y1 = neighbor.node.inputVector[1]
                        )

                    }

                }

            }

        }

    }


}
