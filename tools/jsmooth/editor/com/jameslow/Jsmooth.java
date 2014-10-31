package com.jameslow;

import java.io.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

//We currently put the outfile in the same dir as the newly created exefile/jarfile
//As Jsmooth has a problem handling paths, as it was design for windows
//This should be fixed sometime.
public class Jsmooth extends Task {
	private String infile,outfile,exefile,jarfile,icofile;
	private String mainclass;
	private static final String EXE_TAG = "${exefile}";
	private static final String JAR_TAG = "${jarfile}";
	private static final String CLASS_TAG = "${mainclass}";
	private static final String ICO_TAG = "${icofile}";
	
	public void execute() throws BuildException {
		readFile();
	}
	public void setInFile(String infile) {
		this.infile = infile;
	}
	public void setOutFile(String outfile) {
		this.outfile = outfile;
	}
	public void setExeFile(String exefile) {
		this.exefile = exefile;
	}
	public void setJarFile(String jarfile) {
		this.jarfile = jarfile;
	}
	public void setMainClass(String mainclass) {
		this.mainclass = mainclass;
	}
	public void setIcoFile(String icofile) {
		this.icofile = icofile;
	}
	
	private void readFile() {
		try {
			String line = null;
			String newline = null;
			BufferedReader in = new BufferedReader(new FileReader(infile));
			PrintWriter out = new PrintWriter(new FileWriter(outfile));
			while ((line = in.readLine()) != null) {
				if ((newline = replaceTag(EXE_TAG,line,exefile)) != null) {
					out.println(newline);
				} else if ((newline = replaceTag(JAR_TAG,line,jarfile)) != null) {
					out.println(newline);
				} else if ((newline = replaceTag(CLASS_TAG,line, mainclass)) != null) {
					out.println(newline);
				} else if ((newline = replaceTag(ICO_TAG,line, icofile)) != null) {
					out.println(newline);
				} else {
					out.println(line);
				}
			}
			in.close();
			out.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	private String replaceTag(String tag, String line, String newstr) {
		int start = line.indexOf(tag);
		if (start > 0) {
			line = line.substring(0,start) + newstr + line.substring(start + tag.length());
			return line;
		}
		return null;
	}
}
