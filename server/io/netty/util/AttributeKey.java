package io.netty.util;

public final class AttributeKey<T> extends AbstractConstant<AttributeKey<T>> {
   private static final ConstantPool<AttributeKey<Object>> pool = new ConstantPool<AttributeKey<Object>>() {
      protected AttributeKey<Object> newConstant(int var1, String var2) {
         return new AttributeKey(var1, var2);
      }
   };

   public static <T> AttributeKey<T> valueOf(String var0) {
      return (AttributeKey)pool.valueOf(var0);
   }

   public static boolean exists(String var0) {
      return pool.exists(var0);
   }

   public static <T> AttributeKey<T> newInstance(String var0) {
      return (AttributeKey)pool.newInstance(var0);
   }

   public static <T> AttributeKey<T> valueOf(Class<?> var0, String var1) {
      return (AttributeKey)pool.valueOf(var0, var1);
   }

   private AttributeKey(int var1, String var2) {
      super(var1, var2);
   }

   // $FF: synthetic method
   AttributeKey(int var1, String var2, Object var3) {
      this(var1, var2);
   }
}
