package joptsimple.internal;

import java.lang.reflect.Method;
import joptsimple.ValueConverter;

class MethodInvokingValueConverter<V> implements ValueConverter<V> {
   private final Method method;
   private final Class<V> clazz;

   MethodInvokingValueConverter(Method var1, Class<V> var2) {
      super();
      this.method = var1;
      this.clazz = var2;
   }

   public V convert(String var1) {
      return this.clazz.cast(Reflection.invoke(this.method, var1));
   }

   public Class<V> valueType() {
      return this.clazz;
   }

   public String valuePattern() {
      return null;
   }
}
