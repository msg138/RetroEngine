package com.retro.engine.loading;

import com.jogamp.opengl.GL2;
import com.retro.engine.debug.Debug;
import com.retro.engine.ui.Image;
import com.retro.engine.util.file.RetroFile;

/**
 * Created by Michael on 7/19/2016.
 */
public class LoadData {

    private String m_fileName;

    public LoadData(String fname){
        m_fileName = fname;
    }

    public void load(GL2 gl){
        RetroFile file = new RetroFile(m_fileName);
        String root = file.getLineData(0).getData();
        for(int i=1;i<file.getLines();i++)
        {
            RetroFile.LineData ld = file.getLineData(i);
            String[] loadData = ld.getDataSplit("\\|");
            Debug.out("Loading : " + loadData[0] + " " + root+loadData[1] + " "+loadData[2]);
            if(loadData.length >= 3)
                loadData(loadData[0], root + loadData[1], loadData[2], gl);
        }
    }

    public boolean loadData(String assetType, String assetFile, String assetName, GL2 gl){
        switch(assetType){
            case "IMAGE":
                new Image(assetFile, assetName, gl);
                return true;
            case "MODEL":

                return true;
        }

        return false;
    }

    public void lastMinuteLoads(GL2 gl){
        // Can be overridden in the retroprocess.
    }
}
