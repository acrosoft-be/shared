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
package be.acrosoft.gaia.shared.rich.run;

import java.io.FileInputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import be.acrosoft.gaia.shared.rich.RichDocument;
import be.acrosoft.gaia.shared.rich.readers.RTFReader;
import be.acrosoft.gaia.shared.rich.writers.StyledTextWriter;

@SuppressWarnings({"javadoc","nls"})
public class Run
{
  public static void main(String[] args) throws Exception
  {
    Display display=Display.getDefault();
    Shell shell=new Shell(display);
    shell.setSize(800,600);
    shell.setLayout(new FillLayout());
    
    StyledText st=new StyledText(shell,SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL|SWT.WRAP);
    
    //RichDocument document=new RTFReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\Document.rtf"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\Syllabus FSA2240.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\PPL\\PROCEDURE ADMINISTRATIVE.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\PPL\\info cours ppl email.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\PPL\\Information cours PPL Namur et Bruxelles 2010.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\Mes fichiers reçus\\Contrat de fourniture de logiciel informatique - Centroptic.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\testlist.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\testtable.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\testtable2.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\religion.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\Cours FSA2300 Plan détaillé.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\INGI2210LABO1.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\reseau.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\usine.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\1016\\usermanual.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\mémoire\\full.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\airbus.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\2710\\27105.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\labo telecom\\Intro Telecom - Labo2.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\labo telecom\\Labo 3.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\mémoire\\final\\Exemple.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\2766\\AR021008fr\\planmodule_appli_rep.doc"));
    //RichDocument document=new DOCReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\Mes fichiers reçus\\A trip to Japan.doc"));
    //RichDocument document=new RTFReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\testtable.rtf"));
    //RichDocument document=new RTFReader().read(new FileInputStream("D:\\Users\\Plouf\\Downloads\\borders.rtf"));
    RichDocument document=new RTFReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\simpletable.rtf"));
    //RichDocument document=new RTFReader().read(new FileInputStream("D:\\Users\\Plouf\\Documents\\simple.rtf"));
    new StyledTextWriter().write(document,st);
    
    shell.open();
    while(!shell.isDisposed())
    {
      if(!display.readAndDispatch())
        display.sleep();
    }
  }
}
