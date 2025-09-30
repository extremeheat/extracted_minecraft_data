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

   public HashMapPalette(int var1, List<T> var2) {
      this(var1);
      CrudeIncrementalIntIdentityHashBiMap var10001 = this.values;
      Objects.requireNonNull(var10001);
      var2.forEach(var10001::add);
   }

   public HashMapPalette(int var1) {
      this(var1, CrudeIncrementalIntIdentityHashBiMap.create(1 << var1));
   }

   private HashMapPalette(int var1, CrudeIncrementalIntIdentityHashBiMap<T> var2) {
      super();
      this.bits = var1;
      this.values = var2;
   }

   public static <A> Palette<A> create(int var0, List<A> var1) {
      return new HashMapPalette<A>(var0, var1);
   }

   public int idFor(T var1, PaletteResize<T> var2) {
      int var3 = this.values.getId(var1);
      if (var3 == -1) {
         var3 = this.values.add(var1);
         if (var3 >= 1 << this.bits) {
            var3 = var2.onResize(this.bits + 1, var1);
         }
      }

      return var3;
   }

   public boolean maybeHas(Predicate<T> var1) {
      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         if (var1.test(this.values.byId(var2))) {
            return true;
         }
      }

      return false;
   }

   public T valueFor(int var1) {
      Object var2 = this.values.byId(var1);
      if (var2 == null) {
         throw new MissingPaletteEntryException(var1);
      } else {
         return (T)var2;
      }
   }

   public void read(FriendlyByteBuf var1, IdMap<T> var2) {
      this.values.clear();
      int var3 = var1.readVarInt();

      for(int var4 = 0; var4 < var3; ++var4) {
         this.values.add(var2.byIdOrThrow(var1.readVarInt()));
      }

   }

   public void write(FriendlyByteBuf var1, IdMap<T> var2) {
      int var3 = this.getSize();
      var1.writeVarInt(var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         var1.writeVarInt(var2.getId(this.values.byId(var4)));
      }

   }

   public int getSerializedSize(IdMap<T> var1) {
      int var2 = VarInt.getByteSize(this.getSize());

      for(int var3 = 0; var3 < this.getSize(); ++var3) {
         var2 += VarInt.getByteSize(var1.getId(this.values.byId(var3)));
      }

      return var2;
   }

   public List<T> getEntries() {
      ArrayList var1 = new ArrayList();
      Iterator var10000 = this.values.iterator();
      Objects.requireNonNull(var1);
      var10000.forEachRemaining(var1::add);
      return var1;
   }

   public int getSize() {
      return this.values.size();
   }

   public Palette<T> copy() {
      return new HashMapPalette<T>(this.bits, this.values.copy());
   }
}
