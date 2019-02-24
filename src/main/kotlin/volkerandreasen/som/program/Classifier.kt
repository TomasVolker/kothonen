package volkerandreasen.som.program

import org.openrndr.application
import org.openrndr.draw.colorBuffer
import org.openrndr.extensions.Screenshots
import org.openrndr.ffmpeg.ScreenRecorder
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.doubleZeros
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import tomasvolker.numeriko.core.performance.forEach
import tomasvolker.numeriko.core.primitives.indicator
import volkerandreasen.openrndr.write
import volkerandreasen.som.SOMTraining
import volkerandreasen.som.SelfOrganizingMap
import volkerandreasen.som.data.loadMnist
import volkerandreasen.som.distanceSquared
import volkerandreasen.som.grid.Grid2DGaussianTopology
import volkerandreasen.som.sequence.asShuffledSequence
import volkerandreasen.som.sequence.exponentialSequence
import kotlin.random.Random

fun SelfOrganizingMap<*>.getClosest(position: DoubleArray1D): SelfOrganizingMap.Node =
        graph.minBy { distanceSquared(it.position, position) } ?: error("SOM cannot be empty")

fun main() {

    val data = loadMnist("digit4_16x16_learn.txt")

    application {

        configure {
            width = 800
            height = 800
        }

        program {

            extend(Screenshots())

            val buffer = colorBuffer(16, 16)
            val gridWidth = 10
            val gridHeight = 10

            extend {

                forEach(gridWidth, gridHeight) { x, y ->
                    val image = data.getOrElse(gridWidth * y + x) { doubleZeros(16, 16) to 0 }
                    buffer.write(image.first)
                    val cellWidth = width.toDouble() / gridWidth
                    val cellHeight = height.toDouble() / gridHeight
                    drawer.image(
                        buffer,
                        x = x * cellWidth,
                        y = y * cellHeight,
                        width = cellWidth,
                        height = cellHeight
                    )
                }

            }
        }

    }

    val map = SelfOrganizingMap(
        topology = Grid2DGaussianTopology(
            width = 20,
            height = 20
        ),
        initializer = { Random.nextDoubleArray1D(16 * 16) }
    )

    val training = SOMTraining(
        som = map,
        learningRateSequence = exponentialSequence(0.5, 0.1, 2000),
        deviationSequence = exponentialSequence(10.0, 0.1, 2000),
        dataSource = data.map { it.first.linearView() }.asShuffledSequence(),
        maxIterations = 2000
    )

    training.run()

    val labelMap = map.graph.associateWith { node ->
        data.minBy { distanceSquared(it.first.linearView(), node.position) }?.second
    }

    val test = loadMnist("digit10_16x16_test.txt")

    val accuracy = test.map { labelMap[map.getClosest(it.first.linearView())] == it.second }
        .map { it.indicator() }
        .average()

    println("Accuracy: $accuracy")

}