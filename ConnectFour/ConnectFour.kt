package connectfour

import java.util.*

// CONSTANTS
const val DEFAULT_ROWS = 6
const val DEFAULT_COLS = 7
const val VERTICAL = "║"
const val L_CORNER = "╚"
const val R_CORNER = "╝"
const val CONNECT = "╩"
const val BOTTOM = "═"
const val PLAYER1TOKEN = "o"
const val PLAYER2TOKEN = "*"
// Phrases
const val INCORRECT_COLUMN = "Incorrect column number"

// Variables
lateinit var board: MutableList<MutableList<String>>
var runningGame = false

//Data Classes
data class Player(val name: String, val token: String)

fun main() {
    printTitle()
    println("First player's name:")
    val player1 = Player(readln(), PLAYER1TOKEN)
    println("Second player's name:")
    val player2 = Player(readln(), PLAYER2TOKEN)
    initBoard()
    println("${player1.name} VS ${player2.name}")
    println("${board.size} X ${board.first().size} board")
    drawBoard(board)
    play(player1, player2)
}

fun checkWin(player: Player): Boolean {
    for (row in board.indices) {
        for (col in board[row].indices) {
            // Check for Vertical Wins
            if (board.getOrNull(row + 3) != null) {
                if (board[row][col] == player.token
                    && board[row + 1][col] == player.token
                    && board[row + 2][col] == player.token
                    && board[row + 3][col] == player.token
                ) return true
            }
            // Check for horizontal wins
            if (board[row].getOrNull(col + 3) != null) {
                if (board[row][col] == player.token
                    && board[row][col + 1] == player.token
                    && board[row][col + 2] == player.token
                    && board[row][col + 3] == player.token
                ) return true
            }
            // Check for diagonal bottom right
            if (board[row].getOrNull(col + 3) != null && board.getOrNull(row + 3) != null) {
                //println("Inside diagonal if")
                if (board[row][col] == player.token
                    && board[row + 1][col + 1] == player.token
                    && board[row + 2][col + 2] == player.token
                    && board[row + 3][col + 3] == player.token
                ) return true
            }
            // Check for diagonal upper right
            if (board[row].getOrNull(col - 3) != null && board.getOrNull(row + 3) != null) {
                //println("Inside diagonal if")
                if (board[row][col] == player.token
                    && board[row + 1][col - 1] == player.token
                    && board[row + 2][col - 2] == player.token
                    && board[row + 3][col - 3] == player.token
                ) return true
            }
        }
    }
    return false
}

fun endGame() {
    runningGame = false
    println("Game over!")
}

fun play(player1: Player, player2: Player) {
    runningGame = true
    val playerList = mutableListOf(player1, player2)
    while (runningGame) {
        println("${playerList.first().name}'s turn:")
        val input = readln()
        when {
            input == "end" -> {
                endGame()
            }
            input.toIntOrNull() == null -> println(INCORRECT_COLUMN)
            input.toInt() !in 1..board.first().size -> println("The column number is out of range (1 - ${board.first().size})")
            else -> {
                if (setToken(input.toInt() - 1, playerList.first())) {
                    if (checkWin(playerList.first())) {
                        println("Player ${playerList.first().name} won")
                        endGame()
                        return
                    }
                    if (board.none { it.contains(" ") }) {
                        println("It is a draw")
                        endGame()
                        return
                    }
                    Collections.swap(playerList, 0, 1)
                } else println("Column $input is full")
            }
        }
    }
}

fun setToken(col: Int, player: Player): Boolean {
    for (i in board.lastIndex downTo 0) {
        if (board[i][col] == " ") {
            board[i][col] = player.token
            drawBoard(board)
            return true
        }
    }
    return false
}

fun printTitle() = println("Connect Four")

fun initBoard() {
    println("Set the board dimensions (Rows x Columns)")
    println("Press Enter for default (6 x 7)")
    val userInput = readln().replace("""\s""".toRegex(), "")
    // checks if default dimension is wanted
    if (userInput == "") {
        board = MutableList(DEFAULT_ROWS) { MutableList(DEFAULT_COLS) {" "} }
        // Otherwise, check the input
    } else if (checkDimensions(userInput)) board =
        MutableList(userInput[0].digitToInt()) { MutableList(userInput[2].digitToInt()) {" "} }
    // If check fails, call initBoard() again
    else initBoard()
}

fun checkDimensions(input: String): Boolean {
    // Regex to check the correct format
    val checkFormat = """\d+[xX]\d+""".toRegex()

    // checking the format and
    if (!checkFormat.matches(input)) {
        println("Invalid input")
        return false
    }
    // regex to split the numbers into array
    val dimensionRegX = """[xX]""".toRegex()
    val dimensions = input.split(dimensionRegX).map { it.toInt() }
    // checking if rows are within range
    if (dimensions.first() !in 5..9) {
        println("Board rows should be from 5 to 9")
        return false
    }
    // checking if columns are within range
    if (dimensions.last() !in 5..9) {
        println("Board columns should be from 5 to 9")
        return false
    }
    // if everything is okay, the fun will return true
    return true
}

fun drawBoard(board : MutableList<MutableList<String>>) {
    val sb = StringBuilder()

    // Column Numbers
    sb.append(" ")
    for (i in 1..board.first().size) {
        sb.append("$i ")
    }
    sb.appendLine()

    // Middle Part of Board
    for (i in board.indices) {
        sb.append(VERTICAL)
        for (j in board[i].indices) {
            sb.append("${board[i][j]}$VERTICAL")
        }
        sb.appendLine()
    }

    // Bottom of Board
    sb.append(L_CORNER)
    for (i in board.first().indices) {
        if (i < board.first().lastIndex) sb.append("$BOTTOM$CONNECT") else sb.append("$BOTTOM$R_CORNER").appendLine()
    }
    println(sb.toString())
}