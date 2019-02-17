package numeriko.som.program

import numeriko.openrndr.Grid2D
import numeriko.openrndr.PanZoom
import numeriko.som.*
import numeriko.som.topology.*
import org.openrndr.*
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.DrawQuality
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.extensions.Screenshots
import org.openrndr.math.Matrix44
import org.openrndr.math.transforms.ortho
import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun squareData() = List(1000) { Random.nextDoubleArray1D(2, 0.25, 0.75) }
fun discData() = List(1000) {
    val radius = /*sqrt(Random.nextDouble())*/Random.nextDouble()
    val angle = Random.nextDouble(2 * PI)
    D[radius * cos(angle), radius * sin(angle)]
}

fun main() {

    val map = SelfOrganizingMap(
        topology = Grid2DGaussianTopology(
            width = 10,
            height = 10
        ),
        initializer = { Random.nextDoubleArray1D(2, 0.25, 0.75) }
    )

    application(
        configuration = configuration {
            windowResizable = true
            width = 800
            height = 800
        },
        program = SomProgram(
            training = SOMTraining(
                som = map,
                learningRateSequence = ExponentialSequence(0.5, 0.1, 1000),
                deviationSequence = ExponentialSequence(3.0, 0.1, 1000),
                dataSource = discData()
            ),
            topology = map.topology
        )
    )

}

class SomProgram(
    val training: SOMTraining,
    val topology: Topology
): Program() {

    val font by lazy { Resources.fontImageMap("IBMPlexMono-Bold.ttf", 16.0) }

    override fun setup() {

        backgroundColor = ColorRGBa.WHITE

        extend(Screenshots())

        extend(PanZoom()) {
            camera.view = ortho(
                xMag = 0.001,
                yMag = 0.001,
                zNear = -1.0,
                zFar = 1.0
            )
        }

        extend(Grid2D()) {
            deltaX = 1.0
            deltaY = 1.0
        }

        extend { update() }

        keyboard.keyDown.listen { onKeyEvent(it) }
        keyboard.keyRepeat.listen { onKeyEvent(it) }

    }

    fun onKeyEvent(event: KeyEvent) {

    }

    fun update() {
        training.step()
    }

    override fun draw() {

        drawer.run {

            drawStyle.quality = DrawQuality.PERFORMANCE

            drawDomain()

            drawNodes()

            drawParameters()

        }

    }

    private fun Drawer.drawDomain() {

        (training.dataSource as? List<DoubleArray1D>)?.let { points ->
            stroke = ColorRGBa.GREEN.shade(0.3).opacify(0.4)
            points.forEach {
                circle(x = it[0], y = it[1], radius = 0.01)
            }
        }


    }

    private fun Drawer.drawNodes() {


        stroke = ColorRGBa.BLUE
        training.som.graph.forEach {
            drawEdges(it)
        }

        stroke = ColorRGBa.RED
        fill = ColorRGBa.RED
        training.som.graph.forEach {
            circle(
                x = it.position[0],
                y = it.position[1],
                radius = 0.02
            )
        }
    }

    private fun Drawer.drawEdges(node: SelfOrganizingMap.Node) {

        val neighbors = topology.neighbors(node.index).map { i -> training.som.graph[i] }

        for (neighbor in neighbors) {

            lineSegment(
                x0 = node.position[0],
                y0 = node.position[1],
                x1 = neighbor.position[0],
                y1 = neighbor.position[1]
            )

        }

    }

    private fun Drawer.drawParameters() {
        isolated {

            ortho()
            view = Matrix44.IDENTITY
            model = Matrix44.IDENTITY

            fontMap = font
            fill = ColorRGBa.BLACK
            text(
                "deviation: %g\nlearningRate: %g".format(
                    training.som.topology.deviation,
                    training.som.learningRate
                ),
                y = 16.0
            )

        }
    }

}