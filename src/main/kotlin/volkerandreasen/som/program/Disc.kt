package volkerandreasen.som.program

import volkerandreasen.som.*
import volkerandreasen.som.data.discData
import volkerandreasen.som.grid.Grid1DGaussianTopology
import volkerandreasen.som.grid.Grid2DGaussianTopology
import volkerandreasen.som.sequence.asShuffledSequence
import volkerandreasen.som.sequence.exponentialSequence
import volkerandreasen.som.topology.GaussianTopology
import volkerandreasen.som.topology.Topology
import org.openrndr.*
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import kotlin.random.Random

fun somOnDisc1D() {

    runSom2D(
        data = discData(),
        map = SelfOrganizingMap(
            topology = Grid1DGaussianTopology(
                size = 10
            ),
            initializer = { Random.nextDoubleArray1D(2) }
        )
    )

}

fun somOnDisc2D() {

    runSom2D(
        data = discData(),
        map = SelfOrganizingMap(
            topology = Grid2DGaussianTopology(
                width = 10,
                height = 10
            ),
            initializer = { Random.nextDoubleArray1D(2) }
        )
    )

}

fun runSom2D(
    data: List<DoubleArray1D>,
    map: SelfOrganizingMap<GaussianTopology>
) {

    application(
        configuration = configuration {
            windowResizable = true
            width = 800
            height = 800
        },
        program = SomProgram2D(
            training = SOMTraining(
                som = map,
                learningRateSequence = exponentialSequence(0.5, 0.1, 1000),
                deviationSequence = exponentialSequence(3.0, 0.1, 1000),
                dataSource = data.asShuffledSequence(),
                maxIterations = 1000
            ),
            topology = map.topology as Topology,
            dataset = data
        )
    )

}

