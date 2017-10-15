package be.acrosoft.gaia.shared.rich.readers.rtf;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.acrosoft.gaia.shared.rich.NumberedList;
import be.acrosoft.gaia.shared.rich.ParagraphStyle;
import be.acrosoft.gaia.shared.rich.RGBColor;
import be.acrosoft.gaia.shared.rich.RichDocument;
import be.acrosoft.gaia.shared.rich.TextStyle;

/**
 * Global rtf information.
 */
public class Global
{
  private RTFParser _parser;
  private Map<Integer,RGBColor> _colors;
  private Map<Integer,Font> _fonts;
  private Map<Integer,TextStyle> _styles;
  private Map<Integer,List<ParagraphStyle>> _lists;
  private Map<Integer,Integer> _listOverrides;
  private Map<Integer,Map<Integer,NumberedList>> _numberedLists;
  private Charset _charset;
  private RichDocument _document;
  private int _defaultFont;
  private int _pred;
  
  /**
   * Create a new Global.
   * @param parser parser.
   * @param document document.
   */
  public Global(RTFParser parser,RichDocument document)
  {
    _pred=0;
    _defaultFont=-1;
    _document=document;
    _parser=parser;
    _charset=Charset.defaultCharset();
    _colors=new HashMap<Integer,RGBColor>();
    _fonts=new HashMap<Integer,Font>();
    _styles=new HashMap<Integer,TextStyle>();
    _lists=new HashMap<Integer,List<ParagraphStyle>>();
    _listOverrides=new HashMap<Integer,Integer>();
    _numberedLists=new HashMap<Integer,Map<Integer,NumberedList>>();
  }
  
  /**
   * Get the default font index.
   * @return default font index, or -1 if not defined.
   */
  public int getDefaultFontIndex()
  {
    return _defaultFont;
  }

  /**
   * Set the default font index.
   * @param index default font index.
   */
  public void setDefaultFontIndex(int index)
  {
    _defaultFont=index;
  }
  
  /**
   * Get the document.
   * @return the document.
   */
  public RichDocument getDocument()
  {
    return _document;
  }
  
  /**
   * Get the RTF parser.
   * @return the parser.
   */
  public RTFParser getParser()
  {
    return _parser;
  }

  /**
   * Set the colors.
   * @param colors The colors to set.
   */
  public void setColors(Map<Integer,RGBColor> colors)
  {
    _colors=colors;
  }

  /**
   * Get the colors.
   * @return Returns the colors.
   */
  public Map<Integer,RGBColor> getColors()
  {
    return _colors;
  }

  /**
   * Set the charset.
   * @param charset The charset to set.
   */
  public void setCharset(Charset charset)
  {
    _charset=charset;
  }

  /**
   * Get the charset.
   * @return Returns the charset.
   */
  public Charset getCharset()
  {
    return _charset;
  }

  /**
   * Set the fonts.
   * @param fonts The fonts to set.
   */
  public void setFonts(Map<Integer,Font> fonts)
  {
    _fonts=fonts;
  }

  /**
   * Get the fonts.
   * @return Returns the fonts.
   */
  public Map<Integer,Font> getFonts()
  {
    return _fonts;
  }

  /**
   * Set the styles.
   * @param styles The styles to set.
   */
  public void setStyles(Map<Integer,TextStyle> styles)
  {
    _styles=styles;
  }

  /**
   * Get the styles.
   * @return Returns the styles.
   */
  public Map<Integer,TextStyle> getStyles()
  {
    return _styles;
  }

  /**
   * Set the lists.
   * @param lists The lists to set.
   */
  public void setLists(Map<Integer,List<ParagraphStyle>> lists)
  {
    _lists=lists;
  }

  /**
   * Get the lists.
   * @return Returns the lists.
   */
  public Map<Integer,List<ParagraphStyle>> getLists()
  {
    return _lists;
  }

  /**
   * Set the listOverrides.
   * @param listOverrides The listOverrides to set.
   */
  public void setListOverrides(Map<Integer,Integer> listOverrides)
  {
    _listOverrides=listOverrides;
  }

  /**
   * Get the listOverrides.
   * @return Returns the listOverrides.
   */
  public Map<Integer,Integer> getListOverrides()
  {
    return _listOverrides;
  }

  /**
   * Set the numberedLists.
   * @param numberedLists The numberedLists to set.
   */
  public void setNumberedLists(Map<Integer,Map<Integer,NumberedList>> numberedLists)
  {
    _numberedLists=numberedLists;
  }

  /**
   * Get the numberedLists.
   * @return Returns the numberedLists.
   */
  public Map<Integer,Map<Integer,NumberedList>> getNumberedLists()
  {
    return _numberedLists;
  }
  
  /**
   * Get the numbered list for the given list id and level.
   * @param listId list id.
   * @param level level.
   * @return numbered list.
   */
  public NumberedList getNumberedList(int listId,int level)
  {
    Map<Integer,NumberedList> gl=getNumberedLists().get(listId);
    if(gl==null)
    {
      gl=new HashMap<Integer,NumberedList>();
      getNumberedLists().put(listId,gl);
    }
    
    NumberedList ans=gl.get(level);
    if(ans==null)
    {
      ans=new NumberedList();
      gl.put(level,ans);
    }
    return ans;
  }
  
  /**
   * Get the numbered list for the given list id and level.
   * @param listId list id.
   * @param level level.
   * @return numbered list.
   */
  public NumberedList getNumberedListWithPred(int listId,int level)
  {
    Map<Integer,NumberedList> gl=getNumberedLists().get(listId);
    if(gl==null)
    {
      gl=new HashMap<Integer,NumberedList>();
      getNumberedLists().put(listId,gl);
    }
    
    NumberedList ans=gl.get(level);
        
    if(ans==null || _pred<level)
    {
      ans=new NumberedList();
      gl.put(level,ans);
    }
    _pred=level;
    return ans;
  }
}
