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
import java.util.Map;

import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.FieldsDocumentPart;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Field;
import org.apache.poi.hwpf.usermodel.HWPFList;
import org.apache.poi.hwpf.usermodel.OfficeDrawing;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;

import be.acrosoft.gaia.shared.rich.Alignment;
import be.acrosoft.gaia.shared.rich.DocumentReader;
import be.acrosoft.gaia.shared.rich.ImageSection;
import be.acrosoft.gaia.shared.rich.NumberedList;
import be.acrosoft.gaia.shared.rich.Paragraph;
import be.acrosoft.gaia.shared.rich.ParagraphStyle.BulletStyle;
import be.acrosoft.gaia.shared.rich.RGBColor;
import be.acrosoft.gaia.shared.rich.RichDocument;
import be.acrosoft.gaia.shared.rich.TextSection;

/**
 * DOCReader.
 */
public class DOCReader implements DocumentReader<InputStream>
{
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
  
  private int parseCharacterRun(int runCount,HWPFDocument doc,Range parentRange,CharacterRun run,Paragraph richParagraph,String preamble)
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
      System.out.println((int)code);
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
          richParagraph.getSections().add(imageSection);
          EscherContainerRecord root=draw.getOfficeArtSpContainer();
          if(root!=null)
          {
            scanRecord(root);
          }
          //System.out.println(draw.getOfficeArtSpContainer());
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
            i=parseCharacterRun(i,doc,subRange,subRun,richParagraph,""); //$NON-NLS-1$
          }
          while(parentRange.getCharacterRun(runCount).getEndOffset()<field.getFieldEndOffset()) runCount++;
          runCount++;
        }
        else
        {
           //TODO parse manually
          System.out.println(run.getStartOffset()-doc.getMainTextboxRange().getStartOffset());
          for(Field f:doc.getFields().getFields(FieldsDocumentPart.TEXTBOX))
          {
            System.out.println(f);
          }
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
    while(text.endsWith("\n") || text.endsWith("\r")) //$NON-NLS-1$ //$NON-NLS-2$
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
  
  private void parseSectionRange(HWPFDocument doc,Range section,RichDocument target,Nesting nesting,Map<Integer,NumberedList> richListMap)
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
      richParagraph.getStyle().setIndent(indent/20);
      richParagraph.getStyle().setFirstLineIndent((poiParagraph.getFirstLineIndent())/20);
      
      int spacingDelta=poiParagraph.getSpacingBefore()-previousSpacingAfter;
      if(spacingDelta<0) spacingDelta=0;
      
      previousSpacingAfter=poiParagraph.getSpacingAfter();
      
      richParagraph.getStyle().setSpacingBefore(spacingDelta/20);
      richParagraph.getStyle().setSpacingAfter(poiParagraph.getSpacingAfter()/20);

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
              richListMap.put(lsid,numberedList);
            }
            richParagraph.getStyle().setList(numberedList);
            richParagraph.getStyle().setBulletText(list.getListData().getLevel(1).getNumberText().substring(1)+postFix);
            richParagraph.getStyle().setBulletStyle(BulletStyle.LIST_NUMBER);
          }
          

        }
      }

      if(poiParagraph.isInTable())
      {
        //TODO parse table here
        i+=section.getTable(poiParagraph).numParagraphs();
        continue;
      }
      
      int runCount=poiParagraph.numCharacterRuns();
      for(int j=0;j<runCount;)
      {
        CharacterRun run=poiParagraph.getCharacterRun(j);
        //System.out.println(Arrays.toString(run.text().getBytes()));
        j=parseCharacterRun(j,doc,poiParagraph,run,richParagraph,preamble);
        preamble=""; //$NON-NLS-1$
      }
      target.getParagraphs().add(richParagraph);
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
      parseSectionRange(doc,section,ans,nesting,richListMap);
    }
    catch(IOException ex)
    {
      throw new RuntimeException(ex);
    }
    
    return ans;
  }

}
