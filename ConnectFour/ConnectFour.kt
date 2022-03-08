package connectfour

import java.util.*

fun main() {
    ConnectFour.printTitle()
    // get all the configurations for the game
    // name of players, board dimensions and number of rounds to play
    val config = ConnectFour.getGameConfig()
    val player1 = ConnectFour.Player(config["player1"]!!, ConnectFour.PLAYER1TOKEN)
    val player2 = ConnectFour.Player(config["player2"]!!, ConnectFour.PLAYER2TOKEN)

    val dimensions: List<Int> = config["dimensions"]!!.split(" ").map { it.toInt() }
    val rounds = config["rounds"]!!.toInt()
    val game = ConnectFour.Game(dimensions)
    println("${player1.name} VS ${player2.name}")
    println("${game.board.size} X ${game.board.first().size} board")
    if (rounds == 1) println("Single game") else println("Total $rounds games")
    game.play(player1, player2, rounds)
}

class ConnectFour {
    companion object {
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

        // Functions
        fun printTitle() = println("Connect Four")
        fun getGameConfig(): Map<String, String> {
            // Asking for players name and create player objects
            println("First player's name:")
            val player1 = readln()
            println("Second player's name:")
            val player2 = readln()
            // Asking for Dimensions and initialize board object
            val dimensions = getDimensions()
            val rounds = getRounds()
            return mapOf("player1" to player1,
                "player2" to player2,
                "dimensions" to dimensions,
                "rounds" to rounds
            )
        }

        fun getDimensions() : String{
            println("Set the board dimensions (Rows x Columns)")
            println("Press Enter for default (6 x 7)")
            val userInput = readln().replace("""\s""".toRegex(), "")
            // checks if default dimension is wanted
            return if (userInput == "") {
                "$DEFAULT_ROWS $DEFAULT_COLS"
                // Otherwise, check the input
            } else if (checkDimensions(userInput)) "${userInput[0]} ${userInput[2]}"
            // If check fails, call getDimensions() again
            else getDimensions()
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

        fun getRounds(): String {
            // Asking for single or multi rounds
            println("Do you want to play single or multiple games?\n" +
                    "For a single game, input 1 or press Enter\n" +
                    "Input a number of games:")
            val input = readln()
            return when {
                input == "" -> { "1" }
                input.toIntOrNull() != null && input.toInt() > 0 -> input
                else -> { println("Invalid input"); getRounds() }
            }
        }

    }

    // Data Classes
    data class Player(val name: String, val token: String, var score: Int = 0, var started: Boolean = false)

    // Holds gamelogic
    class Game(val dimensions: List<Int>) {

        val board = MutableList(dimensions[0]) { MutableList(dimensions[1]) {" "} }
        var runningGame = false

        fun clearBoard(board: MutableList<MutableList<String>>) {
            for (row in board) {
                for (col in row.indices) {
                    row[col] = " "
                }
            }
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
        fun play(player1: Player, player2: Player, rounds: Int) {
            runningGame = true
            val playerList = mutableListOf(player1, player2)
            playerList[0].started = true
            var currentRound = 1

            for (round in 1..rounds) {

                if (rounds > 1) println("Game #$currentRound")

                drawBoard(board)
                while (runningGame) {
                    println("${playerList.first().name}'s turn:")
                    val input = readln()
                    when {
                        input == "end" -> {
                            endGame()
                            return
                        }
                        input.toIntOrNull() == null -> println(INCORRECT_COLUMN)
                        input.toInt() !in 1..board.first().size -> println("The column number is out of range (1 - ${board.first().size})")
                        else -> {
                            if (setToken(input.toInt() - 1, playerList.first())) {
                                if (checkWin(playerList.first())) {
                                    println("Player ${playerList.first().name} won")
                                    playerList.first().score += 2
                                    if (rounds > 1) printScore(player1, player2)
                                    if (currentRound == rounds) {
                                        endGame()
                                        return
                                    } else runningGame = false
                                }
                                if (board.none { it.contains(" ") }) {
                                    println("It is a draw")
                                    playerList.first().score += 1
                                    playerList.last().score += 1
                                    if (rounds > 1) printScore(player1, player2)
                                    if (currentRound == rounds) {
                                        endGame()
                                        return
                                    } else runningGame = false
                                }
                                Collections.swap(playerList, 0, 1)
                            } else println("Column $input is full")
                        }
                    }
                }
                currentRound++
                runningGame = true
                clearBoard(board)
                // Making sure that they're alternating starting
                if (playerList.first().started) {
                    Collections.swap(playerList, 0, 1)
                }
                playerList.first().started = true
                playerList.last().started = false
            }
        }
        fun printScore(player1: Player, player2: Player) {
            println("Score")
            println("${player1.name}: ${player1.score} ${player2.name}: ${player2.score}")
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
                if (i < board.first().lastIndex) sb.append("$BOTTOM$CONNECT") else sb.append("$BOTTOM$R_CORNER")
            }
            println(sb.toString())
        }

    }

}







