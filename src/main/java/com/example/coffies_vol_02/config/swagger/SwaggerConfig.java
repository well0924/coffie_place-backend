package com.example.coffies_vol_02.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.*;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@OpenAPIDefinition
public class SwaggerConfig {

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)// Swagger 에서 제공해주는 기본 응답 코드를 표시할 것이면 true
                .ignoredParameterTypes(AuthenticationPrincipal.class)//Spring Security에 사용될 경우 설정
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example")) // Controller가 들어있는 패키지. 이 경로의 하위에 있는 api만 표시됨.
                .paths(PathSelectors.ant("/api/**")) // 위 패키지 안의 api 중 지정된 path만 보여줌. (any()로 설정 시 모든 api가 보여짐)
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Coffies Rest API Documentation")//swagger ui 에 나오는 제목
                .description("CoffiesVol.02")//swagger ui에 api 설명
                .version("0.2")//version
                .build();
    }

    //swagger 충돌 대처 코드
    //https://dkswnkk.tistory.com/672
    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
                List<T> copy = mappings.stream()
                        .filter(mapping -> mapping.getPatternParser() == null)
                        .collect(Collectors.toList());
                mappings.clear();
                mappings.addAll(copy);
            }

            @SuppressWarnings("unchecked")
            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                try {
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true);
                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        };

    }
}