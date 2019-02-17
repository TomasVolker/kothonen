package numeriko.som.program

import numeriko.som.SOMTraining
import numeriko.som.SelfOrganizingMap
import numeriko.som.data.discData
import numeriko.som.grid.Grid2DGaussianTopology
import numeriko.som.sequence.asShuffledSequence
import numeriko.som.sequence.exponentialSequence
import numeriko.som.topology.GaussianTopology
import numeriko.som.topology.Topology
import org.openrndr.application
import org.openrndr.configuration
import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.functions.normalized
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextGaussian
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun sphereData() = List(1000) {
    Random.run { D[nextGaussian(), nextGaussian(), nextGaussian()] }.normalized()
}

fun somOnSphere() {

    runSom3D(
        data = sphereData(),
        map = SelfOrganizingMap(
            topology = Grid2DGaussianTopology(
                width = 20,
                height = 20
            ),
            initializer = { Random.nextDoubleArray1D(3) }
        )
    )

}

fun runSom3D(
    data: List<DoubleArray1D>,
    map: SelfOrganizingMap<GaussianTopology>
) {

    application(
        configuration = configuration {
            windowResizable = true
            width = 1000
            height = 800
        },
        program = SomProgram3D(
            training = SOMTraining(
                som = map,
                learningRateSequence = exponentialSequence(0.5, 0.1, 2000),
                deviationSequence = exponentialSequence(10.0, 0.1, 2000),
                dataSource = data.asShuffledSequence(),
                maxIterations = 2000
            ),
            topology = map.topology as Topology,
            dataset = data
        )
    )

}