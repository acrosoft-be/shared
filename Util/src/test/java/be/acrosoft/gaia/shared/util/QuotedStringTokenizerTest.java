package be.acrosoft.gaia.shared.util;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

/**
 * QuotedStringTokenizerTest.
 */
@SuppressWarnings({"javadoc","nls"})
public class QuotedStringTokenizerTest
{
  @Test
  public void testEmpty()
  {
    assertEquals(0,QuotedStringTokenizer.parse("").length);
    assertEquals(0,QuotedStringTokenizer.parse("  ").length);
    assertEquals(0,QuotedStringTokenizer.parse("'").length);
    assertEquals(0,QuotedStringTokenizer.parse("\"").length);
    assertEquals(0,QuotedStringTokenizer.parse("''").length);
    assertEquals(0,QuotedStringTokenizer.parse("\"\"").length);
    assertEquals(0,QuotedStringTokenizer.parse(" '").length);
    assertEquals(0,QuotedStringTokenizer.parse(" '' ").length);
    assertEquals(0,QuotedStringTokenizer.parse("  \"\"  ").length);
  }
  
  @Test
  public void testOneLetter()
  {
    assertArrayEquals(new String[] {"a"},QuotedStringTokenizer.parse("a"));
    assertArrayEquals(new String[] {"a"},QuotedStringTokenizer.parse("a "));
    assertArrayEquals(new String[] {"a"},QuotedStringTokenizer.parse(" a "));
    assertArrayEquals(new String[] {"a"},QuotedStringTokenizer.parse("'a'"));
    assertArrayEquals(new String[] {"a"},QuotedStringTokenizer.parse(" 'a' "));
    assertArrayEquals(new String[] {"a"},QuotedStringTokenizer.parse(" \"a\" "));
    assertArrayEquals(new String[] {"a"},QuotedStringTokenizer.parse("'a"));
    assertArrayEquals(new String[] {"a"},QuotedStringTokenizer.parse("a'"));
    assertArrayEquals(new String[] {"a"},QuotedStringTokenizer.parse(" a'"));
    assertArrayEquals(new String[] {" "},QuotedStringTokenizer.parse("' '"));
    assertArrayEquals(new String[] {" "},QuotedStringTokenizer.parse("' "));
    assertArrayEquals(new String[] {" "},QuotedStringTokenizer.parse("' ''"));
  }
  
  @Test
  public void testOneWord()
  {
    assertArrayEquals(new String[] {"abc"},QuotedStringTokenizer.parse("abc"));
    assertArrayEquals(new String[] {"abc"},QuotedStringTokenizer.parse(" abc"));
    assertArrayEquals(new String[] {"abc"},QuotedStringTokenizer.parse("abc "));
    assertArrayEquals(new String[] {"abc"},QuotedStringTokenizer.parse("'abc'"));
    assertArrayEquals(new String[] {"abc"},QuotedStringTokenizer.parse(" 'abc' "));
    assertArrayEquals(new String[] {"abc"},QuotedStringTokenizer.parse(" 'abc"));
    assertArrayEquals(new String[] {"abc"},QuotedStringTokenizer.parse(" abc'"));
  }

  @Test
  public void testTwoWords()
  {
    assertArrayEquals(new String[] {"abc","d"},QuotedStringTokenizer.parse("abc d"));
    assertArrayEquals(new String[] {"abc","d"},QuotedStringTokenizer.parse("'abc' d"));
    assertArrayEquals(new String[] {"abc","d"},QuotedStringTokenizer.parse("'abc' \"d\""));
    assertArrayEquals(new String[] {"abc","d"},QuotedStringTokenizer.parse(" 'abc'    \"d\" "));
    assertArrayEquals(new String[] {"abc","d"},QuotedStringTokenizer.parse(" 'abc'    d"));
  }

  @Test
  public void testSepQuoting()
  {
    assertArrayEquals(new String[] {"abc d","e"},QuotedStringTokenizer.parse("'abc d' e"));
    assertArrayEquals(new String[] {"abc d","e"},QuotedStringTokenizer.parse("\"abc d\"    'e'"));
    assertArrayEquals(new String[] {" abc   d ","e"},QuotedStringTokenizer.parse(" \" abc   d \"    'e'"));
    assertArrayEquals(new String[] {"abc   de"},QuotedStringTokenizer.parse("abc'   'de"));
    assertArrayEquals(new String[] {"two","words","+allowed word","-forbidden word"},QuotedStringTokenizer.parse("two words +'allowed word' -'forbidden word'"));
  }
  
  @Test
  public void testQuoteQuoting()
  {
    assertArrayEquals(new String[] {"abc\"d","e"},QuotedStringTokenizer.parse("'abc\"d' e"));
    assertArrayEquals(new String[] {"abc'd","e'''"},QuotedStringTokenizer.parse("\"abc'd\" e\"'''\""));
  }
  
  @Test
  public void testRangeEmpty()
  {
    assertEquals(0,QuotedStringTokenizer.parseRanges("").size());
    assertEquals(0,QuotedStringTokenizer.parseRanges("  ").size());
  }
  
  @Test
  public void testRangeOneLetter()
  {
    List<Pair<Integer,Integer>> ranges;
    ranges=QuotedStringTokenizer.parseRanges("a");
    assertEquals(1,ranges.size());
    assertEquals(new Pair<Integer,Integer>(0,1),ranges.get(0));

    ranges=QuotedStringTokenizer.parseRanges("  a");
    assertEquals(1,ranges.size());
    assertEquals(new Pair<Integer,Integer>(2,1),ranges.get(0));

    ranges=QuotedStringTokenizer.parseRanges("a  ");
    assertEquals(1,ranges.size());
    assertEquals(new Pair<Integer,Integer>(0,1),ranges.get(0));

    ranges=QuotedStringTokenizer.parseRanges("  a  ");
    assertEquals(1,ranges.size());
    assertEquals(new Pair<Integer,Integer>(2,1),ranges.get(0));
  }
  
  @Test
  public void testRangeOneWord()
  {
    List<Pair<Integer,Integer>> ranges;
    ranges=QuotedStringTokenizer.parseRanges("abc");
    assertEquals(1,ranges.size());
    assertEquals(new Pair<Integer,Integer>(0,3),ranges.get(0));

    ranges=QuotedStringTokenizer.parseRanges("  abc");
    assertEquals(1,ranges.size());
    assertEquals(new Pair<Integer,Integer>(2,3),ranges.get(0));

    ranges=QuotedStringTokenizer.parseRanges("abc  ");
    assertEquals(1,ranges.size());
    assertEquals(new Pair<Integer,Integer>(0,3),ranges.get(0));

    ranges=QuotedStringTokenizer.parseRanges("  abc  ");
    assertEquals(1,ranges.size());
    assertEquals(new Pair<Integer,Integer>(2,3),ranges.get(0));
  }
  
  @Test
  public void testRangeTwoWords()
  {
    List<Pair<Integer,Integer>> ranges;
    ranges=QuotedStringTokenizer.parseRanges("ab c");
    assertEquals(2,ranges.size());
    assertEquals(new Pair<Integer,Integer>(0,2),ranges.get(0));
    assertEquals(new Pair<Integer,Integer>(3,1),ranges.get(1));

    ranges=QuotedStringTokenizer.parseRanges("  ab  c");
    assertEquals(2,ranges.size());
    assertEquals(new Pair<Integer,Integer>(2,2),ranges.get(0));
    assertEquals(new Pair<Integer,Integer>(6,1),ranges.get(1));

    ranges=QuotedStringTokenizer.parseRanges("ab  c  ");
    assertEquals(2,ranges.size());
    assertEquals(new Pair<Integer,Integer>(0,2),ranges.get(0));
    assertEquals(new Pair<Integer,Integer>(4,1),ranges.get(1));

    ranges=QuotedStringTokenizer.parseRanges("  ab  c  ");
    assertEquals(2,ranges.size());
    assertEquals(new Pair<Integer,Integer>(2,2),ranges.get(0));
    assertEquals(new Pair<Integer,Integer>(6,1),ranges.get(1));
  }
}
