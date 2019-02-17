package volkerandreasen.som.program

fun readNumber(): Int {

    while(true) {

        val number = readLine()?.toIntOrNull()

        if (number != null && number in 1..8)
            return number
        else
            println("Invalid question number, try again:")

    }

}

fun main() {

    println("""
Kothonen v1.0

Select demo:
1 - 2D map on square
2 - 2D map on disc
3 - 1D map on disc
4 - 2D map on set 1
5 - 2D map on set 2
6 - 2D map on xor
7 - 2D map on MNIST
8 - 2D map on 3D Sphere
""")

    when(readNumber()) {
        1 -> somOnSquare()
        2 -> somOnDisc2D()
        3 -> somOnDisc1D()
        4 -> somOnSet1()
        5 -> somOnSet2()
        6 -> somOnXor()
        7 -> somOnMnist()
        8 -> somOnSphere()
    }

}