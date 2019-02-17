package volkerandreasen.som.program

import volkerandreasen.som.*
import volkerandreasen.som.data.fileData
import volkerandreasen.som.grid.Grid2DGaussianTopology
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import kotlin.random.Random

fun somOnSet1() {

    runSom2D(
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

    runSom2D(
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

    runSom2D(
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
