package net.minecraft.client.audio;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

final class SoundHandler$1 implements ParameterizedType {
   SoundHandler$1() {
      super();
   }

   @Override
   public Type[] getActualTypeArguments() {
      return new Type[]{String.class, SoundList.class};
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
