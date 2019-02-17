package numeriko.openrndr

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import tomasvolker.numeriko.core.interfaces.array2d.double.DoubleArray2D
import tomasvolker.numeriko.core.performance.forEach

fun ColorBuffer.write(array: DoubleArray2D) {

    shadow.buffer.rewind()
    forEach(width, height) { x, y ->
        shadow[x, y] = ColorRGBa.WHITE.shade(array[x, y])
    }
    shadow.upload()

}
