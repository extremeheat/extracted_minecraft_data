package net.minecraft.client.main;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

class Main$1$1 implements ParameterizedType {
   Main$1$1(Main$1 var1) {
      super();
      this.field_152580_a = var1;
   }

   @Override
   public Type[] getActualTypeArguments() {
      return new Type[]{String.class};
   }

   @Override
   public Type getRawType() {
      return Collection.class;
   }

   @Override
   public Type getOwnerType() {
      return null;
   }
}
