/*
 * Created on 14-Jan-2004 at 13:16:14.
 * This source code is published under the GNU General Public Licence and
 * the enough-software-licence for commercial use.
 * Please refer to accompanying LICENSE.txt or visit www.enough.de for details.
 */
package de.enough.polish.ant;

import de.enough.polish.Variable;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.TextUtil;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <p>Preprocesses J2ME-source files and inserts variables, (un)comments parts of the source and so on.</p>
 * <p>
 * This task is compatible with the WtkPreprocess - task of the antenna project.
 * See http://antenna.sourceforge.org for details of the great antenna project.
 * </p>
 * <p>copyright enough software 2004</p>
 * <pre>
 * history
 *        14-Jan-2004 - rob creation
 * </pre>
 * @author Robert Virkus, robert@enough.de
 */
public class PreprocessTask extends ConditionalTask {
	
	/** the directories containing the source-files */
	private ArrayList sourceFiles;
	/** the destination directory for the preprocessed files */
	private File destinationDir;
	/** holds all defined variables */
	private ArrayList variables;
	/** holds all defined symbols */
	private ArrayList symbols;
	private boolean verbose;
	private boolean backup;
	private boolean indent;
	private boolean test;
	private String newExt;
	

	/**
	 * Creates a new ProProcessTask
	 */
	public PreprocessTask() {
		// all initialisation is done within init()
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#init()
	 */
	public void init() throws BuildException {
		super.init();
		//TODO define standard include
		//createInclude().setName("**/*.java");
		this.variables = new ArrayList();
		this.symbols = new ArrayList();
		this.sourceFiles = new ArrayList();
		this.indent = true;
	}

	/**
	 * Sets the source directory(ies).
	 * Several directories can be specified, either seperated with ":" or ";".
	 * 
	 * @param name the name of the source-directory(ies).
	 */
	public void setSrcdir( String name ) {
		String[] sources = TextUtil.split( name, ':' );
		if (sources.length == 1) {
			sources = TextUtil.split( name, ';' );
		}
		for (int i = 0; i < sources.length; i++) {
			File source = new File( sources[i] );
			this.sourceFiles.add( source );
		}
	}

	/**
	 * Adds a set of files which should be preprocessed.
	 * This can be used for defining additional sources besides the srcdir-attribute.
	 * Alternatively several source directories can be specified in the
	 * srcdir-attribute, when the directories are seperated with a ":" or ";".
	 * 
	 * @param additionalFileset the list of files which should also be preprocessed.
	 */
	public void addConfiguredFileset( FileSet additionalFileset ) {
		this.sourceFiles.add( additionalFileset );
	}
	
	/**
	 * Sets the destination directory
	 * 
	 * @param destinationDir the destination directory
	 */
	public void setDestdir(File destinationDir) {
		this.destinationDir = destinationDir;
	}

	
	/**
	 * Adds a nested variable-definition to this preprocess.
	 * 
	 * @param variable the variable-definition
	 */
	public void addConfiguredVariable( Variable variable ) {
		if (variable.getName() == null) {
			throw new BuildException("nested element variable needs to have the attribute [name] defined.");
		} else if (variable.getValue() == null) {
			throw new BuildException("nested variable [" + variable.getName() + "] needs to have the attribute [value] defined.");
		}
		this.variables.add( variable );
	}
	
	/**
	 * sets the variables-file.
	 * This is used as an alternative to nested variables.
	 * The file has all defined variables in a properties-pattern:
	 * <pre>
	 * # [comment]
	 * [name]=[value]
	 * [name]=[value]
	 * </pre>
	 * 
	 * @param variablesFile (File) the file defining all the variables.
	 */
	public void setVariables( File variablesFile ) {
		// read the variable-definitions from a property-file:
		try {
			String[] lines = FileUtil.readTextFile( variablesFile );
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				if (!(line.startsWith("#") || "".equals(line)) ) {
					int equalsPos = line.indexOf( '=');
					if (equalsPos != -1) {
						String name = line.substring( 0, equalsPos ).trim();
						String value = line.substring( equalsPos + 1).trim();
						Variable variable = new Variable( name, value );
						this.variables.add( variable );
					}
				}
			}
		} catch (IOException e) {
			throw new BuildException( e );
		}
	}

	/**
	 * Sets the defined symbols, they are seperated with commas.
	 * 
	 * @param symbols the symbols seperated with commas.
	 */
	public void setSymbols( String symbols ) {
		String[] singleSymbols = TextUtil.split(symbols, ",");
		for (int i = 0; i < singleSymbols.length; i++) {
			String symbol = singleSymbols[i].trim();
			this.symbols.add( symbol );
		}
	}
	
	/**
	 * Triggers the verbose mode.
	 * In verbose mode many detail information will be logged.
	 * 
	 * @param verbose true when the verbose mode should be enabled.
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	/**
	 * Triggers the backup of the source-files.
	 * 
	 * @param backup true when backups should be made
	 */
	public void setBackup(boolean backup) {
		this.backup = backup;
	}
	
	/**
	 * Turns the indentation of out-commented parts on and off.
	 * Normally the comments will be placed where the code starts.
	 * When indent is false, the comments will be placed at the start
	 * of the line.
	 * 
	 * @param indent true when the comments should be placed at the start of the source code.
	 */
	public void setIndent(boolean indent) {
		this.indent = indent;
	}
	
	/**
	 * For testing puposes only.
	 * 
	 * @param test true when the test mode should be enabled
	 */
	public void setTest(boolean test) {
		this.test = test;
	}
	
	/**
	 * Sets the new extension for the preprocessed files.
	 * With the extension "java" a file named "ClassName.j2me.pp"
	 * would be renamed to "ClassName.java"
	 * 
	 * @param newext the new extension
	 */
	public void setNewext(String newext) {
		this.newExt = newext;
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException 
	{
		// check if this task should be executed:
		if (!isActive()) {
			// this task should not be executed, so just return:
			return;
		}
		
		// check if all needed settings are specified:
		if (this.destinationDir == null) {
			throw new BuildException("Need a destination directory for the preprocessed files, "
					+ "please specify the attribute [destdir].");
		}
		
		// now get all included source files and preprocess each one of them:
		Object[] sourceDirectories = this.sourceFiles.toArray();
		for (int i = 0; i < sourceDirectories.length; i++) {
			Object object = sourceDirectories[i];
			if (object instanceof File) {
				File sourceDir = (File) object;
				
				DirectoryScanner directoryScanner = getDirectoryScanner( sourceDir );
				String[] fileNames = directoryScanner.getIncludedFiles();
				preprocess( sourceDir, fileNames );
			} else if (object instanceof FileSet ){
				FileSet fileSet = (FileSet) object;
				String[] fileNames = fileSet.getDirectoryScanner( getProject() ).getIncludedFiles();
				File sourceDir = fileSet.getDir(getProject());
				preprocess( sourceDir, fileNames );
			} else {
				throw new BuildException("cannot handle source input [" + object.toString() 
						+ "] of type [" + object.getClass().getName() + "].");
			}
		}
	}

	/**
	 * @param sourceDir
	 * @return
	 */
	private DirectoryScanner getDirectoryScanner(File sourceDir) {
		// TODO enough implement getDirectoryScanner
		return null;
	}

	/**
	 * Preprocesses all given files.
	 * 
	 * @param sourceDir the source directory
	 * @param sourceFileNames the names of the files which should be preprocessed.
	 * @throws BuildException when a specified file could not be preprocessed.
	 */
	private void preprocess(File sourceDir, String[] sourceFileNames)
	throws BuildException
	{
		// TODO enough implement preprocess
		// TODO where are style settings defined?
		
		// polish.api.mmapi: polish values are overwritten from project to device 
		// project.api.mmapi
		// vendor.api.mmapi
		// group.api.mmapi
		// device.api.mmapi
		
		// vendor.colors.focus
		
		// basic class: ValueContainer
		// # prerequisite polish.midp1
		// # ! polish.midp1
	}

}
