package volkerandreasen.som.program

import volkerandreasen.som.SelfOrganizingMap
import volkerandreasen.som.data.squareData
import volkerandreasen.som.grid.Grid2DGaussianTopology
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import kotlin.random.Random

fun somOnSquare() {

    runSom2D(
        data = squareData(),
        map = SelfOrganizingMap(
            topology = Grid2DGaussianTopology(
                width = 10,
                height = 10
            ),
            initializer = { Random.nextDoubleArray1D(2, 0.25, 0.75) }
        )
    )

}