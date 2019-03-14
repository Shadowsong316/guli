package com.guli.common.handler;

import com.guli.common.constants.ResultCodeEnum;
import com.guli.common.exception.GuliException;
import com.guli.common.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理类
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R error(Exception e){
//        e.printStackTrace();
        log.error(e.getMessage());
        return R.error();
    }
    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseBody
    public R error(BadSqlGrammarException e){
//        e.printStackTrace();
        log.error(e.getMessage());
        return R.setResult(ResultCodeEnum.BAD_SQL_GRAMMAR);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public R error(JsonParseException e){
//        e.printStackTrace();
        log.error(e.getMessage());
        return R.setResult(ResultCodeEnum.JSON_PARSE_ERROR);
    }
    @ExceptionHandler(GuliException.class)
    @ResponseBody
    public R error(GuliException e){
//        e.printStackTrace();
        log.error(e.getMessage());
        return R.error().message(e.getMessage()).code(e.getCode());
    }
}
