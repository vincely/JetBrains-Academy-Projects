import kotlin.math.abs

fun main() {
    val game = Game()
    game.startGame()
}

class Board {
    var gameField: MutableList<MutableList<Spot>> = mutableListOf()
    private val size = 8
    var pawnSet = PawnSet()
    val deadPawnSet: MutableList<MutableList<Pawn>> = MutableList(2) { mutableListOf() }
    val xCordsMap = hashMapOf('a' to 0, 'b' to 1, 'c' to 2, 'd' to 3, 'e' to 4, 'f' to 5, 'g' to 6, 'h' to 7)
    val moveList = mutableListOf<Move>()

    init {
        initBoard()
    }

    private fun initBoard() {
        for (y in size downTo 1) {
            var mutList = mutableListOf<Spot>()
            for (x in 'a'..'h') {
                when (y) {
                    7 -> mutList.add(Spot(pawnSet.pawnList.last()[y - 1], x, y))
                    2 -> mutList.add(Spot(pawnSet.pawnList.first()[y - 1], x, y))
                    else -> mutList.add(Spot(pawn = null, x, y))
                }
            }
            gameField.add(mutList)
        }

    }

    fun drawBoard() {
        val s = StringBuilder()
        for ((indexRow, row) in gameField.withIndex()) {
            s.append("  +---+---+---+---+---+---+---+---+").appendLine()
            s.append("${row[0].y} ")
            for ((indexSpot, spot) in row.withIndex()) {
                s.append("| $spot ")
                if (indexSpot == row.lastIndex) s.append("|")
            }
            s.appendLine()
            if (indexRow == row.lastIndex) {
                s.append("  +---+---+---+---+---+---+---+---+").appendLine()
            }
        }
        s.append("  ")
        for ((index, elem) in gameField[0].withIndex()) {
            s.append("  ${elem.x} ")
            if (index == gameField[0].lastIndex) s.append(" ")
        }
        s.appendLine()
        println(s.toString())
    }



    fun movePiece (move: Move): Boolean{
        if (move.start.pawn != null) {
            if (move.player.isWhite != move.start.pawn!!.isWhite) {
                when (move.player.isWhite) {
                    true -> println("No white pawn at ${move.start.x}${move.start.y}")
                    else -> println("No black pawn at ${move.start.x}${move.start.y}")
                }
                return false
            }
        } else {
            when (move.player.isWhite) {
                true -> println("No white pawn at ${move.start.x}${move.start.y}")
                else -> println("No black pawn at ${move.start.x}${move.start.y}")
            }
            return false
        }
        val possibleMoves = getPossibleDestinations(move!!.start)
        if (possibleMoves != null) {
            if (Pair(move.end.x, move.end.y) in possibleMoves) {
                if (move.end.getOccupied()) {
                    addPawnToDeadList(move.end.pawn!!)
                }
                move.end.pawn = move.start.pawn
                move.start.pawn = null
                if (moveList.size > 0) {
                    val lastMove = moveList.last()
                    if (!lastMove.player.isWhite && lastMove.enPassantMe && move.end.y == lastMove.end.y + 1 && move.end.x == lastMove.end.x) {
                        addPawnToDeadList(lastMove.end.pawn!!)
                        getSpot(move.end.x, move.end.y - 1)?.pawn = null
                    }
                    if (lastMove.player.isWhite && lastMove.enPassantMe && move.end.y == lastMove.end.y - 1 && move.end.x == lastMove.end.x) {
                        addPawnToDeadList(lastMove.end.pawn!!)
                        getSpot(move.end.x, move.end.y + 1)?.pawn = null
                    }
                }
                moveList.add(move)

                return true
            }
        }



        println("Invalid Input")
        return false
    }

    fun addPawnToDeadList(pawn: Pawn) {
        if (pawn.isWhite) {
            deadPawnSet[0].add(pawn)
        } else {
            deadPawnSet[1].add(pawn)
        }
    }

    /**
     * returns spot based on user coordinates
     */
    fun getSpot(x: Char, y: Int): Spot? {
        if (x !in 'a'..'h' || y !in 1..8) return null
        val xCord = xCordsMap[x]!!
        val yCord = 8 - y
        return gameField[yCord][xCord]
    }

    fun getPossibleDestinations(startSpot: Spot?): MutableList<Pair<Char, Int>>? {
        val destList: MutableList<Pair<Char, Int>> = mutableListOf()
        if (startSpot?.pawn == null) return null
        val startX = startSpot.x
        val startY = startSpot.y
        val topDiagL = getSpot(startX - 1 , startY + 1)
        val topDiagR = getSpot(startX + 1 , startY + 1)
        val botDiagL = getSpot(startX - 1 , startY - 1)
        val botDiagR = getSpot(startX + 1 , startY - 1)
        val oneDown = getSpot(startX, startY - 1)
        val oneUp = getSpot(startX, startY + 1)
        val twoDown = getSpot(startX, startY - 2)
        val twoUp = getSpot(startX, startY + 2)
        if (startSpot.pawn!!.isWhite) {
            if (topDiagL?.pawn != null && !topDiagL.pawn!!.isWhite) destList.add(Pair(startX - 1, startY + 1))
            if (topDiagR?.pawn != null && !topDiagR.pawn!!.isWhite) destList.add(Pair(startX + 1, startY + 1))
            if (startSpot.y == 2 && twoUp?.pawn == null && oneUp?.pawn == null) {
                destList.add(Pair(startX, startY + 2))
            }
            if (oneUp?.pawn == null) destList.add(Pair(startX, startY + 1))
            if (moveList.size > 0 && moveList.last().start == getSpot(startX - 1, startY + 2) && moveList.last().end == getSpot(startX - 1, startY)) { //doublejump from left top to the left side
                destList.add(Pair(startX - 1, startY + 1))
            }
            if (moveList.size > 0 && moveList.last().start == getSpot(startX + 1, startY + 2) && moveList.last().end == getSpot(startX + 1, startY)) { //doublejump from right top to the right side
                destList.add(Pair(startX + 1, startY + 1))
            }

        } else if (!startSpot.pawn!!.isWhite) {
            if (botDiagL?.pawn != null && botDiagL.pawn!!.isWhite) destList.add(Pair(startX - 1, startY - 1))
            if (botDiagR?.pawn != null && botDiagR.pawn!!.isWhite) destList.add(Pair(startX + 1, startY - 1))
            if (startSpot.y == 7 && twoDown?.pawn == null && oneDown?.pawn == null) {
                destList.add(Pair(startX, startY - 2))
            }
            if (oneDown?.pawn == null) destList.add(Pair(startX, startY - 1))
            if (moveList.size > 0 && moveList.last().start == getSpot(startX - 1, startY - 2) && moveList.last().end == getSpot(startX - 1, startY)) { //doublejump from left bottom to the left side
                destList.add(Pair(startX - 1, startY - 1))
            }
            if (moveList.size > 0 && moveList.last().start == getSpot(startX + 1, startY - 2) && moveList.last().end == getSpot(startX + 1, startY)) { //doublejump from right bottom to the right side
                destList.add(Pair(startX + 1, startY - 1))
            }
        }
        return destList
    }
}

data class Pawn(
    var isWhite: Boolean,
    var isKilled: Boolean = false,

    ) {
    override fun toString(): String {
        return if (isWhite) "W" else "B"
    }
}

class PawnSet {
    val pawnList: MutableList<MutableList<Pawn>> = mutableListOf()
    init {
        for (set in 0..1) {
            val innerList = mutableListOf<Pawn>()
            for (index in 0..7) {
                if (set == 0) {
                    innerList.add(Pawn(isWhite = true))
                } else {
                    innerList.add(Pawn(isWhite = false))
                }
            }
            pawnList.add(innerList)
        }
    }
}

data class Move(val player: Player, val start: Spot, val end: Spot, var enPassantMe: Boolean = false)

data class Spot(
    var pawn: Pawn? = null,
    var x: Char,
    var y: Int,
) {
    val xCordsMap = hashMapOf('a' to 0, 'b' to 1, 'c' to 2, 'd' to 3, 'e' to 4, 'f' to 5, 'g' to 6, 'h' to 7)

    override fun toString(): String {
        return if (pawn != null) pawn.toString() else " "
    }

    fun getOccupied(): Boolean {
        return pawn != null
    }
    fun getXCord(): Int? {
        return xCordsMap[x]
    }

    fun getYCord(): Int {
        return 8 - y
    }

}

class Player(val isWhite: Boolean, var name: String = "No name Set") {

    fun getColorString(): String {
        return if (isWhite) "White" else "Black"
    }
}

class Game {
    val board = Board()
    val player1 = Player(isWhite = true)
    val player2 = Player(isWhite = false)
    var playerTurn = player1
    var gameState = GameState.ACTIVE

    init {
        printTitle()
        configGame()
        board.drawBoard()
    }
    fun configGame() {
        println("First Player's name:")
        player1.name = readln()
        println("Second Player's name:")
        player2.name = readln()
    }
    fun startGame() {
        while (gameState == GameState.ACTIVE) {
            println("${playerTurn.name}'s turn:")
            val move = handleMoveInput() ?: continue
            if (move.let { board.movePiece(it) }) {
                playerTurn = if (playerTurn == player1) player2 else player1
                board.drawBoard()
            }
            // Checking for draw
            if (checkForDraw() == 0) {
                gameState = GameState.DRAW
            }
            // Check if anyone won
            val winner = checkWin(move)
            if (winner != null) {
                if (winner.isWhite) {
                    gameState = GameState.WHITE_WIN
                } else {
                    gameState = GameState.BLACK_WIN
                }
            }
        }
        when (gameState) {
            GameState.WHITE_WIN -> println("White Wins!")
            GameState.BLACK_WIN -> println("Black Wins!")
            GameState.DRAW -> println("Stalemate!")
        }
        exitGame()

    }

    fun checkForDraw(): Int {
        var possibleMoves1 = 0
        var possibleMoves2 = 0
        for (row in board.gameField) {
            for (col in row) {
                if (col.pawn?.isWhite == true) {
                    possibleMoves1 += board.getPossibleDestinations(col)?.size ?: continue
                } else if (col.pawn?.isWhite == false) {
                    possibleMoves2 += board.getPossibleDestinations(col)?.size ?: continue
                }
            }
        }

        return minOf(possibleMoves1, possibleMoves2)
    }

    fun exitGame() {
        println("Bye!")
        gameState = GameState.INACTIVE
    }

    fun checkWin(move: Move): Player? {
        for (list in board.deadPawnSet) {
            if (list.size == 8) {
                return move.player
            }
        }

        for ((index, row) in board.gameField.withIndex()) {
            when(index) {
                1, 2, 3, 4, 5, 6 -> continue
            }
            for (col in row.indices) {
                if (index == 0 && row[col].getOccupied()) {
                    return player1
                } else if(index == 7 && row[col].getOccupied()) {
                    return player2
                }
            }
        }
        return null


    }

    fun handleMoveInput(): Move? {
        val move = readln()
        val regX = """[a-h][1-8][a-h][1-8]""".toRegex()
        when {
            move == "exit" -> {
                println("Bye!")
                gameState = GameState.INACTIVE
                return null
            }
            regX.matches(move) -> {
                return playerMove(playerTurn, move[0], move[1].digitToInt(), move[2], move[3].digitToInt())
            }
            else -> {
                println("Invalid Input")
                return null
            }

        }
    }

    /**
     * Creating a seperate move for a human player,
     * because in case there will be a computer player in the future,
     * the code can be extended easier
     */
    fun playerMove (player: Player, startX: Char, startY: Int, destX: Char, destY: Int ): Move {
        val startSpot: Spot = board.getSpot(startX, startY)!!
        val destSpot: Spot = board.getSpot(destX, destY)!!
        val move = Move(player, startSpot, destSpot)
        if (abs(move.start.y - move.end.y) == 2) move.enPassantMe = true
        return move
    }
    fun printTitle() {
        println("Pawns-Only Chess")
    }


}

enum class GameState {
    ACTIVE, BLACK_WIN, WHITE_WIN, DRAW, INACTIVE
}