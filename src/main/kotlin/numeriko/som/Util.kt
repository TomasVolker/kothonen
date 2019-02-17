package numeriko.som

import tomasvolker.numeriko.core.interfaces.array1d.double.DoubleArray1D
import tomasvolker.numeriko.core.interfaces.array1d.generic.indices
import tomasvolker.numeriko.core.interfaces.array2d.generic.Array2D
import tomasvolker.numeriko.core.interfaces.array2d.generic.get
import tomasvolker.numeriko.core.primitives.squared
import tomasvolker.numeriko.core.primitives.sumDouble

fun distanceSquared(vector1: DoubleArray1D, vector2: DoubleArray1D) =
    sumDouble(vector1.indices) { i -> (vector1[i] - vector2[i]).squared() }

fun <T> Array2D<T>.getOrNull(i0: Int, i1: Int): T? =
    if (i0 in 0 until shape0 && i1 in 0 until shape1)
        this[i0, i1]
    else
        null
