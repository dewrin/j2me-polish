/*
 * Created on 16-Jan-2004 at 12:17:12.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.preprocess;

import de.enough.polish.*;
import de.enough.polish.Project;
import de.enough.polish.util.*;

import java.io.*;
import java.util.HashMap;
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
	private static final int CHANGED = 2;
	private static final int NOT_PROCESSED= 4;
	public static final Pattern DIRECTIVE_PATTERN = 
		Pattern.compile("\\s*(//#if\\s+|//#ifdef\\s+|//#ifndef\\s+|//#elif\\s+|//#elifdef\\s+|//#elifndef\\s+|//#else|//#endif|//#include\\s+|//#endinclude|//#style |//#debug|//#mdebug|//#enddebug|//#define\\s+|//#undefine\\s+|//#=\\s+)");

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
	private StyleSheet styleSheet;
	private HashMap withinIfDirectives;
	private HashMap ignoreDirectives;
	private HashMap supportedDirectives;
	private int ifDirectiveCount;
	private BooleanEvaluator booleanEvaluator;

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
			Project project,
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
		this.variables = variables;
		this.symbols = symbols;
		this.verbose = verbose;
		this.backup = backup;
		this.indent = indent;
		this.newExtension = newExt;
		this.styleSheet = new StyleSheet();
		this.booleanEvaluator = new BooleanEvaluator( symbols );
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
	 * Retrieves the style sheet with all found style definitions of the preprocessed code.
	 * 
	 * @return the style sheet
	 */
	public StyleSheet getStyleSheet() {
		return this.styleSheet;
	}
	
	/**
	 * Preprocesses the given file and saves it to the destination directory.
	 * 
	 * @param sourceDir the directory containing the source file
	 * @param fileName the name (and path)of the source file
	 * @return true when the file was preprocessed or changed
	 * @throws FileNotFoundException when the file was not found
	 * @throws IOException when the file could not be read or written
	 * @throws PreprocessException when the preprocessing fails
	 */
	public boolean preprocess( File sourceDir, String fileName ) 
	throws FileNotFoundException, IOException, PreprocessException
	{
		File sourceFile = new File( sourceDir.getAbsolutePath()  + "/" + fileName );
		String[] sourceLines = FileUtil.readTextFile( sourceFile );
		StringList lines = new StringList( sourceLines );
		// set source directory:
		this.variables.put( "polish.source", sourceDir.getAbsolutePath() );
		String className = fileName.substring(0, fileName.indexOf('.'));
		className = TextUtil.replace( className, "/", "." );
		boolean preprocessed = preprocess( className, lines );
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
				) {
			// the file needs to be written
			if (this.backup && destinationFile.exists() ) {
				// create backup:
				destinationFile.renameTo( new File( destinationFile.getAbsoluteFile() + ".bak") );
			}
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
	 * @throws PreprocessException when the preprocessing fails.
	 */
	protected boolean preprocess(String className, StringList lines) 
	throws PreprocessException 
	{
		boolean changed = false;
		try {
			while (lines.next()) {
				String line = lines.getCurrent();
				if (line.indexOf('#') != -1) {
					// could be that there is a preprocess instruction:
					int result = checkForFirstLevelDirective( className, lines, line, line.trim() );
					if (result == CHANGED ) {
						changed = true;
					}
				}
			}
		} catch (PreprocessException e) {
			reset();
			throw e;
		}
		return changed;
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
	 * @throws PreprocessException when there was a syntax error in the directives
	 */
	protected int checkForFirstLevelDirective( String className, StringList lines, String line, String trimmedLine ) 
	throws PreprocessException 
	{
		if (!trimmedLine.startsWith("//#")) {
			// this is not a preprocesssing directive:
			return NOT_PROCESSED;
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
			return NOT_PROCESSED;
		}
		String command = trimmedLine.substring(3, spacePos);
		String argument = trimmedLine.substring( spacePos + 1 ).trim();
		boolean changed = false;
		if ("ifdef".equals(command)) {
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
	throws PreprocessException
	{
		if ( (this.ifDirectiveCount > 0) && (this.withinIfDirectives.get( command ) != null) ) {
			return NOT_PROCESSED;
		} else if ( this.ignoreDirectives.get( command ) != null ) {
			return DIRECTIVE_FOUND;
		} else {
			throw new PreprocessException(
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
	 * @throws PreprocessException when the preprocessing fails
	 */
	private boolean processIfdef(String argument, StringList lines, String className )
	throws PreprocessException
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
	 * @throws PreprocessException when the preprocessing fails
	 */
	private boolean processIfndef(String argument, StringList lines, String className ) 
	throws PreprocessException
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
	 * @throws PreprocessException when the preprocessing fails
	 */
	private boolean processIfVariations(boolean conditionFulfilled, StringList lines, String className )
	throws PreprocessException
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
			int result = NOT_PROCESSED; 
			if (conditionFulfilled) {
				result = checkForFirstLevelDirective( className, lines, line, trimmedLine );
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
					throw new PreprocessException( className + " line " + (lines.getCurrentIndex() +1) 
							+ ": found directive #elifdef after #else branch.");
				}
				String symbol = line.substring( 10 ).trim();
				conditionFulfilled = (this.symbols.get( symbol ) != null);
			} else if (trimmedLine.startsWith("//#elifndef") && (this.ifDirectiveCount == currentIfDirectiveCount)) {
				if (elseFound) {
					throw new PreprocessException( className + " line " + (lines.getCurrentIndex() +1) 
							+ ": found directive #elifndef after #else branch.");
				}
				String symbol = line.substring( 11 ).trim();
				conditionFulfilled = (this.symbols.get( symbol ) == null);
			} else if (trimmedLine.startsWith("//#elif") && (this.ifDirectiveCount == currentIfDirectiveCount)) {
				if (elseFound) {
					throw new PreprocessException( className + " line " + (lines.getCurrentIndex() +1) 
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
			throw new PreprocessException(className + " line " + (commandStartLine +1) 
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
	 * @throws PreprocessException when there is an syntax error in the expression
	 */
	protected boolean checkIfCondition(String argument, String className, StringList lines ) 
	throws PreprocessException 
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
	 * @throws PreprocessException when the preprocessing fails
	 */
	private boolean processIf(String argument, StringList lines, String className ) 
	throws PreprocessException
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
	 * @throws PreprocessException when the preprocessing fails
	 */
	private void processDefine(String argument, StringList lines, String className ) 
	throws PreprocessException
	{
		this.symbols.put( argument, Boolean.TRUE );
	}

	/**
	 * Processes the #undefine command.
	 * 
	 * @param argument the symbol which should be undefined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @throws PreprocessException when the preprocessing fails
	 */
	private void processUndefine(String argument, StringList lines, String className ) 
	throws PreprocessException
	{
		this.symbols.remove( argument );
	}

	/**
	 * Processes the #= command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when the content has been changed
	 * @throws PreprocessException when the preprocessing fails
	 */
	private boolean processVariable(String argument, StringList lines, String className ) 
	throws PreprocessException
	{
		try {
			String line = PropertyUtil.writeProperties( argument, this.variables, true );
			lines.setCurrent( line );
			return true;
		} catch (IllegalArgumentException e) {
			throw new PreprocessException( className + " line " + (lines.getCurrentIndex() +1)
					+ ": unable to include file [" + argument + "]: " + e.getMessage()  );
		}
	}

	/**
	 * Processes the #include command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made (always)
	 * @throws PreprocessException when the preprocessing fails
	 */
	private boolean processInclude(String argument, StringList lines, String className) 
	throws PreprocessException
	{
		String file = argument;
		try {
			file = PropertyUtil.writeProperties( argument, this.variables, true );
			String[] includes = FileUtil.readTextFile( file );
			lines.insert( includes );
			return true;
		} catch (IllegalArgumentException e) {
			throw new PreprocessException( className + " line " + (lines.getCurrentIndex() +1)
					+ ": unable to include file [" + argument + "]: " + e.getMessage()  );
		} catch (IOException e) {
			if (!argument.equals(file)) {
				argument += "] / [" + file;
			}
			throw new PreprocessException( className + " line " + (lines.getCurrentIndex() +1)
					+ ": unable to include file [" + argument + "]: " + e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	/**
	 * Processes the #style command.
	 * 
	 * @param argument the symbol which needs to be defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws PreprocessException when the preprocessing fails
	 */
	private boolean processStyle(String argument, StringList lines, String className) 
	throws PreprocessException
	{
		// when the #style directive is followed by a new operator, then
		// the defined style will be included as last argument in the new operator,
		// otherwise the defined style will be set as the current style in the stylesheet:
		String nextLine = null;
		int styleDirectiveLine = lines.getCurrentIndex() + 1;
		if (lines.next()) {
			nextLine = lines.getCurrent();
		}
		if ( (nextLine != null) && (nextLine.indexOf("new ") != -1) && (!includesDirective( nextLine )) ) {
			uncommentLine( nextLine, lines );
			while ( nextLine.indexOf(';') == -1) {
				if (!lines.next()) {
					throw new PreprocessException(
							className + " line " + styleDirectiveLine
							+ ": unable to process #style directive: there is a new operator without closing semicolon in the following line(s)."
							);
				}
				if ( includesDirective( nextLine) ) {
					throw new PreprocessException(
							className + " line " + styleDirectiveLine
							+ ": unable to process #style directive: there is a new operator without closing semicolon in the following line(s)."
					);
				}
				nextLine = lines.getCurrent();
				
				uncommentLine( nextLine, lines );
			}
			// get uncommented line:
			nextLine = lines.getCurrent();
			int parenthesisPos = nextLine.indexOf(')');
			if ( parenthesisPos == -1 ) {
				throw new PreprocessException(
						className + " line " + styleDirectiveLine
						+ ": unable to process #style directive: there is a new operator without closing parenthesis in the following line(s). " 
						+ "The closing parenthesis needs to be on the same line as the semicolon!" 
				);
			}
			// append the style-parameter as the last argument:
			StringBuffer buffer = new StringBuffer();
			buffer.append( nextLine.substring(0, parenthesisPos ) )
					.append( ", StyleSheet.getStyle( \"")
					.append( argument )
					.append( "\") " )
					.append( nextLine.substring( parenthesisPos ) );
			lines.setCurrent( buffer.toString() );
		} else { // either there is no next line or the next line has no new operator
			lines.prev();
			lines.insert( "\tStyleSheet.setCurrentStyle( \"" + argument + "\" );"  );
		}
		// mark the style as beeing used:
		this.styleSheet.addStyle( argument );
		return true;
	}

	/**
	 * Processes the #debug command.
	 * 
	 * @param argument the debug-level if defined
	 * @param lines the source code
	 * @param className the name of the source file
	 * @return true when changes were made
	 * @throws PreprocessException when the preprocessing fails
	 */
	private boolean processDebug(String argument, StringList lines, String className) 
	throws PreprocessException
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
	 * @throws PreprocessException when the preprocessing fails
	 */
	private boolean processMdebug(String argument, StringList lines, String className) 
	throws PreprocessException
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
			throw new PreprocessException(
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
		String debugVerbose = "de.enough.polish.util.Debug.debug(System.currentTimeMillis(), "
				+ "\" - " + className + " line " + (lines.getCurrentIndex() + 1) + "\" );";
		lines.insert( debugVerbose );
		lines.next();
	}
	
	/**
	 * Checks if the given line contains a directive.
	 *  
	 * @param line the line which should be tested 
	 * @return true when the given line includes a preprocessing directive.
	 */
	public static final boolean includesDirective(String line) {
		Matcher matcher = DIRECTIVE_PATTERN.matcher( line );
		return matcher.find();
	}
}
