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
package be.acrosoft.gaia.shared.rich.writers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TextChangeListener;
import org.eclipse.swt.custom.TextChangedEvent;
import org.eclipse.swt.custom.TextChangingEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextLayout;

import be.acrosoft.gaia.shared.rich.Alignment;
import be.acrosoft.gaia.shared.rich.DocumentWriter;
import be.acrosoft.gaia.shared.rich.ImageSection;
import be.acrosoft.gaia.shared.rich.ImageSection.Unit;
import be.acrosoft.gaia.shared.rich.NumberedList;
import be.acrosoft.gaia.shared.rich.Paragraph;
import be.acrosoft.gaia.shared.rich.ParagraphStyle;
import be.acrosoft.gaia.shared.rich.RGBColor;
import be.acrosoft.gaia.shared.rich.RichDocument;
import be.acrosoft.gaia.shared.rich.Section;
import be.acrosoft.gaia.shared.rich.TextSection;
import be.acrosoft.gaia.shared.rich.TextStyle;

/**
 * StyledTextWriter.
 */
public class StyledTextWriter implements DocumentWriter<StyledText>
{
  private static class ImageInfo
  {
    private int left;
    private int top;
    private int width;
    private int height;
    private Image image;
    private int offset;
  }
  
  private static class LineInfo
  {
    private int line;
    private ParagraphStyle style;
  }
  
  private static class FontMapping
  {
    private String name;
    private int size;
    private int style;
    private Font font;
  }
  
  private static class ColorMapping
  {
    private RGBColor rgbcolor;
    private Color color;
    
  }
  
  private static class Holder
  {
    private List<ImageInfo> images;
    private List<LineInfo> lines;
    private List<FontMapping> fonts;
    private List<ColorMapping> colors;
    private DisposeListener disposeListener;
    private PaintObjectListener paintObjectListener;
    private TextChangeListener textChangeListener;
    
    private void dispose(StyledText text)
    {
      text.removeDisposeListener(disposeListener);
      text.removePaintObjectListener(paintObjectListener);
      text.getContent().removeTextChangeListener(textChangeListener);
      text.setText(""); //$NON-NLS-1$
      for(ImageInfo nfo:images)
      {
        if(nfo.image!=null)
        {
          nfo.image.dispose();
        }
      }
      for(ColorMapping map:colors)
      {
        map.color.dispose();
      }
      for(FontMapping map:fonts)
      {
        map.font.dispose();
      }
    }
  }
  
  private Font getFont(Holder holder,StyledText text,String name,int size,int style)
  {
    for(FontMapping map:holder.fonts)
    {
      if(map.name.equals(name) && map.size==size && map.style==style)
      {
        return map.font;
      }
    }
    Font ans=new Font(text.getDisplay(),name,size,style);
    FontMapping map=new FontMapping();
    map.font=ans;
    map.name=name;
    map.size=size;
    map.style=style;
    holder.fonts.add(map);
    return ans;
  }
  
  private Color getColor(Holder holder,StyledText text,RGBColor color)
  {
    for(ColorMapping map:holder.colors)
    {
      if(map.rgbcolor.equals(color))
      {
        return map.color;
      }
    }
    Color ans=new Color(text.getDisplay(),(int)(color.red()*255),(int)(color.green()*255),(int)(color.blue()*255));
    ColorMapping map=new ColorMapping();
    map.color=ans;
    map.rgbcolor=color;
    holder.colors.add(map);
    return ans;
  }
  
  private void formatStyle(Holder holder,StyledText text,StyleRange style,ParagraphStyle ps,TextStyle ts)
  {
    int st=SWT.NONE;
    if(ts.isItalic()) st|=SWT.ITALIC;
    if(ts.isBold()) st|=SWT.BOLD;
    Font font=getFont(holder,text,ts.getFontName(),(int)ts.getFontSize(),st);
    style.font=font;
    style.fontStyle=st;
    style.underline=ts.isUnderline();
    Color foreGround=getColor(holder,text,ts.getForeGround());
    Color backGround=getColor(holder,text,ts.getBackGround());
    style.foreground=foreGround;
    style.background=backGround;
  }
  
  private int pointToPixel(StyledText text,double pt)
  {
    pt/=72.0;
    pt*=text.getDisplay().getDPI().x;
    return (int)pt;
  }
    
  @Override
  public void write(RichDocument doc,StyledText text)
  {
    String dataKey=StyledTextWriter.this.getClass()+".holder"; //$NON-NLS-1$
    Holder previousHolder=(Holder)text.getData(dataKey);
    if(previousHolder!=null)
    {
      previousHolder.dispose(text);
    }
    
    text.setTabs(15);
    
    Holder holder=new Holder();
    text.setData(dataKey,holder);
    holder.images=new ArrayList<ImageInfo>();
    holder.lines=new ArrayList<LineInfo>();
    holder.fonts=new ArrayList<FontMapping>();
    holder.colors=new ArrayList<ColorMapping>();
    holder.disposeListener=e->
    {
      holder.dispose(text);
      text.setData(dataKey,null);
    };
    holder.paintObjectListener=event->
    {
      GC gc = event.gc;
      StyleRange style = event.style;
      int x=event.x;
      int y=event.y+event.ascent-style.metrics.ascent;
      
      if(event.bullet==null)
      {
        for(ImageInfo nfo:holder.images)
        {
          if(nfo.offset==style.start)
          {
            boolean wasAdvanced=gc.getAdvanced();
            gc.setAntialias(SWT.ON);
            gc.setInterpolation(SWT.HIGH);
            if(nfo.image!=null)
            {
              gc.drawImage(nfo.image,0,0,nfo.image.getBounds().width,nfo.image.getBounds().height,x+nfo.left,y+nfo.top,nfo.width,nfo.height);
            }
            else
            {
              gc.setLineDash(new int[] {2,2});
              gc.fillRectangle(x+nfo.left,y+nfo.top,nfo.width,nfo.height);
              gc.drawRectangle(x+nfo.left,y+nfo.top,nfo.width,nfo.height);
            }
            gc.setAdvanced(wasAdvanced);
            break;
          }
        }
      }
      else
      {
        int line=text.getLineAtOffset(style.start);
        for(LineInfo nfo:holder.lines)
        {
          if(nfo.line==line)
          {
            String txt=null;
            if(nfo.style.getBulletStyle()==ParagraphStyle.BulletStyle.BULLET)
              txt=nfo.style.getBulletText();
            else if(nfo.style.getBulletStyle()==ParagraphStyle.BulletStyle.LIST_NUMBER)
              txt=""+(event.bulletIndex+1)+nfo.style.getBulletText(); //$NON-NLS-1$
            else if(nfo.style.getBulletStyle()==ParagraphStyle.BulletStyle.LIST_LOWER_ALPHABETIC)
              txt=(char)(event.bulletIndex+'a')+nfo.style.getBulletText();
            else if(nfo.style.getBulletStyle()==ParagraphStyle.BulletStyle.LIST_UPPER_ALPHABETIC)
              txt=(char)(event.bulletIndex+'A')+nfo.style.getBulletText();
            else if(nfo.style.getBulletStyle()==ParagraphStyle.BulletStyle.LIST_LOWER_ROMAN)
              txt=Roman.toString(event.bulletIndex+1)+nfo.style.getBulletText();
            else if(nfo.style.getBulletStyle()==ParagraphStyle.BulletStyle.LIST_UPPER_ROMAN)
              txt=Roman.toString(event.bulletIndex+1).toUpperCase()+nfo.style.getBulletText();
            
            if(txt!=null)
            {
              TextLayout layout=new TextLayout(text.getDisplay());
              layout.setAscent(event.ascent);
              layout.setDescent(event.descent);
              layout.setText(txt);
              StyleRange rng=new StyleRange();
              formatStyle(holder,text,rng,nfo.style,nfo.style.getBulletTextStyle());
              rng.metrics=null;
              layout.setStyle(rng,0,layout.getText().length());
              int px=event.x;
              px+=pointToPixel(text,nfo.style.getIndent()+nfo.style.getFirstLineIndent());
              
              layout.draw(event.gc, px,event.y);
              
              layout.dispose();
            }
            
            break;
          }
        }
      }
    };
    holder.textChangeListener=new TextChangeListener()
    {
      @Override
      public void textChanged(TextChangedEvent arg0)
      {
      }

      @Override
      public void textChanging(TextChangingEvent e)
      {
        int start = e.start;
        int replaceCharCount = e.replaceCharCount;
        int newCharCount = e.newCharCount;
        
        Iterator<ImageInfo> it=holder.images.iterator();
        while(it.hasNext())
        {
          ImageInfo nfo=it.next();
          int offset=nfo.offset;
          if (start <= offset && offset < start + replaceCharCount)
          {
            it.remove();
            offset = -1;
          }
          if (offset != -1 && offset >= start) offset += newCharCount - replaceCharCount;
          nfo.offset = offset;
        }
      }

      @Override
      public void textSet(TextChangedEvent arg0)
      {
      }
    };
    
    
    text.addDisposeListener(holder.disposeListener);
    text.addPaintObjectListener(holder.paintObjectListener);
    text.getContent().addTextChangeListener(holder.textChangeListener);
    
    boolean firstParagraph=true;
    List<StyleRange> rngs=new ArrayList<StyleRange>();
    StringBuffer buffer=new StringBuffer();
    Map<NumberedList,Bullet> lists=new HashMap<NumberedList,Bullet>();

    List<ImageInfo> images=new ArrayList<ImageInfo>();
    
    for(Paragraph par:doc)
    {
      if(!firstParagraph)
        buffer.append(text.getLineDelimiter());
      firstParagraph=false;
      
      if(par.getStyle().getSpacingBefore()>3)
      {
        StyleRange rng=new StyleRange();
        rng.start=buffer.length();
        rng.length=text.getLineDelimiter().length()+1;
        rng.font=getFont(holder,text,"",(int)par.getStyle().getSpacingBefore(),SWT.NONE); //$NON-NLS-1$
        
        buffer.append(" "+text.getLineDelimiter()); //$NON-NLS-1$
        rngs.add(rng);
      }
      
      for(Section sect:par)
      {
        if(sect instanceof TextSection)
        {
          TextSection textSection=(TextSection)sect;
          StyleRange rng=new StyleRange();
          rng.start=buffer.length();
          rng.length=textSection.getText().length();
          formatStyle(holder,text,rng,par.getStyle(),textSection.getStyle());
          buffer.append(textSection.getText());
          rngs.add(rng);
        }
        else if(sect instanceof ImageSection)
        {
          try
          {
            ImageSection image=(ImageSection)sect;
            Image img=null;
            if(image.getData()!=null)
            {
              ByteArrayInputStream bis=new ByteArrayInputStream(image.getData());
              img=new Image(text.getDisplay(),bis);
            }
            StyleRange rng=new StyleRange();
            rng.start=buffer.length();
            rng.length=1;
            int w=(int)image.getWidth();
            int h=(int)image.getHeight();
            if(w>=0 && image.getWidthUnit()==Unit.POINT) w=pointToPixel(text,w);
            if(h>=0 && image.getHeightUnit()==Unit.POINT) h=pointToPixel(text,h);
            int ox=pointToPixel(text,image.getLeftOffset());
            int oy=pointToPixel(text,image.getTopOffset());
            if(img!=null && image.getWidth()<0)
            {
              w=img.getImageData().width;
            }
            if(img!=null && image.getHeight()<0)
            {
              h=img.getImageData().height;
            }
            rng.metrics=new GlyphMetrics(h+oy,0,w+ox);
            buffer.append("\ufffc"); //$NON-NLS-1$
            rngs.add(rng);
            ImageInfo nfo=new ImageInfo();
            nfo.image=img;
            nfo.width=w;
            nfo.height=h;
            nfo.left=ox;
            nfo.top=oy;
            nfo.offset=rng.start;
            images.add(nfo);
          }
          catch(Exception ex)
          {
          }
        }
      }
      if(par.getStyle().getSpacingAfter()>3)
      {
        StyleRange rng=new StyleRange();
        rng.start=buffer.length();
        rng.length=text.getLineDelimiter().length()+1;
        rng.font=getFont(holder,text,"",(int)par.getStyle().getSpacingAfter(),SWT.NONE); //$NON-NLS-1$

        buffer.append(text.getLineDelimiter()+" "); //$NON-NLS-1$
        rngs.add(rng);
      }
      
    }
    
    
    //text.setFont(getFont(holder,text,"",1,SWT.NONE)); //$NON-NLS-1$
    text.setText(buffer.toString());
    
    StyleRange[] allRanges=rngs.toArray(new StyleRange[0]);
    text.setStyleRanges(allRanges);
    
    int lineNumber=0;
    for(Paragraph par:doc)
    {
      if(par.getStyle().getSpacingBefore()>3)
      {
        lineNumber++;
      }
      ParagraphStyle ps=par.getStyle();
      if(ps.getAlignment().equals(Alignment.LEFT))
        text.setLineAlignment(lineNumber,1,SWT.LEFT);
      else if(ps.getAlignment().equals(Alignment.CENTER))
        text.setLineAlignment(lineNumber,1,SWT.CENTER);
      else if(ps.getAlignment().equals(Alignment.RIGHT))
        text.setLineAlignment(lineNumber,1,SWT.RIGHT);
      else if(ps.getAlignment().equals(Alignment.JUSTIFY))
        text.setLineJustify(lineNumber,1,true);
      
      text.setLineIndent(lineNumber,1,pointToPixel(text,ps.getFirstLineIndent()+ps.getIndent()));
      text.setLineWrapIndent(lineNumber,1,pointToPixel(text,ps.getIndent()));
      if(ps.getBulletStyle()!=ParagraphStyle.BulletStyle.NONE)
      {
        Bullet bullet=lists.get(ps.getList());
        if(bullet==null)
        {
          StyleRange style=new StyleRange();
          formatStyle(holder,text,style,par.getStyle(),ps.getBulletTextStyle());
          style.metrics=new GlyphMetrics(0, 0, 0);
          bullet=new Bullet(ST.BULLET_CUSTOM,style);
          bullet.text=ps.getBulletText();
          lists.put(ps.getList(),bullet);
        }
        text.setLineIndent(lineNumber,1,pointToPixel(text,ps.getIndent()));
        text.setLineWrapIndent(lineNumber,1,pointToPixel(text,ps.getIndent()));
        
        text.setLineBullet(lineNumber,1,bullet);
        LineInfo nfo=new LineInfo();
        nfo.line=lineNumber;
        nfo.style=ps;
        holder.lines.add(nfo);
      }      
      
      lineNumber++;
      if(par.getStyle().getSpacingAfter()>3)
      {
        lineNumber++;
      }
    }
    
    holder.images=images;
  }

}
