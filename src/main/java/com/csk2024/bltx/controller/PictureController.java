package com.csk2024.bltx.controller;

import com.csk2024.bltx.model.TPicture;
import com.csk2024.bltx.model.TUser;
import com.csk2024.bltx.query.PictureQuery;
import com.csk2024.bltx.result.R;
import com.csk2024.bltx.service.PictureService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
public class PictureController {

    @Resource
    private PictureService pictureService;

    /**
     * 查询图片列表
     */
    @PostMapping("/api/pics")
    public R userList(@RequestBody PictureQuery pictureQuery){
        PageInfo<TPicture> pageInfo = pictureService.getPicturesByPage(pictureQuery);
        return R.OK(pageInfo);
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/api/pics/{id}")
    public R delPic(@PathVariable("id") Integer id){
        int del = pictureService.delPic(id);
        return del >= 1 ? R.OK() : R.FAIL();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/api/pics")
    public R batchDel(@RequestParam("ids") String ids){
        List<String> id = Arrays.asList(ids.split(","));
        int del = pictureService.batchDel(id);
        return del > 0 ? R.OK() : R.FAIL();
    }

    /**
     * 查询图片信息
     */
    @GetMapping("/api/pics/info/{id}")
    public R info(@PathVariable("id") Integer id){
        PictureQuery pictureQuery = pictureService.info(id);
        if(pictureQuery == null){
            return R.FAIL("图片离家出走了喵，请联系管理员~");
        }else {
            return R.OK(pictureQuery);
        }
    }
}
