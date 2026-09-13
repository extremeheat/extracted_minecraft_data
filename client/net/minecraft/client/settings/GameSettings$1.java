package net.minecraft.client.settings;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

final class GameSettings$1 implements ParameterizedType {
   GameSettings$1() {
      super();
   }

   @Override
   public Type[] getActualTypeArguments() {
      return new Type[]{String.class};
   }

   @Override
   public Type getRawType() {
      return List.class;
   }

   @Override
   public Type getOwnerType() {
      return null;
   }
}
