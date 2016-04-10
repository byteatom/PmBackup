package chenhua.pmbackup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class Root {
	
	public class OutputReader extends Thread {
		private BufferedReader mReader = null;
		private LinkedList<String> mOutput = null;
		
		public OutputReader(InputStream input, LinkedList<String> output) {
			mReader = new BufferedReader(new InputStreamReader(input));
			mOutput = output;
		}
		
		public void run() {
			try {
				String line = null;
				boolean newCmd = true;
				while ((line = mReader.readLine()) != null) {
					synchronized(mOutput) {
						if (newCmd) {
							newCmd = false;
							mOutput.clear();
						}
						mOutput.add(line);
						if (!mReader.ready()) {
							newCmd =true;
							mOutput.notify();
						}
					}
				}
			} catch (Exception e) {
			}
			
			try {
				mReader.close();
			} catch (Exception e) {			
			}
		}
	}
	
	Process mProcess = null;
	DataOutputStream mWriter = null;
	OutputReader mReader = null;
	LinkedList<String> mOutput = null;
	
	public boolean open() {
		try {			
			mProcess = new ProcessBuilder().command("su").redirectErrorStream(true).start();
		} catch (Exception e) {
			return false;
		}
		
		mWriter = new DataOutputStream(mProcess.getOutputStream());
		mOutput = new LinkedList<String>();
		mReader = new OutputReader(mProcess.getInputStream(), mOutput);	
		mReader.start();
			
		return true;
	}
	
	public LinkedList<String> run(String cmd) {
		try {			
			synchronized(mOutput) {
				mWriter.write((cmd + "\n").getBytes("UTF-8"));
				mWriter.flush();
				mOutput.wait(10000);
				return new LinkedList<String>(mOutput);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public void close() {
		try { 
			mWriter.write("exit\n".getBytes("UTF-8"));
			mWriter.flush();			
			mProcess.waitFor();
			mWriter.close();
			mReader.join();
			mProcess.destroy();
		} catch (Exception e) { 				
		}	
		
		return;
	}
}
