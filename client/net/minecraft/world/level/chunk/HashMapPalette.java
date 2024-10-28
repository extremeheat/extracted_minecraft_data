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
   private final IdMap<T> registry;
   private final CrudeIncrementalIntIdentityHashBiMap<T> values;
   private final PaletteResize<T> resizeHandler;
   private final int bits;

   public HashMapPalette(IdMap<T> var1, int var2, PaletteResize<T> var3, List<T> var4) {
      this(var1, var2, var3);
      CrudeIncrementalIntIdentityHashBiMap var10001 = this.values;
      Objects.requireNonNull(var10001);
      var4.forEach(var10001::add);
   }

   public HashMapPalette(IdMap<T> var1, int var2, PaletteResize<T> var3) {
      this(var1, var2, var3, CrudeIncrementalIntIdentityHashBiMap.create(1 << var2));
   }

   private HashMapPalette(IdMap<T> var1, int var2, PaletteResize<T> var3, CrudeIncrementalIntIdentityHashBiMap<T> var4) {
      super();
      this.registry = var1;
      this.bits = var2;
      this.resizeHandler = var3;
      this.values = var4;
   }

   public static <A> Palette<A> create(int var0, IdMap<A> var1, PaletteResize<A> var2, List<A> var3) {
      return new HashMapPalette(var1, var0, var2, var3);
   }

   public int idFor(T var1) {
      int var2 = this.values.getId(var1);
      if (var2 == -1) {
         var2 = this.values.add(var1);
         if (var2 >= 1 << this.bits) {
            var2 = this.resizeHandler.onResize(this.bits + 1, var1);
         }
      }

      return var2;
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
         return var2;
      }
   }

   public void read(FriendlyByteBuf var1) {
      this.values.clear();
      int var2 = var1.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.values.add(this.registry.byIdOrThrow(var1.readVarInt()));
      }

   }

   public void write(FriendlyByteBuf var1) {
      int var2 = this.getSize();
      var1.writeVarInt(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.writeVarInt(this.registry.getId(this.values.byId(var3)));
      }

   }

   public int getSerializedSize() {
      int var1 = VarInt.getByteSize(this.getSize());

      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         var1 += VarInt.getByteSize(this.registry.getId(this.values.byId(var2)));
      }

      return var1;
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
      return new HashMapPalette(this.registry, this.bits, this.resizeHandler, this.values.copy());
   }
}
