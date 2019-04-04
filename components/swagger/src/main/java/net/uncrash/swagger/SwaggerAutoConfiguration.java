package net.uncrash.swagger;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;


/**
 * Swagger 2 configurations
 *
 * @author Acris
 * @date 2019/04/03
 */
@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerAutoConfiguration {

    private final TypeResolver typeResolver;
    private final SwaggerProperties properties;

    @Bean
    public Docket petApi() {
        if (!properties.isEnabled()) {
            return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.none())
                .paths(PathSelectors.none())
                .build();
        }

        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .pathMapping("/")
            .directModelSubstitute(LocalDate.class, String.class)
            .genericModelSubstitutes(ResponseEntity.class)
            .alternateTypeRules(
                AlternateTypeRules.newRule(typeResolver.resolve(DeferredResult.class,
                    typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                    typeResolver.resolve(WildcardType.class)))
            .useDefaultResponseMessages(false)
            .globalResponseMessage(RequestMethod.GET,
                Lists.newArrayList(new ResponseMessageBuilder()
                    .code(500)
                    .message("500 message")
                    .responseModel(new ModelRef("Error"))
                    .build()))
            .enableUrlTemplating(true)
            .globalOperationParameters(
                Lists.newArrayList(new ParameterBuilder()
                    .name("someGlobalParameter")
                    .description("Description of someGlobalParameter")
                    .modelRef(new ModelRef("string"))
                    .parameterType("query")
                    .required(true)
                    .build()))
            .tags(new Tag("Pet Service", "All apis relating to pets"));
    }


    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
            .deepLinking(true)
            .displayOperationId(false)
            .defaultModelsExpandDepth(1)
            .defaultModelExpandDepth(1)
            .defaultModelRendering(ModelRendering.EXAMPLE)
            .displayRequestDuration(false)
            .docExpansion(DocExpansion.NONE)
            .filter(false)
            .maxDisplayedTags(null)
            .operationsSorter(OperationsSorter.ALPHA)
            .showExtensions(false)
            .tagsSorter(TagsSorter.ALPHA)
            .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
            .validatorUrl(null)
            .build();
    }
}
