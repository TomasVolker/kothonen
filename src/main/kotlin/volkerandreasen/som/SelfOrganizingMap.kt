package volkerandreasen.som


import volkerandreasen.som.topology.GaussianTopology
import volkerandreasen.som.topology.WeightedTopology
import tomasvolker.numeriko.core.functions.times
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D

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

        for(index in topology.support(closest.index)) {
            val node = graph[index]
            val delta =input - node.position
            node.position += learningRate * topology.weight(closest.index, index) * delta
        }

    }

    data class Node(
        val index: Int,
        var position: DoubleArray1D
    )

}

class SOMTraining(
    val som: SelfOrganizingMap<GaussianTopology>,
    val learningRateSequence: Sequence<Double>,
    val deviationSequence: Sequence<Double>,
    val dataSource: Sequence<DoubleArray1D>,
    val maxIterations: Int = Int.MAX_VALUE
) {

    var iteration = 0

    val data = dataSource.iterator()
    val learningRate = learningRateSequence.iterator()
    val deviation = deviationSequence.iterator()

    fun isRunning() =
        iteration < maxIterations && learningRate.hasNext() && deviation.hasNext() && data.hasNext()

    fun step() {

        if (isRunning()) {
            som.learningRate = learningRate.next()
            som.topology.deviation = deviation.next()
            som.learn(data.next())
            iteration++
        }

    }

    fun run() {
        while(isRunning()) {
            step()
        }
    }

}
