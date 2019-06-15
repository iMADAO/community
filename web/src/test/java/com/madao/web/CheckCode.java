package com.madao.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CheckCode {
    public static void main(String[] args){
        try {
            Scanner in = new Scanner(System.in);
            String path = in.next();
            List<Integer> list = getJavaCodeLineNumInProject(path);
            System.out.println("java: " + list.get(0));
            System.out.println("html： " + list.get(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> getJavaCodeLineNumInProject(String path) throws IOException {
        LinkedList<Path> linkedList = new LinkedList<>();
        Path projectPath = Paths.get(path);
        linkedList.push(projectPath);
        Integer javaLineCount = 0;
        Integer htmlLineCount = 0;
        while(!linkedList.isEmpty()){
            Path newPath = linkedList.pop();
            if(Files.isRegularFile(newPath )){
                //读取文件
                if(newPath.toString().endsWith(".java")){
                    javaLineCount = readFileLine(newPath, javaLineCount);
                }else if(newPath.toString().endsWith(".html")){
                    htmlLineCount = readFileLine(newPath, htmlLineCount);
                }

            }else if(Files.isDirectory(newPath)) {
//                将目录中子文件或者子目录添加到对列中
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(newPath);
                for (Path subPath : directoryStream) {
                    linkedList.push(subPath);
                }
            }
        }
        List<Integer> resultList = new ArrayList<>();
        resultList.add(javaLineCount);
        resultList.add(htmlLineCount);
        return resultList;

    }

    private static Integer readFileLine(Path path, Integer count){
        BufferedReader reader =  null;
        try {
            if (Files.isRegularFile(path)) {
                reader = Files.newBufferedReader(path);
                String line = "";
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().equals("")) {
                        count++;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(reader!=null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return count;
    }
}
