package com.madao.web.controller;

import com.madao.api.entity.Article;
import com.madao.api.entity.FileUploadResult;
import com.madao.api.entity.User;
import com.madao.api.enums.OperateEnum;
import com.madao.api.service.ArticleService;
import com.madao.api.utils.KeyUtil;
import com.madao.api.utils.ResultUtil;
import com.madao.api.utils.ResultView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileController {
    public static final String DOCLOCAL_PATH = "/doc";

    @Autowired
    ArticleService articleService;

    //上传文档，保存到本地
    @ResponseBody
    @RequestMapping("/docfile/upload")
    public ResultView upload(@RequestParam("file") MultipartFile docFile, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultUtil.returnFail("用户未登录,请登录后重试");
        }
        Long userId = user.getUserId();
        if (docFile.isEmpty()) {
            return ResultUtil.returnFail("未选择文件");
        }
        String filename = docFile.getOriginalFilename();

        String ext= null;
        if(filename.contains(".")){
            ext = filename.substring(filename.lastIndexOf("."));
        }else{
            ext = "";
        }

        String nfileName = KeyUtil.genUniquKey() + ext;
        Path docPath = Paths.get(DOCLOCAL_PATH);
        if(Files.notExists(docPath)){
            try {
                System.out.println("creating path........." + docPath);
                Files.createDirectory(docPath);
            } catch (IOException e) {
                e.printStackTrace();
                return ResultUtil.returnFail("请稍后重试");
            }
        }
        File targetFile = Paths.get(DOCLOCAL_PATH, nfileName).toFile();
        try {
            docFile.transferTo(targetFile);
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            return ResultUtil.returnFail("请稍后重试");
        }

        return ResultUtil.returnSuccess(targetFile.toString());
    }

    //文件下载
    @RequestMapping("/file/download/{articleId}")
    public String downloadFile(@PathVariable("articleId") Long articleId, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

        ResultView<Article> resultView = articleService.getArticleById(articleId);
        if(resultView.getCode()!=0){
            return "";
        }
        Article article = (Article) resultView.getData();
        System.out.println(articleId);

        String fileName = article.getOriginName();
        String filePathStr = article.getFilePath();
        Path filePath = Paths.get(filePathStr);
        File file = filePath.toFile();
        // 如果文件名不为空，则进行下载
            // 如果文件名存在，则进行下载
        if (filePath!=null && Files.exists(filePath)) {
            // 配置文件下载
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // 实现文件下载
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                System.out.println("Download successfully!");
            }
            catch (Exception e) {
                System.out.println("Download failed!");
            }
            finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        articleService.increaseArticleDownloadCount(articleId);
        return null;
    }

}
