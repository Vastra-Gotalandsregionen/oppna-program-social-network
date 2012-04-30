package se.vgregion.social.util;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Patrik Bergström
 */
public class ImageUtil {

    public static void writeImageToStream(OutputStream outputStream, int croppedWidth, int croppedHeight,
                                          BufferedImage bufferedImage) throws IOException {
        final float targetProportion = ((float) croppedWidth) / croppedHeight;
        float widthHeightProportion = ((float) bufferedImage.getWidth()) / bufferedImage.getHeight();
        int newWidth;
        int newHeight;
        int shiftRight = 0;
        if (widthHeightProportion > targetProportion) {
            // wider than tall (compared to the targetProportion), fix height and crop width
            newHeight = bufferedImage.getHeight();
            newWidth = (int) (newHeight * targetProportion);
            shiftRight = (int) (((float) bufferedImage.getWidth()) / 2 - ((float) newWidth) / 2);
        } else {
            // taller than wide, fix width and crop height
            newWidth = bufferedImage.getWidth();
            newHeight = (int) (newWidth / targetProportion);
        }

        BufferedImage dimg = new BufferedImage(croppedWidth, croppedHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
        g.drawImage(bufferedImage, 0, 0, croppedWidth, croppedHeight, 0 + shiftRight, 0, newWidth + shiftRight,
                newHeight, null);
        g.dispose();

        JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(outputStream);

        JPEGEncodeParam param = jpegEncoder.getDefaultJPEGEncodeParam(dimg);
        param.setQuality(0.8f, false);
        jpegEncoder.setJPEGEncodeParam(param);

        jpegEncoder.encode(dimg);
    }

}
