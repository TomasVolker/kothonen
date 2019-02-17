package numeriko.som.program

import numeriko.openrndr.Grid2D
import numeriko.openrndr.PanZoom
import numeriko.som.Resources
import numeriko.som.SOMTraining
import numeriko.som.SelfOrganizingMap
import numeriko.som.topology.Topology
import org.openrndr.KeyEvent
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.DrawQuality
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.extensions.Screenshots
import org.openrndr.math.Matrix44
import org.openrndr.math.transforms.ortho
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D

class SomProgram2D(
    val training: SOMTraining,
    val topology: Topology,
    val dataset: Collection<DoubleArray1D> = emptyList(),
    val nodeRadius: Double = 0.01,
    val dataRadius: Double = 0.005
): Program() {

    val font by lazy { Resources.fontImageMap("IBMPlexMono-Bold.ttf", 16.0) }

    override fun setup() {

        backgroundColor = ColorRGBa.WHITE

        extend(Screenshots())

        extend(PanZoom()) {
            camera.view = ortho(
                xMag = 0.001,
                yMag = 0.001,
                zNear = -1.0,
                zFar = 1.0
            )
        }

        extend(Grid2D()) {
            deltaX = 1.0
            deltaY = 1.0
        }

        extend { update() }

    }

    fun update() {
        training.step()
    }

    override fun draw() {

        drawer.run {

            drawStyle.quality = DrawQuality.PERFORMANCE

            drawDomain()

            drawNodes()

            drawParameters()

        }

    }

    private fun Drawer.drawDomain() {

        stroke = ColorRGBa.GREEN.shade(0.3).opacify(0.4)
        dataset.forEach { point ->
            circle(x = point[0], y = point[1], radius = dataRadius)
        }

    }

    private fun Drawer.drawNodes() {

        stroke = ColorRGBa.BLUE
        training.som.graph.forEach {
            drawEdges(it)
        }

        stroke = ColorRGBa.RED
        fill = ColorRGBa.RED
        training.som.graph.forEach {
            circle(
                x = it.position[0],
                y = it.position[1],
                radius = nodeRadius
            )
        }
    }

    private fun Drawer.drawEdges(node: SelfOrganizingMap.Node) {

        val neighbors = topology.neighbors(node.index).map { i -> training.som.graph[i] }

        for (neighbor in neighbors) {

            lineSegment(
                x0 = node.position[0],
                y0 = node.position[1],
                x1 = neighbor.position[0],
                y1 = neighbor.position[1]
            )

        }

    }

    private fun Drawer.drawParameters() {
        isolated {

            ortho()
            view = Matrix44.IDENTITY
            model = Matrix44.IDENTITY

            fontMap = font
            fill = ColorRGBa.BLACK
            text(
                "deviation: %g\nlearningRate: %g".format(
                    training.som.topology.deviation,
                    training.som.learningRate
                ),
                y = 16.0
            )

        }
    }

}