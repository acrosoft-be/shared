/**
 * Copyright Acropolis Software SPRL (http://www.acrosoft.be)
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


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.function.Function;

import org.junit.Test;

/**
 * PartitionTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class PartitionTest
{
  /**
   * MyArrayCategoryProvider.
   */
  static class MyArrayCategoryProvider implements Function<Integer,String>
  {
    @Override
    public String apply(Integer item)
    {
      if(item.intValue()%2==0) return "Odd";
      return "Even";
    }

  }

  @Test
  public void testMerge()
  {
    Integer[] data=new Integer[] {1,2,3,5,7,8,10,12,4,5,6};
    Partition<Integer,String> part=Partition.array(data).using(new MyArrayCategoryProvider()).merge();
    
    List<Partition<Integer,String>.Element> elements=part.getElements();
    assertEquals(elements.size(),2);

    String[] strings=new String[data.length];
    
    for(Partition<Integer,String>.Element el:elements)
    {
      if(el.getCategory().equals("Odd"))
      {
        assertArrayEquals(el.getItems(),new Integer[] {2,8,10,12,4,6});
        el.inject(new String[] {"Two","Height","Ten","Twelve","Four","Six"}).into(strings);
      }
      else if(el.getCategory().equals("Even"))
      {
        assertArrayEquals(el.getItems(),new Integer[] {1,3,5,7,5});
        el.inject(new String[] {"One","Three","Five","Seven","Five"}).into(strings);
      }
      else
      {
        fail("Unexpected category "+el.getCategory());
      }
    }
    
    assertArrayEquals(strings,new String[] {"One","Two","Three","Five","Seven","Height","Ten","Twelve","Four","Five","Six"});
  }

  @Test
  public void testOrder()
  {
    Integer[] data=new Integer[] {1,2,3,5,7,8,10,12,4,5,6};
    Partition<Integer,String> part=Partition.array(data).using(new MyArrayCategoryProvider()).preserveOrder();
    
    List<Partition<Integer,String>.Element> elements=part.getElements();
    assertEquals(elements.size(),3+3);
    
    String[] strings=new String[data.length];
    
    assertEquals(elements.get(0).getCategory(),"Even");
    assertArrayEquals(elements.get(0).getItems(),new Integer[] {1});
    elements.get(0).inject(new String[] {"One"}).into(strings);
    
    assertEquals(elements.get(1).getCategory(),"Odd");
    assertArrayEquals(elements.get(1).getItems(),new Integer[] {2});
    elements.get(1).inject(new String[] {"Two"}).into(strings);
    
    assertEquals(elements.get(2).getCategory(),"Even");
    assertArrayEquals(elements.get(2).getItems(),new Integer[] {3,5,7});
    elements.get(2).inject(new String[] {"Three","Five","Seven"}).into(strings);

    assertEquals(elements.get(3).getCategory(),"Odd");
    assertArrayEquals(elements.get(3).getItems(),new Integer[] {8,10,12,4});
    elements.get(3).inject(new String[] {"Height","Ten","Twelve","Four"}).into(strings);

    assertEquals(elements.get(4).getCategory(),"Even");
    assertArrayEquals(elements.get(4).getItems(),new Integer[] {5});
    elements.get(4).inject(new String[] {"Five"}).into(strings);

    assertEquals(elements.get(5).getCategory(),"Odd");
    assertArrayEquals(elements.get(5).getItems(),new Integer[] {6});
    elements.get(5).inject(new String[] {"Six"}).into(strings);

    assertArrayEquals(strings,new String[] {"One","Two","Three","Five","Seven","Height","Ten","Twelve","Four","Five","Six"});
  }
  
  @Test
  public void testEmpty()
  {
    Integer[] data=new Integer[0];
    assertEquals(0,Partition.array(data).using(i->i).merge().getElements().size());
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testInvalidInjectWrongPartition()
  {
    Partition.array(new Integer[] {1,2}).using(i->i).merge().getElements().get(0).inject(new Integer[] {1,2}).into(new Integer[2]);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testInvalidInjectWrongTarget()
  {
    Partition.array(new Integer[] {1,2}).using(i->i).merge().getElements().get(0).inject(new Integer[] {1}).into(new Integer[3]);
  }
  
}
