/*
 * Created on 18-Jan-2004 at 10:14:15.
 *
 * Copyright (c) 2004 Robert Virkus / enough software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * www.enough.de/j2mepolish for details.
 */
package de.enough.polish.preprocess;

import de.enough.polish.util.CastUtil;
import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;

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
	public static final int GREATER = 5;
	public static final int LESSER = 6;
	public static final int EQUALS = 7;
	public static final int GREATER_EQUALS = 8;
	public static final int LESSER_EQUALS = 9;

	private static final String SYMBOL = "(\\w|-|:|\\.)+"; 
	protected static final Pattern SYMBOL_PATTERN = Pattern.compile( SYMBOL ); 
	private static final String OPERATOR = "(&&|\\^|\\|\\||==|>=|<=|>|<)"; 
	protected static final Pattern OPERATOR_PATTERN = Pattern.compile( OPERATOR ); 
	private static final String TERM = "\\(\\s*!?\\s*" + SYMBOL + "\\s*(" + OPERATOR 
								       + "\\s*!?\\s*" + SYMBOL + "\\s*)+\\)";
	protected static final Pattern TERM_PATTERN = Pattern.compile( TERM );

	private HashMap symbols;
	private HashMap variables;

	/**
	 * Creates a new boolean evaluator.
	 * 
	 * @param symbols a map containing all defined symbols
	 * @param variables a map containing all defined variables
	 * @throws NullPointerException when symbols or variables are null
	 */ 
	public BooleanEvaluator( HashMap symbols, HashMap variables ) {
		setEnvironment(symbols, variables);
	}

	/**
	 * Sets the environment for this evaluator.
	 * 
	 * @param symbols a map containing all defined symbols
	 * @param variables a map containing all defined variables
	 * @throws NullPointerException when symbols or variables are null
	 */
	public void setEnvironment( HashMap symbols, HashMap variables ) {
		if (symbols == null) {
			throw new NullPointerException("Got invalid symbols: [null].");
		} 
		if ( variables == null) {
			throw new NullPointerException("Got invalid variables: [null].");
		}
		this.symbols = symbols;
		this.variables = variables;
	}
	
	/**
	 * Evaluates the given expression.
	 * 
	 * @param expression the expression containing defined (or undefined) symbols and the operators &&, ||, ! and ^.
	 *              A valid expression is for example "( symbol1 ||symbol2 ) && !symbol3" 
	 * @param fileName the name of the source code file
	 * @param line the line number in the source code file (first line is 1)
	 * @return true when the expression yields to true
	 * @throws BuildException when there is a syntax error in the expression
	 */
	public boolean evaluate( String expression, String fileName, int line ) 
	throws BuildException 
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
	 * @throws BuildException when there is a paranthesis in the term
	 */
	protected boolean evaluateTerm(String term, String fileName, int line)
	throws BuildException
	{
		// check for parenthesisses:
		if (term.indexOf('(') != -1) {
			throw new BuildException(fileName + " line " + line 
					+ ": invalid/additional parenthesis \"(\" in term [" + term 
					+ "] (the term might be simplified)." ); 
		}
		if (term.indexOf(')') != -1) {
			throw new BuildException(fileName + " line " + line 
					+ ": invalid/additional parenthesis \")\" in term [" + term 
					+ "] (the term might be simplified)." ); 
		}
		Matcher symbolMatcher = SYMBOL_PATTERN.matcher( term );
		Matcher operatorMatcher = OPERATOR_PATTERN.matcher( term );
		int lastSymbolEnd = 0;
		boolean result = true;
		int operator = NONE;
		String symbol = null;
		String lastSymbol = null; // is needed for >, <, ==, <= and >=
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
			if (operator == NONE) {
				result = symbolResult;
			} else if (operator == AND) {
				result &= symbolResult;
			} else if (operator ==  OR) {
				result |= symbolResult;
			} else if (operator == XOR) {
				result ^= symbolResult; 
			} else if (operator == INVALID) {
				throw new BuildException(fileName + " line " + line 
						+ ": found no operator before symbol [" + symbol + "] in term [" + term 
						+ "] (both symbol and term might be simplified)." );
			} else {
				//System.out.println("comparing [" + lastSymbol + "] with [" + symbol + "].");
				// this is either >, <, ==, >= or <=
				String var  = (String) this.variables.get( symbol );
				if (var == null) {
					var = symbol;
				}
				String lastVar = (String) this.variables.get( lastSymbol );
				if (lastVar == null) {
					lastVar = lastSymbol;
				}
				if ( operator == EQUALS ) {
					result = var.equals( lastVar );
					//System.out.println( var + " == " + lastVar + " = " + result);
				} else {
					// this is either >, <, >= or <= - so a numerical comparison is required
					int numVar = 0;
					int numLastVar = 0;
					try {
						numVar = CastUtil.getInt( var );
						numLastVar = CastUtil.getInt( lastVar );
					} catch (Exception e) {
						throw new BuildException(fileName + " line " + line 
								+ ": unable to parse integer-arguments [" + symbol + "] or [" 
								+ lastSymbol + "] in term [" + term 
								+ "] (both symbols and term might be simplified)." );
					}
					if (operator == GREATER ) {
						result = numLastVar > numVar;
						//System.out.println( numLastVar + " > " + numVar + " = " + result );
					} else if (operator == LESSER) {
						result = numLastVar < numVar;
						//System.out.println( numLastVar + " < " + numVar + " = " + result );
					} else if (operator == GREATER_EQUALS) {
						result = numLastVar >= numVar;
						//System.out.println( numLastVar + " >= " + numVar + " = " + result );
					} else if (operator == LESSER_EQUALS) {
						result = numLastVar <= numVar;
						//System.out.println( numLastVar + " <= " + numVar + " = " + result );
					} else {
						throw new BuildException(fileName + " line " + line 
								+ ": unknown operator in term [" + term + "]. The term might be simplified." ); 
					}
					//System.out.println("result: " + result);
				}
			}
			
			// evaluate next operator:
			if (operatorMatcher.find()) {
				// check if operator is in the correct position:
				int operatorPos = operatorMatcher.start();
				if (operatorPos < lastSymbolEnd ) {
					throw new BuildException( fileName + " line " + line  
							+": found invalid/additional operator in [" + term +"] (that term might be simplified)." );
					
				}
				String empty = term.substring( lastSymbolEnd + 1, operatorPos ).trim();
				if (empty.length() > 0) {
					throw new BuildException( fileName + " line " + line  
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
				} else if (">".equals( operatorSymbol)) {
					operator = GREATER;
				} else if ("<".equals( operatorSymbol)) {
					operator = LESSER;
				} else if ("==".equals( operatorSymbol)) {
					operator = EQUALS;
				} else if (">=".equals( operatorSymbol)) {
					operator = GREATER_EQUALS;
				} else if ("<=".equals( operatorSymbol)) {
					operator = LESSER_EQUALS;
				}
			} else { // no more operator found:
				operator = INVALID;
			}
			lastSymbol = symbol;
			//System.out.println("operator == " + operator);
		} // while there are more symbols
		// check if there are any more operators after the last symbol:
		if (operator != INVALID){
			throw new BuildException( fileName + " line " + line  
					+ ": found invalid/additional operator [" + operatorMatcher.group() 
					+ "] after symbol [" + symbol + "] in term [" + term 
					+ "] (both symbol and term might be simplified)." ); 
		}
		return result;
	}
	
}
