package numeriko.som.program

import numeriko.som.*
import numeriko.som.data.discData
import numeriko.som.grid.Grid2DGaussianTopology
import numeriko.som.sequence.asShuffledSequence
import numeriko.som.sequence.exponentialSequence
import org.openrndr.*
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import kotlin.random.Random

fun main() {

    val map = SelfOrganizingMap(
        topology = Grid2DGaussianTopology(
            width = 10,
            height = 10
        ),
        initializer = { Random.nextDoubleArray1D(2, 0.25, 0.75) }
    )

    val data = discData()

    application(
        configuration = configuration {
            windowResizable = true
            width = 800
            height = 800
        },
        program = SomProgram(
            training = SOMTraining(
                som = map,
                learningRateSequence = exponentialSequence(0.5, 0.1, 1000),
                deviationSequence = exponentialSequence(3.0, 0.1, 1000),
                dataSource = data.asShuffledSequence(),
                maxIterations = 1000
            ),
            topology = map.topology,
            dataset = data
        )
    )

}

