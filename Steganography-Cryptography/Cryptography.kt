import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val iController = ImageController()
    val ui = UI(imageController = iController)
    ui.startMenu()

}

class ImageController {

    private var inputFile = ""
    private var secretMsg = ""
    private var outputFile = ""

    /**
     * reads imageFile and catches io Exceptions.
     * returns a bufferedImage if it can be read.
     * otherwise, it will return null
     */
    private fun readImage(file: File): BufferedImage? {
        return try {
            ImageIO.read(file)
        } catch (e: Exception) {
            println("Can't read input file!")
            null
        }
    }

    /**
     * saves the image file from the buffer in png
     * @param buffImage type BufferedImage
     * @param file
     *
     */
    private fun saveImageInPNG(buffImage: BufferedImage, file: File) {
        ImageIO.write(buffImage, "png", file)
        println("Message saved in ${file.path} image.")
    }

    /** Asks user to enter input name and output name.
     *  Then returns both names in a List<File> in the Format (input, output).
     *  The names are read via readln()
     * */
    private fun getIOFilePaths(): List<File> {
        println("Input image file:")
        inputFile = readln()
        println("Output image file:")
        outputFile = readln()
        return listOf(File(inputFile), File(outputFile))
    }

    /**
     * Asks for User Input to enter message to be hidden
     * function returns String containing the message
     */
    private fun getSecretMessage(): String {
        println("Message to hide:")
        return readln()
    }

    /**
     * changes the last bit of all colors of all pixels of a bufferedImage to 1
     * This method was only needed for Stage 2
     */
    fun changeLeastBit(buffImage: BufferedImage) {
        for (x in 0 until buffImage.width) {
            for (y in 0 until buffImage.height) {
                val color = Color(buffImage.getRGB(x, y)) // reading current rgb values for coordinate x and y
                val r = color.red
                val g = color.green
                val b = color.blue
                val newColor = Color(r or 1, g or 1, b or 1)
                buffImage.setRGB(x, y, newColor.rgb)
            }
        }
    }

    private fun encryptMsg(msg: ByteArray, buffImage: BufferedImage) {
        val anchoredMSG = msg + byteArrayOf(0, 0, 3) // Adding the anchor
        var index = 0
        val flatStrBitArr =
            anchoredMSG.joinToString("") { it.to8BitString() } // This Array holds the msg in 8 bit as a flat String
        loop@ for (y in 0 until buffImage.height) {
            for (x in 0 until buffImage.width) {
                if (index == anchoredMSG.size * 8) break@loop // breaks the loop when all elements from the array are set
                val color = Color(buffImage.getRGB(x, y)) // reading current rgb values for coordinate x and y
                val r = color.red
                val g = color.green
                val b = color.blue
                var bBitStr = b.toUByte().to8BitString()
                bBitStr = bBitStr.substring(0, bBitStr.lastIndex) + flatStrBitArr[index]
                val newColor = Color(r, g, bBitStr.toInt(2))
                buffImage.setRGB(x, y, newColor.rgb)
                index++
            }
        }
    }

    private fun decryptMsg(): String {
        println("Input image file:")
        val input = readln()
        val buffImage = readImage(File(input)) ?: return ""
        val secretMsg = mutableListOf<String>()
        val anchor = mutableListOf("00000000", "00000000", "00000011")
        val queue = MutableList(3) { "" }
        val tempCharList = mutableListOf<Char>()
        loop@ for (y in 0 until buffImage.height) {
            for (x in 0 until buffImage.width) {
                if (anchor.toTypedArray().contentDeepEquals(queue.toTypedArray())) break@loop
                val color = Color(buffImage.getRGB(x, y))
                val hiddenBitInBlue = color.blue and 1
                if (tempCharList.size < 8) {
                    tempCharList.add(hiddenBitInBlue.digitToChar())
                } else {
                    queue.add(tempCharList.joinToString(""))
                    queue.removeAt(0)
                    secretMsg.add(tempCharList.joinToString(""))
                    tempCharList.clear()
                    tempCharList.add(hiddenBitInBlue.digitToChar())
                }
            }
        }
        val password = requestPW()
        val msgArr = secretMsg.run { dropLast(3) }.map { it.toByte(2) }.toByteArray().decodeToString()

        val decryptedMsg = pwdEncryptMsg(msgArr, password)
        return decryptedMsg.decodeToString()

    }

    /**
     * controller Function that covers all the logic for hiding a message in a png.
     * the encrypted message will be output in the set directory
     */
    fun hideMessage() {
        val pathList = getIOFilePaths()
        val buffImage = readImage(pathList.first()) ?: return
        val buffImageConv = BufferedImage(
            buffImage.width,
            buffImage.height,
            BufferedImage.TYPE_INT_RGB
        ) // Converting bufferedImage bImage to correct Type
        buffImageConv.run { createGraphics().drawImage(buffImage, 0, 0, null) }

        secretMsg = getSecretMessage()
        val password = requestPW()
        if (secretMsg.length * 8 > buffImageConv.width * buffImageConv.height) {
            println("The input image is not large enough to hold this message.")
            return
        }
        val msgWithPW = pwdEncryptMsg(secretMsg, password)
        encryptMsg(msgWithPW, buffImageConv)
        saveImageInPNG(buffImageConv, pathList.last())

    }

    /**
     * symmetric key function. encrypts and decrypts a string with a password string.
     * if you use the password string on the result again, you will receive the original string back.
     */
    private fun pwdEncryptMsg(msg: String, pwd: String): ByteArray {

        val secretByteArr = msg.encodeToByteArray()

        var pwdByteArr = pwd.encodeToByteArray()
        while (pwdByteArr.size < secretByteArr.size) {
            pwdByteArr += if (2 * pwdByteArr.size < secretByteArr.size) {
                pwdByteArr
            } else {
                pwdByteArr.copyOfRange(0, secretByteArr.size - pwdByteArr.size)
            }
        }
        val encrWithXOR = mutableListOf<Byte>()
        for (index in secretByteArr.indices) {
            encrWithXOR.add((secretByteArr[index].toInt() xor pwdByteArr[index].toInt()).toByte())
        }

        return encrWithXOR.toByteArray()
    }

    private fun requestPW(): String {
        println("Password:")
        return readln()
    }

    fun showMessage() {
        val decrypted = decryptMsg()
        println("Message:")
        println(decrypted)
    }

    private fun Byte.to8BitString(): String {
        val strRep = this.toString(2)
        return "0".repeat(8 - strRep.length) + strRep
    }

    private fun UByte.to8BitString(): String {
        val strRep = this.toString(2)
        return "0".repeat(8 - strRep.length) + strRep
    }
}


class UI(val imageController: ImageController) {

    fun startMenu() {

        while (true) {
            println("Task (hide, show, exit):")
            when (val input = readln()) {
                "exit" -> {
                    println("Bye!"); return
                }
                "hide" -> imageController.hideMessage()
                "show" -> imageController.showMessage()
                else -> println("Wrong task: $input")
            }
        }
    }

}








