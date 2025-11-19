package io.viola.orm.annotation.helper;

import io.viola.orm.annotations.Column;
import io.viola.orm.annotations.Id;
import io.viola.orm.annotations.Lob;
import io.viola.orm.classgraph.ClassGraph;
import io.viola.orm.classgraph.GraphField;
import io.viola.orm.classgraph.ReflectionCaching;
import io.viola.orm.columns.*;
import io.viola.orm.helpers.ClassHelper;
import io.viola.orm.helpers.LogicalStream;
import io.viola.orm.helpers.Mapper;
import io.viola.orm.multipart.MultiFieldIDColumn;
import io.viola.orm.multipart.MultiFieldId;
import io.viola.orm.table.EntityModel;
import io.viola.orm.table.TableColumns;

import javax.lang.model.element.VariableElement;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;

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
            ReflectionCaching.class,
            ClassGraph.class,
            GraphField.class
        })
            classBuilder
                .append("\nimport ")
                .append(c.getName())
                .append(";");

        StringBuilder entityColumnsFunctionsBuilder = new StringBuilder();
        StringBuilder columnsFieldsBuilder = new StringBuilder();
        List<GraphField> graphFields = new ArrayList<>();
        List<GraphField> primaryKeyFields = new ArrayList<>();

        for(VariableElement f : fields) {
            String targetColumnClass = "";
            String fieldName = "";
            String realName = "";
            Class<?> type = ASTHelper.classOf(f);

            if(ASTHelper.isAnnotationPresent(f, Id.class) || ASTHelper.isAnnotationPresent(f, Column.class) || ASTHelper.isAnnotationPresent(f, Lob.class)) {
                fieldName = f.getSimpleName().toString();
                realName = LogicalStream
                        .of(f.getAnnotation(Column.class))
                        .ifTrue(c -> c != null && !c.name().isEmpty())
                        .thenReturn(Column::name)
                        .otherwise(c -> f.getSimpleName().toString())
                        .get();

                if(ASTHelper.isAnnotationPresent(f, Lob.class)) {
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

                GraphField field = new GraphField(
                        fieldName,
                        realName,
                        type
                );
                field.setIsLob(ASTHelper.isAnnotationPresent(f, Lob.class));
                field.setIsPrimaryKey(ASTHelper.isAnnotationPresent(f, Id.class));

                if(field.isPrimaryKey()) {
                    primaryKeyFields.add(field);
                }

                graphFields.add(field);
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

        builder.append("""
                    static {
                        ClassGraph<%s> graph = new ClassGraph<>();
                        graph.tableName = "%s";
                        graph.entityClass = %s.class;
                        %s
                
                        ReflectionCaching.getCache().registerGraph(%s.class, graph);
                    }
                """.formatted(
                    className,
                    tableName,
                    className,
                    graphFields
                        .stream()
                        .map(gf -> "\n\tgraph.addField(new GraphField(\"%s\", \"%s\", %s.class).setIsLob(%s).setIsPrimaryKey(%s);".formatted(
                            gf.getFieldName(),
                                gf.getColumnName(),
                                gf.getClazz().getSimpleName(),
                                gf.isLob() ? "true" : "false",
                                gf.isPrimaryKey() ? "true" : "false"
                        ))
                        .collect(Collectors.joining()),
                    className
                ))
                .append("\n");

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
