/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * author: http://www.java2s.com/Tutorial/Java/0320__Network/SavebinaryfilefromURL.htm
 */

package ldc.bibleget;


import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class BinDown {

    public void down(String ur, String fName) throws Exception {

    URL u = new URL(ur);
    URLConnection uc = u.openConnection();
    String contentType = uc.getContentType();
    int contentLength = uc.getContentLength();
    if (contentType.startsWith("text/") || contentLength == -1) {
      throw new IOException("This is not a binary file.");
    }
    InputStream raw = uc.getInputStream();
    InputStream in = new BufferedInputStream(raw);
    byte[] data = new byte[contentLength];
    int bytesRead = 0;
    int offset = 0;
    while (offset < contentLength) {
      bytesRead = in.read(data, offset, data.length - offset);
      if (bytesRead == -1)
        break;
      offset += bytesRead;
    }
    in.close();

    if (offset != contentLength) {
      throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
    }

    String filename = fName; //u.getFile().substring(filename.lastIndexOf('/') + 1);
    FileOutputStream out = new FileOutputStream(filename);
    out.write(data);
    out.flush();
    out.close();
    System.out.println("File Writen: " + fName);
  }

    public void mkdir(String dir){
        File f = new File(dir);
        try{
            if(f.mkdir())
                System.out.println("Created: " + dir);
            else
                System.out.println("Not Created: " + dir);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

