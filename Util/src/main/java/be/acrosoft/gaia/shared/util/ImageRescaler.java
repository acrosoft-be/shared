/**
 * Copyright Acropolis Software SPRL (https://www.acrosoft.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.acrosoft.gaia.shared.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Rescale an image.
 */
public class ImageRescaler
{
  /**
   * Checks if the image represented by file can be rescaled.
   * @param file the input file.
   * @return true if it can be rescaled, false otherwise.
   * @throws IOException
   */
  public static boolean canRescale(File file) throws IOException
  {
    InputStream input = new BufferedInputStream(new FileInputStream(file));
    return ImageRescaler.canRescale(input);
  }

  /**
   * Checks if the image represented by this input stream can be rescaled.<br/>
   * Note that the stream is automatically closed by this method.
   * @param input the input stream.
   * @return true if it can be rescaled, false otherwise.
   * @throws IOException
   */
  public static boolean canRescale(InputStream input) throws IOException
  {
    try(ImageInputStream iis = ImageIO.createImageInputStream(input))
    {
      Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
      return it.hasNext();
    }
    finally
    {
      input.close();
    }
  }
  
  private static BufferedImage imageToBufferedImage(Image im)
  {
    BufferedImage bi = new BufferedImage(im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
    Graphics bg = bi.getGraphics();
    bg.drawImage(im, 0, 0, null);
    bg.dispose();
    return bi;
 }

/**
 * Rescale the image available via this input stream and writes the result to this output stream.
 * The format of the new image is the same as the source image.
 * Aspect ration is preserved.
 * @param input the source image input stream. The stream will be closed after this method returns.
 * @param output the result output stream. The stream will <em>NOT</em> be closed by this method.
 * @param maxX maximum target width.
 * @param maxY maximum target height.
 * @return true if rescaling is successful, false otherwise (i.e. the format was not supported).
 * @throws IOException if an error occurred. In this case, the input stream will also be closed.
 */
  public static boolean rescale(InputStream input, OutputStream output,int maxX,int maxY) throws IOException
  {
    try(ImageInputStream iis = ImageIO.createImageInputStream(input))
    {
      if(iis == null) return false;
      
      Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
      if(!it.hasNext()) return false;
      ImageReader reader = it.next();
      reader.setInput(iis);
      
      String format = reader.getFormatName();
      BufferedImage img = reader.read(0);
      if(img == null) return false;
      
      int scaleX = img.getWidth();
      int scaleY = img.getHeight();
      if(scaleX>maxX)
      {
        scaleY = (int)(((float)scaleY/scaleX)*maxX);
        scaleX = maxX;
      }
      if(scaleY>maxY)
      {
        scaleX = (int)(((float)scaleX/scaleY)*maxY);
        scaleY = maxY;
      }
      Image newImg = img.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
      
      return ImageIO.write(imageToBufferedImage(newImg), format, output);
    }
    finally
    {
      input.close();
    }
  }

}
