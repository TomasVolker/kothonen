package numeriko.som

import org.openrndr.Extension
import org.openrndr.MouseEvent
import org.openrndr.Program
import org.openrndr.draw.Drawer
import org.openrndr.math.Matrix44
import org.openrndr.math.transforms.transform

private class Camera2D {
    var view = Matrix44.IDENTITY
    fun mouseDragged(event: MouseEvent) {
        view *= transform { translate(event.dragDisplacement / view[0].x) }
    }

    fun mouseScrolled(event: MouseEvent) {
        val delta = view.inversed * event.position.vector3(z = 1.0)

        view *= transform {
            translate(delta)
            scale(1.0 + event.rotation.y * 0.01)
            translate(-delta)
        }
    }
}

class PanZoom : Extension {

    override var enabled: Boolean = true

    private val camera = Camera2D()
    override fun setup(program: Program) {
        program.mouse.dragged.listen {
            if (!it.propagationCancelled) {
                camera.mouseDragged(it)
            }
        }

        program.mouse.scrolled.listen {
            if (!it.propagationCancelled) {
                camera.mouseScrolled(it)
            }
        }
    }
    override fun beforeDraw(drawer: Drawer, program: Program) {
        drawer.view = camera.view
    }
}