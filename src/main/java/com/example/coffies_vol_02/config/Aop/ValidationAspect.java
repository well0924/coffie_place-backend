package com.example.coffies_vol_02.config.Aop;

import com.example.coffies_vol_02.config.Exception.Dto.CommonResponse;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Aspect
@Component
public class ValidationAspect {
    @Around("execution(* com.example.coffies_vol_02.*..*ApiController.*(..))")
    public Object apiAdvice(ProceedingJoinPoint proceedingjoinpoint)throws Throwable{

        String typeName = proceedingjoinpoint.getSignature().getDeclaringTypeName();
        String name = proceedingjoinpoint.getSignature().getName();
        Object[] args = proceedingjoinpoint.getArgs();

        for(Object arg : args) {
            if(arg instanceof BindingResult) {
                BindingResult bindingResult = (BindingResult)arg;

                if(bindingResult.hasErrors()) {

                    Map<String,String> errorMap = new HashMap<>();

                    for(FieldError error : bindingResult.getFieldErrors()) {
                        String validationkey = String.format("valid_%s", error.getField());

                        log.info(typeName + "." + name + "() => 필드 : " + error.getField() + ", 메세지 : " + error.getDefaultMessage());

                        errorMap.put(validationkey, error.getDefaultMessage());
                    }

                    return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(),errorMap);
                }
            }
        }
        return proceedingjoinpoint.proceed();
    }
}
