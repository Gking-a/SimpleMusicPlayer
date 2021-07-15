/*
 */

package com.gking.simplemusicplayer.base;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.SongActivity;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.gking.simplemusicplayer.impl.MyApplicationImpl.myApplication;

public abstract class BaseActivity extends AppCompatActivity {
    public <T extends View> T f(int id) {
        return super.findViewById(id);
    }
    public MyApplicationImpl getMyApplication(){
        return ((MyApplicationImpl) getApplication());
    }
    public Activity getContext() {
        return context;
    }
    boolean loadControlPanel=false;

    public void setLoadControlPanel(boolean loadControlPanel) {
        this.loadControlPanel = loadControlPanel;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(loadControlPanel)
            loadControlPanel();
    }

    @Override
    protected void onPause() {
        View v=y;
        View control=((MyApplicationImpl) getApplication()).controlPanel;
        ViewGroup parentViewGroup = (ViewGroup) control.getParent();
        if(parentViewGroup!=null) {
            int index = parentViewGroup.indexOfChild(control);
            parentViewGroup.removeView(control);
            control.setLayoutParams(control.getLayoutParams());
            parentViewGroup.addView(v, index);
        }
        super.onPause();
    }
    protected final void init(Activity activity,boolean loadControlPanel){
        this.context=activity;
        this.loadControlPanel=loadControlPanel;
    }
    public void setContext(Activity context) {
        this.context = context;
    }
    View y;
    Activity context=this;
    //when onStart(),call this
    private void loadControlPanel(){
        View v=context.findViewById(R.id.control);
        y=v;
        View control=((MyApplicationImpl) getApplication()).controlPanel;
        ViewGroup parentViewGroup = (ViewGroup) v.getParent();
        if(parentViewGroup!=null) {
            int index = parentViewGroup.indexOfChild(v);
            parentViewGroup.removeView(v);
            control.setLayoutParams(v.getLayoutParams());
            parentViewGroup.addView(control, index);
        }
        View.OnClickListener onClickListener=v1 -> startActivity(new Intent(this, SongActivity.class));
        myApplication.controlPanel.setOnClickListener(onClickListener);
        myApplication.Cover.setOnClickListener(onClickListener);
        myApplication.Name.setOnClickListener(onClickListener);
        myApplication.Author.setOnClickListener(onClickListener);
    }
    public void makeToast(Object msg){
        Toast.makeText(getApplication(),msg.toString(),Toast.LENGTH_LONG).show();
    }
    public boolean ifOps(){
        if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService( APP_OPS_SERVICE);
            if (appOpsMgr == null) {
                return true;
            } else {
                try {
                    Class cls = Class.forName("android.content.Context");
                    Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                    declaredField.setAccessible(true);
                    Object obj = declaredField.get(cls);
                    if (!(obj instanceof String)) {
                        return false;
                    }
                    String str2 = (String) obj;
                    obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                    cls = Class.forName("android.app.AppOpsManager");
                    Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                    declaredField2.setAccessible(true);
                    Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                    int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), getPackageName());
                    return result == declaredField2.getInt(cls);
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }
}
