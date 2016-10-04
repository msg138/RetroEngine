package com.retro.engine.util.file;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.List;

/**
 * Created by Michael on 8/5/2016.
 */
public class RetroFile {

    private File m_file;

    private boolean m_fileExists;

    private LineData[] m_lines;
    private String m_allData;

    public RetroFile(){
        m_fileExists = false;
        m_file = null;
        m_lines = null;
        m_allData = "";
    }
    public RetroFile(String fname){
        this();
        loadFile(fname);
    }
    public RetroFile(String fname, String data, boolean create, boolean append){
        this();
        writeFile(fname, data, create, append);
    }

    public int getLines(){
        if(m_lines == null)
            return 0;
        return m_lines.length;
    }
    public LineData getLineData(int ld){
        return m_lines[ld];
    }

    public String getAllData(){
        return m_allData;
    }

    public boolean fileExists()
    {
        return m_fileExists;
    }

    public boolean loadFile(String fname){
        if(m_file != null)
            m_file = null;
        m_file = new File(fname);
        if(m_file.exists()){
            try {
                List<String> str = Files.readAllLines(m_file.toPath());
                m_lines = new LineData[str.size()];
                for(int i=0;i<str.size();i++)
                {
                    m_lines[i] = new LineData(str.get(i));
                    m_allData += str.get(i) + "\n";
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            m_fileExists = true;
            return true;
        }
        m_fileExists = false;
        return false;
    }

    public boolean writeFile(String fname, String data, boolean create, boolean append){
        return writeFile(fname, data.split("\n"), create, append);
    }

    public boolean writeFile(String fname, String[] data, boolean create, boolean append){
        if(m_file != null)
            m_file = null;
        m_file = new File(fname);
        try {
            if (!m_file.exists() && create) {
                m_file.createNewFile();
            }
            if(m_file.exists() || create){
                FileWriter fw = new FileWriter(fname, append);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                for(String d : data) {
                    pw.println(d);
                }
                pw.close();
                bw.close();
                fw.close();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public static class LineData{
        private String m_data;

        public LineData(String d){
            m_data = d;
        }

        public String getData(){
            return m_data;
        }
        public String[] getDataSplit(String cs){
            return m_data.split(cs);
        }
    }
}
