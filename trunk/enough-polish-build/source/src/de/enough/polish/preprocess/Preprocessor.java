/*
 * Created on 16-Jan-2004 at 12:17:12.
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

import de.enough.polish.PolishProject;
import de.enough.polish.util.*;

import org.apache.tools.ant.BuildException;

import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Preprocesses source code.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        16-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class Preprocessor {
	
	private static final int DIRECTIVE_FOUND = 1;
	/**
	 * A value indicating that the file has been changed.
	 * CHANGED has the value 2. 
	 */
	public static final int CHANGED = 2;
	/**
	 * A value indicating that the file has not been changed.
	 * NOT_CHANGED has the value 4. 
	 */
	public static final int NOT_CHANGED = 4;
	/**
	 * A value indicating that the file should be skipped altogether.
	 * This can be when e.g. a MIDP2-emulating class should not be
	 * copied to a MIDP2-system. 
	 * An example are the de.enough.polish.ui.game-classes.
	 * 
	 * SKIP_FILE has the value 8. 
	 */
	public static final int SKIP_FILE = 8;
	
	public static final Pattern DIRECTIVE_PATTERN = 
		Pattern.compile("\\s*(//#if\\s+|//#ifdef\\s+|//#ifndef\\s+|//#elif\\s+|//#elifdef\\s+|//#elifndef\\s+|//#else|//#endif|//#include\\s+|//#endinclude|//#style |//#debug|//#mdebug|//#enddebug|//#define\\s+|//#undefine\\s+|//#=\\s+|//#condition\\s+)");

	private DebugManager debugManager;
	private File destinationDir;
	/** holds all defined variables */
	private HashMap variables;
	/** holds all defined symbols */
	HashMap symbols;
	private boolean verbose;
	private boolean backup;
	private boolean indent;
	boolean enableDebug;
	private String newExtension;
	private HashMap withinIfDirectives;
	private HashMap ignoreDirectives;
	private HashMap supportedDirectives;
	private int ifDirectiveCount;
	private BooleanEvaluator booleanEvaluator;
	private StyleSheet styleSheet;
	private boolean usePolishGui;

	/**
	 * Creates a new Preprocessor - usually for a specific device or a device group.
	 * 
	 * @param project the project settings
	 * @param destinationDir the destination directory for the preprocessed files
	 * @param variables the defined variables
	 * @param symbols the defined symbols
	 * @param verbose true when lots of information should be printed 
	 * @param backup true when the found source files should be backuped
	 * @param indent true when comments should be intended
	 * @param newExt the new extension for preprocessed files
	 */
	public Preprocessor(
			PolishProject project,
			File destinationDir,
			HashMap variables,
			HashMap symbols,
			boolean verbose,
			boolean backup,
			boolean indent,
			String newExt) 
	{
		this.debugManager = project.getDebugManager();
		this.enableDebug = project.isDebugEnabled();
		this.usePolishGui = project.usesPolishGui();
		if (variables == null) {
			variables = new HashMap();
		}
		this.variables = variables;
		if (symbols == null) {
			symbols = new HashMap();
		}
		this.symbols = symbols;
		this.verbose = verbose;
		this.backup = backup;
		this.indent = indent;
		this.newExtension = newExt;
		this.booleanEvaluator = new BooleanEvaluator( symbols, variables );
		this.destinationDir = destinationDir;
		
		this.withinIfDirectives = new HashMap();
		this.withinIfDirectives.put( "elifdef", Boolean.TRUE );
		this.withinIfDirectives.put( "elifndef", Boolean.TRUE );
		this.withinIfDirectives.put( "else", Boolean.TRUE );
		this.withinIfDirectives.put( "elif", Boolean.TRUE );
		this.withinIfDirectives.put( "endinclude", Boolean.TRUE );
		this.withinIfDirectives.put( "endif", Boolean.TRUE );
		this.withinIfDirectives.put( "debug", Boolean.TRUE );
		this.withinIfDirectives.put( "mdebug", Boolean.TRUE );
		this.ignoreDirectives = new HashMap();
		this.ignoreDirectives.put( "endinclude", Boolean.TRUE );
		this.supportedDirectives = new HashMap();
		this.supportedDirectives.putAll( this.withinIfDirectives );
		this.supportedDirectives.putAll( this.ignoreDirectives );
		this.supportedDirectives.put( "if", Boolean.TRUE );
		this.supportedDirectives.put( "ifdef", Boolean.TRUE );
		this.supportedDirectives.put( "include", Boolean.TRUE );
		this.supportedDirectives.put( "define", Boolean.TRUE );
		this.supportedDirectives.put( "undefine", Boolean.TRUE );
	}

	/**
	 * Sets the direcotry to which the preprocessed files should be copied to.
	 * 
	 * @param path The target path. 
	 */
	public void setTargetDir(String path) {
		this.destinationDir = new File( path );
		if (!this.destinationDir.exists()) {
			this.destinationDir.mkdirs();
		}
	}

	/**
	 * Sets the symbols. Any old settings will be discarded.
	 * 
	 * @param symbols All new symbols, defined in a HashMap.
	 * @throws BuildException when an invalid symbol is defined (currently only "false" is checked);
	 */
	public void setSymbols(HashMap symbols) {
		// check symbols:
		Set keySet = symbols.keySet();
		for (Iterator iter = keySet.iterator(); iter.hasNext();) {
			String symbol = (String) iter.next();
			if ("false".equals(symbol)) {
				throw new BuildException("The symbol [false] must not be defined. Please check your settings in your build.xml, devices.xml, groups.xml and vendors.xml");
			}
		}
		this.symbols = symbols;
		this.booleanEvaluator.setEnvironment(symbols, this.variables);
	}
	
	/**
	 * Turns the support for the J2ME Polish GUI on or off.
	 *  
	 * @param usePolishGui true when the GUI is supported, false otherwise
	 */
	public void setUsePolishGui( boolean usePolishGui ) {
		this.usePolishGui = usePolishGui;
		if (usePolishGui) {
			addSymbol("polish.usePolishGui");
		} else {
			removeSymbol("polish.usePolishGui");
		}
	}
	
	/**
	 * Adds a single symbol to the list.
	 * 
	 * @param name The name of the symbol.
	 */
	public void addSymbol( String name ) {
		this.symbols.put( name, Boolean.TRUE );
	}

	/**
	 * Removes a symbol from the list of defined symbols.
	 * 
	 * @param name The name of the symbol.
	 */
	public void removeSymbol(String name) {
		this.symbols.remove(name);
	}
	
	/**
	 * Sets the variables, any old settings will be lost.
	 * 
	 * @param variables the variables.
	 */
	public void setVariables(HashMap variables) {
		this.variables = variables;
		this.booleanEvaluator.setEnvironment(this.symbols, variables);
	}
	
	/**
	 * Adds a variable to the list of existing variables.
	 * When a variable with the given name already exists, it
	 * will be overwritten.
	 * 
	 * @param name The name of the variable.
	 * @param value The value of the variable.
	 */
	public void addVariable( String name, String value ) {
		this.variables.put( name, value );
	}
	
	/**
	 * Adds all the variables to the existing variables.
	 * When a variable already exists, it will be overwritten.
	 * 
	 * @param additionalVars A map of additional variables.
	 */
	public void addVariables( Map additionalVars ) {
		this.variables.putAll(additionalVars);
	}
	
	
	/**
	 * Preprocesses the given file and saves it to the destination directory.
	 * 
	 * @param sourceDir the directory containing the source file
	 * @param fileName the name (and path)of the source file
	 * @return true when the file was preprocessed or changed
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when the file could not be read or written
	 * @throws BuildException when the preprocessing fails
	 */
	public boolean preprocess( File sourceDir, String fileName ) 
	throws FileNotFoundException, IOException, BuildException
	{
		this.ifDirectiveCount = 0;
		File sourceFile = new File( sourceDir.getAbsolutePath()  + "/" + fileName );
		String[] sourceLines = FileUtil.readTextFile( sourceFile );
		StringList lines = new StringList( sourceLines );
		// set source directory:
		this.variables.put( "polish.source", sourceDir.getAbsolutePath() );
		String className = fileName.substring(0, fileName.indexOf('.'));
		className = TextUtil.replace( className, "/", "." );
		int result = preprocess( className, lines );
		if (result == SKIP_FILE) {
			return false;
		}
		boolean preprocessed = (result == CHANGED);
		if (this.newExtension != null) {
			// change the extension of the file:
			int dotPos = fileName.indexOf('.');
			if (dotPos != -1) {
				fileName = fileName.substring( 0, dotPos + 1 ) + this.newExtension;
			} else {
				fileName += "." + this.newExtension;
			}
		}
		File destinationFile = new File( this.destinationDir.getAbsolutePath() + "/" + fileName );
		if (preprocessed 
				|| ( !destinationFile.exists() ) 
				|| ( sourceFile.lastModified() > destinationFile.lastModified() ) 
				) 
		{
			// the file needs to be written
			if (this.backup && destinationFile.exists() ) {
				// create backup:
				destinationFile.renameTo( new File( destinationFile.getAbsolutePath() + ".bak") );
			}
			// save preprocessed file:
			FileUtil.writeTextFile( destinationFile, lines.getArray() );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Resets this preprocessor.
	 * The internal state is reset to allow new preprocessing of other files. 
	 */
	public void reset() {
		this.ifDirectiveCount = 0;
	}

	/**
	 * Preprocesses the given source code.
	 * 
	 * @param className the name of the source file.
	 * @param lines the source code. Changes are made directly in the code.
	 * @return true when changes were made.
	 * @throws BuildException when the preprocessing fails.
	 */
	public int preprocess(String className, StringList lines) 
	throws BuildException 
	{
		boolean changed = false;
		try {
			while (lines.next()) {
				String line = lines.getCurrent();
				if (line.indexOf('#') != -1) {
					// could be that there is a preprocess instruction:
					int result = processSingleLine( className, lines, line, line.trim() );
					if (result == CHANGED ) {
						changed = true;
					} else if (result == SKIP_FILE) {
						return SKIP_FILE;
					}
				}
			}
		} catch (BuildException e) {
			reset();
			throw e;
		}
		if (changed) {
			return CHANGED;
		} else {
			return NOT_CHANGED;
		}
	}
	
	/**
	 * Checks a single line for a preprocessing directive.
	 * 
	 * @param className the name of the source file
	 * @param lines the source code lines
	 * @param line the specific line
	 * @param trimmedLine the same line but without spaces at the beginning or end
	 * @return either NOT_PROCESSED when no first level directive was found, 
	 * 			CHANGED when a directive was found and changes were made or
	 * 			DIRECTIVE_FOUND when a valid first level directive was found but no changes were made 
	 * @throws BuildException when there was a syntax error in the directives
	 */
	protected int processSingleLine( String className, StringList lines, String line, String trimmedLine ) 
	throws BuildException 
	{
		if (!trimmedLine.startsWith("//#")) {
			// this is not a preprocesssing directive:
			return NOT_CHANGED;
		}
		int spacePos = trimmedLine.indexOf(' ');
		if (spacePos == -1) {
			spacePos = trimmedLine.indexOf('\t');
		}
		
		if (spacePos == -1) { // directive has no argument
			// only #debug and #mdebug can have no arguments:
			if (trimmedLine.equals("//#debug")) {
				boolean changed = processDebug( null, lines, className );
				if (changed) {
					return CHANGED;
				} else {
					return DIRECTIVE_FOUND;
				}
			} else if (trimmedLine.equals("//#mdebug")) {
				boolean changed = processMdebug( null, lines, className );
				if (changed) {
					return CHANGED;
				} else {
					return DIRECTIVE_FOUND;
				}
			}
			// when the argument is within an if-branch, there might be other valid directives:
			return checkInvalidDirective( className, lines, line, trimmedLine.substring(3).trim(), null );
		} else if (this.ifDirectiveCount > 0 &&  spacePos == 3) {
			// this is just an outcommented line
			return NOT_CHANGED;
		}
		String command = trimmedLine.substring(3, spacePos);
		String argument = trimmedLine.substring( spacePos + 1 ).trim();
		boolean changed = false;
		if ("condition".equals(command)) {
			// a precondition must be fullfilled for this source file:
			if (! checkIfCondition(argument, className, lines)) {
				return SKIP_FILE;
			}
		} else if ("ifdef".equals(command)) {
			changed = processIfdef( argument, lines, className );
		} else if ("ifndef".equals(command)) {
			changed = processIfndef( argument, lines, className );
		} else if ("if".equals(command)) {
			changed = processIf( argument, lines, className );
		} else if ("define".equals(command)) {
			// define never changes the source directly:
			processDefine( argument, lines, className );
		} else if ("undefine".equals(command)) {
			// undefine never changes the source directly:
			processUndefine( argument, lines, className );
		} else if ("=".equals(command)) {
			changed = processVariable( argument, lines, className );
		} else if ("include".equals(command)) {
			changed = processInclude( argument, lines, className );
		} else if ("style".equals( command) ) {
			changed = processStyle( argument, lines, className );
		} else if ("debug".equals( command) ) {
			changed = processDebug( argument, lines, className );
		} else if ("mdebug".equals( command) ) {
			changed = processMdebug( argument, lines, className );
		} else {
			return checkInvalidDirective( className, lines, line, command, argument );
		}
		if (changed) {
			return CHANGED;
		} else {
			return DIRECTIVE_FOUND;
		}
	}
	
	private int checkInvalidDirective( String className, StringList lines, String line, String command, String argument )
	throws BuildException
	{
		if ( (this.ifDirectiveCount > 0) && (this.withinIfDirectives.get( command ) != null) ) {
			return NOT_CHANGED;
		} else if ( this.ignoreDirectives.get( command ) != null ) {
			return DIRECTIVE_FOUND;
		} else {
			throw new BuildException(
					className + " line " + (lines.getCurrentIndex() + 1) 
					+ ": unable to process command [" + command 
					+ "] with argument [" + argument 
					+ "] in line [" + line + "]." );
		}
		
	}


	/**
	 * Processes the #ifdef command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when any lines were actually changed
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processIfdef(String argument, StringList lines, String className )
	throws BuildException
	{
		boolean conditionFulfilled = (this.symbols.get( argument ) != null);
		return processIfVariations( conditionFulfilled, lines, className );
	}

	/**
	 * Processes the #ifndef command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className name of the file which is processed
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processIfndef(String argument, StringList lines, String className ) 
	throws BuildException
	{
		boolean conditionFulfilled = (this.symbols.get( argument ) == null);
		return processIfVariations( conditionFulfilled, lines, className );
	}
	
	/**
	 * Processes the #ifdef, #ifndef, #if, #else, #elifdef, #elifndef and #elif directives.
	 * 
	 * @param conditionFulfilled true when the ifdef or ifndef clause is true
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when any lines were actually changed
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processIfVariations(boolean conditionFulfilled, StringList lines, String className )
	throws BuildException
	{
		this.ifDirectiveCount++;
		int currentIfDirectiveCount = this.ifDirectiveCount;
		boolean endifFound = false;
		boolean elseFound = false;
		int commandStartLine = lines.getCurrentIndex();
		boolean processed = false;
		while (lines.next()) {
			String line = lines.getCurrent();
			String trimmedLine = line.trim();
			int result = NOT_CHANGED; 
			if (conditionFulfilled) {
				result = processSingleLine( className, lines, line, trimmedLine );
			}
			if (result == CHANGED ) {
				processed = true;
			} else if ( result == DIRECTIVE_FOUND ) {
				// another directive was found and processed, but no changes were made
			} else if ( trimmedLine.startsWith("//#ifdef ") ) {
				// we are currently in a branch which is not true (conditionFulfilled == false)
				this.ifDirectiveCount++;
			} else if ( trimmedLine.startsWith("//#if ") ) {
				// we are currently in a branch which is not true (conditionFulfilled == false)
				this.ifDirectiveCount++;
			} else if (trimmedLine.startsWith("//#else") && (this.ifDirectiveCount == currentIfDirectiveCount)) {
				conditionFulfilled = !conditionFulfilled;
				elseFound = true;
			} else if ( trimmedLine.startsWith("//#elifdef") && (this.ifDirectiveCount == currentIfDirectiveCount)) {
				if (elseFound) {
					throw new BuildException( className + " line " + (lines.getCurrentIndex() +1) 
							+ ": found directive #elifdef after #else branch.");
				}
				String symbol = line.substring( 10 ).trim();
				conditionFulfilled = (this.symbols.get( symbol ) != null);
			} else if (trimmedLine.startsWith("//#elifndef") && (this.ifDirectiveCount == currentIfDirectiveCount)) {
				if (elseFound) {
					throw new BuildException( className + " line " + (lines.getCurrentIndex() +1) 
							+ ": found directive #elifndef after #else branch.");
				}
				String symbol = line.substring( 11 ).trim();
				conditionFulfilled = (this.symbols.get( symbol ) == null);
			} else if (trimmedLine.startsWith("//#elif") && (this.ifDirectiveCount == currentIfDirectiveCount)) {
				if (elseFound) {
					throw new BuildException( className + " line " + (lines.getCurrentIndex() +1) 
							+ ": found directive #elifndef after #else branch.");
				}
				String argument = line.substring( 8 ).trim();
				conditionFulfilled = checkIfCondition( argument, className, lines );
			} else if (trimmedLine.startsWith("//#endif")) {
				if (this.ifDirectiveCount == currentIfDirectiveCount ) {
					endifFound = true;
					break;
				} else {
					this.ifDirectiveCount--;
				}
			} else {
				// this line has to be either commented out or to be uncommented:
				boolean changed = false;
				if (conditionFulfilled) {
					changed = uncommentLine( line, lines );
				} else {
					changed = commentLine( line, trimmedLine, lines );
				}
				if (changed) {
					processed = true;
				}
			}
		} // loop until endif is found
		if (!endifFound) {
			throw new BuildException(className + " line " + (commandStartLine +1) 
					+": #ifdef is not terminated with #endif!" );
		}
		this.ifDirectiveCount--;
		return processed;
	}

	/**
	 * Checks whether an if-condition is true.
	 * 
	 * @param argument the if-expression e.g. "(symbol1 || symbol2) && symbol3"
	 * @param className the name of the file
	 * @param lines the list of source code
	 * @return true when the argument results in true
	 * @throws BuildException when there is an syntax error in the expression
	 */
	protected boolean checkIfCondition(String argument, String className, StringList lines ) 
	throws BuildException 
	{
		return this.booleanEvaluator.evaluate( argument, className, lines.getCurrentIndex() + 1);
	}

	/**
	 * Comments a line, so it will not be compiled.
	 *  
	 * @param line the line
	 * @param trimmedLine the line without any spaces or tabs at the start
	 * @param lines the list were changes are written to
	 * @return true when the line were changed
	 */
	private boolean commentLine(String line, String trimmedLine, StringList lines ) {
		if (trimmedLine.startsWith("//#")) {
			return false;
		}
		String newLine;
		if (this.indent) {
			char[] lineChars = line.toCharArray();
			int insertPos = 0;
			for (int i = 0; i < lineChars.length; i++) {
				char c = lineChars[i];
				if ( c != ' ' && c != '\t') {
					insertPos = i;
					break;
				}
			}
			newLine = line.substring(0, insertPos ) + "//# " + line.substring( insertPos );
		} else {
			newLine =  "//# " + line;
		}
		lines.setCurrent( newLine );
		return true;
	}

	/**
	 * Removes the commenting of a line, so it will be compiled.
	 *  
	 * @param line the line
	 * @param lines the list of lines in which changes are saved
	 * @return true when the line was actually changed
	 */
	private boolean uncommentLine(String line, StringList lines ) {
		int commentPos = line.indexOf("//# ");
		if (commentPos == -1) {
			commentPos = line.indexOf("//#\t");
			if (commentPos == -1) {
				return false;
			}
		}
		String newLine = line.substring( 0, commentPos ) + line.substring( commentPos + 4);
		lines.setCurrent( newLine );
		return true;
	}

	/**
	 * Processes the #ifdef command.
	 * 
	 * @param argument the symbols which need to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processIf(String argument, StringList lines, String className ) 
	throws BuildException
	{
		boolean conditionFulfilled = checkIfCondition( argument, className, lines );
		return processIfVariations( conditionFulfilled, lines, className );
	}

	/**
	 * Processes the #define command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @throws BuildException when the preprocessing fails
	 */
	private void processDefine(String argument, StringList lines, String className ) 
	throws BuildException
	{
		if (argument.equals("false")) {
			throw new BuildException( className + " line " + (lines.getCurrentIndex() +1) 
					+ ": found invalid #define directive: the symbol [false] cannot be defined.");
		}
		this.symbols.put( argument, Boolean.TRUE );
	}

	/**
	 * Processes the #undefine command.
	 * 
	 * @param argument the symbol which should be undefined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @throws BuildException when the preprocessing fails
	 */
	private void processUndefine(String argument, StringList lines, String className ) 
	throws BuildException
	{
		if (argument.equals("true")) {
			throw new BuildException( className + " line " + (lines.getCurrentIndex() +1) 
					+ ": found invalid #undefine directive: the symbol [true] cannot be defined.");
		}
		this.symbols.remove( argument );
	}

	/**
	 * Processes the #= command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when the content has been changed
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processVariable(String argument, StringList lines, String className ) 
	throws BuildException
	{
		try {
			String line = PropertyUtil.writeProperties( argument, this.variables, true );
			lines.setCurrent( line );
			return true;
		} catch (IllegalArgumentException e) {
			throw new BuildException( className + " line " + (lines.getCurrentIndex() +1)
					+ ": unable to preprocess //#= in line [" + argument + "]: " + e.getMessage()  );
		}
	}

	/**
	 * Processes the #include command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made (always)
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processInclude(String argument, StringList lines, String className) 
	throws BuildException
	{
		String file = argument;
		try {
			file = PropertyUtil.writeProperties( argument, this.variables, true );
			String[] includes = FileUtil.readTextFile( file );
			lines.insert( includes );
			return true;
		} catch (IllegalArgumentException e) {
			throw new BuildException( className + " line " + (lines.getCurrentIndex() +1)
					+ ": unable to include file [" + argument + "]: " + e.getMessage()  );
		} catch (IOException e) {
			if (!argument.equals(file)) {
				argument += "] / [" + file;
			}
			throw new BuildException( className + " line " + (lines.getCurrentIndex() +1)
					+ ": unable to include file [" + argument + "]: " + e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	/**
	 * Processes the #style command.
	 * 
	 * @param styleNames the name of the style(s)
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processStyle(String styleNames, StringList lines, String className) 
	throws BuildException
	{
		if (!this.usePolishGui) {
			return false;
		}
		int styleDirectiveLine = lines.getCurrentIndex() + 1;
		if (this.styleSheet == null) {
			throw new BuildException(
					className + " line " + styleDirectiveLine
					+ ": unable to process #style directive: no style-sheet found. Please create [resources/polish.css].");
		}
		// get the style-name:
		String[] styles = TextUtil.splitAndTrim(styleNames, ',');
		String style = null;
		for (int i = 0; i < styles.length; i++) {
			String name = styles[i].toLowerCase();
			if (this.styleSheet.isDefined(name)) {
				style = name;
				break;
			}
		}
		if (style == null) {
			String message;
			if (styles.length == 1) {
				message = "the style [" + styleNames + "] is not defined. Please define the style in the appropriate polish.css file.";
			} else {
				message = "none of the styles [" + styleNames + "] is defined. Please define at least one of the styles in the appropriate polish.css file.";
			}
			throw new BuildException(
					className + " line " + styleDirectiveLine
					+ ": unable to process #style directive: " + message );
		}
		// when the #style directive is followed by a new operator, then
		// the defined style will be included as last argument in the new operator,
		// otherwise the defined style will be set as the current style in the stylesheet:
		String nextLine = null;
		if (lines.next()) {
			nextLine = lines.getCurrent();
			String trimmed = nextLine.trim(); 
			if ( trimmed.startsWith("//#=") ) {
				processSingleLine(className, lines, nextLine, trimmed);
				nextLine = lines.getCurrent();
			}
		}
		// get the statement which follows the #style-directive and
		// which is closed by a semicolon:
		if ( nextLine != null ) {
			uncommentLine( nextLine, lines );
			while ( nextLine.indexOf(';') == -1) {
				if (!lines.next()) {
					throw new BuildException(
							className + " line " + styleDirectiveLine
							+ ": unable to process #style directive: there is a new operator without closing semicolon in the following line(s)."
							);
				}
				if ( containsDirective( nextLine) ) {
					throw new BuildException(
							className + " line " + styleDirectiveLine
							+ ": unable to process #style directive: there is a new operator without closing semicolon in the following line(s)."
					);
				}
				nextLine = lines.getCurrent();				
				uncommentLine( nextLine, lines );
			}
			// get uncommented line:
			nextLine = lines.getCurrent();
			int parenthesisPos = nextLine.lastIndexOf(')');
			if ( parenthesisPos == -1 ) {
				throw new BuildException(
						className + " line " + styleDirectiveLine
						+ ": unable to process #style directive: the statement which follows the #style directive must be closed by a parenthesis and a semicolon on the same line: [);]. "  
				);
			}
			// append the style-parameter as the last argument:
			StringBuffer buffer = new StringBuffer();
			buffer.append( nextLine.substring(0, parenthesisPos ) )
					.append( ", StyleSheet." )
					.append( style )
					.append( "Style " )
					.append( nextLine.substring( parenthesisPos ) );
			lines.setCurrent( buffer.toString() );
		} else { // either there is no next line or the next line has no new operator
			lines.prev();
			lines.insert( "\tStyleSheet.currentStyle = StyleSheet." + style + "Style;"  );
		}
		// mark the style as beeing used:
		this.styleSheet.addUsedStyle( style );
		return true;
	}

	/**
	 * Processes the #debug command.
	 * 
	 * @param argument the debug-level if defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processDebug(String argument, StringList lines, String className) 
	throws BuildException
	{
		lines.next();
		String line = lines.getCurrent();
		if (!this.enableDebug) {
			return (commentLine( line, line.trim(), lines ));
		}
		if (argument == null || "".equals(argument)) {
			argument = "debug";
		}
		if (this.debugManager.isDebugEnabled( className, argument )) {
			boolean verboseDebug = this.debugManager.isVerbose();
			if (verboseDebug) {
				insertVerboseDebugInfo( lines, className );
			}
			return (verboseDebug | uncommentLine( line, lines ));
		} else {
			return commentLine( line, line.trim(), lines );
		}
	}

	/**
	 * Processes the #mdebug command.
	 * 
	 * @param argument the debug-level if defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws BuildException when the preprocessing fails
	 */
	private boolean processMdebug(String argument, StringList lines, String className) 
	throws BuildException
	{
		boolean debug = false;
		boolean changed = false;
		if (this.enableDebug) {
			if (argument == null || "".equals(argument)) {
				argument = "debug";
			}
			debug = this.debugManager.isDebugEnabled( className, argument );
		}
		int startLine = lines.getCurrentIndex() + 1;
		boolean endTagFound = false;
		boolean verboseDebug = (debug && this.debugManager.isVerbose());
		if (verboseDebug) {
			insertVerboseDebugInfo( lines, className );
		}
		while ( lines.next() ) {
			String line = lines.getCurrent();
			String trimmedLine = line.trim();
			if (trimmedLine.startsWith("//#enddebug")) {
				endTagFound = true;
				break;
			}
			if (debug) {
				changed = changed | uncommentLine( line, lines );
			} else {
				changed = changed | commentLine( line, trimmedLine, lines );
			}
		}
		if (! endTagFound ) {
			throw new BuildException(
					className + " line " + startLine
					+ ": missing #enddebug directive for multi-line debug directive #mdebug."
			);
		}
		return (verboseDebug || changed);
	}
	
	/**
	 * Inserts verbose debugging information (time, class-name and source-code line).
	 * 
	 * @param lines the source code
	 * @param className the name of the class
	 */
	private void insertVerboseDebugInfo( StringList lines, String className ) {
		String debugVerbose = "(System.currentTimeMillis() + "
			+ "\" - " + className 
			+ " line " + (lines.getCurrentIndex() + 1 - lines.getNumberOfInsertedLines()) 
			+ "\" );";
		if (this.debugManager.useGui()) {
			debugVerbose = "de.enough.polish.util.Debug.debug" + debugVerbose;
		} else {
			debugVerbose = "System.out.println" + debugVerbose;
		}
		lines.insert( debugVerbose );
		lines.next();
	}
	
	/**
	 * Checks if the given line contains a directive.
	 *  
	 * @param line the line which should be tested 
	 * @return true when the given line includes a preprocessing directive.
	 */
	public static final boolean containsDirective(String line) {
		Matcher matcher = DIRECTIVE_PATTERN.matcher( line );
		return matcher.find();
	}

	/**
	 * Sets the style sheet.
	 * 
	 * @param styleSheet the new style sheet
	 */
	public void setSyleSheet(StyleSheet styleSheet) {
		this.styleSheet = styleSheet;
	}
	
	/**
	 * Retrieves the style sheet.
	 * 
	 * @return the style sheet.
	 */
	public StyleSheet getStyleSheet() {
		return this.styleSheet;
	}

	/**
	 * Determines whether the given symbol is defined.
	 * 
	 * @param symbol the symbol 
	 * @return true when the symbol is defined
	 */
	public boolean hasSymbol(String symbol) {
		return (this.symbols.get( symbol) != null);
	}

	/**
	 * Retrieves the value of a variable.
	 * 
	 * @param name the name of the variable
	 * @return the value of the variable
	 */
	public String getVariable(String name) {
		return (String) this.variables.get(name);
	}


}
