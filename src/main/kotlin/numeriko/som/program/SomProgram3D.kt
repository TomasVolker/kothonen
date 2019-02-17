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
import org.openrndr.extensions.Debug3D
import org.openrndr.extensions.Screenshots
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import org.openrndr.math.transforms.ortho
import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D

class SomProgram3D(
    val training: SOMTraining,
    val topology: Topology,
    val dataset: Collection<DoubleArray1D> = emptyList()
): Program() {

    val font by lazy { Resources.fontImageMap("IBMPlexMono-Bold.ttf", 16.0) }

    override fun setup() {

        backgroundColor = ColorRGBa.BLACK

        extend(Screenshots())

        extend(ScreenRecorder())

        extend(Debug3D())

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
            lineSegment(point.toVector3(), point.toVector3())
        }

    }

    private fun Drawer.drawNodes() {
        stroke = ColorRGBa.WHITE
        fill = ColorRGBa.WHITE
        strokeWeight = 1.0

        for (node in training.som.graph) {

            drawEdges(node)

        }
    }

    fun DoubleArray1D.toVector3() = Vector3(this[0], this[1], this[2])

    private fun Drawer.drawEdges(node: SelfOrganizingMap.Node) {

        val neighbors = topology.neighbors(node.index).map { i -> training.som.graph[i] }

        neighbors.map { neighbor ->
            listOf(
                node.position.toVector3(),
                neighbor.position.toVector3()
            )
        }.let { lineStrips(it) }

    }

    private fun Drawer.drawParameters() {
        isolated {

            ortho()
            view = Matrix44.IDENTITY
            model = Matrix44.IDENTITY

            fontMap = font
            fill = ColorRGBa.WHITE
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