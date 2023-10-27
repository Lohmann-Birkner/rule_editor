package de.lb.ruleprocessor.checkpoint.utils;

import java.io.File;
import java.io.*;
import de.lb.ruleprocessor.checkpoint.server.exceptions.ExcException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Überschrift: Checkpoint DRG</p>
 *
 * <p>Beschreibung: Fallmanagement DRG</p>
 *
 * <p>Copyright: </p>
 *
 * <p>Organisation: </p>
 *
 * @author not attributable
 * @version 2.0
 */
public class UtlFileManager
{

    private static final Logger LOG = Logger.getLogger(UtlFileManager.class.getName());
    
	private static UtlFileManager m_fmgr = null;

	private UtlFileManager()
	{
	}

	public static UtlFileManager fileManager(){
		if (m_fmgr==null)
			m_fmgr = new UtlFileManager();
		return m_fmgr;
	}

	public byte[] getBytes(File in)
	{
		if (in == null)
			return null;
		byte[] b = null;
		try
		{
			BufferedInputStream binput = new BufferedInputStream(new FileInputStream(in));
			int n = binput.available();
			b = new byte[n];
			binput.read(b, 0, n);
		}catch (Exception ex)
		{
			ExcException.createException(ex);
		}
		return b;
	}

	public byte[] getBytes(InputStream in)
	{
		if (in == null)
			return null;
		byte[] b = null;
		try
		{
			boolean weiter=true;
			int lg=8192, avail, bytesRead;
			byte buffer[] = new byte[lg];
//			BufferedInputStream input = new BufferedInputStream(in);

//			ByteArrayOutputStream output = new ByteArrayOutputStream();
			File f = File.createTempFile("testdoc", ".b");
			FileOutputStream output = new FileOutputStream(f);
			DataInputStream input = new DataInputStream(in);
			while(weiter) {
				try {
					avail = input.available();
					if(avail > lg)
						avail = lg;
					bytesRead = input.read(buffer, 0, avail);
				} catch(Exception e) {
					bytesRead = -1;
				}
				if(bytesRead == -1) {
					weiter = false;
				} else if(bytesRead == 0) {
					Thread.sleep(100);
				} else {
					output.write(buffer, 0, bytesRead);
				}
			}
			input.close();
			output.flush();
			output.close();
			b = this.getBytes(f);
			f.delete();
		}catch (Exception ex)
		{
			ExcException.createException(ex);
		}
		return b;
	}

	public java.util.Vector getSplitBytes(File in)
	{
		java.util.Vector v = new java.util.Vector();
		if (in == null)
			return null;
		byte[] b = null;
		try
		{
			// File splitten
			BufferedReader reader = new BufferedReader(new FileReader(in));
			BufferedWriter writer;
			String line = reader.readLine();
			int linenumber=0;
			int splitFile=1;
			writer = new BufferedWriter(new FileWriter(new File(in.getAbsolutePath()+splitFile)));
			do{
				writer.write(line);
				writer.newLine();
				linenumber++;
				if(linenumber%20000==0){
					splitFile++;
					writer.flush();
					writer.close();
					writer = new BufferedWriter(new FileWriter(new File(in.getAbsolutePath()+splitFile)));
				}
				line = reader.readLine();
			}while(line!=null);
			writer.flush();
			writer.close();
			//Byte Arrays erzeugen
			for(int i = 1; i <= splitFile; i++) {
				BufferedInputStream binput = new BufferedInputStream(new FileInputStream(new File(in.getAbsolutePath()+i)));
				int n = binput.available();
				b = new byte[n];
				binput.read(b, 0, n);
				v.add(b);
			}
		} catch(Exception ex) {
			ExcException.createException(ex);
		}
		return v;
	}


	public void deleteFile(final File f){
		Thread thr = new Thread(){
			public void run(){
				boolean del = false;
				del=f.delete();
				for(int i=0;i<5 || del;i++){
					try {
						Thread.sleep(500);
					} catch(InterruptedException ex) {
					}
					del=f.delete();
					if(del)
						LOG.log(Level.INFO, "Datei "+f.getName()+" konnte gelöscht werden.");
				}
				//if(!del)
				//	ExcLogfile.writeToLog("Datei "+f.getName()+" konnte gelöscht werden.");
			};
		};
		thr.start();
	}


	public static String getSeparatorLine() {
		return mSeparatorLine;
	}

	private static final String mSeparatorLine = System.getProperty( "line.separator");

}
