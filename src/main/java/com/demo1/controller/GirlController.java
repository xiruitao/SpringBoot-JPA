package com.demo1.controller;

import com.demo1.domain.Girl;
import com.demo1.domain.Result;
import com.demo1.repository.GirlRepository;
import com.demo1.service.GirlService;
import com.demo1.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class GirlController {

    private final static Logger logger = LoggerFactory.getLogger(GirlController.class);

    @Autowired
    private GirlRepository girlRepository;

    @Autowired
    private GirlService girlService;

    /**
     * 查询所有女生列表
     * @return
     */
    @GetMapping(value = "/girls")
    public List<Girl> girlList() {
        logger.info("girlList");
        return girlRepository.findAll();
    }

    //查询一个女生
    @GetMapping(value = "/girls/{id}")
    public Girl girlFindOne(@PathVariable("id") Integer id) {
        Optional<Girl> girlOption = girlRepository.findById(id);
        if(girlOption!=null&&girlOption.isPresent()){
            return girlOption.get();
        }else {
            return null;
        }
//        return girlRepository.findById(id).get();
    }

//    /**
//     * 添加一个女生
//     * @param cupSize
//     * @param age
//     * @return
//     */
//    @PostMapping(value = "/girls")
//    public Girl girlAdd(@RequestParam("cupSize") String cupSize,
//                        @RequestParam("age") Integer age) {
//        Girl girl = new Girl();
//        girl.setCupSize(cupSize);
//        girl.setAge(age);
//
//        return girlRepository.save(girl);
//    }
//    /*
//    *添加一个女生,若女生属性多时
//    */
//    @PostMapping(value = "/girls")
//    public Girl girlAdd(Girl girl) {
//        girl.setCupSize(girl.getCupSize());
//        girl.setAge(girl.getAge());
//
//        return girlRepository.save(girl);
//    }

    /**
     * 添加一个女生，限制18岁以下未成年少女
     * @return
     */
//    @PostMapping(value = "/girls")
//    public Girl girlAdd(@Valid Girl girl, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            System.out.println(bindingResult.getFieldError().getDefaultMessage());
//            return null;
//        }
//
//        girl.setCupSize(girl.getCupSize());
//        girl.setAge(girl.getAge());
//
//        return girlRepository.save(girl);
//    }
    //添加异常处理
//    @PostMapping(value = "/girls")
//    public Result<Girl> girlAdd(@Valid Girl girl, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            Result result = new Result();
//            result.setCode(0);
//            result.setMsg(bindingResult.getFieldError().getDefaultMessage());
//            return result;
//        }
//
//        girl.setCupSize(girl.getCupSize());
//        girl.setAge(girl.getAge());
//
//        Result result = new Result();
//        result.setCode(1);
//        result.setMsg("成功");
//        result.setData(girlRepository.save(girl));
//        return result;
//    }
    //工具类优化
    @PostMapping(value = "/girls")
    public Result<Girl> girlAdd(@Valid Girl girl, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(0, bindingResult.getFieldError().getDefaultMessage());
        }

        girl.setCupSize(girl.getCupSize());
        girl.setAge(girl.getAge());

        return ResultUtil.success(girlRepository.save(girl));
    }

    //更新
    @PutMapping(value = "/girls/{id}")
    public Girl girlUpdate(@PathVariable("id") Integer id,
                           @RequestParam("cupSize") String cupSize,
                           @RequestParam("age") Integer age) {
        Girl girl = new Girl();
        girl.setId(id);
        girl.setCupSize(cupSize);
        girl.setAge(age);

        return girlRepository.save(girl);
    }

    //删除
    @DeleteMapping(value = "/girls/{id}")
    public void girlDelete(@PathVariable("id") Integer id) {
        girlRepository.deleteById(id);
    }

    //通过年龄查询女生列表
    @GetMapping(value = "/girls/age/{age}")
    public List<Girl> girlListByAge(@PathVariable("age") Integer age) {
        return girlRepository.findByAge(age);
    }

    //同时插入两个girl
    @PostMapping(value = "/girls/two")
    public void girlTwo() {
        girlService.insertTwo();
    }

    //限制年龄查询回复
    @GetMapping(value = "girls/getAge/{id}")
    public void getAge(@PathVariable("id") Integer id) throws Exception{
        girlService.getAge(id);
    }
}
