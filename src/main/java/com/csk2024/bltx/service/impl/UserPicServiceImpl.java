package com.csk2024.bltx.service.impl;

import com.csk2024.bltx.constant.Constants;
import com.csk2024.bltx.mapper.TPictureMapper;
import com.csk2024.bltx.model.TPicture;
import com.csk2024.bltx.query.PictureQuery;
import com.csk2024.bltx.result.R;
import com.csk2024.bltx.service.UserPicService;
import com.csk2024.bltx.utils.JWTUtils;
import com.csk2024.bltx.utils.PythonServerUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserPicServiceImpl implements UserPicService {
    @Resource
    private TPictureMapper tPictureMapper;
    @Value("${hong.path}")
    private String basePath;
    @Override
    public PageInfo<TPicture> getPicturesByPage(PictureQuery pictureQuery) {
        pictureQuery.setUserId(JWTUtils.parseUserFromJWT(pictureQuery.getToken()).getId());
        PageHelper.startPage(pictureQuery.getCurrent(), Constants.PAGE_SIZE);
        List<TPicture> list = tPictureMapper.selectUserPicsByPage(pictureQuery);
        return new PageInfo<>(list);
    }

    @Override
    public int delPic(Integer id) {
        return tPictureMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int batchDel(List<String> id) {
        return tPictureMapper.batchDelete(id);
    }

    @Override
    public R upload(PictureQuery pictureQuery) {
        pictureQuery.setUserId(JWTUtils.parseUserFromJWT(pictureQuery.getToken()).getId());
        MultipartFile picture = pictureQuery.getPicture();
        if(!Objects.equals(picture.getContentType(), "image/tif") && !Objects.equals(picture.getContentType(), "image/tiff")){
            return R.FAIL("文件类型不正确，请上传tif或tiff图片!");
        }
        //原始文件名
        String originalFilename = picture.getOriginalFilename();//xxx.tif
        //对原始名进行截取"."后面的字符
        String suffix = null;//.tif
        if (originalFilename != null) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        long timestamp = System.currentTimeMillis();
        String fileName = timestamp + suffix;
        //创建一个目录对象
        String path = basePath + pictureQuery.getUserId() + "\\\\";
        String picPath = path + fileName;
        File dir = new File(path);
        //判断当前目录是否存在：目录不存在，需要创建
        if(!dir.exists()) dir.mkdirs();
        try {
            //将临时文件转存到指定位置
            picture.transferTo(new File(picPath));
        } catch (IOException e) {
            e.printStackTrace();
            return R.FAIL("文件上传失败！");
        }
        pictureQuery.setUrl(picPath);
        TPicture tPicture = new TPicture();
        BeanUtils.copyProperties(pictureQuery,tPicture);
        int i = tPictureMapper.insert(tPicture);
        if (i > 0){
            return R.OK();
        }else{
            return R.FAIL("文件上传失败!");
        }
    }

    @Override
    public PictureQuery info(Integer id) {
        TPicture tPicture = tPictureMapper.selectByPrimaryKey(id);
        if(tPicture == null){
            return null;
        }
        PictureQuery pictureQuery = new PictureQuery();
        BeanUtils.copyProperties(tPicture,pictureQuery);
        try {
            Path imgPath = Paths.get(pictureQuery.getUrl());
            byte[] bytes = Files.readAllBytes(imgPath);
            pictureQuery.setEncodedString(Base64.getEncoder().encodeToString(bytes));
            pictureQuery.setUrl("");
            return pictureQuery;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int predict(Integer id) {
        TPicture tPicture = tPictureMapper.selectByPrimaryKey(id);
        if(tPicture.getResult() != null && !tPicture.getResult().isEmpty()){
            return 1;
        }
        if(tPicture.getUrl() == null || tPicture.getUrl().isEmpty()){
            throw new RuntimeException("图片不存在喵~请刷新页面喵~");
        }
        String predict = PythonServerUtils.predict(tPicture.getUrl());
        if(predict == null){
            throw new RuntimeException("服务器出现问题了喵~请联系管理员处理喵~");
        }else if ("error".equals(predict)){
            throw new RuntimeException("分类预测失败喵~请联系管理员喵~");
        }else {
            String[] strings = predict.split(",");
            tPicture.setResult(strings[0]);
            BigDecimal a = new BigDecimal("100");
            BigDecimal b = new BigDecimal(strings[1]);
            BigDecimal result = b.multiply(a);
            tPicture.setProbability(result);
            return tPictureMapper.updateByPrimaryKey(tPicture);
        }
    }
}
