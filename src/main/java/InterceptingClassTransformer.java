import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.runtime.Desc;
import javassist.scopedpool.ScopedClassPoolFactoryImpl;
import javassist.scopedpool.ScopedClassPoolRepositoryImpl;
import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class transformer which intercepts the method call, used to emit the debug information.
 */
public class InterceptingClassTransformer implements ClassFileTransformer {

    /**
     * We use JUL as this is an java agent which should not depend on any other framework than java.
     */
    private static final Logger log = Logger.getLogger(InterceptingClassTransformer.class.getName());
    private ScopedClassPoolFactoryImpl scopedClassPoolFactory = new ScopedClassPoolFactoryImpl();

    private ClassPool rootPool;

    public void init() {

        //Sets the useContextClassLoader =true to get any class type to be correctly resolved with correct OSGI module
        Desc.useContextClassLoader = true;
        rootPool = ClassPool.getDefault();
    }

    /**
     * An agent provides an implementation of this interface method in order to transform class files.
     * Transforms the given class file and returns a new replacement class file.
     * We check our config with classes and intercept only when the Corresponding Class Name, Method Name, Method
     * Signature matches.
     *
     * @param loader              The defining loader of the class to be transformed, may be {@code null}
     *                            if the bootstrap loader.
     * @param className           The name of the class in the internal form of fully qualified class.
     * @param classBeingRedefined If this is triggered by a redefine or re transform, the class being redefined.
     * @param protectionDomain    The protection domain of the class being defined or redefined.
     * @param classfileBuffer     The input byte buffer in class file format - Have to be instrumented.
     * @return The transformed byte code.
     * @throws IllegalClassFormatException The IllegalClassFormat Exception.
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {

        byte[] byteCode = classfileBuffer;
        // If you wanted to intercept all the classs then you can remove this conditional check.
        if (className.equals("Example")) {
            log.info("Transforming the class " + className);
            try {
                ClassPool classPool = scopedClassPoolFactory.create(loader, rootPool,
                        ScopedClassPoolRepositoryImpl.getInstance());
                CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                CtMethod[] methods = ctClass.getDeclaredMethods();

                for (CtMethod method : methods) {
                    if (method.getName().equals("main")){
                        method.insertAfter("System.out.println(\"Logging using Agent\");");
                    }
                }
                byteCode = ctClass.toBytecode();
                ctClass.detach();
            } catch (Throwable ex) {
                log.log(Level.SEVERE, "Error in transforming the class: " + className, ex);
            }
        }
        return byteCode;
    }
}
