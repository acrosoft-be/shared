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
