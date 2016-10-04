package com.retro.engine;

import com.retro.engine.ui.Window;

/**
 * Created by Michael on 7/15/2016.
 */
public class RetroEngine {

    private static final String c_version = "RetroEngine V0.0.1";

    public static String getVersion(){
        return c_version;
    }

    public static AppProcess createApp(String title, int width, int height, RetroProcess rp){
        return new AppProcess(new Window(title, width, height, rp));
    }
}
