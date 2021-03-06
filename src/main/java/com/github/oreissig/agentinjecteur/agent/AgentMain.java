package com.github.oreissig.agentinjecteur.agent;

import javassist.CannotCompileException;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

public class AgentMain {
    private static final InjectionTransformer transformer = new InjectionTransformer();

    public static void premain(String agentArgs, Instrumentation inst) {
        initializeAgent(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        initializeAgent(inst);
    }

    private static void initializeAgent(Instrumentation inst) {
        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            try {
                return transformer.transform(className, classfileBuffer);
            } catch (IOException | CannotCompileException e) {
                new Exception("Could not inject " + className, e).printStackTrace();
                return classfileBuffer;
            }
        });
    }
}
