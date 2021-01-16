package joptsimple.internal;

import java.lang.reflect.Constructor;
import joptsimple.ValueConverter;

class ConstructorInvokingValueConverter<V> implements ValueConverter<V> {
   private final Constructor<V> ctor;

   ConstructorInvokingValueConverter(Constructor<V> var1) {
      super();
      this.ctor = var1;
   }

   public V convert(String var1) {
      return Reflection.instantiate(this.ctor, var1);
   }

   public Class<V> valueType() {
      return this.ctor.getDeclaringClass();
   }

   public String valuePattern() {
      return null;
   }
}
