package com.ryan.utility;

import android.app.*;
import android.os.*;
import android.view.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.widget.*;
import android.content.pm.*;
import android.content.pm.PackageManager.*;
import java.text.*;
import java.util.*;
import android.util.*;
import android.content.res.*;

public class MainActivity extends Activity
{
	public static MainActivity t;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		t=this;
		setContentView(R.layout.splash);
		getActionBar().hide();
		findViewById(R.id.splashLayout).postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				ArrayList<PInfo> apps=getInstalledApps(false);
				View.OnClickListener ocl=new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						setContentView(R.layout.appdata);
						PInfo data=(PInfo)v.getTag();
						((ImageView)findViewById(R.id.appdataIcon)).setImageDrawable(data.icon);
						((TextView)findViewById(R.id.appdataName)).setText(data.applicationName);
						((TextView)findViewById(R.id.appdataPackage)).setText(data.packageName);
						((TextView)findViewById(R.id.appdataVersion)).setText(data.versionName);
						((TextView)findViewById(R.id.appdataVersionCode)).setText(Integer.toString(data.versionCode));
						DateFormat df=DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
						df.setCalendar(Calendar.getInstance());
						df.setTimeZone(TimeZone.getDefault());
						PackageInfo packInfo=null;
						try
						{
							packInfo=getPackageManager().getPackageInfo(data.packageName, PackageManager.GET_PERMISSIONS);
						}
						catch (PackageManager.NameNotFoundException ex)
						{
						}
						if (packInfo!=null)
						{
							((TextView)findViewById(R.id.appdataInstall)).setText(df.format(new Date((packInfo.firstInstallTime))));
							((TextView)findViewById(R.id.appdataUpdate)).setText(df.format(new Date((packInfo.lastUpdateTime))));
							String[] permissions=packInfo.requestedPermissions;
							if (permissions!=null)
							{
								((TextView)findViewById(R.id.appdataPermissionInitial)).setText(permissions[0]);
								LinearLayout l=(LinearLayout)findViewById(R.id.appdataPermissionsArray);
								LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); // All permissions use the same LayoutParams except the first
								lp.setMargins(0, 10, 0, 0);
								for (int i=1; i<permissions.length; ++i)
								{
									TextView tv=new TextView(t);
									tv.setText(permissions[i]);
									l.addView(tv, lp);
								}
								permissions=null;
							}
							ApplicationInfo appInfo=packInfo.applicationInfo;
							if (appInfo!=null)
							{
								((TextView)findViewById(R.id.appdataAppDataDir)).setText(appInfo.dataDir);
								((TextView)findViewById(R.id.appdataProcess)).setText(appInfo.processName);
								((TextView)findViewById(R.id.appdataTarget)).setText(Integer.toString(appInfo.targetSdkVersion));
								((TextView)findViewById(R.id.appdataTargetName)).setText(versionName(appInfo.targetSdkVersion));
								((TextView)findViewById(R.id.appdataOSVersion)).setText(Integer.toString(Build.VERSION.SDK_INT));
								((TextView)findViewById(R.id.appdataOSVersionName)).setText(versionName(Build.VERSION.SDK_INT));
								((TextView)findViewById(R.id.appdataOSVersionNumber)).setText(Build.VERSION.RELEASE);
							}
							else
								findViewById(R.id.appdataAppLayout).setVisibility(View.GONE);
						}
						else
						{
							findViewById(R.id.appdataPermissionsLayout).setVisibility(View.GONE);
							findViewById(R.id.appdataAppLayout).setVisibility(View.GONE);
							TextView tv=new TextView(t);
							tv.setText("WARNING: Unable to reload PackageInfo");
							RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
							lp.addRule(RelativeLayout.BELOW, R.id.appdataUpdate);
							lp.setMargins(0, 10, 0, 0);
							((RelativeLayout)findViewById(R.id.appdataInnerLayout)).addView(tv, lp);
							Log.wtf("AppInfo", "Unable to load PackageInfo on second try for package "+data.packageName);
						}
					}
				};
				setContentView(R.layout.main);
				LinearLayout lay=(LinearLayout)findViewById(R.id.mainAppLayout);
				for (int i=0; i<apps.size(); ++i)
				{
					LinearLayout l=apps.get(i).getDisplayableLayout(t);
					l.setOnClickListener(ocl);
					l.setBackgroundDrawable(getResources().getDrawable(R.drawable.appentry));
					lay.addView(l);
				}
			}
		}, 100);
	}
	
	public ArrayList<PInfo> getInstalledApps(boolean getSysPackages)
	{
		ArrayList<PInfo> out=new ArrayList<PInfo>();
		List<PackageInfo> tmp=getPackageManager().getInstalledPackages(0);
		for (int i=0; i<tmp.size(); ++i)
		{
			PackageInfo p=tmp.get(i);
			if ((!getSysPackages) && (p.versionName==null || "".equals(p)))
				continue;
			PInfo n=new PInfo();
			n.applicationName=p.applicationInfo.loadLabel(getPackageManager()).toString();
			n.packageName=p.packageName;
			n.versionName=p.versionName;
			n.versionCode=p.versionCode;
			n.icon=p.applicationInfo.loadIcon(getPackageManager());
			out.add(n);
		}
		return out;
	}
	
	public static int DPtoPX (int dp)
	{
		DisplayMetrics dm=t.getResources().getDisplayMetrics();
		return Math.round(dp*(dm.xdpi/DisplayMetrics.DENSITY_DEFAULT));
	}
	
	public static String versionName(int sdkVers)
	{
		switch (sdkVers)
		{
		case Build.VERSION_CODES.BASE:
			return "Base Android";
		case Build.VERSION_CODES.BASE_1_1:
			return "Android first update";
		case Build.VERSION_CODES.CUPCAKE:
			return "Cupcake";
		case Build.VERSION_CODES.DONUT:
			return "Donut";
		case Build.VERSION_CODES.ECLAIR:
		case Build.VERSION_CODES.ECLAIR_0_1:
		case Build.VERSION_CODES.ECLAIR_MR1:
			return "Eclair";
		case Build.VERSION_CODES.FROYO:
			return "Froyo";
		case Build.VERSION_CODES.GINGERBREAD:
		case Build.VERSION_CODES.GINGERBREAD_MR1:
			return "Gingerbread";
		case Build.VERSION_CODES.HONEYCOMB:
		case Build.VERSION_CODES.HONEYCOMB_MR1:
		case Build.VERSION_CODES.HONEYCOMB_MR2:
			return "Honeycomb";
		case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
		case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
			return "Ice Cream Sandwich";
		case Build.VERSION_CODES.JELLY_BEAN:
		case Build.VERSION_CODES.JELLY_BEAN_MR1:
		case Build.VERSION_CODES.JELLY_BEAN_MR2:
			return "Jelly Bean";
		case Build.VERSION_CODES.KITKAT:
			return "KitKat";
		case Build.VERSION_CODES.KITKAT_WATCH:
			return "KitKat Watch Edition";
		case Build.VERSION_CODES.LOLLIPOP:
		case Build.VERSION_CODES.LOLLIPOP_MR1: // Update from here
			return "Lollipop";
		/*case Build.VERSION_CODES.CUR_DEVELOPMENT: // Comment/Uncomment this as needed
			return "Development version";*/
		default:
			return "<Unknown>";
		}
	}
}

class PInfo
{
	public String applicationName="";
	public String packageName="";
	public String versionName="";
	public int versionCode=0;
	public Drawable icon;
	
	public LinearLayout getDisplayableLayout(Activity c)
	{
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 0, 0, 15);
		LinearLayout out=new LinearLayout(c);
		out.setLayoutParams(lp);
		out.setOrientation(LinearLayout.HORIZONTAL);
		out.setTag(this);
		ImageView iv=new ImageView(c);
		iv.setImageDrawable(icon);
		lp=new LinearLayout.LayoutParams(MainActivity.DPtoPX(48), MainActivity.DPtoPX(48));
		lp.setMargins(1, 0, 1, 0);
		out.addView(iv, lp);
		TextView tv=new TextView(c);
		tv.setText(applicationName);
		lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.gravity=Gravity.CENTER;
		out.addView(tv, lp);
		return out;
	}
}
