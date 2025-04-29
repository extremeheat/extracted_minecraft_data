package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

public class ValueInputContextHelper {
   final HolderLookup.Provider lookup;
   private final DynamicOps<Tag> ops;
   final ValueInput.ValueInputList emptyChildList = new ValueInput.ValueInputList() {
      public boolean isEmpty() {
         return true;
      }

      public Stream<ValueInput> stream() {
         return Stream.empty();
      }

      public Iterator<ValueInput> iterator() {
         return Collections.emptyIterator();
      }
   };
   private final ValueInput.TypedInputList<Object> emptyTypedList = new ValueInput.TypedInputList<Object>() {
      public boolean isEmpty() {
         return true;
      }

      public Stream<Object> stream() {
         return Stream.empty();
      }

      public Iterator<Object> iterator() {
         return Collections.emptyIterator();
      }
   };
   private final ValueInput empty = new ValueInput() {
      public <T> Optional<T> read(String var1, Codec<T> var2) {
         return Optional.empty();
      }

      public <T> Optional<T> read(MapCodec<T> var1) {
         return Optional.empty();
      }

      public Optional<ValueInput> child(String var1) {
         return Optional.empty();
      }

      public ValueInput childOrEmpty(String var1) {
         return this;
      }

      public Optional<ValueInput.ValueInputList> childrenList(String var1) {
         return Optional.empty();
      }

      public ValueInput.ValueInputList childrenListOrEmpty(String var1) {
         return ValueInputContextHelper.this.emptyChildList;
      }

      public <T> Optional<ValueInput.TypedInputList<T>> list(String var1, Codec<T> var2) {
         return Optional.empty();
      }

      public <T> ValueInput.TypedInputList<T> listOrEmpty(String var1, Codec<T> var2) {
         return ValueInputContextHelper.this.<T>emptyTypedList();
      }

      public boolean getBooleanOr(String var1, boolean var2) {
         return var2;
      }

      public byte getByteOr(String var1, byte var2) {
         return var2;
      }

      public int getShortOr(String var1, short var2) {
         return var2;
      }

      public Optional<Integer> getInt(String var1) {
         return Optional.empty();
      }

      public int getIntOr(String var1, int var2) {
         return var2;
      }

      public long getLongOr(String var1, long var2) {
         return var2;
      }

      public Optional<Long> getLong(String var1) {
         return Optional.empty();
      }

      public float getFloatOr(String var1, float var2) {
         return var2;
      }

      public double getDoubleOr(String var1, double var2) {
         return var2;
      }

      public Optional<String> getString(String var1) {
         return Optional.empty();
      }

      public String getStringOr(String var1, String var2) {
         return var2;
      }

      public HolderLookup.Provider lookup() {
         return ValueInputContextHelper.this.lookup;
      }

      public Optional<int[]> getIntArray(String var1) {
         return Optional.empty();
      }
   };

   public ValueInputContextHelper(HolderLookup.Provider var1, DynamicOps<Tag> var2) {
      super();
      this.lookup = var1;
      this.ops = var1.<Tag>createSerializationContext(var2);
   }

   public DynamicOps<Tag> ops() {
      return this.ops;
   }

   public HolderLookup.Provider lookup() {
      return this.lookup;
   }

   public ValueInput empty() {
      return this.empty;
   }

   public ValueInput.ValueInputList emptyList() {
      return this.emptyChildList;
   }

   public <T> ValueInput.TypedInputList<T> emptyTypedList() {
      return this.emptyTypedList;
   }
}
