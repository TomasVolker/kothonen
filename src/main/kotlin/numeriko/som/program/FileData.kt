package numeriko.som.program

import numeriko.som.*
import numeriko.som.data.discData
import numeriko.som.data.fileData
import numeriko.som.grid.Grid1DGaussianTopology
import numeriko.som.grid.Grid2DGaussianTopology
import numeriko.som.sequence.asShuffledSequence
import numeriko.som.sequence.exponentialSequence
import org.openrndr.*
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import kotlin.random.Random

fun somOnSet1() {

    runSom(
        data = fileData("set1.txt"),
        map = SelfOrganizingMap(
            topology = Grid2DGaussianTopology(
                width = 10,
                height = 10
            ),
            initializer = { Random.nextDoubleArray1D(2, 0.0, 1.0) }
        )
    )

}

fun somOnSet2() {

    runSom(
        data = fileData("set2.txt"),
        map = SelfOrganizingMap(
            topology = Grid2DGaussianTopology(
                width = 10,
                height = 10
            ),
            initializer = { Random.nextDoubleArray1D(2, 0.0, 1.0) }
        )
    )

}

fun somOnXor() {

    runSom(
        data = fileData("set_xor.txt"),
        map = SelfOrganizingMap(
            topology = Grid2DGaussianTopology(
                width = 10,
                height = 10
            ),
            initializer = { Random.nextDoubleArray1D(2, 0.0, 1.0) }
        )
    )

}
