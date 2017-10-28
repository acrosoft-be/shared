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
package be.acrosoft.gaia.shared.util;

import java.io.Serializable;

/**
 * Pair.
 * @param <A> a type.
 * @param <B> b type.
 */
public class Pair<A,B> implements Serializable
{
  private static final long serialVersionUID=1L;
  /**
   * A.
   */
  public A a;
  /**
   * B.
   */
  public B b;
  
  /**
   * Create a new Pair.
   * @param aa a.
   * @param ab b.
   */
  public Pair(A aa,B ab)
  {
    a=aa;
    b=ab;
  }
  
  private boolean equals(Object x,Object y)
  {
    if(x==null && y==null) return true;
    if(x==null || y==null) return false;
    return x.equals(y);
  }
  
  @Override
  public boolean equals(Object o)
  {
    if(o==null) return false;
    if(!(o instanceof Pair)) return false;
    Pair<?,?> op=(Pair<?,?>)o;
    return equals(a,op.a)&&equals(b,op.b);
  }
  
  @Override
  public int hashCode()
  {
    if(a==null && b==null) return 0;
    if(a==null) return b.hashCode();
    if(b==null) return a.hashCode();
    return a.hashCode()^b.hashCode();
  }
  
  @SuppressWarnings("nls")
  @Override
  public String toString()
  {
    String s1=a==null?"null":a.toString();
    String s2=b==null?"null":b.toString();
    return "Pair<"+s1+","+s2+">";
  }
  
  /**
   * Create a pair.
   * @param <A>
   * @param <B>
   * @param a
   * @param b
   * @return pair
   */
  public static <A,B> Pair<A,B> pair(A a,B b)
  {
    return new Pair<A,B>(a,b);
  }

}
