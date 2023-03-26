import java.io.*
import java.nio.file.*
import java.util.zip.GZIPInputStream
import javax.imageio.*
import java.awt.image.*

val labelPath = Paths.get("train-labels-idx1-ubyte.gz")
val imagePath = Paths.get("train-images-idx3-ubyte.gz")

val labelStream = DataInputStream(GZIPInputStream(Files.newInputStream(labelPath)))
val imageStream = DataInputStream(GZIPInputStream(Files.newInputStream(imagePath)))

labelStream.skip(4)
imageStream.skip(4)

val labelSize = labelStream.readInt()
val imageSize = imageStream.readInt()

val imageHeight = imageStream.readInt()
val imageWidth = imageStream.readInt()

(0..9).forEach { number -> File(number.toString()).mkdir() }
val indexNumber = List(10) { it to 0 }.toMap().toMutableMap()

for (number in 1..imageSize) {
    val label = labelStream.readUnsignedByte()

    val image = (1..imageHeight * imageWidth).map { imageStream.readUnsignedByte() }
        .map { it.toInt() }
        .chunked(28)

    val outputImage: BufferedImage = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_USHORT_GRAY)
    (0 until imageWidth).forEach { width ->
        (0 until imageHeight).forEach { height ->
            outputImage.setRGB(width, height, image[height][width].toRGB())
        }
    }
    ImageIO.write(outputImage, "PNG", File("${label}/${indexNumber[label]?.also { indexNumber[label] = indexNumber[label]!! + 1 }.toString()}.png"))
}

fun Int.toRGB(): Int {
    assert(this < 256)
    return -16777216 + (16 * 16 * 16 * 16 * this) + (16 * 16 * this) + this
}
