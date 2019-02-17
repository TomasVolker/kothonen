package numeriko.som.program

import numeriko.openrndr.Grid2D
import numeriko.openrndr.PanZoom
import numeriko.som.Resources
import numeriko.som.SelfOrganizingMap
import numeriko.som.topology.GaussianTopology
import numeriko.som.topology.Grid2DGaussianTopology
import numeriko.som.topology.Topology
import org.openrndr.*
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Matrix44
import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun main() {

    val map = SelfOrganizingMap(
        topology = Grid2DGaussianTopology(
            width = 10,
            height = 10,
            deviation = 100.0
        ),
        initializer = { Random.nextDoubleArray1D(2) }
    )

    application(
        configuration = configuration {
            windowResizable = true
            width = 800
            height = 600
        },
        program = Som2DProgram(
            map = map,
            topology = map.topology
        )
    )

}

class Som2DProgram(
    val map: SelfOrganizingMap<GaussianTopology>,
    val topology: Topology
): Program() {

    val font by lazy { Resources.fontImageMap("IBMPlexMono-Bold.ttf", 16.0) }

    var point = D[0.0, 0.0]

    override fun setup() {

        backgroundColor = ColorRGBa.BLUE.shade(0.2)

        extend(PanZoom())

        extend(Grid2D())

        extend { update() }

        keyboard.keyDown.listen { onKeyEvent(it) }
        keyboard.keyRepeat.listen { onKeyEvent(it) }

    }

    fun onKeyEvent(event: KeyEvent) {
        when(event.key) {
            KEY_ARROW_UP -> map.topology.deviation *= 1.1
            KEY_ARROW_DOWN -> map.topology.deviation *= 0.9
            KEY_ARROW_RIGHT -> map.learningRate *= 1.1
            KEY_ARROW_LEFT -> map.learningRate *= 0.9
            //KEY_SPACEBAR -> update()
        }
    }

    fun update() {

        val radius = Random.nextDouble(0.0, 200.0)
        val angle = Random.nextDouble(0.0, 2 * PI)

        point = D[100.0 + radius * cos(angle), 100.0 + radius * sin(angle)]

        map.learn(point)

    }

    override fun draw() {

        drawer.run {

            drawDomain()

            drawNodes()

            drawPoint()

            drawParameters()

        }

    }

    private fun Drawer.drawDomain() {
        fill = ColorRGBa.RED.shade(0.6).opacify(0.3)
        circle(x = 100.0, y = 100.0, radius = 200.0)
    }

    private fun Drawer.drawNodes() {
        stroke = ColorRGBa.WHITE
        fill = ColorRGBa.WHITE
        strokeWeight = 1.0

        for (node in map.graph) {

            circle(
                x = node.position[0],
                y = node.position[1],
                radius = 10.0
            )

            drawEdges(node)

        }
    }

    private fun Drawer.drawEdges(node: SelfOrganizingMap.Node) {

        val neighbors = topology.neighbors(node.index).map { i -> map.graph[i] }

        for (neighbor in neighbors) {

            lineSegment(
                x0 = node.position[0],
                y0 = node.position[1],
                x1 = neighbor.position[0],
                y1 = neighbor.position[1]
            )

        }

    }

    private fun Drawer.drawPoint() {
        fill = ColorRGBa.RED
        circle(x = point[0], y = point[1], radius = 5.0)
    }

    private fun Drawer.drawParameters() {
        isolated {

            ortho()
            view = Matrix44.IDENTITY
            model = Matrix44.IDENTITY

            fontMap = font

            text(
                "deviation: ${map.topology.deviation}\nlearningRate: ${map.learningRate}",
                y = 16.0
            )

        }
    }

}