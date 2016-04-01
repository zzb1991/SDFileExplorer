package com.example.sdfileexplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class SDFileExplorer extends Activity {

	ListView listView;
	TextView textView;  
	//记录当前的父文件夹
	File currentParent;
	//记录当前路径下所有文件的文件数组
	File[] currentFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.list);
        textView=(TextView)findViewById(R.id.path);
        File root=new File("/mnt/sdcard/");
        if(root.exists()){
        	currentParent=root;
        	currentFiles=currentParent.listFiles();
        	inflateListView(currentFiles);
        }
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(currentFiles[position].isFile()) return;
				File[] temp=currentFiles[position].listFiles();
				if(temp==null || temp.length==0){
					Toast.makeText(SDFileExplorer.this, "当前路径不可访问或该路径下没有文件", Toast.LENGTH_LONG).show();
				}else{
					currentParent=currentFiles[position];
					currentFiles=temp;
					inflateListView(currentFiles);
				}
			}
		});
        Button parent=(Button)findViewById(R.id.parent);
        parent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(!currentParent.getCanonicalPath().equals("/mnt/sdcard")){
						currentParent=currentParent.getParentFile();
						currentFiles=currentParent.listFiles();
						inflateListView(currentFiles);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
    }

    private void inflateListView(File[] files) {
		List<Map<String,Object>> listItems=new ArrayList<Map<String,Object>>();
		for(int i=0;i<files.length;i++){
			Map<String, Object> listItem=new HashMap<String, Object>();
			if(files[i].isDirectory()){
				listItem.put("icon", R.drawable.folder);
			}else{
				listItem.put("icon", R.drawable.file);
			}
			listItem.put("fileName", files[i].getName());
			listItems.add(listItem);
		}
		SimpleAdapter simpleAdapter=new SimpleAdapter(this, listItems, R.layout.list_item, 
				new String[]{"icon","fileName"}, new int[]{R.id.icon,R.id.filename});
		listView.setAdapter(simpleAdapter);
		try {
			textView.setText("当前路径为： "+currentParent.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
