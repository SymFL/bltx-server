package com.csk2024.bltx.service.impl;

import com.csk2024.bltx.constant.Constants;
import com.csk2024.bltx.mapper.TPictureMapper;
import com.csk2024.bltx.model.TPicture;
import com.csk2024.bltx.model.TUser;
import com.csk2024.bltx.query.PictureQuery;
import com.csk2024.bltx.service.PictureService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {
    @Resource
    private TPictureMapper tPictureMapper;
    @Override
    public PageInfo<TPicture> getPicturesByPage(PictureQuery pictureQuery) {
        PageHelper.startPage(pictureQuery.getCurrent(), Constants.PAGE_SIZE);
        List<TPicture> list = tPictureMapper.selectPicsByPage(pictureQuery);
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
}
