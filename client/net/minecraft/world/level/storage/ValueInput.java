package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;

public interface ValueInput {
   <T> Optional<T> read(String name, Codec<T> codec);

   /** @deprecated */
   @Deprecated
   <T> Optional<T> read(MapCodec<T> codec);

   Optional<ValueInput> child(String name);

   ValueInput childOrEmpty(String name);

   Optional<ValueInputList> childrenList(String name);

   ValueInputList childrenListOrEmpty(String name);

   <T> Optional<TypedInputList<T>> list(String name, Codec<T> codec);

   <T> TypedInputList<T> listOrEmpty(String name, Codec<T> codec);

   boolean getBooleanOr(String name, boolean defaultValue);

   byte getByteOr(String name, byte defaultValue);

   int getShortOr(String name, short defaultValue);

   Optional<Integer> getInt(String name);

   int getIntOr(String name, int defaultValue);

   long getLongOr(String name, long defaultValue);

   Optional<Long> getLong(String name);

   float getFloatOr(String name, float defaultValue);

   double getDoubleOr(String name, double defaultValue);

   Optional<String> getString(String name);

   String getStringOr(String name, String defaultValue);

   Optional<int[]> getIntArray(String name);

   /** @deprecated */
   @Deprecated
   HolderLookup.Provider lookup();

   public interface TypedInputList<T> extends Iterable<T> {
      boolean isEmpty();

      Stream<T> stream();
   }

   public interface ValueInputList extends Iterable<ValueInput> {
      boolean isEmpty();

      Stream<ValueInput> stream();
   }
}
