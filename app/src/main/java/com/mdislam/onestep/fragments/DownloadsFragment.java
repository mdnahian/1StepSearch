package com.mdislam.onestep.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdislam.onestep.R;
import com.mdislam.onestep.activities.ViewMediaActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mdislam on 12/26/15.
 */
public class DownloadsFragment extends Fragment {

    private ListView downloadsList;

    private String currentDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/OneStepSearch/";
    private ArrayList<String> directoy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.downloads_fragment, container, false);

        downloadsList = (ListView) rootView.findViewById(R.id.downloadsList);

        directoy = new ArrayList<>();

        changeDirectory(currentDirectory);

        return rootView;
    }



    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<String>();
        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }

        return MyFiles;
    }


    private void changeDirectory(String newDirectory){

        directoy.clear();

        directoy = GetFiles(newDirectory);

        try {
            if (directoy.size() == 0) {
                directoy.add(0, "BACK");
            }
        } catch (NullPointerException e){
            directoy = new ArrayList<>();
        }

        if(!currentDirectory.equals(Environment.getExternalStorageDirectory().getAbsolutePath()+"/OneStepSearch/")){
            directoy.add(0, "BACK");
        }

        downloadsList.setAdapter(new DirectoryAdapter());


    }



    private class DirectoryAdapter extends ArrayAdapter<String>{

        public DirectoryAdapter() {
            super(getActivity().getApplicationContext(), R.layout.downloads_list_item, directoy);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final String name = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.downloads_list_item, parent, false);
            }

            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            TextView fileName = (TextView) convertView.findViewById(R.id.fileName);
            ImageView deleteBtn = (ImageView) convertView.findViewById(R.id.deleteBtn);

            File file = new File(currentDirectory+name);
            if(file.exists()){
                if(file.isDirectory()){

                    fileName.setText(name);

                    icon.setImageResource(R.drawable.folder);

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentDirectory += (name+"/");
                            changeDirectory(currentDirectory);
                        }
                    });
                } else{

                    fileName.setText(name);

                    icon.setImageResource(R.drawable.file);

                    fileName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity().getApplicationContext(), ViewMediaActivity.class);
                            intent.putExtra("uri", currentDirectory + name);
                            startActivity(intent);
                        }
                    });

                    deleteBtn.setVisibility(View.VISIBLE);

                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            (new AlertDialog.Builder(getActivity())
                                    .setTitle("Deleting File!")
                                    .setMessage("Are you sure you want to delete " + name + "?")
                                    .setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            File file = new File(currentDirectory + name);
                                            if (file.delete()) {
                                                Toast.makeText(getActivity().getApplicationContext(), "File Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity().getApplicationContext(), "Failed to Delete File", Toast.LENGTH_SHORT).show();
                                            }

                                            changeDirectory(currentDirectory);
                                        }
                                    })).show();
                        }
                    });

                }
            } else{
                icon.setImageResource(R.drawable.thumbnail);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()+"/OneStepSearch/";
                        changeDirectory(currentDirectory);
                    }
                });
            }



            return convertView;
        }
    }


}
