package be.acrosoft.gaia.shared.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import be.acrosoft.gaia.shared.util.Debug;
import be.acrosoft.gaia.shared.util.GaiaRuntimeException;

/**
 * AbstractResourcesProvider. This class is responsible for loading resources. As the instance's class loader will be
 * used to load the resource, this class has been declared abstract. This is the implementor's responsibility to put
 * the class in a classloader that is able to fetch the requested data.
 * The resources are of different forms, each of them are loaded from a different location:
 * <ul>
 *   <li>Strings : strings are fetched from a ResourceBundle. The resource bundle name is the implementing class's
 *   package name followed by ".strings.messages".</li>
 *   <li>Images : images are loaded using the implementing class's class loader. The full image path is the implementing class's
 *   package name followed by "/images" and postfixed with an extension that is chosen from within "gif","bmp","jpg","jpeg" in that order.</li>
 * </ul>
 */
public abstract class AbstractResourcesProvider
{
  private ResourceBundle _bundle;
  private ResourceBundle _default;
  
  private Object _lock=new Object();
  private ScriptEngine _engine;
  private Map<String,CompiledScript> _scripts;
  
  private static AbstractResourcesProvider _mine=new AbstractResourcesProvider() {};
  

  /**
   * Create a new AbstractResourcesProvider.
   */
  public AbstractResourcesProvider()
  {
    String cl=getClass().getPackage().getName();
    cl+=".strings.messages"; //$NON-NLS-1$
    try
    {
      _bundle=ResourceBundle.getBundle(cl,Locale.getDefault(),getClass().getClassLoader());
      if(Debug.isDebug())
      {
        _default=ResourceBundle.getBundle(cl,Locale.ENGLISH,getClass().getClassLoader());
      }
    }
    catch(MissingResourceException ex)
    {
      _bundle=null;
    }
  }
  
  private String processScript(String script,Object... objects)
  {
    try
    {
      CompiledScript compiled=null;
      synchronized(_lock)
      {
        if(_engine==null)
        {
          _engine=new ScriptEngineManager(getClass().getClassLoader()).getEngineByName("JavaScript"); //$NON-NLS-1$
        }
        
        if(_engine instanceof Compilable)
        {
          if(_scripts==null)
          {
            _scripts=new HashMap<String,CompiledScript>();
          }
          compiled=_scripts.get(script);
          if(compiled==null && _engine instanceof Compilable)
          {
            compiled=((Compilable)_engine).compile(script);
            _scripts.put(script,compiled);
          }
        }
      }
      
      Bindings bind=_engine.createBindings();
      bind.put("arg",objects); //$NON-NLS-1$
      for(int i=0;i<objects.length;i++)
      {
        bind.put(""+(char)('a'+i),objects[i]); //$NON-NLS-1$
      }
      Object ans;
      
      if(compiled==null)
      {
        ans=_engine.eval(script,bind);
      }
      else
      {
        ans=compiled.eval(bind);
      }
      if(ans==null)
      {
        return ""; //$NON-NLS-1$
      }
      return String.format(ans.toString(),objects);
    }
    catch(ScriptException ex)
    {
      ex.printStackTrace();
      if(Debug.isDebug())
      {
        throw new GaiaRuntimeException(ex);
      }
      return ex.getLocalizedMessage();
    }
  }
  

  /**
   * Get the localized string based on the given key.
   * @param key key.
   * @param objects string parameters.
   * @return localized string.
   * @throws NoSuchResourceException if the key is not found.
   */
  public String getString(String key,Object...objects)
  {
    try
    {
      if(_bundle==null)
        throw new MissingResourceException("Bundle not found",getClass().toString(),key); //$NON-NLS-1$
      
      if(Debug.isDebug())
      {
        _default.getString(key);
      }
      String str=_bundle.getString(key);
      String trim=str.trim();
      if(trim.startsWith("{") && trim.endsWith("}")) //$NON-NLS-1$ //$NON-NLS-2$
      {
        trim=trim.substring(1,trim.length()-1);
        return processScript(trim,objects);
      }
      
      return String.format(_bundle.getString(key),objects);
    }
    catch(MissingResourceException e)
    {
      if(Debug.isDebug())
        throw new NoSuchResourceException(key);

      int pos=key.lastIndexOf('.');
      if(pos<0) return key;
      return key.substring(pos+1);
    }
  }
  
  private InputStream getImage(String path,String extension)
  {
    String fname=path+"."+extension; //$NON-NLS-1$
    return getClass().getClassLoader().getResourceAsStream(fname);
  }
  
  private InputStream getDirectImage(String key)
  {
    String cl=getClass().getPackage().getName();
    cl+=".images"; //$NON-NLS-1$
    cl+="."+key; //$NON-NLS-1$
    cl=cl.replace('.','/');
    
    String[] extensions=new String[] {"png","gif","ico"};   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    
    for(String e:extensions)
    {
      InputStream stream=getImage(cl,e);
      if(stream!=null) return stream;
    }
    
    return null;
  }
  
  /**
   * Get an input stream to the localized image based on the given key.
   * @param key key.
   * @return localized image.
   * @throws NoSuchResourceException if the key is not found.
   */
  public InputStream getImage(String key)
  {
    return getDirectImage(key);
  }
  
  /**
   * Get an input stream to the localized image based on the given key. If
   * an image of that exact size cannot be found, return null.
   * @param key key.
   * @param size requested size.
   * @return localized image, or null if not found.
   */
  public InputStream getImage(String key,int size)
  {
    return getDirectImage(key+"_"+size); //$NON-NLS-1$
  }
  
  /**
   * Get an input stream to the localized image based on the given key. If
   * an image of that exact size cannot be found, the closest smaller
   * matching in size will be returned but only if the resulting
   * image is close to the requested size. If not found,
   * null is returned.
   * @param key key.
   * @param preferredSize preferred size.
   * @return localized image.
   */
  public InputStream findCloseImage(String key,int preferredSize)
  {
    InputStream ans;

    ans=getDirectImage(key+"_"+preferredSize); //$NON-NLS-1$
    if(ans!=null) return ans;
    
    //Try smaller images
    for(int i=ImageSize.values().length-1;i>=0;i--)
    {
      ImageSize s=ImageSize.values()[i];
      if(s.getSize()<preferredSize && s.getSize()*4>preferredSize*3)
      {
        ans=getDirectImage(key+"_"+s.getSize()); //$NON-NLS-1$
        if(ans!=null) return ans;
      }
    }
    
    return null;
  }
  
  /**
   * Get an input stream to the localized image based on the given key. If
   * an image of that exact size cannot be found, the closest smaller
   * matching in size will be returned.
   * @param key key.
   * @param preferredSize preferred size.
   * @return localized image.
   */
  public InputStream findImage(String key,int preferredSize)
  {
    InputStream ans;

    ans=getDirectImage(key+"_"+preferredSize); //$NON-NLS-1$
    if(ans!=null) return ans;
    
    //Try smaller images
    for(int i=ImageSize.values().length-1;i>=0;i--)
    {
      ImageSize s=ImageSize.values()[i];
      if(s.getSize()<preferredSize)
      {
        ans=getDirectImage(key+"_"+s.getSize()); //$NON-NLS-1$
        if(ans!=null) return ans;
      }
    }
    
    //Try unsized image
    ans=getDirectImage(key);
    if(ans!=null) return ans;
    
    if(Debug.isDebug())
      throw new NoSuchResourceException(key);

    if(key.equals("error")) //$NON-NLS-1$
      throw new NoSuchResourceException(key);
    
    return _mine.getDirectImage("error"); //$NON-NLS-1$
  }
  
  /**
   * Get the scalable image (SVG file) from the given images key.
   * @param key image key.
   * @return SVG image, or null if not found.
   */
  public InputStream getScalableImage(String key)
  {
    String cl=getClass().getPackage().getName();
    cl+=".images"; //$NON-NLS-1$
    cl+="."+key; //$NON-NLS-1$
    cl=cl.replace('.','/');
    
    String fnamez=cl+".svgz"; //$NON-NLS-1$
    InputStream zis=getClass().getClassLoader().getResourceAsStream(fnamez);
    if(zis!=null)
    {
      try
      {
        return new GZIPInputStream(zis);
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
    }
    
    String fname=cl+".svg"; //$NON-NLS-1$
    return getClass().getClassLoader().getResourceAsStream(fname);
  }
}
