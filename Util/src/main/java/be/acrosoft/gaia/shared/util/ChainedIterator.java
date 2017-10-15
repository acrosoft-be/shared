package be.acrosoft.gaia.shared.util;

import java.util.Iterator;

/**
 * ChainedIterator.
 * @param <K> parameter.
 */
public class ChainedIterator<K> implements Iterator<K>
{
  private Iterator<K>[] _iterators;
  private int _current;
  private boolean _currentConsumed;
  
  /**
   * Create a new ChainedIterator.
   * @param iterators iterators to combine.
   */
  public ChainedIterator(Iterator<K>[] iterators)
  {
    _iterators=iterators;
    _current=0;
    _currentConsumed=false;
    while(_current<_iterators.length && !_iterators[_current].hasNext())
      _current++;
  }

  private boolean findNext()
  {
    if(!_currentConsumed) return _current<_iterators.length;
    _currentConsumed=true;
    while(_current<_iterators.length && !_iterators[_current].hasNext())
    {
      _current++;
    }
    return _current<_iterators.length;
  }
  
  @Override
  public boolean hasNext()
  {
    return findNext();
  }

  @Override
  public K next()
  {
    findNext();
    _currentConsumed=true;
    return _iterators[_current].next();
  }

  @Override
  public void remove()
  {
    _iterators[_current].remove();
  }

}
