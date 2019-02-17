package numeriko.som.data

import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.array2d.double.DoubleArray2D
import tomasvolker.numeriko.core.interfaces.factory.doubleArray2D
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.toDoubleArray1D
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun squareData() = List(1000) { Random.nextDoubleArray1D(2, 0.25, 0.75) }

fun discData() = List(1000) {
    val radius = /*sqrt(Random.nextDouble())*/Random.nextDouble()
    val angle = Random.nextDouble(2 * PI)
    D[radius * cos(angle), radius * sin(angle)]
}


fun fileData(path: String): List<DoubleArray1D> =
    File(path).useLines { lines ->
        lines.drop(3).map {
            it.split(" ").map { it.toDouble() }.take(2).toDoubleArray1D()
        }.toList()
    }

fun loadMnist(path: String): List<DoubleArray2D> =
    File(path).useLines { lines ->
        lines.drop(3)
            .filter { it.isNotBlank() }
            .map {
                val listOfList = it.split(" ").filter { it.isNotBlank() }.map { it.toDouble() }.chunked(16)
                doubleArray2D(16, 16) { x, y -> listOfList[y][x] }
            }.toList()
    }