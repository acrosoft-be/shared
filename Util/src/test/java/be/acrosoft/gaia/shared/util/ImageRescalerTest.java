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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;

@SuppressWarnings({"javadoc","nls"})
public class ImageRescalerTest
{
  private List<String> getPaths(String name)
  {
    String base="be/acrosoft/gaia/shared/util/"+name;
    return Arrays.asList(base+".png",base+".jpg");
  }
  
  @Test
  public void testNoNeedRescale() throws IOException
  {
    for(String img:getPaths("50x50"))
    {
      assertTrue(ImageRescaler.canRescale(getClass().getClassLoader().getResourceAsStream(img)));
      ByteArrayOutputStream bos=new ByteArrayOutputStream();
      assertTrue(ImageRescaler.rescale(getClass().getClassLoader().getResourceAsStream(img),bos,100,80));
      BufferedImage result=ImageIO.read(new ByteArrayInputStream(bos.toByteArray()));
      assertEquals(50,result.getWidth());
      assertEquals(50,result.getHeight());
    }
  }

  @Test
  public void testNeedRescaleX() throws IOException
  {
    for(String img:getPaths("100x50"))
    {
      assertTrue(ImageRescaler.canRescale(getClass().getClassLoader().getResourceAsStream(img)));
      ByteArrayOutputStream bos=new ByteArrayOutputStream();
      assertTrue(ImageRescaler.rescale(getClass().getClassLoader().getResourceAsStream(img),bos,50,60));
      BufferedImage result=ImageIO.read(new ByteArrayInputStream(bos.toByteArray()));
      assertEquals(50,result.getWidth());
      assertEquals(25,result.getHeight());
    }
  }

  @Test
  public void testNeedRescaleY() throws IOException
  {
    for(String img:getPaths("50x100"))
    {
      assertTrue(ImageRescaler.canRescale(getClass().getClassLoader().getResourceAsStream(img)));
      ByteArrayOutputStream bos=new ByteArrayOutputStream();
      assertTrue(ImageRescaler.rescale(getClass().getClassLoader().getResourceAsStream(img),bos,60,50));
      BufferedImage result=ImageIO.read(new ByteArrayInputStream(bos.toByteArray()));
      assertEquals(25,result.getWidth());
      assertEquals(50,result.getHeight());
    }
  }

  @Test
  public void testNeedRescaleBoth() throws IOException
  {
    for(String img:getPaths("50x100"))
    {
      assertTrue(ImageRescaler.canRescale(getClass().getClassLoader().getResourceAsStream(img)));
      ByteArrayOutputStream bos=new ByteArrayOutputStream();
      assertTrue(ImageRescaler.rescale(getClass().getClassLoader().getResourceAsStream(img),bos,20,50));
      BufferedImage result=ImageIO.read(new ByteArrayInputStream(bos.toByteArray()));
      assertEquals(20,result.getWidth());
      assertEquals(40,result.getHeight());
    }
  }

  @Test
  public void testInvalid() throws IOException
  {
    for(String img:getPaths("invalid"))
    {
      assertFalse(ImageRescaler.canRescale(getClass().getClassLoader().getResourceAsStream(img)));
      ByteArrayOutputStream bos=new ByteArrayOutputStream();
      assertFalse(ImageRescaler.rescale(getClass().getClassLoader().getResourceAsStream(img),bos,40,40));
    }
  }
}
