package com.codecobra.chime;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class JTextConsole extends JTextArea {
	private PipedInputStream piOut;
    private PipedInputStream piErr;
    private PipedOutputStream poOut;
    private PipedOutputStream poErr;
    private PrintStream poOutOrig;
    private PrintStream poErrOrig;
    private boolean direct = false;
    private Thread tOut, tErr;

    public JTextConsole() throws IOException {
    	super();
    	
        // Set up System.out
        piOut = new PipedInputStream();
        poOut = new PipedOutputStream(piOut);

        // Set up System.err
        piErr = new PipedInputStream();
        poErr = new PipedOutputStream(piErr);

        // Add a scrolling text area
        this.setEditable(false);
        //this.setRows(20);
        //this.setColumns(50);
    }
    public void direct() {
    	direct = true;
        poOutOrig = System.out;
        poErrOrig = System.err;
        System.setOut(new PrintStream(poOut, true));
        System.setErr(new PrintStream(poErr, true));
        
    	// Create reader threads
        tOut = new ReaderThread(piOut,this);
        tOut.start();
        tErr = new ReaderThread(piErr,this);
        tErr.start();
    }
	public void restore() {
		direct = false;
		System.setOut(poOutOrig);
        System.setErr(poErrOrig);
    }

    class ReaderThread extends Thread {
        PipedInputStream pi;
        JTextArea text;

        ReaderThread(PipedInputStream pi, JTextArea text) {
            this.pi = pi;
            this.text = text;
        }

        public void run() {
            final byte[] buf = new byte[1024];
            try {
                while (direct) {
                    final int len = pi.read(buf);
                    if (len == -1) {
                        break;
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                        	text.append(new String(buf, 0, len));

                            // Make sure the last line is always visible
                        	text.setCaretPosition(text.getDocument().getLength());

                        	/*
                            // Keep the text area down to a certain character size
                            int idealSize = 1000;
                            int maxExcess = 500;
                            int excess = text.getDocument().getLength() - idealSize;
                            if (excess >= maxExcess) {
                            	text.replaceRange("", 0, excess);
                            }
                            */
                        }
                    });
                }
            } catch (IOException e) {
            }
        }
    }
}
