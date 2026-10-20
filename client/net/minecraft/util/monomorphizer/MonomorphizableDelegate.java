package net.minecraft.util.monomorphizer;

import com.google.common.reflect.TypeToken;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.SharedConstants;

public abstract class MonomorphizableDelegate<Interface, Target extends Interface> {
   private static final ConcurrentMap<Class<?>, MonomorphicDelegateTemplate<?>> TEMPLATES = new ConcurrentHashMap();
   protected final Target target;
   private final MonomorphicDelegateTemplate<Target> delegateTemplate;

   protected MonomorphizableDelegate(final Target target) {
      super();
      this.target = target;
      this.delegateTemplate = (MonomorphicDelegateTemplate)TEMPLATES.computeIfAbsent(this.getClass(), MonomorphizableDelegate::validateAndCreateTemplate);
   }

   private static MonomorphicDelegateTemplate<?> validateAndCreateTemplate(final Class<?> delegateClass) {
      if ((delegateClass.getModifiers() & 16) == 0) {
         throw new IllegalStateException(String.valueOf(delegateClass) + " must be final");
      } else {
         Type superclass = delegateClass.getGenericSuperclass();
         if (superclass instanceof ParameterizedType) {
            ParameterizedType parameterizedSuperclass = (ParameterizedType)superclass;
            Type[] typeParameters = parameterizedSuperclass.getActualTypeArguments();
            Class interfaceClass = TypeToken.of(typeParameters[0]).getRawType();
            Class targetClass = TypeToken.of(typeParameters[1]).getRawType();
            if (interfaceClass.isInterface() && interfaceClass.isAssignableFrom(delegateClass)) {
               validateReferenceDelegate(delegateClass, interfaceClass);
               return MonomorphicDelegateTemplate.createTemplate(targetClass);
            } else {
               String var6 = String.valueOf(delegateClass);
               throw new IllegalStateException(var6 + " must implement " + String.valueOf(interfaceClass) + " as an interface");
            }
         } else {
            String var10002 = String.valueOf(delegateClass);
            throw new IllegalStateException(var10002 + " does not parameterize " + String.valueOf(superclass));
         }
      }
   }

   private static void validateReferenceDelegate(final Class<?> delegateClass, final Class<?> interfaceClass) {
      for(Method interfaceMethod : interfaceClass.getMethods()) {
         if (interfaceMethod.getDeclaringClass() != Object.class && !interfaceMethod.accessFlags().contains(AccessFlag.STATIC) && Arrays.stream(delegateClass.getDeclaredMethods()).noneMatch((method) -> method.getName().equals(interfaceMethod.getName()) && method.getReturnType() == interfaceMethod.getReturnType() && Arrays.equals(method.getParameterTypes(), interfaceMethod.getParameterTypes()))) {
            String var10002 = String.valueOf(delegateClass);
            throw new IllegalStateException(var10002 + " must implement " + String.valueOf(interfaceMethod));
         }
      }

   }

   public Interface asInterface() {
      return (Interface)this;
   }

   public Interface makeMonomorphic(final MethodHandles.Lookup lookup) {
      if (SharedConstants.DEBUG_DISABLE_RUNTIME_MONOMORPHIZATION) {
         return (Interface)this.asInterface();
      } else if (!lookup.lookupClass().isNestmateOf(this.target.getClass())) {
         throw new IllegalArgumentException("lookup must be a nestmate of " + String.valueOf(this.target.getClass()));
      } else {
         return (Interface)this.delegateTemplate.create(lookup, this.target);
      }
   }

   public final boolean equals(final Object obj) {
      return super.equals(obj);
   }

   public final int hashCode() {
      return super.hashCode();
   }

   public final String toString() {
      return "MonomorphizableDelegate[" + String.valueOf(this.target) + "]";
   }
}
