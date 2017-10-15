package be.acrosoft.gaia.shared.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * This class implements an array partition. A partition is a set of Element. Each
 * partition element holds a subset of the original array, all element items belonging to the same
 * category. There can be either a single partition element per category if the partition is merged,
 * or there can be several partition element per category if the partition is not merged in order
 * to preserve ordering.<br/>
 * Once an array partition is created, several arrays can be merged into a single, bigger array,
 * following the partition.
 * @param <T> array item type.
 * @param <C> category type.
 */
public class Partition<T,C> implements Iterable<Partition<T,C>.Element>
{
  /**
   * Data holder for the partition.
   * @param <T> item type.
   */
  public static interface Holder<T>
  {
    /**
     * Split logic, created from a holder, than can be used to create a partition.
     * @param <T> item type.
     * @param <C> category type.
     */
    public static interface SplitLogic<T,C>
    {
      /**
       * Create a partition, merging all categories.
       * @return new partition.
       */
      public Partition<T,C> merge();
      
      /**
       * Create a partition, preserving order.
       * @return new partition.
       */
      public Partition<T,C> preserveOrder();
    }

    /**
     * Create a split logic using the given function to map elements to their category.
     * @param function mapping function.
     * @return split logic.
     */
    public <C> SplitLogic<T,C> using(Function<T,C> function);
  }
  
  /**
   * Data injection source, created from an element.
   * @param <P> item type.
   */
  public static interface InjectSource<P>
  {
    /**
     * Inject into given target array.
     * @param target target array.
     */
    public void into(P[] target);
  }
  
  /**
   * Element.
   */
  public class Element
  {
    private C _category;
    private T[] _items;
    private int _firstIndex;
    private boolean _merged;
    
    private Element(C category,T[] items,boolean merged,int firstIndex)
    {
      _category=category;
      _items=items;
      _firstIndex=firstIndex;
      _merged=merged;
    }
    
    /**
     * Get the category.
     * @return the category.
     */
    public C getCategory()
    {
      return _category;
    }
    
    /**
     * Get the items.
     * @return the items.
     */
    public T[] getItems()
    {
      return _items;
    }
    
    /**
     * If this element is not merged, get the first index.
     * @return the first index. Undefined if isMerged() returns true.
     */
    public int getFirstIndex()
    {
      return _firstIndex;
    }
    
    /**
     * Check whether this element is a merged element.
     * @return true if element is a merge, false otherwise.
     */
    public boolean isMerged()
    {
      return _merged;
    }
    
    /**
     * Inject an array into a larger array by following this element mapping.
     * The source array must have the same size as the partition.
     * The target array must have the same size as the partitioned array.
     * @param <P> array type.
     * @param items source array.
     * @return source injection to specify target array.
     */
    public <P> InjectSource<P> inject(P[] items)
    {
      return new InjectSource<P>()
      {
        @Override
        public void into(P[] full)
        {
          if(items.length!=getItems().length) throw new IllegalArgumentException();
          if(full.length!=_categories.size()) throw new IllegalArgumentException();
          
          int itemIndex=0;
          int first=0;
          if(!isMerged()) first=getFirstIndex();
          C cat=getCategory();
          for(int i=first;i<full.length;i++)
          {
            if(_categories.get(i).equals(cat))
            {
              full[i]=items[itemIndex++];
              if(itemIndex==items.length) break;
            }
          }
        }
      };
    }
    
  }
  
  private ArrayList<Element> _elements;
  private ArrayList<C> _categories;
 
  /**
   * Create a new ArrayPartition.
   * @param array array to partition.
   * @param provider array category provider used to assign a category to each array item.
   * @param merge true if a merged partition should be created, false is order must be
   * preserved.
   */
  private Partition(T[] array,Function<T,C> provider,boolean merge)
  {
    _categories=new ArrayList<>(array.length);
    for(int i=0;i<array.length;i++)
      _categories.add(provider.apply(array[i]));
    
    if(array.length==0)
    {
      _elements=new ArrayList<>();
    }
    else
    {
      if(!merge)
        parseKeep(array);
      else
        parseMerge(array);
    }
  }
  
  /**
   * Create a new holder, first step to create a partition.
   * @param array array to be partitioned.
   * @return data holder.
   */
  public static <T> Holder<T> array(T... array)
  {
    return new Holder<T>()
    {
      @Override
      public <C> be.acrosoft.gaia.shared.util.Partition.Holder.SplitLogic<T,C> using(Function<T,C> function)
      {
        return new SplitLogic()
        {
          @Override
          public Partition<T,C> preserveOrder()
          {
            return new Partition<T,C>(array,function,false);
          }
          
          @Override
          public Partition<T,C> merge()
          {
            return new Partition<T,C>(array,function,true);
          }
        };
      }
      
    };
  }
  
  @SuppressWarnings("unchecked")
  private T[] toArray(List<T> list,T[] array)
  {
    Object ans=Array.newInstance(array.getClass().getComponentType(),list.size());
    for(int i=0;i<list.size();i++)
      Array.set(ans,i,list.get(i));
    return (T[])ans;
  }
  
  private void parseKeep(T[] array)
  {
    ArrayList<Element> global=new ArrayList<Element>();
    ArrayList<T> current=new ArrayList<T>();
    int first=0;
    C currentCategory=_categories.get(first);
    current.add(array[first]);
    
    for(int i=1;i<array.length;i++)
    {
      if(_categories.get(i).equals(currentCategory))
      {
        current.add(array[i]);
      }
      else
      {
        Element el=new Element(currentCategory,toArray(current,array),false,first);
        first=i;
        global.add(el);
        current.clear();
        currentCategory=_categories.get(first);
        current.add(array[first]);
      }
    }
    
    Element el=new Element(currentCategory,toArray(current,array),false,first);
    global.add(el);
    
    _elements=global;
  }
  
  private void parseMerge(T[] array)
  {
    HashMap<C,ArrayList<T>> map=new HashMap<C,ArrayList<T>>();
    
    for(int i=0;i<array.length;i++)
    {
      C cat=_categories.get(i);
      ArrayList<T> element=map.get(cat);
      if(element==null)
      {
        element=new ArrayList<T>();
        map.put(cat,element);
      }
      element.add(array[i]);
    }
    
    Set<Map.Entry<C,ArrayList<T>>> entrySet=map.entrySet();
    _elements=new ArrayList<>(entrySet.size());
    
    for(Map.Entry<C,ArrayList<T>> category:entrySet)
    {
      ArrayList<T> elements=category.getValue();
      T[] els=toArray(elements,array);
      _elements.add(new Element(category.getKey(),els,true,-1));
    }
  }
  
  /**
   * Get all the elements in this partition.
   * @return all the partition elements.
   */
  public List<Element> getElements()
  {
    return Collections.unmodifiableList(_elements);
  }

  @Override
  public Iterator<Element> iterator()
  {
    return getElements().iterator();
  }
}
