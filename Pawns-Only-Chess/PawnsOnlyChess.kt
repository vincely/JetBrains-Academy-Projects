fun main() {
    /*val pair = Pair('a', 2)
    val list = mutableListOf(Pair('a', 2))
    println("pair is in list: ${pair in list}")*/
    val game = Game()
    game.startGame()
}

class Board {
    var gameField: MutableList<MutableList<Spot>> = mutableListOf()
    private val size = 8
    var pawnSet = PawnSet()
    val deadPawnSet: MutableList<MutableList<Pawn>> = MutableList(2) { mutableListOf() }
    val xCordsMap = hashMapOf('a' to 0, 'b' to 1, 'c' to 2, 'd' to 3, 'e' to 4, 'f' to 5, 'g' to 6, 'h' to 7)

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
        val tempDeadPawnList = mutableListOf<Pawn>()

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
        /*val pair = Pair(move.start.x, move.start.y)
        println("This is the pair: $pair")*/
        val possibleMoves = getPossibleDestinations(move!!.start)
        println("Possible moves are: $possibleMoves") //Zum checken, ob die Vorhersage der Züge funzt
        if (possibleMoves != null) {
            if (Pair(move.end.x, move.end.y) in possibleMoves) {
                if (move.player.isWhite && move.end.pawn != null) {
                    deadPawnSet[0].add(move.end.pawn!!)
                } else if (!move.player.isWhite && move.end.pawn != null){
                    deadPawnSet[1].add(move.end.pawn!!)
                }
                move.end.pawn = move.start.pawn
                move.start.pawn = null
                return true
            }
        }

        println("Invalid Input")
        return false
    }

    // looks good so far
    fun getSpot(x: Char, y: Int): Spot? {
        if (x !in 'a'..'h' || y !in 1..8) return null
        val xCord = xCordsMap[x]!!
        val yCord = 8 - y
        return gameField[yCord][xCord]
    }
    // works
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
        if (startSpot.pawn!!.isWhite) {
            if (topDiagL?.pawn != null && !topDiagL.pawn!!.isWhite) destList.add(Pair(startX - 1, startY + 1))
            if (topDiagR?.pawn != null && !topDiagR.pawn!!.isWhite) destList.add(Pair(startX + 1, startY + 1))
            if (oneUp?.pawn == null) destList.add(Pair(startX, startY + 1))

        } else if (!startSpot.pawn!!.isWhite) {
            if (botDiagL?.pawn != null && botDiagL.pawn!!.isWhite) destList.add(Pair(startX - 1, startY - 1))
            if (botDiagR?.pawn != null && botDiagR.pawn!!.isWhite) destList.add(Pair(startX + 1, startY - 1))
            if (oneDown?.pawn == null) destList.add(Pair(startX, startY - 1))
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
    fun getPawnSet(): MutableList<MutableList<Pawn>> {
        return pawnList
    }
}

data class Move(val player: Player, val start: Spot, val end: Spot)

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

}

class Game {
    var isWhiteTurn = true
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
        }
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
        return Move(player, startSpot, destSpot)
    }
    fun printTitle() {
        println("Pawns-Only Chess")
    }


}

enum class GameState {
    ACTIVE, BLACK_WIN, WHITE_WIN, DRAW, INACTIVE
}