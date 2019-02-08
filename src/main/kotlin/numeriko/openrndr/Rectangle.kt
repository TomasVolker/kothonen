package numeriko.openrndr

import org.openrndr.math.Matrix44
import org.openrndr.shape.Rectangle

operator fun Matrix44.times(rectangle: Rectangle) =
        Rectangle(
            corner = (this * rectangle.corner.vector3().toHomogeneous()).xy,
            width = this.c0r0 * rectangle.width,
            height = this.c1r1 * rectangle.height
        )
