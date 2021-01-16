package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class ResourceLeakDetectorFactory {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ResourceLeakDetectorFactory.class);
   private static volatile ResourceLeakDetectorFactory factoryInstance = new ResourceLeakDetectorFactory.DefaultResourceLeakDetectorFactory();

   public ResourceLeakDetectorFactory() {
      super();
   }

   public static ResourceLeakDetectorFactory instance() {
      return factoryInstance;
   }

   public static void setResourceLeakDetectorFactory(ResourceLeakDetectorFactory var0) {
      factoryInstance = (ResourceLeakDetectorFactory)ObjectUtil.checkNotNull(var0, "factory");
   }

   public final <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> var1) {
      return this.newResourceLeakDetector(var1, 128);
   }

   /** @deprecated */
   @Deprecated
   public abstract <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> var1, int var2, long var3);

   public <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> var1, int var2) {
      return this.newResourceLeakDetector(var1, 128, 9223372036854775807L);
   }

   private static final class DefaultResourceLeakDetectorFactory extends ResourceLeakDetectorFactory {
      private final Constructor<?> obsoleteCustomClassConstructor;
      private final Constructor<?> customClassConstructor;

      DefaultResourceLeakDetectorFactory() {
         super();

         String var1;
         try {
            var1 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
               public String run() {
                  return SystemPropertyUtil.get("io.netty.customResourceLeakDetector");
               }
            });
         } catch (Throwable var3) {
            ResourceLeakDetectorFactory.logger.error("Could not access System property: io.netty.customResourceLeakDetector", var3);
            var1 = null;
         }

         if (var1 == null) {
            this.obsoleteCustomClassConstructor = this.customClassConstructor = null;
         } else {
            this.obsoleteCustomClassConstructor = obsoleteCustomClassConstructor(var1);
            this.customClassConstructor = customClassConstructor(var1);
         }

      }

      private static Constructor<?> obsoleteCustomClassConstructor(String var0) {
         try {
            Class var1 = Class.forName(var0, true, PlatformDependent.getSystemClassLoader());
            if (ResourceLeakDetector.class.isAssignableFrom(var1)) {
               return var1.getConstructor(Class.class, Integer.TYPE, Long.TYPE);
            }

            ResourceLeakDetectorFactory.logger.error("Class {} does not inherit from ResourceLeakDetector.", (Object)var0);
         } catch (Throwable var2) {
            ResourceLeakDetectorFactory.logger.error("Could not load custom resource leak detector class provided: {}", var0, var2);
         }

         return null;
      }

      private static Constructor<?> customClassConstructor(String var0) {
         try {
            Class var1 = Class.forName(var0, true, PlatformDependent.getSystemClassLoader());
            if (ResourceLeakDetector.class.isAssignableFrom(var1)) {
               return var1.getConstructor(Class.class, Integer.TYPE);
            }

            ResourceLeakDetectorFactory.logger.error("Class {} does not inherit from ResourceLeakDetector.", (Object)var0);
         } catch (Throwable var2) {
            ResourceLeakDetectorFactory.logger.error("Could not load custom resource leak detector class provided: {}", var0, var2);
         }

         return null;
      }

      public <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> var1, int var2, long var3) {
         ResourceLeakDetector var5;
         if (this.obsoleteCustomClassConstructor != null) {
            try {
               var5 = (ResourceLeakDetector)this.obsoleteCustomClassConstructor.newInstance(var1, var2, var3);
               ResourceLeakDetectorFactory.logger.debug("Loaded custom ResourceLeakDetector: {}", (Object)this.obsoleteCustomClassConstructor.getDeclaringClass().getName());
               return var5;
            } catch (Throwable var6) {
               ResourceLeakDetectorFactory.logger.error("Could not load custom resource leak detector provided: {} with the given resource: {}", this.obsoleteCustomClassConstructor.getDeclaringClass().getName(), var1, var6);
            }
         }

         var5 = new ResourceLeakDetector(var1, var2, var3);
         ResourceLeakDetectorFactory.logger.debug("Loaded default ResourceLeakDetector: {}", (Object)var5);
         return var5;
      }

      public <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> var1, int var2) {
         ResourceLeakDetector var3;
         if (this.customClassConstructor != null) {
            try {
               var3 = (ResourceLeakDetector)this.customClassConstructor.newInstance(var1, var2);
               ResourceLeakDetectorFactory.logger.debug("Loaded custom ResourceLeakDetector: {}", (Object)this.customClassConstructor.getDeclaringClass().getName());
               return var3;
            } catch (Throwable var4) {
               ResourceLeakDetectorFactory.logger.error("Could not load custom resource leak detector provided: {} with the given resource: {}", this.customClassConstructor.getDeclaringClass().getName(), var1, var4);
            }
         }

         var3 = new ResourceLeakDetector(var1, var2);
         ResourceLeakDetectorFactory.logger.debug("Loaded default ResourceLeakDetector: {}", (Object)var3);
         return var3;
      }
   }
}
