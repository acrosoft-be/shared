package be.acrosoft.gaia.shared.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Compress jpeg image.
 */
public class JPegCompressor
{
  private static final int SCALE_Y = 480;
  private static final int SCALE_X = 640;

  /**
   * Checks if the image represented by file can be compressed.
   * @param file the input file.
   * @return true if it can be compressed, false otherwise.
   * @throws IOException
   */
  public static boolean canCompress(File file) throws IOException
  {
    InputStream input = new BufferedInputStream(new FileInputStream(file));
    return JPegCompressor.canCompress(input);
  }

  /**
   * Checks if the image represented by this input stream can be compressed.<br/>
   * Note that the stream is automatically closed by this method.
   * @param input the input stream.
   * @return true if it can be compressed, false otherwise.
   * @throws IOException
   */
  public static boolean canCompress(InputStream input) throws IOException
  {
    try
    {
      ImageInputStream iis = ImageIO.createImageInputStream(input);
      ImageReader reader = null;
      try
      {
        Iterator iter = ImageIO.getImageReaders(iis);
        if (!iter.hasNext())
          return false;
        reader = (ImageReader) iter.next();
        return reader.getFormatName().equals("JPEG"); //$NON-NLS-1$
      }
      finally
      {
        if (reader != null)
        {
          reader.dispose();
        }
        iis.close();
      }
    }
    finally
    {
      input.close();
    }
  }
  
  private static BufferedImage imageToBufferedImage(Image im) {
    BufferedImage bi = new BufferedImage(im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
    Graphics bg = bi.getGraphics();
    bg.drawImage(im, 0, 0, null);
    bg.dispose();
    return bi;
 }

/**
 * Compress this jpg input stream and writes the result to this output stream.
 * @param input the source image input stream.
 * @param output the result output stream.
 * @throws IOException if an error occurred.
 */
  public static void compress(InputStream input, OutputStream output) throws IOException {
    BufferedImage img = ImageIO.read(input);
    
    //reduce image width to max 640*480
    int scaleX = img.getWidth();
    int scaleY = img.getHeight();
    if(scaleX>SCALE_X) {
      scaleY = (int)(((float)scaleY/scaleX)*SCALE_X);
      scaleX = SCALE_X;
    }
    if(scaleY>SCALE_Y) {
      scaleX = (int)(((float)scaleX/scaleY)*SCALE_X);
      scaleY=SCALE_X;
    }
    Image newImg = img.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
    
    //use default JPEG compression settings.
    ImageIO.write(imageToBufferedImage(newImg), "jpeg", output); //$NON-NLS-1$
  }

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception
  {
    compress(new FileInputStream("in.jpg"), new FileOutputStream("scaled.jpg")); //$NON-NLS-1$ //$NON-NLS-2$
  }
}
