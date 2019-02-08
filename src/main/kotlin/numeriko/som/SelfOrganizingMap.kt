package numeriko.som


import org.openrndr.*
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.math.Matrix44
import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.functions.times
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextGaussianArray1D
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class SelfOrganizingMap(
    val topology: WeightedTopology,
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

        topology.support(closest.index).forEach { index ->
            graph[index].position += learningRate * topology.weight(closest.index, index) * delta
        }

    }

    data class Node(
        val index: Int,
        var position: DoubleArray1D
    )

}

fun main() {

    val topology = Grid2DGaussianTopology(
        width = 10,
        height = 10,
        deviation = 100.0
    )

    val map = SelfOrganizingMap(
        topology = topology,
        dimension = 2
    )

    application {

        configure {
            windowResizable = true
        }

        program {

            val font = Resources.fontImageMap("IBMPlexMono-Bold.ttf", 16.0)

            extend(PanZoom())

            extend(Grid2D())

            keyboard.keyDown.listen {
                when(it.key) {
                    KEY_ARROW_UP -> topology.deviation *= 1.1
                    KEY_ARROW_DOWN -> topology.deviation *= 0.9
                    KEY_ARROW_RIGHT -> map.learningRate *= 1.1
                    KEY_ARROW_LEFT -> map.learningRate *= 0.9
                }
            }

            extend {


                val radius = Random.nextDouble(0.0, 200.0)
                val angle = Random.nextDouble(0.0, 2 * PI)

                val point = D[100.0 + radius * cos(angle), 100.0 + radius * sin(angle)]

                map.learn(point)

                drawer.stroke = ColorRGBa.WHITE
                drawer.fill = ColorRGBa.WHITE
                drawer.strokeWeight = 1.0

                map.graph.forEach { node ->
                    drawer.circle(x = node.position[0], y = node.position[1], radius = 10.0)

                    topology.neighbors(node.index).map { map.graph[it] }.forEach { neighbor ->

                        drawer.lineSegment(
                            x0 = node.position[0],
                            y0 = node.position[1],
                            x1 = neighbor.position[0],
                            y1 = neighbor.position[1]
                        )

                    }

                }

                drawer.fill = ColorRGBa.RED
                drawer.circle(x = point[0], y = point[1], radius = 5.0)

                drawer.isolated {

                    ortho()
                    view = Matrix44.IDENTITY
                    model = Matrix44.IDENTITY

                    fontMap = font

                    text(
                        "deviation: ${topology.deviation}\nlearningRate: ${map.learningRate}",
                        y = 16.0
                    )

                }

            }

        }

    }


}
