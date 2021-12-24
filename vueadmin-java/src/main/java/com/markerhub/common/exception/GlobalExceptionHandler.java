package com.markerhub.common.exception;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.http.HttpStatus;
import com.markerhub.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice   //表示 定义 全局控制器异常处理，Rest表示 异步处理
public class GlobalExceptionHandler {

    /**
     * 实体校验异常捕获
     * @param e
     * @return
     */
    // 处理实体校验异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)//响应状态码
    @ExceptionHandler(value = MethodArgumentNotValidException.class)// 指定 捕获的Exception各个类型异常
    public Result handler(MethodArgumentNotValidException e) {

        BindingResult result = e.getBindingResult();//获取异常结果
        ObjectError objectError = result.getAllErrors().stream().findFirst().get();//获取第一个异常

        log.error("实体校验异常：----------------{}", objectError.getDefaultMessage());
        return Result.fail(objectError.getDefaultMessage());
    }

    //处理Assert的异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)    //响应状态码
    @ExceptionHandler(value = IllegalArgumentException.class)  // 指定 捕获的Exception各个类型异常
    public Result handler(IllegalArgumentException e){
        log.error("Assert异常:---------------{}",e.getMessage());
        return Result.fail(e.getMessage());
    }

    //捕捉其他异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(RuntimeException e){
        log.error("运行时异常:---------------{}",e.getMessage());
        return Result.fail(e.getMessage());
    }
}
