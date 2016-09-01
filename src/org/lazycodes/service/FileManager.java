/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lazycodes.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author junel
 */
public class FileManager {
    
    private List<String> files = new ArrayList<>();
    
    public void loadMediaFiles(File folder){

        try{
            if(folder.isDirectory()){

                for (final File fileEntry : folder.listFiles()) {
                   // System.out.println(fileEntry.toString());
                    if (fileEntry.isDirectory()) {
                    //  System.out.println(fileEntry.toString());
                        loadMediaFiles(fileEntry);
                    } else {
                        fileEntry.getName();
                        
                        if(fileEntry.getPath().endsWith(".mp3")){
                            files.add(new File(fileEntry.getPath()).toURI().toString());
                        }                       
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        
    }
    
    public List<String> getFiles(){
        return files;
    }
    
}
