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
package be.acrosoft.gaia.shared.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import org.junit.Test;

import be.acrosoft.gaia.shared.util.Debug;

/**
 * AbstractResourcesProviderTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class AbstractResourcesProviderTest
{
  @Test
  public void testStringNotFound()
  {
    Debug.override(true);   
    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    
    Debug.override(false);   
    assertEquals("NotThere",prov.getString("NotThere"));
    assertEquals("NotThere",prov.getString(".NotThere"));
    assertEquals("NotThere",prov.getString("Key.NotThere"));
    assertEquals("NotThere",prov.getString("A.Key.NotThere"));

    Debug.override(true);
    try
    {
      prov.getString("NotThere");
      fail("Unexpected success");
    }
    catch(NoSuchResourceException ex)
    {
      assertEquals("NotThere",ex.getResource());
    }
    
    Debug.override(null);
  }

  @Test
  public void testLocale()
  {
    AbstractResourcesProvider prov;
    
    Locale old=Locale.getDefault();
    
    Locale.setDefault(Locale.ENGLISH);
    prov=new AbstractResourcesProvider() {};
    assertEquals("Value",prov.getString("Category.Key"));
    
    Locale.setDefault(Locale.FRENCH);
    prov=new AbstractResourcesProvider() {};
    assertEquals("Valeur",prov.getString("Category.Key"));

    Locale.setDefault(Locale.ITALIAN);
    prov=new AbstractResourcesProvider() {};
    assertEquals("Valore",prov.getString("Category.Key"));

    Locale.setDefault(old);
  }
  
  @Test
  public void testError()
  {
    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    assertEquals("ReferenceError: \"bof\" is not defined in <eval> at line number 1",prov.getString("Error"));
  }
  
  @Test
  public void testEmpty()
  {
    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    assertEquals("",prov.getString("Empty"));
  }
  
  private boolean compareStreams(InputStream a,InputStream b) throws IOException
  {
    assertTrue(a!=null);
    assertTrue(b!=null);
    
    assert(a!=null);
    assert(b!=null);
    
    try
    {
      while(true)
      {
        int va=a.read();
        int vb=b.read();
        if(va!=vb) return false;
        if(va==-1) break;
      }
      return true;
    }
    finally
    {
      a.close();
      b.close();
    }
  }
  
  @Test
  public void testImageNotFound() throws IOException
  {
    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    
    Debug.override(false);
    InputStream a=prov.findImage("NotThere",16);
    InputStream b=AbstractResourcesProvider.class.getModule().getResourceAsStream("be/acrosoft/gaia/shared/resources/images/error.png");
    assertTrue(compareStreams(a,b));

    Debug.override(true);

    try
    {
      prov.findImage("NotThere",16);
      fail("Unexpected success");
    }
    catch(NoSuchResourceException ex)
    {
      assertEquals("NotThere",ex.getResource());
    }
    
    Debug.override(null);
  }
  
  private String load(InputStream is) throws IOException
  {
    try
    {
      return new BufferedReader(new InputStreamReader(is)).readLine();
    }
    finally
    {
      is.close();
    }
  }
  
  @Test
  public void testGetImage() throws IOException
  {
    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    assertEquals("png",load(prov.getImage("image")));
    assertEquals("16",load(prov.getImage("sized",16)));
  }
  
  @Test
  public void testMultiSizeImage() throws IOException
  {
    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    InputStream a;
    
    a=prov.findImage("sized",ImageSize.SMALL.getSize());
    assertEquals("16",load(a));

    a=prov.findImage("sized",ImageSize.MEDIUM.getSize());
    assertEquals("24",load(a));

    a=prov.findImage("sized",ImageSize.LARGE.getSize());
    assertEquals("32",load(a));
  }
  
  @Test
  public void testMultiSizeImageNoErrorFallback() throws IOException
  {

    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    InputStream a;
    
    a=prov.findCloseImage("sized",ImageSize.SMALL.getSize());
    assertEquals("16",load(a));

    a=prov.findCloseImage("sized",ImageSize.MEDIUM.getSize());
    assertEquals("24",load(a));

    a=prov.findCloseImage("sized",ImageSize.LARGE.getSize());
    assertEquals("32",load(a));
       
    a=prov.findCloseImage("partialsized1",ImageSize.MEDIUM.getSize());
    assertNull(a);

    a=prov.findCloseImage("partialsized2",28);
    assertEquals("24",load(a));

    a=prov.findCloseImage("partialsized2",20);
    assertEquals("16",load(a));

    a=prov.findCloseImage("partialsized1",28);
    assertNull(a);
    
  }
  
  @Test
  public void testUnsizedFallback() throws IOException
  {
    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    assertEquals("png",load(prov.findImage("image",ImageSize.MEDIUM.getSize())));
  }
  
  @Test
  public void testPartialSizedImage1() throws IOException
  {
    Debug.override(true);

    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    InputStream a;
    
    try
    {
      a=prov.findImage("partialsized1",ImageSize.MEDIUM.getSize());
      fail("Unexpected success");
    }
    catch(NoSuchResourceException ex)
    {
      //Expected
    }

    a=prov.findImage("partialsized1",ImageSize.LARGE.getSize());
    assertEquals("32",load(a));

    Debug.override(null);
  }
  
  @Test
  public void testPartialSizedImage2() throws IOException
  {
    Debug.override(true);

    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    InputStream a;
    
    a=prov.findImage("partialsized2",ImageSize.SMALL.getSize());
    assertEquals("16",load(a));
    
    a=prov.findImage("partialsized2",ImageSize.MEDIUM.getSize());
    assertEquals("24",load(a));

    a=prov.findImage("partialsized2",ImageSize.LARGE.getSize());
    assertEquals("24",load(a));

    Debug.override(null);
  }
  
  @Test
  public void testFormatting()
  {
    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    assertEquals("Hello World!",prov.getString("Format","World!"));
  }

  @Test
  public void testScript()
  {
    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    assertEquals("There is 1 potato!",prov.getString("Potato",1));
    assertEquals("There are 2 potatoes!",prov.getString("Potato",2));
  }
  
  @Test
  public void testNesting()
  {
    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    assertEquals("8 nests",prov.getString("Nested","nests"));
  }
  
  @Test
  public void testVectorial() throws Exception
  {
    AbstractResourcesProvider prov=new AbstractResourcesProvider() {};
    
    String a=load(prov.getScalableImage("vectorial"));
    assertEquals("vectorial",a);
    String b=load(prov.getScalableImage("vectorial2"));
    assertEquals("vectorial2",b);
    String c=load(prov.getScalableImage("vectorial3"));
    assertEquals("vectorial3",c);
  }
}
