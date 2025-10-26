package com.eliasmshallouf.orm.annotation.processor;

import com.eliasmshallouf.orm.annotations.*;
import com.eliasmshallouf.orm.helpers.LogicalStream;
import com.google.auto.service.AutoService;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Set;

import static com.eliasmshallouf.orm.annotation.helper.ASTHelper.getFieldsOf;
import static com.eliasmshallouf.orm.annotation.helper.ASTHelper.isAnnotationPresent;
import static com.eliasmshallouf.orm.annotation.helper.ClassCreator.createClass;

@SupportedAnnotationTypes("com.eliasmshallouf.orm.annotations.Entity")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions("generatedTablesPackage")
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    private Filer filer;
    private Elements elements;
    private String targetPackage;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);

        filer = env.getFiler();
        elements = env.getElementUtils();

        targetPackage = env.getOptions().getOrDefault("generatedTablesPackage", "");
    }

    private String getGeneratePackageOf(String p) {
        if(targetPackage.isEmpty())
            return p;

        if(targetPackage.startsWith("/"))
            return p + "." + targetPackage.substring(1);

        return targetPackage;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            roundEnv.getElementsAnnotatedWith(Entity.class).forEach(element -> {
                if (element.getKind() == ElementKind.CLASS) {
                    createEntityClass(element);
                }
            });
        }

        return false;
    }

    private void createEntityClass(Element element) {
        TypeElement typeElement = (TypeElement) element;

        String originalClassName = typeElement.getSimpleName().toString();
        String newClassName = originalClassName + "Table";
        String packageName = getGeneratePackageOf(elements.getPackageOf(typeElement).toString());

        ArrayList<VariableElement> fields = new ArrayList<>();

        getFieldsOf(typeElement, elements).forEach(enclosed -> {
            if (
                enclosed.getKind() == ElementKind.FIELD &&
                (
                    isAnnotationPresent(enclosed, Column.class) ||
                    isAnnotationPresent(enclosed, Lob.class) ||
                    isAnnotationPresent(enclosed, Id.class)
                )
            ) {
                fields.add((VariableElement) enclosed);
            }
        });

        try {
            JavaFileObject file = filer.createSourceFile(
                packageName + "." + newClassName,
                element
            );

            Writer writer = file.openWriter();

            writer.write(createClass(
                typeElement.asType().toString(),
                originalClassName,
                newClassName,
                LogicalStream
                    .of(element.getAnnotation(Entity.class).name())
                    .ifTrue(s -> !s.isEmpty())
                    .then(s -> s)
                    .otherwise(s -> originalClassName)
                    .get(),
                fields,
                packageName
            ));

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
