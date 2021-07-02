/*
 */

package com.gking.simplemusicplayer.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;

import java.io.File;

import gtools.managers.GHolder;

import static com.gking.simplemusicplayer.impl.MyApplicationImpl.CoverImg;

public abstract class BaseActivity extends AppCompatActivity {
    public <T extends View> T f(int id) {
        return super.findViewById(id);
    }
    public void loadPictures(){
        GHolder<String, Bitmap> gHolder=new GHolder<>();
        DisplayMetrics dm=getResources().getDisplayMetrics();
        int rw= (int) (dm.density*80);
        for (File f: CoverImg.listFiles()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            BitmapFactory.decodeFile(f.getAbsolutePath(),options);
            int w=options.outWidth;
            options=new BitmapFactory.Options();
            options.inJustDecodeBounds=false;
            options.inSampleSize=rw/w;
            options.inPreferredConfig= Bitmap.Config.RGB_565;
            Bitmap bitmap=BitmapFactory.decodeFile(f.getAbsolutePath(),options);
            gHolder.add(f.getName(),bitmap);
        }
        GHolder.standardInstance.add("PlaylistPictures",gHolder);
        System.gc();
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
    public void setContext(Activity context) {
        this.context = context;
    }
    View y;
    Activity context=this;
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
    }
    public void makeToast(Object msg){
        Toast.makeText(getApplication(),msg.toString(),Toast.LENGTH_LONG).show();
    }
}
