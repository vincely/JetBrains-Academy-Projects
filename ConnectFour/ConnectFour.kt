package connectfour

lateinit var player1: String
lateinit var player2: String
const val DEFAULT_ROWS = 6
const val DEFAULT_COLS = 7
lateinit var board: MutableList<MutableList<String>>

fun main() {
    printTitle()
    getPlayersName()
    initBoard()
    printGameConfig()
}

fun printTitle() = println("Connect Four")

fun getPlayersName() {
    println("First player's name:")
    player1 = readln()
    println("Second player's name:")
    player2 = readln()
}

fun initBoard() {
    println("Set the board dimensions (Rows x Columns)")
    println("Press Enter for default (6 x 7)")
    val userInput = readln().replace("""\s""".toRegex(), "")
    // checks if default dimension is wanted
    if (userInput == "") {
        board = MutableList(DEFAULT_ROWS) { MutableList(DEFAULT_COLS) {""} }
        // Otherwise, check the input
    } else if (checkDimensions(userInput)) board =
        MutableList(userInput.first().digitToInt()) { MutableList(userInput.last().digitToInt()) {""} }
        // If check fails, call initBoard() again
        else initBoard()
}

fun printGameConfig() {
    println("$player1 VS $player2")
    println("${board.size} X ${board.first().size} board")
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
