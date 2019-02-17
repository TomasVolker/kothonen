package numeriko.som.program

import numeriko.som.SelfOrganizingMap
import numeriko.som.data.squareData
import numeriko.som.grid.Grid2DGaussianTopology
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import kotlin.random.Random

fun somOnSquare() {

    runSom(
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