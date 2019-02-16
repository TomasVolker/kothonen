package numeriko.som

import numeriko.som.topology.*
import org.openrndr.*
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.extensions.Debug3D
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import org.openrndr.shape.LineSegment
import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.functions.normalized
import tomasvolker.numeriko.core.functions.times
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextGaussian
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun main() {


    val map = SelfOrganizingMap(
        topology = Grid2DGaussianTopology(
            width = 10,
            height = 10,
            deviation = 10.0
        ),
        dimension = 3
    )

    application(
        configuration = configuration {
            fullscreen = true
        },
        program = Som3DProgram(
            map = map,
            topology = map.topology
        )
    )


}

class Som3DProgram(
    val map: SelfOrganizingMap<GaussianTopology>,
    val topology: Topology
): Program() {

    val font by lazy { Resources.fontImageMap("IBMPlexMono-Bold.ttf", 16.0) }

    var point = D[0.0, 0.0, 0.0]

    override fun setup() {

        backgroundColor = ColorRGBa.BLUE.shade(0.2)

        extend(Debug3D())

        //extend(Grid2D())

        extend { update() }

        keyboard.keyDown.listen { onKeyEvent(it) }

    }

    fun onKeyEvent(event: KeyEvent) {

        if (event.key == KEY_ESCAPE) application.exit()

        when(event.key.toChar()) {
            'W' -> map.topology.deviation *= 1.1
            'S' -> map.topology.deviation *= 0.9
            'D' -> map.learningRate *= 1.1
            'A' -> map.learningRate *= 0.9
        }
    }

    fun update() {

        point = 100.0 * Random.run { D[nextGaussian(), nextGaussian(), nextGaussian()] }.normalized()

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
        circle(x = 0.0, y = 0.0, radius = 1.0)
    }

    private fun Drawer.drawNodes() {
        stroke = ColorRGBa.WHITE
        fill = ColorRGBa.WHITE
        strokeWeight = 1.0

        for (node in map.graph) {

            drawEdges(node)

        }
    }

    fun DoubleArray1D.toVector3() = Vector3(this[0], this[1], this[2])
    fun DoubleArray1D.toVector2() = Vector2(this[0], this[1])

    private fun Drawer.drawEdges(node: SelfOrganizingMap.Node) {

        val neighbors = topology.neighbors(node.index).map { i -> map.graph[i] }

        neighbors.map { neighbor ->
            listOf(
                node.position.toVector3(),
                neighbor.position.toVector3()
            )
        }.let { lineStrips(it) }

    }

    private fun Drawer.drawPoint() {
        fill = ColorRGBa.RED
        circle(point.toVector2(), radius = 0.2)
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