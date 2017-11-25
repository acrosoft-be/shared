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
package be.acrosoft.gaia.shared.rich.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.FieldsDocumentPart;
import org.apache.poi.hwpf.usermodel.BorderCode;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Field;
import org.apache.poi.hwpf.usermodel.HWPFList;
import org.apache.poi.hwpf.usermodel.OfficeDrawing;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableRow;

import be.acrosoft.gaia.shared.rich.Alignment;
import be.acrosoft.gaia.shared.rich.BorderStyle;
import be.acrosoft.gaia.shared.rich.BorderStyle.LineStyle;
import be.acrosoft.gaia.shared.rich.DocumentReader;
import be.acrosoft.gaia.shared.rich.ImageSection;
import be.acrosoft.gaia.shared.rich.NumberedList;
import be.acrosoft.gaia.shared.rich.Paragraph;
import be.acrosoft.gaia.shared.rich.ParagraphStyle;
import be.acrosoft.gaia.shared.rich.ParagraphStyle.BulletStyle;
import be.acrosoft.gaia.shared.rich.RGBColor;
import be.acrosoft.gaia.shared.rich.RichDocument;
import be.acrosoft.gaia.shared.rich.TableSection;
import be.acrosoft.gaia.shared.rich.TextSection;
import be.acrosoft.gaia.shared.rich.VerticalAlignment;

/**
 * DOCReader.
 */
public class DOCReader implements DocumentReader<InputStream>
{
  private BorderStyle convertBorderStyle(BorderCode brc)
  {
    BorderStyle ans=new BorderStyle();
    if(brc==null) return ans;
    switch(brc.getBorderType())
    {
      case 0:
        ans.setLineStyle(LineStyle.NONE);
        break;
      default:
        ans.setLineStyle(LineStyle.SOLID);
        break;
    }
    if(brc.getLineWidth()==255)
    {
      ans.setLineStyle(LineStyle.NONE);
    }
    else if(brc.getLineWidth()!=0)
    {
      ans.setLineWidth(brc.getLineWidth()/8.0);
    }
    ans.setMargin(brc.getSpace()/20.0);
    return ans;
  }
  
  private void removeLastEmptyLine(List<Paragraph> content)
  {
    if(content.size()>0)
    {
      Paragraph lastPara=content.get(content.size()-1);
      if(lastPara.getSections().size()==1 && lastPara.getSections().get(0) instanceof TextSection)
      {
        TextSection lastSection=(TextSection)lastPara.getSections().get(0);
        if(lastSection.getText().length()==0)
        {
          content.remove(content.size()-1);
        }
      }
    }
  }
  
  private void scanRecord(EscherRecord record)
  {
    if(record.isContainerRecord())
    {
      for(EscherRecord child:((EscherContainerRecord)record).getChildRecords())
      {
        scanRecord(child);
      }
    }
    else
    {
      if(record instanceof EscherTextboxRecord)
      {
        //EscherTextboxRecord text=(EscherTextboxRecord)record;
        //System.out.println(Arrays.toString(text.getData()));
      }
    }
  }
  
  private int parseCharacterRun(int runCount,HWPFDocument doc,Range parentRange,CharacterRun run,Paragraph richParagraph,String preamble,boolean inField)
  {
    runCount++;
    
    //Picture
    Picture picture=doc.getPicturesTable().extractPicture(run,true);
    if(picture!=null)
    {
      ImageSection imageSection=new ImageSection();
      imageSection.setHeight(picture.getHeight()*picture.getVerticalScalingFactor()/1000,ImageSection.Unit.PIXEL);
      imageSection.setWidth(picture.getWidth()*picture.getHorizontalScalingFactor()/1000,ImageSection.Unit.PIXEL);
      imageSection.setData(picture.getContent());
      richParagraph.getSections().add(imageSection);
      if(runCount<parentRange.numCharacterRuns())
      {
        String nextText=parentRange.getCharacterRun(runCount).text();
        while(nextText.endsWith("\n") || nextText.endsWith("\r") || nextText.endsWith("\u0007")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        {
          nextText=nextText.substring(0,nextText.length()-1);
        }
        if(nextText.length()==0)
        {
          runCount++;
        }
      }
      return runCount;
    }
    //Page jump
    if(run.text().length()==1 && run.text().charAt(0)==12)
    {
      return runCount;
    }
        
    if(run.isSpecialCharacter() && run.text().length()==1)
    {
      char code=run.text().charAt(0);
      // System.out.println((int)code);
      if(code==8)
      {
        //Rendered drawings
        OfficeDrawing draw=doc.getOfficeDrawingsMain().getOfficeDrawingAt(run.getStartOffset());
        if(draw!=null)
        {
          ImageSection imageSection=new ImageSection();
          imageSection.setHeight((draw.getRectangleBottom()-draw.getRectangleTop())/20.0,ImageSection.Unit.POINT);
          imageSection.setWidth((draw.getRectangleRight()-draw.getRectangleLeft())/20.0,ImageSection.Unit.POINT);
          imageSection.setLeftOffset(draw.getRectangleLeft()/20.0);
          imageSection.setTopOffset(draw.getRectangleTop()/20.0);
          imageSection.setData(draw.getPictureData());
          imageSection.setFloating(!inField);
          richParagraph.getSections().add(imageSection);
          EscherContainerRecord root=draw.getOfficeArtSpContainer();
          if(root!=null)
          {
            scanRecord(root);
          }
        }
      }
      else if(code==19)
      {
        Field field=doc.getFields().getFieldByStartOffset(FieldsDocumentPart.MAIN,run.getStartOffset());
        if(field!=null)
        {
          Range subRange=field.secondSubrange(run);
          for(int i=0;i<subRange.numCharacterRuns();)
          {
            CharacterRun subRun=subRange.getCharacterRun(i);
            i=parseCharacterRun(i,doc,subRange,subRun,richParagraph,"",true); //$NON-NLS-1$
          }
          while(parentRange.getCharacterRun(runCount).getEndOffset()<field.getFieldEndOffset()) runCount++;
          runCount++;
        }
        else
        {
           //??
        }
      }
      else if(code==20)
      {
      }
      else if(code==21)
      {
      }
      return runCount;
    }
              
    TextSection richSection=new TextSection();
    String text=run.text();
    
    text=preamble+text;
    while(text.endsWith("\n") || text.endsWith("\r") || text.endsWith("\u0007")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    {
      text=text.substring(0,text.length()-1);
    }
    richSection.setText(text);
    richParagraph.getSections().add(richSection);
    richSection.getStyle().setBold(run.isBold());
    richSection.getStyle().setItalic(run.isItalic());
    richSection.getStyle().setUnderline(run.getUnderlineCode()!=0);
    
    String fontName=run.getFontName();
    int fontSize=run.getFontSize();
    richSection.getStyle().setFontName(fontName);
    richSection.getStyle().setFontSize(fontSize/2);
              
    int color=run.getIco24();
    if(color!=-1)
    {
      int r=color&255;
      int g=(color>>8)&255;
      int b=(color>>16)&255;
      richSection.getStyle().setForeGround(new RGBColor(r/255.0,g/255.0,b/255.0));
    }
    
    return runCount;

  }
  
  private void parseSectionRange(HWPFDocument doc,Range section,List<Paragraph> target,Nesting nesting,Map<Integer,NumberedList> richListMap,int tableLevel)
  {
    int paraCount=section.numParagraphs();
    int previousSpacingAfter=0;

    for(int i=0;i<paraCount;i++)
    {
      Paragraph richParagraph=new Paragraph();
           
      org.apache.poi.hwpf.usermodel.Paragraph poiParagraph=section.getParagraph(i);

      int indent=0;
      if(poiParagraph.getJustification()==0)
      {
        richParagraph.getStyle().setAlignment(Alignment.LEFT);
        indent=poiParagraph.getIndentFromLeft();
      }
      if(poiParagraph.getJustification()==1)
      {
        richParagraph.getStyle().setAlignment(Alignment.CENTER);
      }
      else if(poiParagraph.getJustification()==2)
      {
        richParagraph.getStyle().setAlignment(Alignment.RIGHT);
        indent=poiParagraph.getIndentFromRight();
      }
      else if(poiParagraph.getJustification()==3)
      {
        richParagraph.getStyle().setAlignment(Alignment.JUSTIFY);
        indent=poiParagraph.getIndentFromLeft();
      }
      richParagraph.getStyle().setIndent(indent/20.0);
      richParagraph.getStyle().setFirstLineIndent((poiParagraph.getFirstLineIndent())/20.0);
            
      int spacingDelta=poiParagraph.getSpacingBefore()-previousSpacingAfter;
      if(spacingDelta<0) spacingDelta=0;
      
      previousSpacingAfter=poiParagraph.getSpacingAfter();
      
      richParagraph.getStyle().setSpacingBefore(spacingDelta/20.0);
      richParagraph.getStyle().setSpacingAfter(poiParagraph.getSpacingAfter()/20.0);
      
      String preamble=""; //$NON-NLS-1$
      if(poiParagraph.isInList())
      {
        HWPFList list=poiParagraph.getList();

        String postFix;
        switch(list.getTypeOfCharFollowingTheNumber((char)poiParagraph.getIlvl()))
        {
          case 0:
            postFix="\t"; //$NON-NLS-1$
            break;
          case 1:
            postFix=" "; //$NON-NLS-1$
            break;
          default:
            postFix=""; //$NON-NLS-1$
            break;
        }

        String rawPreamble=list.getNumberText((char)poiParagraph.getIlvl());
        ArrayList<Integer> nest=null;
        if(poiParagraph.getLvl()!=9)
        {
          nest=nesting.next(poiParagraph.getIlvl());
          
          StringBuilder bld=new StringBuilder();
          for(int pos=0;pos<rawPreamble.length();pos++)
          {
            char c=rawPreamble.charAt(pos);
            if(c<nest.size())
            {
              bld.append(Integer.toString(nest.get(c)));
            }
            else
            {
              bld.append(c);
            }
          }
          bld.append(postFix);
          preamble=bld.toString();
        }
        else
        {
          preamble=""; //$NON-NLS-1$            
          int format = list.getNumberFormat((char)poiParagraph.getIlvl());
          if(format == 0x17)
          {
            richParagraph.getStyle().setBulletStyle(BulletStyle.BULLET);
            char c=list.getNumberText((char)poiParagraph.getIlvl()).charAt(0);
            if(c==61623)
            {
              richParagraph.getStyle().getBulletTextStyle().setFontName("Symbol"); //$NON-NLS-1$
              richParagraph.getStyle().setBulletText(c+postFix);
            }
            else if(c>=61000)
            {
              richParagraph.getStyle().getBulletTextStyle().setFontName("Wingdings"); //$NON-NLS-1$
              richParagraph.getStyle().setBulletText(c+postFix);
            }
            else
            {
              richParagraph.getStyle().setBulletText(c+postFix);
            }
          }
          else if(format == 0x00)
          {
            int lsid=list.getLsid();
            NumberedList numberedList=richListMap.get(lsid);
            if(numberedList==null)
            {
              numberedList=new NumberedList();
              numberedList.setInitialValue(list.getStartAt((char)poiParagraph.getIlvl()));
              richListMap.put(lsid,numberedList);
            }
            richParagraph.getStyle().setList(numberedList);
            richParagraph.getStyle().setBulletText(list.getListData().getLevel(1).getNumberText().substring(1)+postFix);
            richParagraph.getStyle().setBulletStyle(BulletStyle.LIST_NUMBER);
          }
          

        }
      }
      
      if(poiParagraph.isInTable() && poiParagraph.getTableLevel()>tableLevel)
      {
        Table t=section.getTable(poiParagraph);
        TableSection ts=new TableSection();
        richParagraph.getSections().add(ts);
        
        //Reset paragraph style
        richParagraph.setStyle(new ParagraphStyle());
        
        for(int r=0;r<t.numRows();r++)
        {
          TableRow row=t.getRow(r);
          TableSection.Row richRow=new TableSection.Row();
          ts.getRows().add(richRow);
          richRow.setHeight(row.getRowHeight()/20.0);
          for(int c=0;c<row.numCells();c++)
          {
            TableCell cell=row.getCell(c);
            int spanCount=1;
            if(cell.isVerticallyMerged())
            {
              if(cell.isFirstVerticallyMerged())
              {
                int leftEdge=cell.getLeftEdge();
                for(int scanRows=r+1;scanRows<t.numRows();scanRows++)
                {
                  boolean found=false;
                  for(int scanCells=0;scanCells<t.getRow(scanRows).numCells();scanCells++)
                  {
                    if(t.getRow(scanRows).getCell(scanCells).getLeftEdge()==leftEdge && t.getRow(scanRows).getCell(scanCells).isVerticallyMerged())
                    {
                      found=true;
                      spanCount++;
                      break;
                    }
                  }
                  if(!found) break;
                }
              }
              else
              {
                spanCount=0;
              }
            }
            TableSection.Cell richCell=new TableSection.Cell();
            richRow.getCells().add(richCell);
            richCell.setWidth(cell.getWidth()/20.0);
            richCell.setSpanY(spanCount);
            
            richCell.setLeftBorder(convertBorderStyle(c==0?row.getLeftBorder():row.getVerticalBorder()));
            richCell.setRightBorder(convertBorderStyle(c==row.numCells()-1?row.getRightBorder():row.getVerticalBorder()));
            richCell.setTopBorder(convertBorderStyle(r==0?row.getTopBorder():row.getHorizontalBorder()));
            richCell.setBottomBorder(convertBorderStyle(r==t.numRows()-1?row.getBottomBorder():row.getHorizontalBorder()));
            
            if(cell.getBrcLeft().getBorderType()!=0) richCell.setLeftBorder(convertBorderStyle(cell.getBrcLeft()));
            if(cell.getBrcRight().getBorderType()!=0) richCell.setRightBorder(convertBorderStyle(cell.getBrcRight()));
            if(cell.getBrcTop().getBorderType()!=0) richCell.setTopBorder(convertBorderStyle(cell.getBrcTop()));
            if(cell.getBrcBottom().getBorderType()!=0) richCell.setBottomBorder(convertBorderStyle(cell.getBrcBottom()));
            
            richCell.getLeftBorder().setMargin(richCell.getLeftBorder().getMargin()+row.getGapHalf()/10.0);
            richCell.getRightBorder().setMargin(richCell.getRightBorder().getMargin()+row.getGapHalf()/10.0);
            
            if(cell.getVertAlign()==1)
            {
              richCell.setVerticalAlignment(VerticalAlignment.CENTER);
            }
            else if(cell.getVertAlign()==2)
            {
              richCell.setVerticalAlignment(VerticalAlignment.BOTTOM);
            }
            parseSectionRange(doc,cell,richCell.getContent(),nesting,richListMap,poiParagraph.getTableLevel());
            
            removeLastEmptyLine(richCell.getContent());
          }
        }

        i+=t.numParagraphs()-1;
      }
      else
      {
        int runCount=poiParagraph.numCharacterRuns();
        for(int j=0;j<runCount;)
        {
          CharacterRun run=poiParagraph.getCharacterRun(j);
          //System.out.println(Arrays.toString(run.text().getBytes()));
          j=parseCharacterRun(j,doc,poiParagraph,run,richParagraph,preamble,false);
          preamble=""; //$NON-NLS-1$
        }
      }
      target.add(richParagraph);
    }
  }
  
  @Override
  public RichDocument read(InputStream is)
  {
    RichDocument ans=new RichDocument();
    Nesting nesting=new Nesting();
    
    Map<Integer,NumberedList> richListMap=new HashMap<>();
    
    try(HWPFDocument doc=new HWPFDocument(is))
    {
      Range overall=doc.getRange();
      //Section section=overall.getSection(0);
      Range section=overall;
      parseSectionRange(doc,section,ans.getParagraphs(),nesting,richListMap,0);
    }
    catch(IOException ex)
    {
      throw new RuntimeException(ex);
    }
    
    return ans;
  }

}
