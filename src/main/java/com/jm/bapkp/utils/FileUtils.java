package com.jm.bapkp.utils;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class FileUtils {  
	  
    /** 
     * the traditional io way  
     * @param filename 
     * @return 
     * @throws IOException 
     */  
    public static byte[] toByteArray(String filename) throws IOException{  
          
        File f = new File(filename);  
        if(!f.exists()){  
            throw new FileNotFoundException(filename);  
        }  
  
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int)f.length());  
        BufferedInputStream in = null;  
        try{  
            in = new BufferedInputStream(new FileInputStream(f));  
            int buf_size = 1024;  
            byte[] buffer = new byte[buf_size];  
            int len = 0;  
            while(-1 != (len = in.read(buffer,0,buf_size))){  
                bos.write(buffer,0,len);  
            }  
            return bos.toByteArray();  
        }catch (IOException e) {  
            e.printStackTrace();  
            throw e;  
        }finally{  
            try{  
                in.close();  
            }catch (IOException e) {  
                e.printStackTrace();  
            }  
            bos.close();  
        }  
    }  
      
      
    /** 
     * NIO way 
     * @param filename 
     * @return 
     * @throws IOException 
     */  
    public static byte[] toByteArray2(String filename)throws IOException{  
          
        File f = new File(filename);  
        if(!f.exists()){  
            throw new FileNotFoundException(filename);  
        }  
          
        FileChannel channel = null;  
        FileInputStream fs = null;  
        try{  
            fs = new FileInputStream(f);  
            channel = fs.getChannel();  
            ByteBuffer byteBuffer = ByteBuffer.allocate((int)channel.size());  
            while((channel.read(byteBuffer)) > 0){  
                // do nothing  
//              System.out.println("reading");  
            }  
            return byteBuffer.array();  
        }catch (IOException e) {  
            e.printStackTrace();  
            throw e;  
        }finally{  
            try{  
                channel.close();  
            }catch (IOException e) {  
                e.printStackTrace();  
            }  
            try{  
                fs.close();  
            }catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
      
      
    /** 
     * Mapped File  way 
     * MappedByteBuffer 可以在处理大文件时，提升性能 
     * @param filename 
     * @return 
     * @throws IOException 
     */  
    public static byte[] toByteArray3(String filename)throws IOException{  
          
        FileChannel fc = null;  
        try{  
            fc = new RandomAccessFile(filename,"r").getChannel();  
            MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
            System.out.println(byteBuffer.isLoaded());  
            byte[] result = new byte[(int)fc.size()];  
            if (byteBuffer.remaining() > 0) {  
//              System.out.println("remain");  
                byteBuffer.get(result, 0, byteBuffer.remaining());  
            }
            return result;  
        }catch (IOException e) {  
            e.printStackTrace();  
            throw e;  
        }finally{  
            try{  
                fc.close();  
            }catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
    
    
    /**
	 * 下载网络文件
	 */
	 public static void  downloadNet(String urlPath, String savePath) throws Exception {
		// 下载网络文件
	        int bytesum = 0;
	        int byteread = 0;
	        URL url = new URL(urlPath);
	        try {
	            URLConnection conn = url.openConnection();
	            InputStream inStream = conn.getInputStream();
	            FileOutputStream fs = new FileOutputStream(savePath);
	            byte[] buffer = new byte[1204];
	            int length;
	            while ((byteread = inStream.read(buffer)) != -1) {
	                bytesum += byteread;
//	                System.out.println(bytesum);
	                fs.write(buffer, 0, byteread);
	            }
	            fs.flush();
	            fs.close();
	            inStream.close();
	        } catch (Exception e) {
	            throw e;
	        }
	    }
	 
	 public static byte[]  getBytesFromNet(String urlPath) throws Exception {

	        URL url = new URL(urlPath);
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	        try {
	            URLConnection conn = url.openConnection();
	            InputStream inStream = conn.getInputStream();
		        byte[] data = new byte[1204];  
		        int count = -1;  
		        while ((count = inStream.read(data, 0, data.length)) != -1)  
		            outStream.write(data, 0, count);  
		        data = null;  
		        inStream.close();
		        return outStream.toByteArray();  
	           
	        } catch (Exception e) {
	            throw e;
	        }
	    }
	// 将InputStream转换成byte数组  
	    public static byte[] InputStreamTOByte(InputStream in) throws IOException {  
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	        byte[] data = new byte[1204];  
	        int count = -1;  
	        while ((count = in.read(data, 0, data.length)) != -1)  
	            outStream.write(data, 0, count);  
	        data = null;  
	        return outStream.toByteArray();  
	  
	    }  
	    
	  //将byte数组写入文件  
	    public static void writeFile(String path, byte[] content) throws IOException {  
	  
	        FileOutputStream fos = new FileOutputStream(path);  
	  
	        fos.write(content);  
	        fos.close();  
	    }  
	    
}  