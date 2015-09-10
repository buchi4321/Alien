package com.cyanflxy.game.bean;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BeanParent {

    private String savePath;

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public void saveFile() {
        Gson gson = new Gson();
        String str = gson.toJson(this);

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(savePath));
            bw.write(str);
            bw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

}
