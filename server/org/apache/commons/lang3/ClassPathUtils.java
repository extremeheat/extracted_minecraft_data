package org.apache.commons.lang3;

public class ClassPathUtils {
   public ClassPathUtils() {
      super();
   }

   public static String toFullyQualifiedName(Class<?> var0, String var1) {
      Validate.notNull(var0, "Parameter '%s' must not be null!", "context");
      Validate.notNull(var1, "Parameter '%s' must not be null!", "resourceName");
      return toFullyQualifiedName(var0.getPackage(), var1);
   }

   public static String toFullyQualifiedName(Package var0, String var1) {
      Validate.notNull(var0, "Parameter '%s' must not be null!", "context");
      Validate.notNull(var1, "Parameter '%s' must not be null!", "resourceName");
      return var0.getName() + "." + var1;
   }

   public static String toFullyQualifiedPath(Class<?> var0, String var1) {
      Validate.notNull(var0, "Parameter '%s' must not be null!", "context");
      Validate.notNull(var1, "Parameter '%s' must not be null!", "resourceName");
      return toFullyQualifiedPath(var0.getPackage(), var1);
   }

   public static String toFullyQualifiedPath(Package var0, String var1) {
      Validate.notNull(var0, "Parameter '%s' must not be null!", "context");
      Validate.notNull(var1, "Parameter '%s' must not be null!", "resourceName");
      return var0.getName().replace('.', '/') + "/" + var1;
   }
}
