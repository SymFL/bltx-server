package com.csk2024.bltx.controller;

import com.csk2024.bltx.model.TPicture;
import com.csk2024.bltx.query.PictureQuery;
import com.csk2024.bltx.query.UserQuery;
import com.csk2024.bltx.result.R;
import com.csk2024.bltx.service.UserPicService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
public class UserPicController {
    @Resource
    private UserPicService userPicService;

    /**
     * 查询图片列表
     */
    @PostMapping("/api/userPics")
    public R userList(@RequestBody PictureQuery pictureQuery,@RequestHeader(value="Authorization") String token){
        pictureQuery.setToken(token);
        PageInfo<TPicture> pageInfo = userPicService.getPicturesByPage(pictureQuery);
        return R.OK(pageInfo);
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/api/userPics/{id}")
    public R delPic(@PathVariable("id") Integer id){
        int del = userPicService.delPic(id);
        return del >= 1 ? R.OK() : R.FAIL();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/api/userPics")
    public R batchDel(@RequestParam("ids") String ids){
        List<String> id = Arrays.asList(ids.split(","));
        int del = userPicService.batchDel(id);
        return del > 0 ? R.OK() : R.FAIL();
    }

    /**
     * 图片上传
     */
    @PostMapping("/api/userPics/upload")
    public R upload(@RequestParam("name") String name, @RequestParam("remark") String remark, @RequestParam("picture") MultipartFile picture, @RequestHeader(value="Authorization") String token){
        PictureQuery pictureQuery = new PictureQuery();
        pictureQuery.setToken(token);
        pictureQuery.setName(name);
        pictureQuery.setRemark(remark);
        pictureQuery.setPicture(picture);
        return userPicService.upload(pictureQuery);
    }
/*
    *//**
     * 下载图片
     *//*
    @GetMapping("/api/userPics/download/{id}")
    public void download(@PathVariable("id") Integer id, HttpServletResponse response){
        userPicService.download(id,response);
    }*/

    /**
     * 查询图片信息
     */
    @GetMapping("/api/userPics/info/{id}")
    public R info(@PathVariable("id") Integer id){
        PictureQuery pictureQuery = userPicService.info(id);
        if(pictureQuery == null){
            return R.FAIL("图片离家出走了喵，请联系管理员~");
        }else {
            return R.OK(pictureQuery);
        }
    }
}
