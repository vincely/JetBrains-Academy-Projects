package tictactoe

import kotlin.math.abs


class TicTacToe {

    companion object {
        const val PLAYER_X = "X"
        const val PLAYER_O = "O"
        const val TOP_BOTTOM = "---------"
        const val LEFT = "| "
        const val RIGHT = " |"
        const val GAME_INIT = "_________"
    }

    enum class States(val state: String) {
        NOT_FINISHED("Game not finished"),
        DRAW("Draw"),
        X_WINS("X wins"),
        O_WINS("O wins"),
        IMPOSSIBLE("Impossible"),
    }

    val gameList2D = MutableList(3) { MutableList(3) { " " } }
    var gameOver = false

    fun setGameState(state: String) {

        val stateTriple = state.chunked(3)
        for (row in 0 until gameList2D.size) {
            for (column in 0 until gameList2D.size) {
                gameList2D[row][column] = stateTriple[row][column].toString()
            }
        }
    }

    fun evaluateState() {
        val gameListAsString = gameList2D.flatten().joinToString("")
        val winnerList = checkWin(gameList2D)
        // check if one player has too many tokens (> 1)
        /* if (abs(gameListAsString.count { it == PLAYER_O.first() } - gameListAsString.count { it == PLAYER_X.first() }) > 1) {
         print(States.IMPOSSIBLE.state)
         // check if there is more than 1 winner
         } else if (winnerList[0] + winnerList[1] > 1) {
             print(States.IMPOSSIBLE.state)
         } else if (gameListAsString.contains('_') && (winnerList[0] + winnerList[1] == 0)) {
             print(States.NOT_FINISHED.state)
         } else */
        if (!gameListAsString.contains('_') && (winnerList[0] + winnerList[1] == 0)) {
            print(States.DRAW.state)
            gameOver = true
            return
        } else if (winnerList[0] == 1) {
            print(States.O_WINS.state)
            gameOver = true
            return
        } else if (winnerList[1] == 1){
            print(States.X_WINS.state)
            gameOver = true
            return
        }
    }

    private fun checkWin(gameList: MutableList<MutableList<String>>): List<Int> {
        var winnerO = 0
        var winnerX = 0
        fun countWinners(gameList: MutableList<MutableList<String>>, rowIndex: Int, colIndex: Int) {
            when(gameList[rowIndex][colIndex]) {
                "X" -> winnerX++
                "O" -> winnerO++
            }
        }
        for (i in 0 until gameList.size) {
            if (gameList[i][0] == gameList[i][1] && gameList[i][1] == gameList[i][2]) {
                countWinners(gameList, i, 0)
            }
        }
        for (j in 0 until gameList.size) {
            if (gameList[0][j] == gameList[1][j] && gameList[1][j] == gameList[2][j]) {
                countWinners(gameList, 0, j)
            }
        }

        if (gameList[0][0] == gameList[1][1] && gameList[1][1] == gameList[2][2] || gameList[0][2] == gameList[1][1] && gameList[2][0] == gameList[1][1]) {
            countWinners(gameList, 1, 1)
        }

        return listOf(winnerO, winnerX)
    }

    fun getGameString(): String {
        val sb = StringBuilder()
        sb.append(TOP_BOTTOM).appendLine()
        for (row in gameList2D.indices) {
            sb.append(LEFT).append(gameList2D[row].joinToString(" ")).append(RIGHT).appendLine()
        }
        sb.append(TOP_BOTTOM).appendLine()
        return sb.toString()
    }

    fun move(xHasTurn: Boolean) {
        print("Enter the coordinates: ")
        val coordinates = readln()
        val formatCheck = """\d\s*\d""".toRegex()
        val cordList = mutableListOf<Int>()
        if (formatCheck.matches(coordinates)) {
            cordList.addAll(coordinates.split(" ").map { it.toInt() })
        } else {
            println("You should enter numbers!")
            move(xHasTurn)
            return
        }

        if (cordList.first() !in 1..3 || cordList.last() !in 1..3) {
            println("Coordinates should be from 1 to 3")
            move(xHasTurn)
            return
        } else if (gameList2D[cordList.first() - 1][cordList.last() - 1] != "_") {
            println("This cell is occupied! Choose another one!")
            move(xHasTurn)
            return
        }
        if (xHasTurn) {
            gameList2D[cordList[0] - 1][cordList[1] - 1] = PLAYER_X
        } else {
            gameList2D[cordList[0] - 1][cordList[1] - 1] = PLAYER_O
        }
        print(getGameString())
    }

    fun play() {
        var xHasTurn = true
        while (!gameOver) {
            move(xHasTurn)
            evaluateState()
            xHasTurn = !xHasTurn
        }
    }
}



fun main() {
    // write your code here
    val ticTacToe = TicTacToe()
    // print("Enter cells: ")
    // val input = readln()
    ticTacToe.setGameState(TicTacToe.GAME_INIT)
    print(ticTacToe.getGameString())
    //print(ticTacToe.gameList2D)
    // ticTacToe.evaluateState()
    // ticTacToe.move()
    ticTacToe.play()
}