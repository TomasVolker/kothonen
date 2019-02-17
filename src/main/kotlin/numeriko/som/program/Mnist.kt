package numeriko.som.program

import numeriko.openrndr.Grid2D
import numeriko.openrndr.PanZoom
import numeriko.openrndr.write
import numeriko.som.ExponentialSequence
import numeriko.som.Resources
import numeriko.som.SOMTraining
import numeriko.som.SelfOrganizingMap
import numeriko.som.topology.Grid2DGaussianTopology
import org.openrndr.KeyEvent
import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.draw.Drawer
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.isolated
import org.openrndr.math.Matrix44
import tomasvolker.numeriko.core.dsl.I
import tomasvolker.numeriko.core.interfaces.array2d.double.DoubleArray2D
import tomasvolker.numeriko.core.interfaces.factory.doubleArray2D
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import tomasvolker.numeriko.core.performance.forEach
import java.io.File
import kotlin.random.Random

fun loadMnist(path: String): List<DoubleArray2D> =
    File(path).useLines { lines ->
        lines.drop(3)
            .filter { it.isNotBlank() }
            .map {
                val listOfList = it.split(" ").filter { it.isNotBlank() }.map { it.toDouble() }.chunked(16)
                doubleArray2D(16, 16) { x, y -> listOfList[y][x] }
            }.toList()
    }


fun main() = application(
    configuration = configuration {
        width = 800
        height = 800
    },
    program = MnistProgram()
)


class MnistProgram: Program() {

    val data = loadMnist("data/digit10_16x16_test.txt").repeat(10)

    val mapWidth = 10
    val mapHeight = 10

    val map = SelfOrganizingMap(
        topology = Grid2DGaussianTopology(
            width = mapWidth,
            height = mapHeight
        ),
        initializer = { Random.nextDoubleArray1D(16 * 16) }
    )

    val training = SOMTraining(
        som = map,
        learningRateSequence = ExponentialSequence(0.5, 0.1, 1000),
        deviationSequence = ExponentialSequence(3.0, 0.1, 1000),
        dataSource = data.map { it.linearView() }
    )

    val font by lazy { Resources.fontImageMap("IBMPlexMono-Bold.ttf", 16.0) }

    val buffer by lazy { colorBuffer(16, 16) }

    override fun setup() {

        backgroundColor = ColorRGBa.BLUE.shade(0.2)

        extend(PanZoom())

        extend(Grid2D())

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

            drawImages()

            drawParameters()

        }

    }

    private fun Drawer.drawImages() {

        val graph = map.graph

        forEach(mapWidth, mapHeight) { x, y ->

            val position = graph[map.topology.toLinear(x, y)].position.withShape(I[16, 16]).as2D()

            buffer.write(position)
            image(buffer, x = 16.0 * x, y = 16.0 * y)

        }

    }

    private fun Drawer.drawParameters() {
        isolated {

            ortho()
            view = Matrix44.IDENTITY
            model = Matrix44.IDENTITY

            fontMap = font

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