package numeriko.som.program

import numeriko.som.*
import numeriko.som.data.discData
import numeriko.som.grid.Grid1DGaussianTopology
import numeriko.som.grid.Grid2DGaussianTopology
import numeriko.som.sequence.asShuffledSequence
import numeriko.som.sequence.exponentialSequence
import numeriko.som.topology.GaussianTopology
import numeriko.som.topology.Topology
import org.openrndr.*
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import kotlin.random.Random

fun somOnDisc1D() {

    runSom(
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

    runSom(
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

fun runSom(
    data: List<DoubleArray1D>,
    map: SelfOrganizingMap<GaussianTopology>
) {

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
            topology = map.topology as Topology,
            dataset = data
        )
    )

}

