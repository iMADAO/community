package com.madao.web.util;

import com.madao.api.utils.KeyUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.XWPFConverterException;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileConverterUtil {

//    public static void main(String[] args){
//        String result = convert2Html("/root/Documents/35-郑泽伟-实验六.docx");
//        System.out.println(result);
//    }

    public static String writeFile(String content, String path) throws Exception{
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        File file = null;
        String randomName = KeyUtil.genUniquKey();
        randomName += ".html";
        try {
            Path realPath = Paths.get(path, randomName);

            if(Files.notExists(Paths.get("path"))){
                Files.createDirectory(Paths.get("path"));
            }
            if(Files.notExists(realPath)){
                Files.createFile(realPath);
            }

            file = realPath.toFile();

            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(content);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            throw fnfe;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw ioe;
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException ie) {
            }
        }

        return file.getName();
    }

    /**
     * 将word转换成html 支持 .doc and .docx
     *
     * @param fileName
     * word文件在 数据库中保存的FTP的文件访问路径
     * @throws TransformerException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static String convert2Html(String fileName) throws Exception{
        Path filePath = Paths.get(fileName);
        //生成的html页面和存放的主路径
        Path targetPath = Paths.get("/usr/local/apache-tomcat-7.0.79/webapps/html");
        String accessPath = "http://localhost:8080/html/";
        //生成的图片存放的主路径
        Path picPath = Paths.get(targetPath.toString(), "img");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream inputStream = null;
        XWPFDocument document = null;
        try {
            inputStream = Files.newInputStream(Paths.get(fileName));
            //用POI解析这个word文件
            document = new XWPFDocument(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        //设置word文档中图片保存的路径
        XHTMLOptions options = XHTMLOptions.create();
        //设置生成的 html页面搜索图片的路径
        options.URIResolver(new BasicURIResolver("/html/img"));
        if(Files.notExists(picPath)){
            try {
                Files.createDirectory(picPath);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
        options.setExtractor(new FileImageExtractor(picPath.toFile()));
        options.setIgnoreStylesIfUnused(false);
        options.setFragment(true);

        try {
            System.out.println("document...." + document);
            System.out.println("out...." + out);
            System.out.println("options...." + options);
            XHTMLConverter.getInstance().convert(document, out, options);
        } catch (XWPFConverterException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }finally {
            try {
                if(out!=null)out.close();
                if(document!=null)document.close();
                if(inputStream!=null)inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //写入文件到tomcat的目录中，之后可以直接访问
        String newFileName = writeFile(new String(out.toByteArray()), targetPath.toString());
        accessPath += newFileName;
        return accessPath;
    }
}
