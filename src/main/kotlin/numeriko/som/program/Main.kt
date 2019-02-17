package numeriko.som.program

fun readNumber(): Int {

    while(true) {

        val number = readLine()?.toIntOrNull()

        if (number != null && 0 < number)
            return number
        else
            println("Invalid question number, try again:")

    }

}

fun main() {

    println("""
Kothonen v1.0
Select question number:""")

    when(readNumber()) {
        1 -> question1()
    }

}