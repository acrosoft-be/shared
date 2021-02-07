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
package be.acrosoft.gaia.shared.rich.ext.readers;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import be.acrosoft.gaia.shared.rich.Alignment;
import be.acrosoft.gaia.shared.rich.DocumentReader;
import be.acrosoft.gaia.shared.rich.ImageSection;
import be.acrosoft.gaia.shared.rich.Paragraph;
import be.acrosoft.gaia.shared.rich.ParagraphStyle.BulletStyle;
import be.acrosoft.gaia.shared.rich.RGBColor;
import be.acrosoft.gaia.shared.rich.RichDocument;
import be.acrosoft.gaia.shared.rich.TextSection;

/**
 * DOCXReader.
 */
public class DOCXReader implements DocumentReader<InputStream>
{
  @SuppressWarnings("deprecation")
@Override
  public RichDocument read(InputStream is)
  {
    RichDocument ans=new RichDocument();
    
    try(XWPFDocument doc=new XWPFDocument(is))
    {
      for(XWPFParagraph poiParagraph:doc.getParagraphs())
      {
        Paragraph richParagraph=new Paragraph();
        
        int indent=0;
        switch(poiParagraph.getAlignment())
        {
          case LEFT:
            richParagraph.getStyle().setAlignment(Alignment.LEFT);
            indent=poiParagraph.getIndentationLeft();
            break;
          case CENTER:
            richParagraph.getStyle().setAlignment(Alignment.CENTER);
            break;
          case RIGHT:
            richParagraph.getStyle().setAlignment(Alignment.RIGHT);
            indent=poiParagraph.getIndentationRight();
            break;
          case BOTH:
            richParagraph.getStyle().setAlignment(Alignment.JUSTIFY);
            indent=poiParagraph.getIndentationLeft();
            break;
          default:
            break;
        }
        richParagraph.getStyle().setIndent(indent/12);
        richParagraph.getStyle().setFirstLineIndent((poiParagraph.getIndentationFirstLine())/12);
        
        if(poiParagraph.getNumID()!=null)
        {
          int level=poiParagraph.getCTP().getPPr().getNumPr().getIlvl().getVal().intValue();
          
          richParagraph.getStyle().setIndent(indent+30+level*30);
          richParagraph.getStyle().setFirstLineIndent(indent-20);
          
          
          richParagraph.getStyle().setBulletStyle(BulletStyle.BULLET);
          switch(level)
          {
            case 0:
              richParagraph.getStyle().setBulletText("-"); //$NON-NLS-1$
              break;
            case 1:
              richParagraph.getStyle().getBulletTextStyle().setFontName("Wingdings"); //$NON-NLS-1$
              richParagraph.getStyle().setBulletText("\u00a1"); //$NON-NLS-1$
              break;
            case 2:
              richParagraph.getStyle().getBulletTextStyle().setFontName("Wingdings"); //$NON-NLS-1$
              richParagraph.getStyle().setBulletText("\u006e"); //$NON-NLS-1$
              break;
            case 3:
              richParagraph.getStyle().getBulletTextStyle().setFontName("Wingdings"); //$NON-NLS-1$
              richParagraph.getStyle().setBulletText("\u006c"); //$NON-NLS-1$
              break;
            case 4:
              richParagraph.getStyle().getBulletTextStyle().setFontName("Wingdings"); //$NON-NLS-1$
              richParagraph.getStyle().setBulletText("\u00a1"); //$NON-NLS-1$
              break;
            case 5:
              richParagraph.getStyle().getBulletTextStyle().setFontName("Wingdings"); //$NON-NLS-1$
              richParagraph.getStyle().setBulletText("\u006e"); //$NON-NLS-1$
              break;
            case 6:
              richParagraph.getStyle().getBulletTextStyle().setFontName("Wingdings"); //$NON-NLS-1$
              richParagraph.getStyle().setBulletText("\u006c"); //$NON-NLS-1$
              break;
            case 7:
              richParagraph.getStyle().getBulletTextStyle().setFontName("Wingdings"); //$NON-NLS-1$
              richParagraph.getStyle().setBulletText("\u00a1"); //$NON-NLS-1$
              break;
            case 8:
              richParagraph.getStyle().getBulletTextStyle().setFontName("Wingdings"); //$NON-NLS-1$
              richParagraph.getStyle().setBulletText("\u006e"); //$NON-NLS-1$
              break;
            case 9:
              richParagraph.getStyle().getBulletTextStyle().setFontName("Wingdings"); //$NON-NLS-1$
              richParagraph.getStyle().setBulletText("\u006c"); //$NON-NLS-1$
              break;
            default:
              richParagraph.getStyle().setBulletText(null);
              break;
          }

        }
        
        for(XWPFRun run:poiParagraph.getRuns())
        {
          for(XWPFPicture picture:run.getEmbeddedPictures())
          {
            ImageSection richSection=new ImageSection();
            richSection.setData(picture.getPictureData().getData());
            richParagraph.getSections().add(richSection);
          }
          TextSection richSection=new TextSection();
          String text=run.getText(run.getTextPosition());
          if(text==null) continue;
          while(text.endsWith("\n") || text.endsWith("\r")) //$NON-NLS-1$ //$NON-NLS-2$
          {
            text=text.substring(0,text.length()-1);
          }
          richSection.setText(text);
          richParagraph.getSections().add(richSection);
          if(run.isBold())
          {
            richSection.getStyle().setBold(true);
          }
          if(run.isItalic())
          {
            richSection.getStyle().setItalic(true);
          }
          if(run.getUnderline()!=UnderlinePatterns.NONE)
          {
            richSection.getStyle().setUnderline(true);
          }
          String fontName=run.getFontFamily();
          int fontSize=run.getFontSize();
          richSection.getStyle().setFontFamily(fontName);
          richSection.getStyle().setFontSize(fontSize/2);
          
          String color=run.getColor();
          if(color!=null)
          {
            String rh=color.substring(0,2);
            String gh=color.substring(2,4);
            String bh=color.substring(4,6);
            int r=Integer.parseInt(rh,16);
            int g=Integer.parseInt(gh,16);
            int b=Integer.parseInt(bh,16);
            richSection.getStyle().setForeGround(new RGBColor(r/255.0,g/255.0,b/255.0));
          }
        }
        ans.getParagraphs().add(richParagraph);
      }
    }
    catch(IOException ex)
    {
      throw new RuntimeException(ex);
    }
    
    return ans;
  }

}
