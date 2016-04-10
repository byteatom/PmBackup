package chenhua.pmbackup;

import java.io.FileWriter;
import java.util.List;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.AsyncTask;

class Backup extends AsyncTask <MainActivity, Void, String> {
	
	MainActivity mActivity;
	static final String mFilePath = "/mnt/extSdCard/software/PmBackup.sh";
	
	protected String doInBackground(MainActivity... activity) {
		
		mActivity = activity[0];
		
		StringBuilder names = new StringBuilder();
		PackageManager pm = mActivity.getApplicationContext().getPackageManager();		
		List<PackageInfo> packs = pm.getInstalledPackages(PackageManager.GET_RECEIVERS | PackageManager.GET_SERVICES);
		int i = 0;
		for (PackageInfo packInfo : packs)
		{
			if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED == 
					pm.getApplicationEnabledSetting(packInfo.packageName) ||
			   (PackageManager.COMPONENT_ENABLED_STATE_DEFAULT == 
					pm.getApplicationEnabledSetting(packInfo.packageName) &&
				!packInfo.applicationInfo.enabled))
			{
				names.append(packInfo.packageName + "\r\n");
				i++;
			}
			if (packInfo.receivers != null)
			{
				for (ActivityInfo receiverInfo : packInfo.receivers)
				{
					if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED == 
							pm.getComponentEnabledSetting(new ComponentName(packInfo.packageName, receiverInfo.name)) ||
						(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT == 
							pm.getComponentEnabledSetting(new ComponentName(packInfo.packageName, receiverInfo.name)) &&
						!receiverInfo.enabled))
					{
						names.append(packInfo.packageName + "/" + receiverInfo.name + "\r\n");
						i++;
					}
				}
			}
			if (packInfo.services != null)
			{
				for (ServiceInfo serviceInfo : packInfo.services)
				{
					if (PackageManager.COMPONENT_ENABLED_STATE_DISABLED == 
							pm.getComponentEnabledSetting(new ComponentName(packInfo.packageName, serviceInfo.name)) ||
						(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT == 
							pm.getComponentEnabledSetting(new ComponentName(packInfo.packageName, serviceInfo.name)) &&
						!serviceInfo.enabled))
					{
						names.append(packInfo.packageName + "/" + serviceInfo.name + "\r\n");
						i++;
					}
				}
			}
		}
		
		StringBuilder log = new StringBuilder(names);		
		log.append("\r\nTotal " + i + " components disabled");
		
		/*if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			log.append("\r\nexternal storage not available!");
		}*/
		
		try {
			FileWriter fw = new FileWriter(mFilePath);
			fw.write(names.toString());
			fw.close();
		} catch (Exception e) {
			log.append("\r\nFailed to save file!");
		}
		
		log.append("\r\nSaved file to " + mFilePath);
		
		return log.toString();
	}
	
	protected void onPostExecute(String log) {
		mActivity.mTxtStatus.setText(log);
		mActivity.ScrollStatusToBotton();
	}
}
