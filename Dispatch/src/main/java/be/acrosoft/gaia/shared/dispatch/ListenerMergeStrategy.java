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
package be.acrosoft.gaia.shared.dispatch;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import be.acrosoft.gaia.shared.dispatch.ListenerMergeStrategy.MergeAction.Condition;
import be.acrosoft.gaia.shared.dispatch.ListenerMergeStrategy.MergeAction.GroupSelector;
import be.acrosoft.gaia.shared.dispatch.ListenerMergeStrategy.MergeAction.Operator;
import be.acrosoft.gaia.shared.dispatch.ListenerMergeStrategy.SingleParameterClassCondition.SingleParameterSelector.SingleParameterGroupSelector;
import be.acrosoft.gaia.shared.util.Pair;

/**
 * A listener merge strategy allows to merge events targeted at the same listener and having similar parameters.
 * The typical use case is an observer of a collection of observable objects that will frequently fire events
 * at the same time. When this happens, the observer would typically prefer to receive one single event reporting
 * about a collection of objects instead of receiving many individual events.
 * <p/>
 * A ListenerMergeStrategy is assigned to a Listener group, as created by {@link Listener#groupOf(Class, ListenerMergeStrategy)}.
 * For the strategy to work as expected, all the observable objects should share the same instance of the strategy.
 * Therefore a strategy is typically created as a static field of the observable class.
 * <p/>
 * The ListenerMergeStrategy works as follows.
 * <p/>
 * Firstly, the events are grouped by listener, as only events targeting common listeners will be merged.<br/>
 * Secondly, the events are further grouped using a configurable group selector based on the event parameters.<br/>
 * Finally, the events that are sharing common listener and common group are merged using a configurable merge logic.<br/>
 * <p/>
 * Obviously, only the events that are currently queued for dispatching and that have not been sent to the listener
 * yet can be successfully merged.
 * <p/>
 * @param <K> listener type.
 */
public class ListenerMergeStrategy<K extends Listener>
{
  /**
   * A MergeAction is an individual merging action (group selector and merge logic) that is triggered upon a configurable
   * condition. A ListenerMergeStrategy may have several of these actions, picking the appropriate one based on the
   * condition. Typically, however, a ListenerMergeStrategy will only have one configured action.
   */
  public static class MergeAction
  {
    /**
     * Extract the group from the array of event parameters.
     */
    public static interface GroupSelector extends Function<Object[],Object> {}
    
    /**
     * The generic merge operator logic, that takes two arrays of parameters and that combines them together.
     * Note that the returned array must have the same size and types as the input arrays. It is acceptable to
     * return a completely new array, or to simply return one of the two arrays after having merely modified them or
     * their content.
     */
    public static interface Operator extends BinaryOperator<Object[]> {}
    
    /**
     * Check whether the given method and its parameters matches the merge condition.
     */
    public static interface Condition extends BiFunction<Method,Object[],Boolean> {}

    private Condition condition;
    private GroupSelector groupSelector;
    private Operator mergeOperator;
    
    private MergeAction(Condition actionCondition,GroupSelector selector,Operator operator)
    {
      condition=actionCondition;
      groupSelector=selector;
      mergeOperator=operator;
    }
    
    /**
     * Create a new ListenerMergeStrategy with this MergeAction as single element.
     * @return new ListenerMergeStrategy.
     */
    public <K extends Listener> ListenerMergeStrategy<K> toStrategy()
    {
      return new ListenerMergeStrategy<K>(Collections.singletonList(this));
    }
    
  }

  /**
   * Utility class to allow simple strategy definition based on event parameter class, assuming the event
   * takes only one parameter.
   * <p/>
   * The intended use is ListenerMergeStrategy.whenParameter(EventParameter.class).mergeBy(e->e.someField).using((e1,e2)->new EventParameter(e1.otherField+e2.otherField)
   * @param <P> Single parameter type.
   */
  public static class SingleParameterClassCondition<P>
  {
    /**
     * Merge the two parameter values, returning the merged value. Note that a completely new parameter may be
     * returned, or either of the two input parameters after having modified it.
     * @param <P> Single parameter type.
     */
    public static interface SingleMergeOperator<P> extends BinaryOperator<P> {}
    
    /**
     * SingleParameterSelector
     * @param <P> Single parameter type.
     */
    public static class SingleParameterSelector<P>
    {
      /**
       * Select the group for the given parameter.
       * @param <P> Single parameter type.
       */
      public static interface SingleParameterGroupSelector<P> extends Function<P,Object> {}
      
      private Class<P> _class;
      private SingleParameterGroupSelector<P> _selector;
      
      private SingleParameterSelector(Class<P> clazz,SingleParameterGroupSelector<P> selector)
      {
        _class=clazz;
        _selector=selector;
      }
      
      /**
       * Finalize the merge action by specifying the merge operator.
       * @param singleOperator operator.
       * @return MergeAction. Call {@link be.acrosoft.gaia.shared.dispatch.ListenerMergeStrategy.MergeAction#toStrategy()}.
       */
      public MergeAction using(SingleMergeOperator<P> singleOperator)
      {
        Condition condition=(m,params)->
        {
          if(params.length!=1) return false;
          Object param=params[0];
          if(param==null) return false;
          return param.getClass().equals(_class);
        };
        
        GroupSelector selector=params->
        {
          P param=(P)params[0];
          return _selector.apply(param);
        };
        
        Operator operator=(p1,p2)->
        {
          P param1=(P)p1[0];
          P param2=(P)p2[0];
          P result=singleOperator.apply(param1,param2);
          if(result==null) return null;
          if(result==param1) return p1;
          if(result==param2) return p2;
          return new Object[] {result};
        };
        
        return new MergeAction(condition,selector,operator);
      }
    }
    
    private Class<P> _class;
    
    private SingleParameterClassCondition(Class<P> clazz)
    {
      _class=clazz;
    }
    
    /**
     * Given one event taking a single parameter p of type P. Return a value that can be used to decide whether this
     * event should be considered for merging with other events of same type. The {@link #equals(Object)} and
     * {@link #hashCode()} on the returned object to decide of equality. 
     * @param selector selector function.
     * @return SingleParameterSelector. Call {@link ListenerMergeStrategy.SingleParameterClassCondition.SingleParameterSelector#using(SingleMergeOperator)} method on it.
     */
    public SingleParameterSelector<P> mergeBy(SingleParameterGroupSelector<P> selector)
    {
      return new SingleParameterSelector(_class,selector);
    }
    
  }
  
  private Map<Pair<K,Object>,Set<QueuedEventItem<K>>> _queuedItems;
  private List<MergeAction> _actions;
  private static final Object REF=new Object();

  /**
   * Create a new ListenerMergeStrategy with the list of given actions.
   * @param actions list of merge actions.
   */
  public ListenerMergeStrategy(List<MergeAction> actions)
  {
    _queuedItems=null;
    _actions=actions;
  }
  
  /**
   * Prepare the creation of a new merge action that should take place when an event has one single
   * parameter of the given class.
   * @param clazz class of the parameter.
   * @return ParameterClassSelector. Call {@link ListenerMergeStrategy.SingleParameterClassCondition#mergeBy(SingleParameterGroupSelector)} on it.
   */
  public static <P> SingleParameterClassCondition<P> whenParameter(Class<P> clazz)
  {
    return new SingleParameterClassCondition<P>(clazz);
  }
  
  private MergeAction mergeAction(Method m,Object[] params)
  {
    for(MergeAction action:_actions)
    {
      if(action.condition.apply(m,params)) return action;
    }
    return null;
  }
  
  //private static int _total = 0;
  //private static int _saved = 0;
  
  /**
   * Try to merge the given event with any already-queued event in this merge strategy.
   * @param listener listener.
   * @param m method to call.
   * @param params event parameters.
   * @return true if merge was performed, false otherwise.
   */
  public synchronized boolean tryMerge(K listener,Method m,Object[] params)
  {
    //if(_total>0)
    //  System.out.println("total="+_total+" saved="+_saved+" ("+100*_saved/_total+"%)");
    //_total++;
    
    MergeAction mergeAction=mergeAction(m,params);
    if(mergeAction==null) return false;
    Pair<K,Object> mergeCriteria=Pair.pair(listener,mergeAction.groupSelector.apply(params));
    
    if(_queuedItems==null)
      _queuedItems=new HashMap<>(5);

    Set<QueuedEventItem<K>> compatibleSet=_queuedItems.get(mergeCriteria);
    if(compatibleSet==null) return false;
    
    if(compatibleSet.size()>100) return false;
    
    for(QueuedEventItem<K> qitem:compatibleSet)
    {
      if(qitem.tryMerge(mergeAction.mergeOperator,listener,m,params))
      {
        //_saved++;
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Enqueue the given event.
   * @param item item.
   * @return Object queuing reference that must be passed to dequeue..
   */
  public synchronized Object enqueue(QueuedEventItem<K> item)
  {
    MergeAction mergeAction=mergeAction(item.getMethod(),item.getParameters());
    if(mergeAction==null) return null;
    Pair<K,Object> mergeCriteria=Pair.pair(item.getListener(),mergeAction.groupSelector.apply(item.getParameters()));

    if(_queuedItems==null)
      _queuedItems=new HashMap<>(5);

    Set<QueuedEventItem<K>> compatibleSet=_queuedItems.get(mergeCriteria);
    if(compatibleSet==null)
    {
      compatibleSet=new HashSet<QueuedEventItem<K>>();
      _queuedItems.put(mergeCriteria,compatibleSet);
    }
    if(compatibleSet.size()>100) return null;
    
    compatibleSet.add(item);
    
    return REF;
  }
  
  /**
   * Dequeue the given event.
   * @param item item.
   * @param reference queuing reference, as returned by enqueue.
   */
  public synchronized void dequeue(QueuedEventItem<K> item,Object reference)
  {
    if(reference==null) return;
    
    MergeAction mergeAction=mergeAction(item.getMethod(),item.getParameters());
    if(mergeAction==null) return;
    Pair<K,Object> mergeCriteria=Pair.pair(item.getListener(),mergeAction.groupSelector.apply(item.getParameters()));

    if(_queuedItems==null)
      _queuedItems=new HashMap<>(5);
    
    Set<QueuedEventItem<K>> compatibleSet=_queuedItems.get(mergeCriteria);
    if(compatibleSet==null) return;
    compatibleSet.remove(item);
    if(compatibleSet.size()==0)
      _queuedItems.remove(mergeCriteria);
  }
}
