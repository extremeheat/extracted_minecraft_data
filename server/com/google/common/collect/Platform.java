package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.lang.reflect.Array;

@GwtCompatible(
   emulated = true
)
final class Platform {
   static <T> T[] newArray(T[] var0, int var1) {
      Class var2 = var0.getClass().getComponentType();
      Object[] var3 = (Object[])((Object[])Array.newInstance(var2, var1));
      return var3;
   }

   static MapMaker tryWeakKeys(MapMaker var0) {
      return var0.weakKeys();
   }

   private Platform() {
      super();
   }
}
