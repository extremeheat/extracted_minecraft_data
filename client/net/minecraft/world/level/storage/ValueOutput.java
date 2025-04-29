package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;

public interface ValueOutput {
   <T> void store(String var1, Codec<T> var2, T var3);

   <T> void storeNullable(String var1, Codec<T> var2, @Nullable T var3);

   /** @deprecated */
   @Deprecated
   <T> void store(MapCodec<T> var1, T var2);

   void putBoolean(String var1, boolean var2);

   void putByte(String var1, byte var2);

   void putShort(String var1, short var2);

   void putInt(String var1, int var2);

   void putLong(String var1, long var2);

   void putFloat(String var1, float var2);

   void putDouble(String var1, double var2);

   void putString(String var1, String var2);

   void putIntArray(String var1, int[] var2);

   ValueOutput child(String var1);

   ValueOutputList childrenList(String var1);

   <T> TypedOutputList<T> list(String var1, Codec<T> var2);

   void discard(String var1);

   boolean isEmpty();

   public interface TypedOutputList<T> {
      void add(T var1);

      boolean isEmpty();
   }

   public interface ValueOutputList {
      ValueOutput addChild();

      void discardLast();

      boolean isEmpty();
   }
}
