package bot

import java.util.Scanner


val scanner = Scanner(System.`in`) // Do not change this line
val quizMap: Map<String, Boolean>
    get() = mapOf(
        "To repeat a statement multiple times." to false,
        "To decompose a program into several small subroutines." to true,
        "To determine the execution time of a program." to false,
        "To interrupt the execution of a program." to false
        )

fun main() {
    greet("Aid", "2020") // change it as you need
    remindName()
    guessAge()
    count()
    test()
    end()
}

fun greet(assistantName: String, birthYear: String) {
    println("Hello! My name is ${assistantName}.")
    println("I was created in ${birthYear}.")
    println("Please, remind me your name.")
}

fun remindName() {
    val name = scanner.nextLine()
    println("What a great name you have, ${name}!")
}

fun guessAge() {
    println("Let me guess your age.")
    println("Enter remainders of dividing your age by 3, 5 and 7.")
    val rem3 = scanner.nextInt()
    val rem5 = scanner.nextInt()
    val rem7 = scanner.nextInt()
    val age = (rem3 * 70 + rem5 * 21 + rem7 * 15) % 105
    println("Your age is ${age}; that's a good time to start programming!")
}

fun count() {
    println("Now I will prove to you that I can count to any number you want.")
    val num = scanner.nextInt()
    for (i in 0..num) {
        print(i)
        println("!")
    }
}

fun test() {
    println("Let's test your programming knowledge.")
    // write your code here
    println("Why do we use methods?")
    for (i in 1..quizMap.size) {
        println("$i. ${quizMap.keys.toList()[i - 1]}")
    }
    while (true) {
        if (quizMap[quizMap.keys.toList()[readln().toInt() - 1]] == true) {
            return
        } else {
            println("Please, try again.")
        }
    }
}

fun end() {
    println("Congratulations, have a nice day!") // Do not change this text
}
