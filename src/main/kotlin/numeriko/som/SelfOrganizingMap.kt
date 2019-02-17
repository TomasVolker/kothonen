package numeriko.som


import numeriko.som.topology.GaussianTopology
import numeriko.som.topology.WeightedTopology
import tomasvolker.numeriko.core.dsl.D
import tomasvolker.numeriko.core.functions.times
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextDoubleArray1D
import tomasvolker.numeriko.core.interfaces.factory.nextGaussianArray1D
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

class SelfOrganizingMap<out T: WeightedTopology>(
    val topology: T,
    var learningRate: Double = 1.0,
    val initializer: ()->DoubleArray1D
) {

    val graph = List(topology.size) { i ->
        Node(
            index = i,
            position = initializer()
        )
    }

    fun learn(input: DoubleArray1D) {

        val closest = graph.minBy { distanceSquared(input, it.position) } ?: error("empty graph")

        val delta = input - closest.position

        for(index in topology.support(closest.index)) {
            graph[index].position += learningRate * topology.weight(closest.index, index) * (input - graph[index].position)
        }

    }

    data class Node(
        val index: Int,
        var position: DoubleArray1D
    )

}

class LinearSequence(
    val first: Double,
    val last: Double,
    val count: Int
): Iterable<Double> {

    val step = (last - first) / (count-1)

    override fun iterator() = object: DoubleIterator() {

        var current = first
        var currentIndex = 0

        override fun hasNext(): Boolean =
            currentIndex < count

        override fun nextDouble(): Double =
                current.also {
                    current += step
                    currentIndex++
                }

    }

}

class ExponentialSequence(
    val first: Double,
    val last: Double,
    val count: Int
): Iterable<Double> {

    val factor = (last / first).pow(1.0 / (count-1))

    override fun iterator() = object: DoubleIterator() {

        var current = first
        var currentIndex = 0

        override fun hasNext(): Boolean =
            currentIndex < count

        override fun nextDouble(): Double =
            current.also {
                current *= factor
                currentIndex++
            }

    }

}

class SOMTraining(
    val som: SelfOrganizingMap<GaussianTopology>,
    val learningRateSequence: Iterable<Double>,
    val deviationSequence: Iterable<Double>,
    val dataSource: Iterable<DoubleArray1D>
) {

    val data = dataSource.iterator()

    val learningRate = learningRateSequence.iterator()
    val deviation = deviationSequence.iterator()

    fun finished() =
        !learningRate.hasNext() || !deviation.hasNext() || !data.hasNext()

    fun step() {

        if (!finished()) {
            som.learningRate = learningRate.next()
            som.topology.deviation = deviation.next()
            som.learn(data.next())
        }

    }

}
