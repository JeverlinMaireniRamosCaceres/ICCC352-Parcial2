package servicios;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class GeneradorQr {

    public static String generarQR(String codigo) {

        try {

            int width = 300;
            int height = 300;

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(codigo, BarcodeFormat.QR_CODE, width, height);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }

            String carpeta = "build/resources/main/publico/qr/";
            String nombreArchivo = codigo + ".png";

            File directorio = new File(carpeta);

            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            File archivo = new File(carpeta + nombreArchivo);

            ImageIO.write(image, "png", archivo);

            return "/qr/" + nombreArchivo;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}