/*
 * Created on 18-Jan-2003 at 10:14:15.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import de.enough.polish.util.TextUtil;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Evaluates boolean expressions based on defined (or undefined) symbols and the operators &&, ||, ! and ^.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        18-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class BooleanEvaluator {
	
	public static final int INVALID = -1;
	public static final int NONE = 0;
	public static final int NOT = 1;
	public static final int AND = 2;
	public static final int OR = 3;
	public static final int XOR = 4;

	protected static final Pattern SYMBOL_PATTERN = 
		Pattern.compile("(\\w|-|:)+"); 
	protected static final Pattern OPERATOR_PATTERN = 
		Pattern.compile("(&&|\\^|\\|\\|)"); 
	protected static final Pattern TERM_PATTERN = 
		Pattern.compile("\\(\\s*!?\\s*(\\w|-|:)+\\s*((&&|\\|\\||\\^)\\s*!?\\s*(\\w|-|:)+\\s*)+\\)"); 

	private HashMap symbols;

	/**
	 * Creates a new boolean evaluator.
	 * 
	 * @param symbols a map containing all defined symbols
	 */ 
	public BooleanEvaluator( HashMap symbols ) {
		this.symbols = symbols;
	}
	
	/**
	 * Evaluates the given expression.
	 * 
	 * @param expression the expression containing defined (or undefined) symbols and the operators &&, ||, ! and ^.
	 *              A valid expression is for example "( symbol1 ||symbol2 ) && !symbol3" 
	 * @param fileName the name of the source code file
	 * @param line the line number in the source code file (first line is 1)
	 * @return true when the expression yields to true
	 * @throws PreprocessException when there is a syntax error in the expression
	 */
	public boolean evaluate( String expression, String fileName, int line ) 
	throws PreprocessException 
	{
		// main loop: evaluate all simple expressions (without parenthesisses)
		Matcher matcher = TERM_PATTERN.matcher( expression );
		boolean foundParenthesis = matcher.find(); 
		while ( foundParenthesis ) {
			String group = matcher.group();
			String term = group.substring( 1, group.length() -1 ); // the term has no parenthesis
			boolean result = evaluateTerm( term, fileName, line );
			expression = TextUtil.replaceFirst( expression, group, "" + result );
			
			// find next "(...)" term:
			foundParenthesis = matcher.find();
			if (!foundParenthesis) {
				matcher = TERM_PATTERN.matcher( expression );
				foundParenthesis = matcher.find();
			}
		}
		// now the expression is simplified to a term without parenthesis:
		return evaluateTerm( expression, fileName, line );
	}

	/**
	 * Evaluates the given simple term.
	 * @param term the simple term without any paranthesis, e.g. "symbol1 && ! symbol2"
	 * @param fileName the name of the source file
	 * @param line the line number of this term
	 * @return true when the term represents true
	 * @throws PreprocessException when there is a paranthesis in the term
	 */
	protected boolean evaluateTerm(String term, String fileName, int line)
	throws PreprocessException
	{
		// check for parenthesisses:
		if (term.indexOf('(') != -1) {
			throw new PreprocessException(fileName + " line " + line 
					+ ": invalid/additional parenthesis \"(\" in term [" + term 
					+ "] (the term might be simplified)." ); 
		}
		if (term.indexOf(')') != -1) {
			throw new PreprocessException(fileName + " line " + line 
					+ ": invalid/additional parenthesis \")\" in term [" + term 
					+ "] (the term might be simplified)." ); 
		}
		Matcher symbolMatcher = SYMBOL_PATTERN.matcher( term );
		Matcher operatorMatcher = OPERATOR_PATTERN.matcher( term );
		int lastSymbolEnd = 0;
		boolean result = true;
		int operator = NONE;
		String symbol = null;
		while (symbolMatcher.find()) {
			// evaluate symbol:
			symbol = symbolMatcher.group();
			int negatePos = term.indexOf( '!', lastSymbolEnd );
			boolean negate = ( (negatePos != -1) && (negatePos < symbolMatcher.start()) );
			lastSymbolEnd = symbolMatcher.end();
			boolean symbolResult = false;
			if ("true".equals( symbol )) {
				symbolResult = true;
			} else if ("false".equals( symbol)) {
				symbolResult = false;
			} else {
				symbolResult = ( this.symbols.get( symbol ) != null );
			}
			if (negate) {
				symbolResult = !symbolResult;
			}
			// combine temporary result with main result:
			switch (operator) {
				case NONE: result = symbolResult; break;
				case AND: result &= symbolResult; break;
				case OR: result |= symbolResult; break;
				case XOR: result ^= symbolResult; break;
				case INVALID:
					throw new PreprocessException(fileName + " line " + line 
							+ ": found no operator before symbol [" + symbol + "] in term [" + term 
							+ "] (both symbol and term might be simplified)." ); 
			}
			
			// evaluate next operator:
			if (operatorMatcher.find()) {
				// check if operator is in the correct position:
				int operatorPos = operatorMatcher.start();
				if (operatorPos < lastSymbolEnd ) {
					throw new PreprocessException( fileName + " line " + line  
							+": found invalid/additional operator in [" + term +"] (that term might be simplified)." );
					
				}
				String empty = term.substring( lastSymbolEnd + 1, operatorPos ).trim();
				if (empty.length() > 0) {
					throw new PreprocessException( fileName + " line " + line  
							+": missing or invalid operator after [" + symbol +"] in term [" + term
							+"] (both symbol and term might be simplified)." );
				}
				// okay operator is in correct position, so know check what operator it is:
				String operatorSymbol = operatorMatcher.group();
				if ("&&".equals( operatorSymbol)) {
					operator = AND;
				} else if ("||".equals( operatorSymbol)) {
					operator = OR;
				} else if ("^".equals( operatorSymbol)) {
					operator = XOR;
				}
			} else { // no more operator found:
				operator = INVALID;
			}
		}
		// check if there are any more operators after the last symbol:
		if (operator != INVALID){
			throw new PreprocessException( fileName + " line " + line  
					+ ": found invalid/additional operator [" + operatorMatcher.group() 
					+ "] after symbol [" + symbol + "] in term [" + term 
					+ "] (both symbol and term might be simplified)." ); 
		}
		return result;
	}
	
}
