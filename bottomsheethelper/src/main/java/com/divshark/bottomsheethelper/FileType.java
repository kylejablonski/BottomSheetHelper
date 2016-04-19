package com.divshark.bottomsheethelper;

/**
 * Created by kyle.jablonski on 4/18/16.
 */
public enum FileType {
    BITMAP(0), TEXT(1);
    int fileType;
    FileType(int fileType){
        this.fileType = fileType;
    }

    int getFileType(){
        return fileType;
    }
}
