package com.cehome.task.client;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;

import jsharp.util.FileKit;
import org.apache.commons.io.FilenameUtils;

public class LogShow {
    protected String name = null;
    protected String charset = "UTF-8";
    protected File[] files = null;
    private String logPath = ""; // path only
    private int fileCount;

    public LogShow(String filename, String charset) {

        this.name = FileKit.extractFileName(filename);
        this.charset = charset;
        logPath = FileKit.extractFilePath(filename);
        // find log files and sort
        File f = new File(logPath);
        files = f.listFiles(new FileFilter() {
            public boolean accept(File f) {
                String tmp = f.getName();//.toLowerCase();
                if( FilenameUtils.wildcardMatch(tmp,name)) {
                    return true;
                }
                return false;
            }
        });
        if(files==null) files=new File[0];
        Arrays.sort(files, new DescFileComparator());
        fileCount = files.length;
        mkdirs(logPath);

    }

    protected void mkdirs(String path) {
        if (path != null) {
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
            if (!f.exists()) {
                System.out.println("Log path " + path + " is not exists");
            }
        }

    }

    /**
     *
     * @param w
     * @param fileLimit
     * @param pageNo
     * @param pageSize
     * @param extraSize
     *            太复杂了，先不弄。 额外返回的数据量，如果设置了此参数，则实际返回数据量为
     *            extraSize+pageSize+extraSize ,int extraSize
     * @throws IOException
     */
    public void displayReverse(Writer w, int fileLimit, long pageNo, long pageSize) throws IOException {
        long begin = (pageNo - 1) * pageSize;
        long end = (pageNo) * pageSize;

        long absfileBegin = 0;
        int index1 = -1;
        int index2 = -1;
        long posi1 = -1;
        long posi2 = -1;
        int day1 = 0;
        int day2 = 0;
        if (fileLimit >= 0) {
            day1 = fileLimit;
            day2 = 0;
        } else {
            day1 = fileCount - 1;
            day2 = 0;
        }
        // newfile -> oldfile
        for (int i = day2; i <= day1; i++)
        // for (int i = day1; i >= day2; i--)
        {
            String name = getName(i);
            File f = new File(logPath + File.separator + name);
            if (f.exists()) {
                long fileLength = f.length();// getFileCharLength(logPath +
                // File.separator +
                // name+".len");
                if (fileLength == 0)
                    continue;

                if (begin >= absfileBegin && begin <= absfileBegin + fileLength) {
                    index1 = i;
                    posi1 = absfileBegin + fileLength - begin;
                }
                if (end >= absfileBegin && end < absfileBegin + fileLength) {
                    index2 = i;
                    posi2 = absfileBegin + fileLength - end;
                }

                absfileBegin += fileLength;
                // System.out.println(name+" absfileBegin:"+absfileBegin);

            }

        }

        // System.out.println("end1");
        if (index1 != -1) {
            if (index2 == -1) {
                index2 = day1;// logDays-1; // 超出文件范围了
                posi2 = 0;
            }

            if (index1 == index2) // 在同一个文件
            {
                String file = logPath + File.separator + getName(index1);
                // +2 -2 就是为了多取几个字符，宁可多也不少
                if (posi2 >= 2)
                    posi2 -= 2;
                // if (posi1 != -1)
                posi1 += 2;
                // long len=new File(file).length();
                doWrite(file, w, posi2, posi1);

                // else dowrite(file,w,)
            } else {
                // for (int i = index1; i >= index2; i--)
                // old -> new
                for (int i = index2; i >= index1; i--) {
                    String file = logPath + File.separator + getName(i);
                    long b = 0;
                    long e = -1;

                    if (i == index2) {
                        b = posi2;
                        if (b >= 2)
                            b -= 2;
                    }
                    if (i == index1) {
                        e = posi1 + 2;
                    }

                    // if (i == index2 && posi2 != -1) e =new
                    // File(file).length()-( posi2 + 2);
                    // if (i == index1 && posi1 >= 2) b =new
                    // File(file).length()-( posi1 - 2);

                    doWrite(file, w, b, e);

                }
            }
        }

        // System.out.println("end2");

    }

    public void display(Writer w, int fileLimit, long pageNo, long pageSize) throws IOException {
        long begin = (pageNo - 1) * pageSize;
        long end = (pageNo) * pageSize;

        long absfileBegin = 0;

        // File f1=null;
        // File f2=null;
        long fileBegin = 0;
        long fileEnd;
        int index1 = -1;
        int index2 = -1;
        long posi1 = -1;
        long posi2 = -1;
        int day1 = 0;
        int day2 = 0;
        if (fileLimit >= 0) {
            day1 = fileLimit;
            day2 = 0;
        } else {
            day1 = fileCount - 1;
            day2 = 0;
        }

        for (int i = day1; i >= day2; i--) {
            String name = getName(i);
            File f = new File(logPath + File.separator + name);
            if (f.exists()) {
                long fileLength = f.length();// getFileCharLength(logPath +
                // File.separator +
                // name+".len");
                if (fileLength == 0)
                    continue;

                if (begin >= absfileBegin && begin <= absfileBegin + fileLength) {
                    index1 = i;
                    posi1 = begin - absfileBegin;
                }
                if (end >= absfileBegin && end < absfileBegin + fileLength) {
                    index2 = i;
                    posi2 = end - absfileBegin;
                }

                absfileBegin += fileLength;
                // System.out.println(name+" absfileBegin:"+absfileBegin);

            }

        }

        if (index1 != -1) {
            if (index2 == -1)
                index2 = day2;// logDays-1; // 超出文件范围了

            if (index1 == index2) // 在同一个文件
            {
                String file = logPath + File.separator + getName(index1);
                // +2 -2 就是为了多取几个字符，宁可多也不少
                if (posi1 >= 2)
                    posi1 -= 2;
                if (posi2 != -1)
                    posi2 += 2;
                doWrite(file, w, posi1, posi2);

                // else dowrite(file,w,)
            } else {
                for (int i = index1; i >= index2; i--) {
                    String file = logPath + File.separator + getName(i);
                    long b = 0;
                    long e = -1;
                    if (i == index1 && posi1 >= 2)
                        b = posi1 - 2;
                    if (i == index2 && posi2 != -1)
                        e = posi2 + 2;
                    doWrite(file, w, b, e);

                }
            }
        }

    }

    /*
     * 由于通过字节定位，也许定位的地方只是“半个汉字”，经测试，发现会段首可能出现 65533这种字符（最多2个）
     */
    private void doWrite(String file, Writer w, long begin, long end) throws IOException {

        File f = new File(file);
        if (!f.exists())
            return;
        InputStream is = null;
        try {
            is = new FileInputStream(f);
            if (begin > 0)
                is.skip(begin);
            if (end < begin) {
                InputStreamReader r = new InputStreamReader(is, charset);
                int n = -1;
                while (true) {
                    n = r.read();
                    if (n == -1 || n != 65533)
                        break;

                }

                while (n != -1) {
                    w.write(n);
                    n = r.read();

                }
                w.flush();
                r.close();
            } else {
                if (f.length() < end)
                    end = f.length();
                byte[] bs = new byte[(int) (end - begin)];
                is.read(bs);

                w.write(new String(bs, charset));
                w.flush();
            }
        } finally {
            if (is != null)
                is.close();
        }
    }

    /**
     * 得到log文件名 0:today 1:yestoday..
     */
    public String getName(int index) {

        if (files.length == 0)
            return name;
        return files[index].getName();

        //if (index < files.length)
        //	return files[files.length - 1 - index].getName();
        //else
        //	return files[files.length - 1].getName();

    }

    public long getTotalSize() {

        long n = 0;
        for (int i = 0; i < files.length; i++)
            n += files[i].length();
        return n;
    }

    public File[] getFiles() {
        return files;
    }

    public static void main(String[] args) throws Exception {
        String path = "D:\\temp\\a";
        String name="dhh_award_prize.log";
        LogShow logShow=new LogShow(path+"\\"+name,"GBK");
        int pageSize=1024;
        BufferedReader reader=new BufferedReader(new InputStreamReader( System.in));
        while (true)
            try {
                System.out.print("pageNo=");
                int pageNo= Integer.parseInt(reader.readLine());
                java.io.PrintWriter w=new java.io.PrintWriter(System.out);
                logShow.displayReverse(w, -1, pageNo, pageSize);
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

}

class DescFileComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        File f1 = (File) o1;
        File f2 = (File) o2;

        long n = (f2.lastModified() - f1.lastModified());
        if (n > 0)
            return 1;
        if (n < 0)
            return -1;
        return 0;

    }
}
