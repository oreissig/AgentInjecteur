package com.github.oreissig.agentinjecteur.agent;

import com.github.oreissig.agentinjecteur.rt.MembersInjector;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static javassist.Modifier.FINAL;
import static javassist.Modifier.PRIVATE;

class InjectionTransformer {

    private final ClassPool pool = ClassPool.getDefault();
    private final String membersInjector = MembersInjector.class.getName();

    public byte[] transform(String className, byte[] classfileBuffer) throws IOException, CannotCompileException {
        CtClass clazz = pool.makeClass(new ByteArrayInputStream(classfileBuffer));

        // TODO @Assisted for constructor params
        // TODO overwrite @FactoryMethod annotated static methods with getter
        List<CtField> injectTargets = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.hasAnnotation(Inject.class)).collect(toList());
        if (injectTargets.isEmpty()) {
            // do nothing
            return classfileBuffer;
        } else {
            injectMembers(clazz, injectTargets);
            return clazz.toBytecode();
        }
    }

    private void injectMembers(CtClass clazz, List<CtField> injectTargets) {
        try {
            for (CtField f : injectTargets) {
                makeFinal(f);
                // TODO replace @javax.inject.Inject by proprietary @Injected annotation to avoid double injection

                String typeName = f.getType().getName();
                String init = "(" + typeName + ") " + membersInjector + ".get(" + typeName + ".class)";
                clazz.removeField(f);
                clazz.addField(f, init);
            }
        } catch (NotFoundException | CannotCompileException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeFinal(CtField f) throws CannotCompileException {
        if ((f.getModifiers() & PRIVATE) > 0) {
            // check that there is no write to an injected field
            f.getDeclaringClass().instrument(new ExprEditor() {
                public void edit(FieldAccess fa) throws CannotCompileException {
                    try {
                        if (f.equals(fa.getField()) && fa.isWriter()) {
                            throw new CannotCompileException("write to private injected field " +
                                    f.getName() + " not allowed");
                        }
                    } catch (NotFoundException e) {
                        throw new CannotCompileException(e);
                    }
                }
            });
            // make field final
            f.setModifiers(f.getModifiers() | FINAL);
        }
    }
}
