/*
 * Created on 18-Jan-2003 at 12:20:26.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import de.enough.polish.util.TextUtil;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * <p>Tests the BooleanReader class</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        18-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class BooleanEvaluatorTest extends TestCase {


	/** 
	 * Creates a new test
	 * @param name the name
	 */
	public BooleanEvaluatorTest(String name) {
		super(name);
	}
	
	public void testPattern() {
		Pattern pattern = BooleanEvaluator.TERM_PATTERN;
		Matcher matcher = pattern.matcher("symbol1 && ( !symb2 || ( symb3 ^ ! symb4 ) )");
		assertTrue( matcher.find() );
		assertEquals( "( symb3 ^ ! symb4 )", matcher.group());
		assertFalse( matcher.find() );
		matcher = pattern.matcher("symbol1 && ( !symb2 || ( symb3 && ! symb4 ) )");
		assertTrue( matcher.find() );
		assertEquals( "( symb3 && ! symb4 )", matcher.group());
		assertFalse( matcher.find() );
		matcher = pattern.matcher("symbol1 && ( !symb2 || ( symb3 || ! symb4 ) )");
		assertTrue( matcher.find() );
		assertEquals( "( symb3 || ! symb4 )", matcher.group());
		assertFalse( matcher.find() );
		matcher = pattern.matcher("symbol1 && ( !symb2 || ( symb3 || ! symb4 ) && !(symb5 ^ symb6 ) )");
		assertTrue( matcher.find() );
		assertEquals( "( symb3 || ! symb4 )", matcher.group());
		assertTrue( matcher.find() );
		assertEquals( "(symb5 ^ symb6 )", matcher.group());
	}
	
	public void testMergingBlocks(){
		Pattern pattern = BooleanEvaluator.TERM_PATTERN;
		String expression = "symbol1 && ( !symb2 || ( symb3 || ! symb4 ) && !(symb5 ^ symb6 ) )";
		Matcher matcher = pattern.matcher( expression );
		assertTrue( matcher.find() );
		assertEquals( "( symb3 || ! symb4 )", matcher.group());
		expression = TextUtil.replaceFirst( expression, "( symb3 || ! symb4 )", "true" );
		assertTrue( matcher.find() );
		assertEquals( "(symb5 ^ symb6 )", matcher.group());
		expression = TextUtil.replaceFirst( expression, "(symb5 ^ symb6 )", "true" );
		assertFalse( matcher.find() );
		
		// go to the next level:
		matcher = pattern.matcher( expression );
		assertTrue( matcher.find() );
		assertEquals( "( !symb2 || true && !true )", matcher.group() );
		assertFalse( matcher.find() );
		expression = TextUtil.replaceFirst( expression, "( !symb2 || true && !true )", "true" );
		
		// last level:
		assertEquals("symbol1 && true", expression );
	}
	
	public void testOperatorPattern() {
		String term = " a && b || c ^ d || e";
		Matcher matcher = BooleanEvaluator.OPERATOR_PATTERN.matcher( term );
		assertTrue( matcher.find() );
		assertEquals( "&&", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "||", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "^", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "||", matcher.group() );
		assertFalse( matcher.find() );
	}
	
	public void testSymbolPattern() {
		String term = " test1 && test2 || test3 ^ test4 || test5";
		Matcher matcher = BooleanEvaluator.SYMBOL_PATTERN.matcher( term );
		assertTrue( matcher.find() );
		assertEquals( "test1", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test2", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test3", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test4", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test5", matcher.group() );
		assertFalse( matcher.find() );
		
		term = " test-1 && test-2 || test-3 ^ test-4 || test-5";
		matcher = BooleanEvaluator.SYMBOL_PATTERN.matcher( term );
		assertTrue( matcher.find() );
		assertEquals( "test-1", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test-2", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test-3", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test-4", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test-5", matcher.group() );
		assertFalse( matcher.find() );

		term = " test-1:defined && test-2:defined || test-3:defined ^ test-4:defined || test-5:defined";
		matcher = BooleanEvaluator.SYMBOL_PATTERN.matcher( term );
		assertTrue( matcher.find() );
		assertEquals( "test-1:defined", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test-2:defined", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test-3:defined", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test-4:defined", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test-5:defined", matcher.group() );
		assertFalse( matcher.find() );

		term = " test.1:defined && test.2:defined || test.3:defined ^ test.4:defined || test.5:defined";
		matcher = BooleanEvaluator.SYMBOL_PATTERN.matcher( term );
		assertTrue( matcher.find() );
		assertEquals( "test.1:defined", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test.2:defined", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test.3:defined", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test.4:defined", matcher.group() );
		assertTrue( matcher.find() );
		assertEquals( "test.5:defined", matcher.group() );
		assertFalse( matcher.find() );
	}
	
	public void testEvaluateTerm() throws PreprocessException {
		HashMap symbols = new HashMap();
		symbols.put( "test1", Boolean.TRUE );
		symbols.put( "test2", Boolean.TRUE );
		symbols.put( "sym-1", Boolean.TRUE );
		symbols.put( "sym-2", Boolean.TRUE );
		symbols.put( "var-1:defined", Boolean.TRUE );
		symbols.put( "var-2:defined", Boolean.TRUE );
		symbols.put( "polish.midp1", Boolean.TRUE );
		symbols.put( "polish.midp2", Boolean.TRUE );
		
		BooleanEvaluator evaluator = new BooleanEvaluator( symbols );
		String term = "test1 ||  test2";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "test1 &&  test2";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "test1 ^  test2";
		assertFalse( evaluator.evaluateTerm( term, "MyClass", 12) );
		
		term = "test1 ||  test2 || test3";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "test1 &&  test2 || test3";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "test1 ^  test2  || test3";
		assertFalse( evaluator.evaluateTerm( term, "MyClass", 12) );
		
		term = "sym-1 ||  sym-2";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "sym-1 &&  sym-2";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "sym-1 ^  sym-2";
		assertFalse( evaluator.evaluateTerm( term, "MyClass", 12) );
		
		term = "var-1:defined ||  var-2:defined";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "var-1:defined &&  var-2:defined";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "var-1:defined ^  var-2:defined";
		assertFalse( evaluator.evaluateTerm( term, "MyClass", 12) );
		
		term = "polish.midp1 ||  polish.midp2";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "polish.midp1 &&  polish.midp2";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "polish.midp1 ^  polish.midp2";
		assertFalse( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "polish.midp1";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		
		term = "true ||  true";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "false ||  true";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "true ||  false";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "false ||  false";
		assertFalse( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "! false ||  false";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "false || ! false";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "!false || !false";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "true &&  true";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "true &&  false";
		assertFalse( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "false &&  true";
		assertFalse( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "true ^  true";
		assertFalse( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "true ^  false";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "false ^  true";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "false ^  !false";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "false ^  false";
		assertFalse( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "false ^  !false || false && true";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		term = "false ^  !false || !false  &&  !  false";
		assertTrue( evaluator.evaluateTerm( term, "MyClass", 12) );
		
		
		// test invalid terms:
		term = "test1 test2";
		try {
			evaluator.evaluateTerm( term, "MyClass", 12 );
			fail( "evaluation of term [" + term + "] should fail.");
		} catch (PreprocessException e) {
			// expected behaviour!
		}
		
		term = "test1 && test2 ||";
		try {
			evaluator.evaluateTerm( term, "MyClass", 12 );
			fail( "evaluation of term [" + term + "] should fail.");
		} catch (PreprocessException e) {
			// expected behaviour!
		}
		
		term = "&& test1 && test2";
		try {
			evaluator.evaluateTerm( term, "MyClass", 12 );
			fail( "evaluation of term [" + term + "] should fail.");
		} catch (PreprocessException e) {
			// expected behaviour!
		}
		
		term = "test1 && || test2";
		try {
			evaluator.evaluateTerm( term, "MyClass", 12 );
			fail( "evaluation of term [" + term + "] should fail.");
		} catch (PreprocessException e) {
			// expected behaviour!
		}
		
		term = "test1 && (test2 || test3";
		try {
			evaluator.evaluateTerm( term, "MyClass", 12 );
			fail( "evaluation of term [" + term + "] should fail.");
		} catch (PreprocessException e) {
			// expected behaviour!
		}
		
		term = "test1 && test2 !) || test3";
		try {
			evaluator.evaluateTerm( term, "MyClass", 12 );
			fail( "evaluation of term [" + term + "] should fail.");
		} catch (PreprocessException e) {
			// expected behaviour!
		}

		term = "test1 && test2 ! || test3";
		try {
			evaluator.evaluateTerm( term, "MyClass", 12 );
			fail( "evaluation of term [" + term + "] should fail.");
		} catch (PreprocessException e) {
			// expected behaviour!
		}
		
		term = "test1 &! test2 || test3";
		try {
			evaluator.evaluateTerm( term, "MyClass", 12 );
			fail( "evaluation of term [" + term + "] should fail.");
		} catch (PreprocessException e) {
			// expected behaviour!
		}
	}
	
	public void testEvaluateExpression() throws PreprocessException {
		HashMap symbols = new HashMap();
		symbols.put( "test1", Boolean.TRUE );
		symbols.put( "test2", Boolean.TRUE );
		symbols.put( "sym-1", Boolean.TRUE );
		symbols.put( "sym-2", Boolean.TRUE );
		symbols.put( "var-1:defined", Boolean.TRUE );
		symbols.put( "var-2:defined", Boolean.TRUE );
		
		BooleanEvaluator evaluator = new BooleanEvaluator( symbols );
		String expression = "test1 && test2";
		assertTrue( evaluator.evaluate(expression, "MyClass", 103 ));
		expression = "test1 && ! test2";
		assertFalse( evaluator.evaluate(expression, "MyClass", 103 ));
		expression = "test1 && (test2 || test3)";
		assertTrue( evaluator.evaluate(expression, "MyClass", 103 ));
		expression = "test1 && (test2 && !test3)";
		assertTrue( evaluator.evaluate(expression, "MyClass", 103 ));
		expression = "test1 && (test2 ^ test3)";
		assertTrue( evaluator.evaluate(expression, "MyClass", 103 ));
		expression = "test1 && (test2 && test3)";
		assertFalse( evaluator.evaluate(expression, "MyClass", 103 ));
		expression = "test1 && (test2 ^ ! test3)";
		assertFalse( evaluator.evaluate(expression, "MyClass", 103 ));

		expression = "true && ((true ^ ! true) || false)";
		assertTrue( evaluator.evaluate(expression, "MyClass", 103 ));
		expression = "true && ((true ^ ! true) || false) && (!true ^ false)";
		assertFalse( evaluator.evaluate(expression, "MyClass", 103 ));
		expression = "(true && ((true ^ ! true) || false) && (!true ^ true)) ";
		assertTrue( evaluator.evaluate(expression, "MyClass", 103 ));
		expression = "true";
		assertTrue( evaluator.evaluate(expression, "MyClass", 103 ));
		expression = "false";
		assertFalse( evaluator.evaluate(expression, "MyClass", 103 ));
		
	}

}
