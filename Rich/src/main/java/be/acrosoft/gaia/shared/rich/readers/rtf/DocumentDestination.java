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
package be.acrosoft.gaia.shared.rich.readers.rtf;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import be.acrosoft.gaia.shared.rich.Alignment;
import be.acrosoft.gaia.shared.rich.ImageSection;
import be.acrosoft.gaia.shared.rich.NumberedList;
import be.acrosoft.gaia.shared.rich.Paragraph;
import be.acrosoft.gaia.shared.rich.ParagraphStyle;
import be.acrosoft.gaia.shared.rich.RGBColor;
import be.acrosoft.gaia.shared.rich.Section;
import be.acrosoft.gaia.shared.rich.TableSection;
import be.acrosoft.gaia.shared.rich.TextSection;
import be.acrosoft.gaia.shared.rich.TextStyle;

/**
 * DocumentDestination.
 */
public class DocumentDestination extends StyledTextDestination
{
  private List<Paragraph> _currentParagraphList;
  private Section _currentSection;
  private Paragraph _currentParagraph;
  private NumberedList _bulletList;
  private NumberedList _simpleList;
  
  private TableSection _currentTable;
  private TableSection.Row _currentTableRow;
  private TableSection.Cell _currentTableCell;
  
  private List<Integer> _cellWidths;
  
  @Override
  public void enter(Context context,Global global)
  {
    _currentSection=null;
    _currentParagraph=null;
    _bulletList=new NumberedList();
    _simpleList=new NumberedList();
    _currentParagraphList=global.getDocument().getParagraphs();
    _cellWidths=new ArrayList<>();
  }
  
  private void closeParagraph(Context context,Global global)
  {
    if(_currentParagraph==null) return;
    _currentParagraph.setStyle(context.getParagraphStyle().clone());
    _currentParagraph=null;
  }
  
  private void newParagraph(Context context,Global global)
  {
    closeParagraph(context,global);
    _currentParagraph=new Paragraph();
    _currentParagraph.setStyle(context.getParagraphStyle());
    _currentParagraphList.add(_currentParagraph);
    _currentSection=null;
  }
  
  private void addText(Context context,Global global,String text)
  {
    //Table cells are lazily created
    if(_currentTableRow!=null && _currentTableCell==null) {
      closeParagraph(context,global);
      _currentTableCell=new TableSection.Cell();
      int width=_cellWidths.get(_currentTableRow.getCells().size());
      _currentTableRow.getCells().add(_currentTableCell);
      _currentParagraphList=_currentTableCell.getContent();
      _currentTableCell.setWidth(width/20.0);
    }
    
    if(_currentParagraph==null)
      newParagraph(context,global);
    
    if(_currentSection==null||!(_currentSection instanceof TextSection))
    {
      _currentSection=new TextSection();
      _currentParagraph.getSections().add(_currentSection);
      TextSection ts=(TextSection)_currentSection;
      ts.setStyle(context.getTextStyle().clone());
    }
    
    TextSection ts=(TextSection)_currentSection;
    if(!ts.getStyle().equals(context.getTextStyle()))
    {
      _currentSection=new TextSection();
      _currentParagraph.getSections().add(_currentSection);
      ts=(TextSection)_currentSection;
      ts.setStyle(context.getTextStyle().clone());
    }
    
    int listId=context.getListId();
    if(listId!=0)
    {
      int level=context.getLevel()+1;
      int id=global.getListOverrides().get(listId);
      NumberedList nl=global.getNumberedListWithPred(id,level);
      ParagraphStyle st=global.getLists().get(id).get(level-1);
      context.getParagraphStyle().setBulletStyle(st.getBulletStyle());
      if(st.getBulletStyle()!=ParagraphStyle.BulletStyle.BULLET)
      {
        context.getParagraphStyle().setBulletTextStyle(context.getTextStyle());
        context.getParagraphStyle().setBulletText(st.getBulletText());
      }
      context.getParagraphStyle().setList(nl);
    }
    
    ts.setText(ts.getText()+text);
  }

  @Override
  public void write(String s,Context context,Global global)
  {
    addText(context,global,s);
  }
    
  @Override
  public void control(String code,String parameter,Context context,Global global)
  {
    String param=parameter;
    if(param==null) param="1"; //$NON-NLS-1$

    if(code.equals("par")) //$NON-NLS-1$
      newParagraph(context,global);
    else if(code.equals("ansicpg")) //$NON-NLS-1$
    {
      SortedMap map=Charset.availableCharsets();
      for(Object k:map.keySet())
      {
        String sk=(String)k;
        if(sk.contains(param))
        {
          global.setCharset(Charset.forName(sk));
          global.getParser().setCharset(global.getCharset());
          break;
        }
      }
    }
    else if(code.equals("pard")) //$NON-NLS-1$
    {
      _currentTable=null;
      
      context.setParagraphStyle(new ParagraphStyle());
      TextStyle ts=global.getStyles().get(0);
      if(ts==null)
      {
        Font fnt=global.getFonts().get(global.getDefaultFontIndex());
        if(fnt!=null)
        {
          ts=new TextStyle();
          ts.setFontName(fnt.name);
          ts.setFontFamily(fnt.family);
          context.setTextStyle(ts);
        }
      }
      else
      {
        context.getTextStyle().applyFontFamily(ts);
        context.getTextStyle().applyFontName(ts);
        context.getTextStyle().applyFontSize(ts);
      }
      
      context.setLevel(0);
      context.setListId(0);
    }
    else if(code.equals("plain")) //$NON-NLS-1$
    {
      context.getTextStyle().setBold(false);
      context.getTextStyle().setItalic(false);
      context.getTextStyle().setUnderline(false);
      context.getTextStyle().setForeGround(RGBColor.BLACK);
      context.getTextStyle().setBackGround(RGBColor.WHITE);
    }
    else if(code.equals("qr")) //$NON-NLS-1$
      context.getParagraphStyle().setAlignment(Alignment.RIGHT);
    else if(code.equals("ql")) //$NON-NLS-1$
      context.getParagraphStyle().setAlignment(Alignment.LEFT);
    else if(code.equals("qc")) //$NON-NLS-1$
      context.getParagraphStyle().setAlignment(Alignment.CENTER);
    else if(code.equals("qj")) //$NON-NLS-1$
      context.getParagraphStyle().setAlignment(Alignment.JUSTIFY);
    else if(code.equals("pntext")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("pnlvlblt") || code.equals("jclisttab")) //$NON-NLS-1$ //$NON-NLS-2$
    {
      context.getParagraphStyle().setBulletStyle(ParagraphStyle.BulletStyle.BULLET);
      context.getParagraphStyle().setList(_bulletList);
    }
    else if(code.equals("pnlvlbody")) //$NON-NLS-1$
    {
      context.getParagraphStyle().setList(_simpleList);
    }
    else if(code.equals("pndec")) //$NON-NLS-1$
      context.getParagraphStyle().setBulletStyle(ParagraphStyle.BulletStyle.LIST_NUMBER);
    else if(code.equals("pnucltr")) //$NON-NLS-1$
      context.getParagraphStyle().setBulletStyle(ParagraphStyle.BulletStyle.LIST_UPPER_ALPHABETIC);
    else if(code.equals("pnlcltr")) //$NON-NLS-1$
      context.getParagraphStyle().setBulletStyle(ParagraphStyle.BulletStyle.LIST_LOWER_ALPHABETIC);
    else if(code.equals("pnucrm")) //$NON-NLS-1$
      context.getParagraphStyle().setBulletStyle(ParagraphStyle.BulletStyle.LIST_UPPER_ROMAN);
    else if(code.equals("pnlcrm")) //$NON-NLS-1$
      context.getParagraphStyle().setBulletStyle(ParagraphStyle.BulletStyle.LIST_LOWER_ROMAN);
    else if(code.equals("pntxta")) //$NON-NLS-1$
      context.setDestination(new BulletTextDestination());
    else if(code.equals("pntxtb")) //$NON-NLS-1$
      context.setDestination(new BulletTextDestination());
    else if(code.equals("pnf")) //$NON-NLS-1$
    {
      Font fnt=global.getFonts().get(Integer.parseInt(param));
      context.getParagraphStyle().getBulletTextStyle().setFontFamily(fnt.family);
      context.getParagraphStyle().getBulletTextStyle().setFontName(fnt.name);
    }
    else if(code.equals("pnfs")) //$NON-NLS-1$
    {
      context.getParagraphStyle().getBulletTextStyle().setFontSize(Integer.parseInt(param)/2.0);
    }
    else if(code.equals("fi")) //$NON-NLS-1$
    {
      double fi=Integer.parseInt(param)/20.0;
      context.getParagraphStyle().setFirstLineIndent(fi);
    }
    else if(code.equals("li")) //$NON-NLS-1$
    {
      double li=Integer.parseInt(param)/20.0;
      context.getParagraphStyle().setIndent(li);
    }
    else if(code.equals("s")) //$NON-NLS-1$
    {
      context.setTextStyle(global.getStyles().get(Integer.parseInt(param)).clone());
    }
    else if(code.equals("deff")) //$NON-NLS-1$
      global.setDefaultFontIndex(Integer.parseInt(param));
    else if(code.equals("fonttbl")) //$NON-NLS-1$
      context.setDestination(new FontTableDestination());
    else if(code.equals("colortbl")) //$NON-NLS-1$
      context.setDestination(new ColorTableDestination());
    else if(code.equals("list")) //$NON-NLS-1$
      context.setDestination(new ListDestination());
    else if(code.equals("listtext")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("ilvl")) //$NON-NLS-1$
    {
      if(parameter==null)
        context.setLevel(0);
      else
        context.setLevel(Integer.parseInt(parameter));
      
      if(context.getParagraphStyle().getBulletStyle()==ParagraphStyle.BulletStyle.BULLET
          && context.getParagraphStyle().isBulletTextDefault())
      {
        context.getParagraphStyle().getBulletTextStyle().setFontName("Symbol"); //$NON-NLS-1$
        context.getParagraphStyle().setBulletText("\u00b7"); //$NON-NLS-1$
        if(context.getLevel()==1)
          context.getParagraphStyle().setBulletText("\u006f"); //$NON-NLS-1$
          
      }
      
    }
    else if(code.equals("ls")) //$NON-NLS-1$
    {
      context.setListId(Integer.parseInt(param));
    }
    else if(code.equals("listoverride")) //$NON-NLS-1$
      context.setDestination(new ListOverrideDestination());
    else if(code.equals("author")) //$NON-NLS-1$
      context.setDestination(new AuthorDestination());
    else if(code.equals("title")) //$NON-NLS-1$
      context.setDestination(new TitleDestination());
    else if(code.equals("operator")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("info")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("stylesheet")) //$NON-NLS-1$
      context.setDestination(new StyleSheetDestination());
    else if(code.equals("header")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("headerf")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("headerl")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("headerr")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("footer")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("footerf")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("footerl")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("footerr")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("nonshppict")) //$NON-NLS-1$
      context.setDestination(new IgnoreDestination());
    else if(code.equals("pict")) //$NON-NLS-1$
    {
      _currentSection=new ImageSection();
      _currentParagraph.getSections().add(_currentSection);
      context.setDestination(new PictureDestination((ImageSection)_currentSection));
    }
    else if(code.equals("trowd")) //$NON-NLS-1$
    {
      _cellWidths=new ArrayList<>();
      if(_currentTable==null)
      {
        _currentTable=new TableSection();
        if(_currentParagraph==null) newParagraph(context,global);
        _currentParagraph.getSections().add(_currentTable);
      }
      _currentTableRow=new TableSection.Row();
      _currentTable.getRows().add(_currentTableRow);
      _currentTableCell=null;
    }
    else if(code.equals("row")) //$NON-NLS-1$
    {
      _currentTableRow=null;
      _currentParagraphList=global.getDocument().getParagraphs();
    }
    else if(code.equals("cell")) //$NON-NLS-1$
    {
      _currentTableCell=null;
    }
    else if(code.equals("cellx")) //$NON-NLS-1$
    {
      _cellWidths.add(Integer.parseInt(param));
    }
    else
      super.control(code,parameter,context,global);
  }

  @Override
  public boolean supportsExtended(String code,Context context,Global global)
  {
    if(code.equals("pn")) return true; //$NON-NLS-1$
    if(code.equals("shppict")) return true; //$NON-NLS-1$
    if(code.equals("listtable")) return true; //$NON-NLS-1$
    if(code.equals("listoverridetable")) return true; //$NON-NLS-1$
    return super.supportsExtended(code,context,global);
  }

}
