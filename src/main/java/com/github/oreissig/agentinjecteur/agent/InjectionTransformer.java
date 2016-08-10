package com.github.oreissig.agentinjecteur.agent;

import com.github.oreissig.agentinjecteur.rt.MembersInjector;
import javassist.*;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static javassist.Modifier.FINAL;

class InjectionTransformer {

    private final ClassPool pool = ClassPool.getDefault();
    private final String membersInjector = MembersInjector.class.getName();

    public byte[] transform(String className, byte[] classfileBuffer) throws IOException {
        try {
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
        } catch (IOException | CannotCompileException e) {
            new Exception("Could not inject " + className, e).printStackTrace();
            return classfileBuffer;
        }
    }

    private void injectMembers(CtClass clazz, List<CtField> injectTargets) {
        try {
            for (CtField f : injectTargets) {
                // make field final
                // TODO check that there is no write to an injected field
                f.setModifiers(f.getModifiers() | FINAL);
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
}
