import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.filter.Filter
import com.sksamuel.scrimage.filter.LensBlurFilter
import com.sksamuel.scrimage.nio.PngWriter
import ij.IJ
import ij.plugin.filter.GaussianBlur
import ij.process.ColorProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.imageio.ImageIO


class ImageProcessing {

    suspend fun backgroundGradient(path: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val file = File(path)
            val image = ImmutableImage.loader().fromFile(file)

            val filter = LensBlurFilter(12f, 2f, 255f, 10)
            val targetWidth = image.width * 2
            val targetHeight = image.height
            image
                .cover(targetWidth, targetHeight, ScaleMethod.FastScale, Position.BottomCenter)
                .filter(filter)
                .overlay(image, (targetWidth - image.width) / 2, (targetHeight - image.height) / 2)

            image.output(PngWriter.NoCompression, File(file.absolutePath.replace(".", "1.")))
        }.join()
    }

    suspend fun trim(path: String, left: Int, top: Int, right: Int, bottom: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            val file = File(path)
            val image = ImmutableImage.loader().fromFile(file)
            val filtered: ImmutableImage = image.trim(left, top, right, bottom)
            filtered.output(PngWriter.NoCompression, File(file.absolutePath.replace(".", "1.")))
        }.join()
    }

    suspend fun flipVertical(path: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val imp = IJ.openImage(path)
            val newImage = imp.processor
            newImage.flipVertical()
            val outputfile = File(path.replace(".", "1."))
            withContext(Dispatchers.IO) {
                ImageIO.write(newImage.bufferedImage, File(path).extension, outputfile)
            }
        }.join()
    }

    suspend fun flipHorizontal(path: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val imp = IJ.openImage(path)
            val newImage = imp.processor
            newImage.flipHorizontal()
            val outputfile = File(path.replace(".", "1."))
            withContext(Dispatchers.IO) {
                ImageIO.write(newImage.bufferedImage, File(path).extension, outputfile)
            }
        }.join()
    }

    suspend fun zoom(path: String, z: Double) {
        CoroutineScope(Dispatchers.Default).launch {
            val file = File(path)
            val image = ImmutableImage.loader().fromFile(file)
            val filtered = image.zoom(z)
            filtered.output(PngWriter.NoCompression, File(file.absolutePath.replace(".", "1.")))
        }.join()
    }

    suspend fun downScale(path: String, x: Double, y: Double) {
        CoroutineScope(Dispatchers.Default).launch {
            val imp = IJ.openImage(path)
            val newImage = ColorProcessor(imp.image).resize((imp.width / x).toInt(), (imp.height / y).toInt())
            val outputfile = File(path.replace(".", "1."))
            withContext(Dispatchers.IO) {
                ImageIO.write(newImage.bufferedImage, File(path).extension, outputfile)
            }
        }.join()
    }

    suspend fun rotateL(path: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val imp = IJ.openImage(path)
            val newImage = ColorProcessor(imp.image).rotateLeft()
            val outputfile = File(path.replace(".", "1."))
            withContext(Dispatchers.IO) {
                ImageIO.write(newImage.bufferedImage, File(path).extension, outputfile)
            }
        }.join()
    }

    suspend fun rotateR(path: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val imp = IJ.openImage(path)
            val newImage = ColorProcessor(imp.image).rotateRight()
            val outputfile = File(path.replace(".", "1."))
            withContext(Dispatchers.IO) {
                ImageIO.write(newImage.bufferedImage, File(path).extension, outputfile)
            }
        }.join()
    }

    suspend fun smooth(path: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val imp = IJ.openImage(path)
            val ip = imp.processor
            GaussianBlur().blurGaussian(ip, 5.0)
            val outputfile = File(path.replace(".", "1."))
            withContext(Dispatchers.IO) {
                ImageIO.write(ip.bufferedImage, "jpg", outputfile)
            }
        }.join()
    }

    suspend fun smooth(path: String, z: Double) {
        CoroutineScope(Dispatchers.Default).launch {
            val imp = IJ.openImage(path)
            val ip = imp.processor
            GaussianBlur().blurGaussian(ip, z)
            val outputfile = File(path.replace(".", "1."))
            withContext(Dispatchers.IO) {
                ImageIO.write(ip.bufferedImage, "jpg", outputfile)
            }
        }.join()
    }

    suspend fun filter(path: String, obj: Filter) {
        CoroutineScope(Dispatchers.Default).launch {
            val file = File(path)
            val image = ImmutableImage.loader().fromFile(file)
            val filtered = image.filter(obj)
            filtered.output(PngWriter.NoCompression, File(file.absolutePath.replace(".", "1.")))
        }.join()
    }
}


