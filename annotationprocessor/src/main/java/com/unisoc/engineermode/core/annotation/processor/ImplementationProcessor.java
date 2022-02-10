package com.unisoc.engineermode.core.annotation.processor;

import com.google.auto.service.AutoService;
import com.unisoc.engineermode.core.annotation.Implementation;
import com.unisoc.engineermode.core.annotation.Property;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@AutoService(Processor.class)
public class ImplementationProcessor extends AbstractProcessor {

    private static List<ImplementationItem> allImplementations = new ArrayList<>();
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        logi("init ImplementationProcessor ");
    }

    @Override
    public boolean process(
        Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (annotations.size() == 0) {
            logi("annotations is empty, return");
            return false;
        }

        logi("process ...");
        Set<? extends Element> annotatedElements =
            roundEnvironment.getElementsAnnotatedWith(Implementation.class);

        if (annotatedElements.size() == 0) {
            logi("no elements with Implementation annotation");
            return false;
        }

        for (Element e : annotatedElements) {
            logi("annotatedElement: " + e.getSimpleName());
            Implementation impl = e.getAnnotation(Implementation.class);
            ImplementationItem item = new ImplementationItem();

            TypeMirror interfaceClassValue = null;
            try {
                impl.interfaceClass();
            } catch( MirroredTypeException mte ) {
                interfaceClassValue = mte.getTypeMirror();
                logi("interfaceClass:" + interfaceClassValue);
            }
            if (interfaceClassValue != null) {
                item.interfaceName = interfaceClassValue.toString();
            }
            item.implementClassName = ((TypeElement) e).getQualifiedName().toString();
            logi(item.implementClassName);
            for (Property p : impl.properties()) {
                List<String> values = item.props.get(p.key());
                if (values == null) {
                    values = new ArrayList<>();
                }
                values.add(p.value());
                item.props.put(p.key(), values);
            }
            allImplementations.add(item);
        }
        try {
            generateJavaFile(allImplementations);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void generateJavaFile(List<ImplementationItem> allInfos) throws IOException {
        JavaFileObject jfo = filer.createSourceFile("com.unisoc.engineermode.core.AllImplementations");
        String t1 = "    ";
        String t2 = "        ";

        try (PrintWriter out = new PrintWriter(jfo.openWriter())) {
            out.println("package com.unisoc.engineermode.core;");
            out.println();
            out.println("import java.util.List;");
            out.println("import java.util.ArrayList;");
            out.println("import java.util.HashMap;");
            out.println("import com.unisoc.engineermode.core.factory.ImplementationInfo;");
            out.println();
            out.println("public class AllImplementations {");
            out.println(t1 + "public static List<ImplementationInfo> impls = new ArrayList<>();");
            out.println();
            out.println(t1 + "public static void init() {");
            out.println(t2 + "ImplementationInfo implInfo ;");
            allInfos.forEach(
                impl -> {
                    out.println(t2 +
                        String.format(
                            "implInfo = new ImplementationInfo(%s, %s);",
                            impl.interfaceName + ".class",
                            wrap(impl.implementClassName)));
                    impl.props.forEach((k, v) ->
                        out.println(String.format(t2 + "implInfo.addProperty(%s, %s);", wrap(k), wrap(v))));
                    out.println(t2 + "impls.add(implInfo);");
                    out.println();
                });

            out.println(t1 + "}");
            out.println("}");
            out.flush();
        }
    }

    private String wrap(List<String> values) {
        return values.stream().collect(Collectors.joining("\",\"", "new String []{\"", "\"}"));
    }

    private String wrap(String in) {
        return "\"" + in + "\"";
    }

    /**
     * for compatibility reasons on android, we override getSupportedAnnotationTypes() and
     * getSupportedSourceVersion() instead of using @SupportedAnnotationTypes
     * and @SupportedSourceVersion
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add("com.unisoc.engineermode.core.annotation.Implementation");
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void logi(CharSequence msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

//    private void loge(CharSequence msg) {
//        messager.printMessage(Diagnostic.Kind.ERROR, msg);
//    }

    private static class ImplementationItem {
        String interfaceName;
        String implementClassName;
        HashMap<String, List<String>> props = new HashMap<>();

        ImplementationItem() { }
    }
}
