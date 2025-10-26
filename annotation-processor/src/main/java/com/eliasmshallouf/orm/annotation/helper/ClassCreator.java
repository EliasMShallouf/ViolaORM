package com.eliasmshallouf.orm.annotation.helper;

import com.eliasmshallouf.orm.annotations.Column;
import com.eliasmshallouf.orm.annotations.Id;
import com.eliasmshallouf.orm.annotations.Lob;
import com.eliasmshallouf.orm.classgraph.GraphField;
import com.eliasmshallouf.orm.columns.*;
import com.eliasmshallouf.orm.helpers.ClassHelper;
import com.eliasmshallouf.orm.helpers.LogicalStream;
import com.eliasmshallouf.orm.helpers.Mapper;
import com.eliasmshallouf.orm.multipart.MultiFieldIDColumn;
import com.eliasmshallouf.orm.multipart.MultiFieldId;
import com.eliasmshallouf.orm.table.EntityModel;
import com.eliasmshallouf.orm.table.TableColumns;

import javax.lang.model.element.VariableElement;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

import static com.eliasmshallouf.orm.annotation.helper.ASTHelper.classOf;
import static com.eliasmshallouf.orm.annotation.helper.ASTHelper.isAnnotationPresent;

public class ClassCreator {
    public static String createClass(
        String fullClassName,
        String className,
        String newClassName,
        String tableName,
        List<VariableElement> fields,
        String p
    ) {
        StringBuilder classBuilder = new StringBuilder();
        classBuilder.append("package ").append(p).append(";\n");
        classBuilder.append("\nimport ").append(fullClassName).append(";");

        Set<Class<?>> importClasses = new HashSet<>();

        StringBuilder builder = new StringBuilder();

        for(Class<?> c : new Class[] {
                EntityModel.class,
                TableColumns.class,
        })
            classBuilder
                .append("\nimport ")
                .append(c.getName())
                .append(";");

        StringBuilder entityColumnsFunctionsBuilder = new StringBuilder();
        StringBuilder columnsFieldsBuilder = new StringBuilder();
        List<GraphField> primaryKeyFields = new ArrayList<>();

        for(VariableElement f : fields) {
            String targetColumnClass = "";
            String fieldName = "";
            String realName = "";
            Class<?> type = classOf(f);

            if(isAnnotationPresent(f, Id.class) || isAnnotationPresent(f, Column.class) || isAnnotationPresent(f, Lob.class)) {
                fieldName = f.getSimpleName().toString();
                realName = LogicalStream
                        .of(f.getAnnotation(Column.class))
                        .ifTrue(c -> c != null && !c.name().isEmpty())
                        .thenReturn(Column::name)
                        .otherwise(c -> f.getSimpleName().toString())
                        .get();

                if(isAnnotationPresent(f, Lob.class)) {
                    targetColumnClass = BlobColumn.class.getSimpleName();

                    importClasses.add(BlobColumn.class);
                } else {
                    type = Mapper.mapFromPrimitive(type);

                    if(type.getSuperclass().equals(Number.class)) {
                        targetColumnClass = NumericColumn.class.getSimpleName() + "<" + type.getSimpleName() + ">";

                        importClasses.add(NumericColumn.class);
                        importClasses.add(type);
                    } else if(
                            type.equals(Date.class) ||
                                    ClassHelper.isImplements(type, Temporal.class)
                    ) {
                        targetColumnClass = DateColumn.class.getSimpleName() + "<" + type.getSimpleName() + ">";

                        importClasses.add(DateColumn.class);
                        importClasses.add(type);
                    } else if(type.equals(Boolean.class)) {
                        targetColumnClass = BooleanColumn.class.getSimpleName();

                        importClasses.add(BooleanColumn.class);
                    } else {
                        targetColumnClass = TextColumn.class.getSimpleName();

                        importClasses.add(TextColumn.class);
                    }
                }

                columnsFieldsBuilder
                        .append("\n\t\tpublic final ")
                        .append(targetColumnClass)
                        .append(" ")
                        .append(fieldName)
                        .append(" = new ")
                        .append(targetColumnClass)
                        .append("(this, \"")
                        .append(realName)
                        .append("\");");

                entityColumnsFunctionsBuilder
                        .append("\n\t")
                        .append("public ")
                        .append(targetColumnClass)
                        .append(" ")
                        .append(fieldName)
                        .append("() {")
                        .append("\n\t\t")
                        .append("return columns.")
                        .append(fieldName)
                        .append(";")
                        .append("\n\t}\n");

                if(isAnnotationPresent(f, Id.class)) {
                    primaryKeyFields.add(new GraphField(
                        fieldName,
                        realName,
                        type
                    ));
                }
            }
        }

        builder
            .append("\n\npublic class ")
            .append(newClassName)
            .append(" extends EntityModel<")
            .append(className)
            .append(", ")
            .append(
                (primaryKeyFields.isEmpty()
                    ? Void.class.getSimpleName()
                    : (primaryKeyFields.size() == 1
                        ? primaryKeyFields.get(0).getClazz().getSimpleName()
                        : (newClassName + "." + newClassName + "Id")
                    )
                )
            ).append("> {\n");

        if(primaryKeyFields.size() > 1) {
            importClasses.add(MultiFieldId.class);
            importClasses.add(MultiFieldIDColumn.class);
            importClasses.add(Map.class);
            importClasses.add(AbstractMap.class);
            importClasses.add(ClassHelper.class);

            builder.append("\tpublic static class ").append(newClassName).append("Id implements ").append(MultiFieldId.class.getSimpleName()).append("<").append(className).append("> {");

            primaryKeyFields
                    .stream()
                    .map(gf -> "\n\t\tpublic " + gf.getClazz().getSimpleName() + " " + gf.getFieldName() + ";")
                    .forEach(builder::append);

            builder.append("\n\n").append("""
                            public %s() { }
                            
                            public %s(%s) {
                                %s
                            }
                            
                            public HelloTableId(%s x) {
                                MultiFieldId.create(this, x);
                            }
                            
                            @Override
                            public Map<String, ?> values() {
                                return ClassHelper.mapOf(%s);
                            }
                            
                            public static %s from(%s x) {
                                return MultiFieldId.from(%s.class, x);
                            }
                    """.formatted(
                    newClassName + "Id",
                        newClassName + "Id",
                        primaryKeyFields.stream().map(gf -> gf.getClazz().getSimpleName() + " " + gf.getFieldName()).collect(Collectors.joining(", ")),
                        primaryKeyFields.stream().map(gf -> "this." + gf.getFieldName() + " = " + gf.getFieldName() + ";").collect(Collectors.joining("\n\t\t\t")),
                        className,
                        primaryKeyFields
                            .stream()
                            .map(gf -> "\n\t\t\t\tnew AbstractMap.SimpleEntry<>(\"" + gf.getColumnName() + "\", this." + gf.getFieldName() + ")")
                            .collect(Collectors.joining(", ")) + "\n\t\t\t",
                        newClassName + "Id",
                        className,
                        newClassName + "Id"
                    )
            );

            builder.append("\t}\n\n");
        }

        builder.append("\tpublic static class Columns extends TableColumns<").append(className).append("> {");
        builder.append(columnsFieldsBuilder);

        builder.append("\n\n\t\tpublic Columns(").append(newClassName).append(" model) {\n");
        builder.append("\t\t\tsuper(model);");
        builder.append("\n\t\t}");
        builder.append("\n\t}");
        builder.append("\n");

        builder.append("""
            
            private final Columns columns = new Columns(this);
            
            public %s() {
                super(%s.class, \"%s\");
                setIdField(%s);
            }
        
            @Override
            public Columns columns() {
                return columns;
            }
        """.formatted(
            newClassName,
            className,
            tableName,
            primaryKeyFields.isEmpty() ? "null" : (
                primaryKeyFields.size() == 1
                    ? (primaryKeyFields.get(0).getFieldName() + "().id()")
                    : ("new "
                        + MultiFieldIDColumn.class.getSimpleName()
                        + "<" + className + ", "
                        + newClassName
                        + "Id>(this, "
                        + primaryKeyFields
                            .stream()
                            .map(gf -> gf.getFieldName() + "()")
                            .collect(Collectors.joining(", "))
                        + ").withMainClassOf("
                        + newClassName + "Id.class"
                        + ")"
                    )
            )
        ));

        builder.append(entityColumnsFunctionsBuilder);
        builder.append("}");
        builder.append("\n");

        importClasses.forEach(c -> {
            classBuilder.append("\nimport ").append(c.getName()).append(";");
        });

        classBuilder.append(builder);
        return classBuilder.toString();
    }
}
