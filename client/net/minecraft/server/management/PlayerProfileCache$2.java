package net.minecraft.server.management;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

final class PlayerProfileCache$2 implements ParameterizedType {
   PlayerProfileCache$2() {
      super();
   }

   @Override
   public Type[] getActualTypeArguments() {
      return new Type[]{PlayerProfileCache$ProfileEntry.class};
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
