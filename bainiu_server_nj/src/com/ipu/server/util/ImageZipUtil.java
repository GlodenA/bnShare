package com.ipu.server.util;

 
    
public class ImageZipUtil {    
//    
//    /**  
//     * 等比例压缩图片文件<br> 先保存原文件，再压缩、上传  
//     * @param oldFile  要进行压缩的文件  
//     * @param newFile  新文件  
//     * @param width  宽度 //设置宽度时（高度传入0，等比例缩放）  
//     * @param height 高度 //设置高度时（宽度传入0，等比例缩放）  
//     * @return 返回压缩后的文件的全路径  
//     */    
//    public static String zipImageFile(File oldFile,File newFile, int width, int height)
//    {    
//        if (oldFile == null) {    
//            return null;    
//        }    
//        try {    
//            /** 对服务器上的临时文件进行处理 */    
//            Image srcFile = ImageIO.read(oldFile);    
//            int w = srcFile.getWidth(null);    
//        //  System.out.println(w);    
//            int h = srcFile.getHeight(null);    
//        //  System.out.println(h);    
//            double bili;    
//            if(width>0){    
//                bili=width/(double)w;    
//                height = (int) (h*bili);    
//            }else{    
//                if(height>0){    
//                    bili=height/(double)h;    
//                    width = (int) (w*bili);    
//                }    
//            }    
//            /** 宽,高设定 */    
//            BufferedImage tag = new BufferedImage(width, height,    
//                    BufferedImage.TYPE_INT_RGB);    
//            tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);    
//            //String filePrex = oldFile.getName().substring(0, oldFile.getName().indexOf('.'));    
//            /** 压缩后的文件名 */    
//            //newImage = filePrex + smallIcon+  oldFile.getName().substring(filePrex.length());    
//    
//            /** 压缩之后临时存放位置 */    
//            FileOutputStream out = new FileOutputStream(newFile);    
//    
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);    
//            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);    
//            /** 压缩质量 */    
//            jep.setQuality(500, true);    
//            encoder.encode(tag, jep);    
//            out.close();    
//    
//        } catch (FileNotFoundException e) {    
//            e.printStackTrace();    
//        } catch (IOException e) {    
//            e.printStackTrace();    
//        }    
//        return newFile.getAbsolutePath();    
//    }    
//    
//    /**  
//     * 按宽度高度压缩图片文件<br> 先保存原文件，再压缩、上传  
//     * @param oldFile  要进行压缩的文件全路径  
//     * @param newFile  新文件  
//     * @param width  宽度  
//     * @param height 高度  
//     * @param quality 质量  
//     * @return 返回压缩后的文件的全路径  
//     */    
//    public static String zipWidthHeightImageFile(File oldFile,File newFile, int width, int height) 
//    {    
//        if (oldFile == null) {    
//            return null;    
//        }    
//        String newImage = null;    
//        try {    
//            /** 对服务器上的临时文件进行处理 */    
//            Image srcFile = ImageIO.read(oldFile);    
//            int w = srcFile.getWidth(null);    
//        //  System.out.println(w);    
//            int h = srcFile.getHeight(null);    
//        //  System.out.println(h);    
//    
//            /** 宽,高设定 */    
//            BufferedImage tag = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);    
//            tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);    
//            //String filePrex = oldFile.substring(0, oldFile.indexOf('.'));    
//            /** 压缩后的文件名 */    
//            //newImage = filePrex + smallIcon+ oldFile.substring(filePrex.length());    
//    
//            /** 压缩之后临时存放位置 */    
//            FileOutputStream out = new FileOutputStream(newFile);    
//    
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);    
//            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);    
//            /** 压缩质量 */    
//            jep.setQuality(500, true);    
//            encoder.encode(tag, jep);    
//            out.close();    
//        } catch (FileNotFoundException e) {    
//            e.printStackTrace();    
//        } catch (IOException e) {    
//            e.printStackTrace();    
//        }    
//        return newImage;    
//    }   
//    
}