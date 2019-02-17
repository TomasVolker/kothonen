package numeriko.som.program

import numeriko.som.*
import numeriko.som.topology.*
import org.openrndr.*
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.toDoubleArray1D
import java.io.File
import kotlin.random.Random



fun fileData(path: String): List<DoubleArray1D> =
    File(path).useLines { lines ->
        lines.drop(3).map {
            it.split(" ").map { it.toDouble() }.take(2).toDoubleArray1D()
        }.toList()
    }

fun <T> List<T>.repeat(n: Int): List<T> = (1..n).flatMap { this }

fun main() {

    val map = SelfOrganizingMap(
        topology = Grid2DGaussianTopology(
            width = 10,
            height = 10
        ),
        initializer = { Random.nextDoubleArray1D(2, 0.0, 1.0) }
    )


    application(
        configuration = configuration {
            windowResizable = true
            width = 800
            height = 800
        },
        program = SomProgram(
            training = SOMTraining(
                som = map,
                learningRateSequence = ExponentialSequence(0.5, 0.1, 1000),
                deviationSequence = ExponentialSequence(3.0, 0.1, 1000),
                dataSource = fileData("data/set_xor.txt").shuffled().repeat(10)
            ),
            topology = map.topology
        )
    )

}
