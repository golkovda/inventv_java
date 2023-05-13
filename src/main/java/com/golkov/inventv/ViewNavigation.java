package com.golkov.inventv;

import javafx.scene.Node;

import java.util.*;

public class ViewNavigation {

    private static final Map<Integer, Stack<Node>> viewStack = new HashMap<Integer, Stack<Node>>(){{
        put(0, new Stack<Node>());
        put(1, new Stack<Node>());
        put(2, new Stack<Node>());
        put(3, new Stack<Node>());
        put(4, new Stack<Node>());
    }};

    public static void push(int index, Node view) {
        viewStack.get(index).push(view);
    }

    public static Node pop(int index) { //Workaround, da return viewStack.pop() unterwartetes Verhalten auslöst
        viewStack.get(index).pop();
        return peek(index);
    }

    public static Node peek(int index) {
        return viewStack.get(index).peek();
    }

    public static int getSize(int index){
        return viewStack.get(index).size();
    }

    public static boolean isEmpty(){
        return viewStack.size() == 0;
    }

    public static void clearStack(int index){
        viewStack.get(index).removeAllElements();
    }
}

