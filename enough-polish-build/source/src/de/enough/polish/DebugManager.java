/*
 * Created on 19-Jan-2003 at 15:06:28.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish;

import de.enough.polish.ant.build.DebugSetting;
import de.enough.polish.ant.build.Filter;
import de.enough.polish.preprocess.PreprocessException;

import java.util.HashMap;

/**
 * <p>Administers the debug-level for all classes.</p>
 *
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        19-Jan-2003 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class DebugManager {
	
	public static final int UNDEFINED = -1;
	public static final int DEBUG = 0;
	public static final int INFO = 1;
	public static final int WARN = 2;
	public static final int ERROR = 3;
	public static final int FATAL = 4;
	public static final int USER_DEFINED = 5;
	
	private HashMap classPatterns;
	private int debugLevel;
	
	private HashMap levelOrder;
	private boolean verbose;
	
	private void init(){
		this.classPatterns = new HashMap();
		this.levelOrder = new HashMap();
		this.levelOrder.put( "debug", new Integer(DEBUG));
		this.levelOrder.put( "info", new Integer(INFO));
		this.levelOrder.put( "warn", new Integer(WARN));
		this.levelOrder.put( "warning", new Integer(WARN));
		this.levelOrder.put( "error", new Integer(ERROR));
		this.levelOrder.put( "fatal", new Integer(FATAL));
	}

	/**
	 * Creates a new debug manager.
	 * 
	 * @param debugLevel the basic debug level for all classes,
	 *              for which no debug level has been defined, e.g. 
	 * 			 "debug", "info", "warn", "error", "fatal" or userdefined.
	 * @param verbose true when before every debug information the class-name and the 
	 * 			 line-number should be printed out.
	 */
	public DebugManager( String debugLevel, boolean verbose ) {
		this.verbose = verbose;
		init();
		Integer level = (Integer) this.levelOrder.get( debugLevel );
		if (level != null) {
			this.debugLevel = level.intValue();
		} else {
			this.levelOrder.put( debugLevel, new Integer( USER_DEFINED ));
			this.debugLevel = USER_DEFINED;
		}
	}
	
	/**
	 * Creates a new debug manager.
	 * 
	 * @param debugLevel the basic debug level for all classes,
	 *              for which no debug level has been defined.
	 * @param verbose true when before every debug information the class-name and the 
	 * 			 line-number should be printed out.
	 * @see #DEBUG
	 * @see #INFO
	 * @see #WARN
	 * @see #ERROR
	 * @see #FATAL
	 */
	public DebugManager( int debugLevel, boolean verbose ) {
		init();
		this.verbose = verbose;
		this.debugLevel = debugLevel;
	}
	
	/**
	 * Creates a new debug manager.
	 * 
	 * @param setting The settings for this manager.
	 * 
	 * @throws PreprocessException when the pattern of an included debug-filter is invalid
	 */
	public DebugManager(DebugSetting setting) throws PreprocessException {
		init();
		this.verbose = setting.isVerbose();
		Integer level = (Integer) this.levelOrder.get( setting.getLevel() );
		if (level != null) {
			this.debugLevel = level.intValue();
		} else {
			this.levelOrder.put( setting.getLevel(), new Integer( USER_DEFINED ));
			this.debugLevel = USER_DEFINED;
		}
		Filter[] filters = setting.getFilters();
		for (int i = 0; i < filters.length; i++) {
			Filter filter = filters[i];
			addDebugSetting( filter.getPattern(), filter.getLevel() );
		}
		// TODO take isVisual into account!
	}

	/**
	 * Adds a debug setting for a specific class or class-pattern.
	 * 
	 * @param pattern the class-name or class-pattern. A pattern is like an import-declaration,
	 * 			e.g. "com.company.package.*"
	 * @param level the level which should be allowed, e.g. "debug", "info", "warn", "error", "fatal" or userdefined.
	 * @throws PreprocessException when the pattern is invalid
	 */
	public void addDebugSetting( String pattern, String level ) 
	throws PreprocessException 
	{
		if (pattern.endsWith(".*")) {
			pattern = pattern.substring(0, pattern.length() - 2);
		} else if (pattern.indexOf('*') != -1) {
			throw new PreprocessException("Invalid class-pattern ["  + pattern + "] for debug-level [" + level +"] found! Patterns must end with a [.*] like import-patterns, e.g. \"com.company.package.*\"");
		}
		Integer order = (Integer) this.levelOrder.get( level );
		if (order == null) {
			order = new Integer( USER_DEFINED );
			this.levelOrder.put( level, order );
		}
		this.classPatterns.put( pattern, order );
	}
	
	/**
	 * Determines whether the verbose mode is enabled.
	 * When the verbose mode is enabled, the current time in ms, the name of the class
	 * as well as the line number in the source code will be printed
	 * out before each debugged information.
	 * Example: "34877 - com.company.package.MyClass - line 23"
	 * 
	 * @return true when the verbose mode is enabled.
	 */
	public boolean isVerbose() {
		return this.verbose;
	}
	
	/**
	 * Checks if for the given class and level the debugging is enabled.
	 *  
	 * @param className the name of the class, e.g. "com.company.packackge.MyClass"
	 * @param levelName the level, e.g. "debug", "info", "warn", "error", "fatal" or userdefined.
	 * @return true when for the given class and level the debugging is enabled.
	 */
	public boolean isDebugEnabled( String className, String levelName ) {
		int level = getLevelOrder( levelName );
		int classLevel = getClassLevel( className );
		return (level >=  classLevel ); 
	}
	
	/**
	 * Determines the level for a given class.
	 * 
	 * @param className the name of the class, e.g. "com.company.packackge.MyClass"
	 * @return the level assigned to the given class, either DEBUG, INFO, WARN, ERROR,
	 * 			FATAL or USER_DEFINED.
	 */
	public int getClassLevel( String className ) {
		Integer level = (Integer) this.classPatterns.get( className );
		if (level != null) {
			return level.intValue();
		}
		int dotPos = className.lastIndexOf('.');
		while (dotPos != -1) {
			className = className.substring( 0, dotPos );
			level = (Integer) this.classPatterns.get( className );
			if (level != null) {
				return level.intValue();
			}
			dotPos = className.lastIndexOf('.');
		}
		return this.debugLevel;	
	}
	
	/**
	 * Retrieves the order of the given level.
	 * 
	 * @param level the name of the level, e.g. "debug"
	 * @return the order or weight of the level
	 */
	public int getLevelOrder( String level ) {
		Integer order = (Integer) this.levelOrder.get( level );
		if (order == null) {
			order = (Integer) this.levelOrder.get( level.toLowerCase() );
		}
		if (order != null) {
			return  order.intValue();
		} else {
			return UNDEFINED;
		}
	}

}
