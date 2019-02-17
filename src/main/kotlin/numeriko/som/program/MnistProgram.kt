package numeriko.som.program

import numeriko.openrndr.write
import numeriko.som.Resources
import numeriko.som.SOMTraining
import numeriko.som.SelfOrganizingMap
import numeriko.som.data.loadMnist
import numeriko.som.grid.Grid2DGaussianTopology
import numeriko.som.sequence.asShuffledSequence
import numeriko.som.sequence.exponentialSequence
import org.openrndr.KeyEvent
import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.configuration
import org.openrndr.draw.Drawer
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.isolated
import org.openrndr.extensions.Screenshots
import org.openrndr.math.Matrix44
import tomasvolker.numeriko.core.dsl.I
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import tomasvolker.numeriko.core.performance.forEach
import kotlin.random.Random

fun somOnMnist() = application(
    configuration = configuration {
        width = 800
        height = 800
    },
    program = MnistProgram("digit4_16x16_learn.txt")
)

class MnistProgram(
    path: String
): Program() {

    val data = loadMnist(path)

    val mapWidth = 20
    val mapHeight = 20

    val map = SelfOrganizingMap(
        topology = Grid2DGaussianTopology(
            width = mapWidth,
            height = mapHeight
        ),
        initializer = { Random.nextDoubleArray1D(16 * 16) }
    )

    val training = SOMTraining(
        som = map,
        learningRateSequence = exponentialSequence(0.5, 0.1, 2000),
        deviationSequence = exponentialSequence(10.0, 0.1, 2000),
        dataSource = data.map { it.linearView() }.asShuffledSequence(),
        maxIterations = 2000
    )

    val font by lazy { Resources.fontImageMap("IBMPlexMono-Bold.ttf", 16.0) }

    val buffer by lazy { colorBuffer(16, 16) }

    override fun setup() {

        backgroundColor = ColorRGBa.BLUE.shade(0.2)

        extend(Screenshots())

        extend { update() }

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

        val imageWidth = width.toDouble() / mapWidth
        val imageHeight = height.toDouble() / mapHeight

        forEach(mapWidth, mapHeight) { x, y ->

            val position = graph[map.topology.toLinear(x, y)].position.withShape(I[16, 16]).as2D()

            buffer.write(position)
            image(
                buffer,
                x = imageWidth * x,
                y = imageHeight * y,
                width = imageWidth,
                height = imageHeight
            )

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