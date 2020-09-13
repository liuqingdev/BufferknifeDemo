package com.lq.animation_complier;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class MyElement {
    private List<VariableElement> variableElementList = new ArrayList<>();

    private List<ExecutableElement> methodElementList = new ArrayList<>();

    public List<VariableElement> getVariableElementList() {
        return variableElementList;
    }

    public List<ExecutableElement> getMethodElementList() {
        return methodElementList;
    }
}
