package com.lq.bufferknife;

import java.lang.reflect.Constructor;

import sun.rmi.runtime.Log;

public class BufferKnife {
    public static void bind(Object object){
        //第四个坑
        String simpleName = object.getClass().getName();
        System.out.println("simpleName:" + simpleName);
        try {
            Class<?> aClass = Class.forName(simpleName + "$ViewBinder");
            Constructor<?> constructor = aClass.getConstructor(object.getClass());
            constructor.newInstance(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
