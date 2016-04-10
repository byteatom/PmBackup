package chenhua.pmbackup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

class Restore extends AsyncTask <MainActivity, String, Void> {
			
	MainActivity mActivity;	
	
	protected Void doInBackground(MainActivity... activity) {
			
		mActivity = activity[0];
		
		ArrayList<String> nameList = new ArrayList<String>();		
		try {
			BufferedReader buf = new BufferedReader(new FileReader(Backup.mFilePath));
			String line;
			while ((line = buf.readLine()) != null) {
				nameList.add(line);
			}
			buf.close();
		} catch (Exception e) {
			publishProgress("\r\nFailed to open file!");
			return null;
		}				

		PackageManager pm = mActivity.getApplicationContext().getPackageManager();
		Root root = new Root();
		if (!root.open()) {
			publishProgress("\r\nFailed to get root!");
			return null;
		}
		
		int total = nameList.size();
		int skipped = 0;
		int succeed = 0;
		int failed = 0;
		for (Iterator<String> iterator = nameList.iterator();
			iterator.hasNext();) {
			String name = iterator.next();			
			if ((name.indexOf('/') == -1 && 
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED == 
					pm.getApplicationEnabledSetting(name)) ||
				(name.indexOf('/') != -1 && 
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED == 
					pm.getComponentEnabledSetting(ComponentName.unflattenFromString(name)))) {
				iterator.remove();
				skipped++;
				continue;
			}
			
			LinkedList<String> output = root.run("pm disable " + name);
			
			if (null == output) {
				continue;
			}
			String first = output.getFirst();
			if (null == first || 
				(!first.equals("Component {" + name + "} new state: disabled") &&
				!first.equals("Package " + name + " new state: disabled"))) {
				continue;
			}
			publishProgress("\r\n[Succeed]: " + first);
			iterator.remove();
			succeed++;
		}
		root.close();
		
		for (Iterator<String> iterator = nameList.iterator();
			iterator.hasNext();) {
			String name = iterator.next();
			publishProgress("\r\n[Failed]: " + name);
			failed++;
		}
		
		publishProgress("\r\n[Total]: " + total + "\r\n[Skipped]: " + skipped + 
						"\r\n[Succeed]: " + succeed + "\r\n[Failed]: " + failed);
				
		return null;
	}
	
	protected void onProgressUpdate (String... progress) {
		mActivity.mTxtStatus.append(progress[0]);
		mActivity.ScrollStatusToBotton();
	}
	
	/*protected void onPostExecute(String strPmState) {
		mActivity.mTxtStatus.setText(strPmState);
		mActivity.ScrollStatusToBotton();
	}*/
}

