package net.minecraft.client.main;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public final class Main$1 implements ParameterizedType {
   public Main$1() {
      super();
   }

   @Override
   public Type[] getActualTypeArguments() {
      return new Type[]{String.class, new Main$1$1(this)};
   }

   @Override
   public Type getRawType() {
      return Map.class;
   }

   @Override
   public Type getOwnerType() {
      return null;
   }
}
