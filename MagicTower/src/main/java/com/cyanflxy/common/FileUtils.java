package com.cyanflxy.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static boolean deleteFolder(File folder) {
        if (!folder.exists()) {
            return true;
        }

        File[] subFiles = folder.listFiles();
        if (subFiles != null) {
            for (File f : subFiles) {
                if (f.isFile()) {
                    if (!f.delete()) {
                        return false;
                    }
                } else {
                    if (!deleteFolder(f)) {
                        return false;
                    }
                }
            }
        }

        return folder.delete();

    }

    public static boolean saveFile(String str, File path) {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(path));
            bw.write(str);
            bw.flush();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;

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

    public static boolean copyFolder(File source, File dest) {
        if (!dest.exists()) {
            if (!dest.mkdir()) {
                return false;
            }
        }

        String[] sourceFiles = source.list();
        if (!Utils.isArrayEmpty(sourceFiles)) {
            for (String file : sourceFiles) {
                File src = new File(source, file);
                File dst = new File(dest, file);
                if (src.isFile()) {
                    if (!copyFile(src, dst)) {
                        return false;
                    }
                } else {
                    if (!copyFolder(src, dst)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean copyFile(File src, File dst) {
        InputStream is = null;
        OutputStream os = null;
        byte[] buffer = new byte[1024];

        try {
            is = new FileInputStream(src);
            os = new FileOutputStream(dst);

            while (true) {
                int len = is.read(buffer);
                if (len <= 0) {
                    break;
                }

                os.write(buffer, 0, len);
            }

            os.flush();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

    }

    public static String getFileContent(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return getInputStreamString(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public static String getInputStreamString(InputStream is) throws IOException {
        byte[] buffer = new byte[is.available()];
        int len = is.read(buffer);
        return new String(buffer, 0, len, "utf-8");
    }
}
