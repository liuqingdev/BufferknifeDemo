package com.lq.animation_complier;

import com.google.auto.service.AutoService;
import com.lq.animation.BindClick;
import com.lq.animation.BindView;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

//第5个坑
@AutoService(Processor.class)
public class AnimationCompier extends AbstractProcessor {
    private Filer filer;

    //typeElement为类元素,一轮解析一个类。
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        //每个类元素对应里面有多个注解元素
        Map<TypeElement, MyElement> map = new HashMap<>();
        Set<? extends Element> viewSet = roundEnvironment.getElementsAnnotatedWith(BindView.class);

        Set<? extends Element> clickSet = roundEnvironment.getElementsAnnotatedWith(BindClick.class);

        Writer writer=null;

        System.out.println("viewset:"+viewSet.size());
        //假如我们有两个textview进行了注解
        for (Element element : viewSet) {
            VariableElement viewElement = (VariableElement) element;
            //这里获取到的就是它的类节点
            TypeElement classElement = (TypeElement) viewElement.getEnclosingElement();
            MyElement myElement = map.get(classElement);
            if (myElement == null) {
                myElement = new MyElement();
                map.put(classElement, myElement);
            }
            myElement.getVariableElementList().add(viewElement);
        }


        for (Element element : clickSet) {
            ExecutableElement clieckElment = (ExecutableElement) element;
            //这里获取到的就是它的类节点
            TypeElement classElement = (TypeElement) clieckElment.getEnclosingElement();
            MyElement myElement = map.get(classElement);
            if (myElement == null) {
                myElement = new MyElement();
                map.put(classElement, myElement);
            }
            myElement.getMethodElementList().add(clieckElment);
        }


        //因为有的类里面没加viewbind注解，所以这里要判断一下
        if (map.size() > 0) {
            //根据注解得到类信息，拼接成我们想要的类
            Iterator<TypeElement> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                TypeElement typeElement = iterator.next();
                MyElement myElement = map.get(typeElement);
                //获取包信息
                PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(typeElement);
                String packageInfo = packageElement.getQualifiedName().toString();
                String activityName  = typeElement.getSimpleName().toString();
                String newClassName = activityName+"$ViewBinder";

                try {
                    JavaFileObject sourceFile = filer.createSourceFile(packageInfo + "." + newClassName);
                     writer = sourceFile.openWriter();
                    StringBuilder builder = new StringBuilder();
                    builder.append("package " + packageInfo+";\n");
                    builder.append("import android.view.View;\n");
                    builder.append("public class "+newClassName+"{\n");
                    //构造方法
                    builder.append("public "+newClassName +"(final "+ typeElement.getQualifiedName() +" target){\n");

                    for (VariableElement variableElement : myElement.getVariableElementList()) {
                        Name viewName = variableElement.getSimpleName();
                        TypeMirror typeMirror = variableElement.asType();
                        int resId = variableElement.getAnnotation(BindView.class).vules();
                        builder.append("target." + viewName + " =(" + typeMirror
                                + ")target.findViewById(" + resId+");\n");
                    }
                    for (ExecutableElement executableElement : myElement.getMethodElementList()) {
                        int[] value = executableElement.getAnnotation(BindClick.class).value();
                        for (int resId : value) {
                            builder.append("(target.findViewById("+ resId+"" +
                                    ")).setOnClickListener(new DebouncingOnClickListener() {\n"+
                           " public void doClick(View p0) {"+
                                "target.onClick(p0); }});"
                            );
                        }
                    }

                    builder.append("}\n}\n");
                    writer.write(builder.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (writer != null) {
                        try {
                            //第二个坑
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return false;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        //添加注解的完整名字
        set.add(BindView.class.getCanonicalName());
        set.add(BindClick.class.getCanonicalName());
        //第一个坑
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //设置注解使用的版本
        return processingEnv.getSourceVersion();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        //这个super必须要，因为它对processingEnv进行了初始化
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }
}
