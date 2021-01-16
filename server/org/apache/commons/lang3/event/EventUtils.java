package org.apache.commons.lang3.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.reflect.MethodUtils;

public class EventUtils {
   public EventUtils() {
      super();
   }

   public static <L> void addEventListener(Object var0, Class<L> var1, L var2) {
      try {
         MethodUtils.invokeMethod(var0, "add" + var1.getSimpleName(), var2);
      } catch (NoSuchMethodException var4) {
         throw new IllegalArgumentException("Class " + var0.getClass().getName() + " does not have a public add" + var1.getSimpleName() + " method which takes a parameter of type " + var1.getName() + ".");
      } catch (IllegalAccessException var5) {
         throw new IllegalArgumentException("Class " + var0.getClass().getName() + " does not have an accessible add" + var1.getSimpleName() + " method which takes a parameter of type " + var1.getName() + ".");
      } catch (InvocationTargetException var6) {
         throw new RuntimeException("Unable to add listener.", var6.getCause());
      }
   }

   public static <L> void bindEventsToMethod(Object var0, String var1, Object var2, Class<L> var3, String... var4) {
      Object var5 = var3.cast(Proxy.newProxyInstance(var0.getClass().getClassLoader(), new Class[]{var3}, new EventUtils.EventBindingInvocationHandler(var0, var1, var4)));
      addEventListener(var2, var3, var5);
   }

   private static class EventBindingInvocationHandler implements InvocationHandler {
      private final Object target;
      private final String methodName;
      private final Set<String> eventTypes;

      EventBindingInvocationHandler(Object var1, String var2, String[] var3) {
         super();
         this.target = var1;
         this.methodName = var2;
         this.eventTypes = new HashSet(Arrays.asList(var3));
      }

      public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
         if (!this.eventTypes.isEmpty() && !this.eventTypes.contains(var2.getName())) {
            return null;
         } else {
            return this.hasMatchingParametersMethod(var2) ? MethodUtils.invokeMethod(this.target, this.methodName, var3) : MethodUtils.invokeMethod(this.target, this.methodName);
         }
      }

      private boolean hasMatchingParametersMethod(Method var1) {
         return MethodUtils.getAccessibleMethod(this.target.getClass(), this.methodName, var1.getParameterTypes()) != null;
      }
   }
}
