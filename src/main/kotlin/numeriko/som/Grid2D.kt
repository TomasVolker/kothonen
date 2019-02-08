package numeriko.som

import numeriko.openrndr.*
import org.openrndr.Extension
import org.openrndr.Program
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import tomasvolker.numeriko.core.primitives.modulo
import kotlin.math.floor

class Grid2D : Extension {

    override var enabled: Boolean = true

    var deltaX: Double = 100.0
    var deltaY: Double = 100.0

    var color = ColorRGBa.GRAY
    var strokeWeight = 1.0

    override fun beforeDraw(drawer: Drawer, program: Program) {

        drawer.stroke = color
        drawer.strokeWeight = strokeWeight

        val worldBounds = drawer.view.inversed * drawer.bounds

        val offsetX = worldBounds.left modulo deltaX
        val countX = floor((worldBounds.width - offsetX) / deltaX).toInt() + 2

        drawer.lineStrips(
            List(countX) { i ->
                val x = worldBounds.left - offsetX + (i+1) * deltaX
                listOf(
                    Vector2(x, worldBounds.top),
                    Vector2(x, worldBounds.bottom)
                )
            }
        )


        val offsetY = worldBounds.top modulo deltaY
        val countY = floor((worldBounds.height - offsetY) / deltaY).toInt() + 2

        drawer.lineStrips(
            List(countY) { i ->
                val y = worldBounds.top - offsetY + (i+1) * deltaY
                listOf(
                    Vector2(worldBounds.left, y),
                    Vector2(worldBounds.right, y)
                )
            }
        )

    }

}