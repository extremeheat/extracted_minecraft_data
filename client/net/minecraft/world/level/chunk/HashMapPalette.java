package net.minecraft.world.level.chunk;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;

public class HashMapPalette<T> implements Palette<T> {
   private final CrudeIncrementalIntIdentityHashBiMap<T> values;
   private final int bits;

   public HashMapPalette(final int bits, final List<T> values) {
      this(bits);
      CrudeIncrementalIntIdentityHashBiMap var10001 = this.values;
      Objects.requireNonNull(var10001);
      values.forEach(var10001::add);
   }

   public HashMapPalette(final int bits) {
      this(bits, CrudeIncrementalIntIdentityHashBiMap.create(1 << bits));
   }

   private HashMapPalette(final int bits, final CrudeIncrementalIntIdentityHashBiMap<T> values) {
      super();
      this.bits = bits;
      this.values = values;
   }

   public static <A> Palette<A> create(final int bits, final List<A> paletteEntries) {
      return new HashMapPalette<A>(bits, paletteEntries);
   }

   public int idFor(final T value, final PaletteResize<T> resizeHandler) {
      int id = this.values.getId(value);
      if (id == -1) {
         id = this.values.add(value);
         if (id >= 1 << this.bits) {
            id = resizeHandler.onResize(this.bits + 1, value);
         }
      }

      return id;
   }

   public boolean maybeHas(final Predicate<T> predicate) {
      for(int i = 0; i < this.getSize(); ++i) {
         if (predicate.test(this.values.byId(i))) {
            return true;
         }
      }

      return false;
   }

   public T valueFor(final int index) {
      T value = this.values.byId(index);
      if (value == null) {
         throw new MissingPaletteEntryException(index);
      } else {
         return value;
      }
   }

   public void read(final FriendlyByteBuf buffer, final IdMap<T> globalMap) {
      this.values.clear();
      int size = buffer.readVarInt();

      for(int i = 0; i < size; ++i) {
         this.values.add(globalMap.byIdOrThrow(buffer.readVarInt()));
      }

   }

   public void write(final FriendlyByteBuf buffer, final IdMap<T> globalMap) {
      int size = this.getSize();
      buffer.writeVarInt(size);

      for(int i = 0; i < size; ++i) {
         buffer.writeVarInt(globalMap.getId(this.values.byId(i)));
      }

   }

   public int getSerializedSize(final IdMap<T> globalMap) {
      int size = VarInt.getByteSize(this.getSize());

      for(int i = 0; i < this.getSize(); ++i) {
         size += VarInt.getByteSize(globalMap.getId(this.values.byId(i)));
      }

      return size;
   }

   public List<T> getEntries() {
      ArrayList<T> list = new ArrayList();
      Iterator var10000 = this.values.iterator();
      Objects.requireNonNull(list);
      var10000.forEachRemaining(list::add);
      return list;
   }

   public int getSize() {
      return this.values.size();
   }

   public Palette<T> copy() {
      return new HashMapPalette<T>(this.bits, this.values.copy());
   }
}
