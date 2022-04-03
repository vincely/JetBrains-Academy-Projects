package parking

fun main() {
    val parkMan = ParkManager()
    parkMan.start()
}

class ParkManager(var lotSize: Int = 0) {

    val lots = mutableMapOf<Int, Lot>()
    init {
        for (index in 1..lotSize) {
            lots[index] = Lot()
        }
    }
    var lotState = SystemStatus.STOPPED
    enum class SystemStatus{
        RUNNING,
        STOPPED
    }

    fun createLots() {
        val mutMap = mutableMapOf<Int, Lot>()
        for (index in 1..lotSize) {
            mutMap[index] = Lot()
        }
        lots.clear()
        lots.putAll(mutMap)
        println("Created a parking lot with $lotSize spots.")
    }

    fun start() {
        lotState = SystemStatus.RUNNING
        while (lotState == SystemStatus.RUNNING) {
            val custOrder = reception()
            val action = extractAction(custOrder)
            when (action) {
                "create" -> {
                    val size = handleCreateInput(custOrder)
                    if (size < 0) continue
                    setParkingLotSize(size)
                    createLots()
                }
                "park" -> {
                    if (lotSize == 0) {
                        println("Sorry, a parking lot has not been created.")
                        continue
                    }
                    val parkArgs = handleParkInput(custOrder)
                    if (parkArgs.size == 1) continue
                    if (!parkCar(parkArgs[0], parkArgs[1])) continue
                }
                "leave" -> {
                    if (lotSize == 0) {
                        println("Sorry, a parking lot has not been created.")
                        continue
                    }
                    val leaveArg = handleLeaveInput(custOrder)
                    if (leaveArg == 0) continue
                    retrieveCar(leaveArg)
                }
                "status" -> printStatus()
                "exit" -> {
                    lotState = SystemStatus.STOPPED
                    continue
                }
                "invalid", "empty" -> {
                    println("Invalid argument")
                }

            }

        }
    }

    fun setParkingLotSize(size: Int) {
        if (size > 0)  this.lotSize = size else println("Parking lot has to be bigger than 0")
    }

    fun reception(): MutableList<String> {
        val input = readln()
        val rex = """(.+\s+.+)||(exit)||(status)""".toRegex()
        val order = mutableListOf<String>()
        if (input.matches(rex)) {
            order.addAll(input.split(" "))
        }
        return order
    }

    fun extractAction(order: MutableList<String>): String {
        if (order.isEmpty()) return "empty"
        return when (order[0]) {
            "park", "leave", "exit", "create", "status" -> order[0]
            else -> "invalid"
        }
    }

    fun printStatus() {
        if (lotSize < 1) {
            println("Sorry, a parking lot has not been created.")
            return
        }
        if (lots.all { !it.value.getStatus() }) {
            println("Parking lot is empty.")
            return
            }

        for (lot in lots) {
            if (lot.value.getStatus()) {
                val car = lot.value.getCar()!!
                println("${lot.key} ${car.id} ${car.color}")
            }
        }

    }

    fun parkCar(id: String, color: String): Boolean {
        var emptyLot: Lot? = null
        var lotID = 0
        for (key in 1..lots.size) {
            if (!lots[key]!!.getStatus()) {
                emptyLot = lots[key]
                lotID = key
                break
            }
        }
        return if (emptyLot == null) {
            println("Sorry, the parking lot is full.")
            false
        } else {
            emptyLot.parkCar(Car(id, color))
            println("$color car parked in spot $lotID.")
            true
        }
    }

    fun handleParkInput(input: MutableList<String>): MutableList<String> {
        if (input.size < 3) return mutableListOf("too little Arguments")
        if (input.size > 3) return mutableListOf("too many Arguments")
        return mutableListOf(input[1], input[2])
    }

    /**
     * return the freed lot-ID if it was occupied
     * returns 0 if there wasn't any car in the spot
     */
    fun retrieveCar(lotID: Int) {
        val lot = lots[lotID]
        if (lot != null && lot.getStatus()) {
            lot.freeLot()
            println("Spot $lotID is free.")
        } else {
            println("There is no car in spot $lotID.")
        }
    }


    /**
     * Handling the input for retrieving the car.
     * It will return 0 if the input is either invalid or the spot number does not exist
     * Otherwise it will return the Spot
     */
    fun handleLeaveInput(input: MutableList<String>): Int {
        if (input.size != 2) {
            println("Invalid arguments")
            return 0
        }
        val spot = input[1].toIntOrNull() ?: 0
        if (spot > lotSize) {
            println("There is no such spot.")
        }
        return spot
    }

    fun handleCreateInput(input: MutableList<String>): Int {
        if (input.size != 2) {
            println("Invalid arguments")
            return -1
        }
        val size = input[1].toIntOrNull() ?: 0
        if (size > 0) {
            return size
        }
        return 0
    }

}

data class Car(private val _id: String, private val _color: String) {
    val id: String = _id.replace("\\s".toRegex(), "")
    val color: String = _color
}

class Lot {
    private var car: Car? = null
    private var occupied: Boolean = false

    fun parkCar(car: Car) {
        this.car = car
        occupied = true
    }

    fun freeLot() {
        this.car = null
        occupied = false
    }

    fun getStatus(): Boolean {
        return occupied
    }

    fun getCar(): Car?{
        return car
    }

}


