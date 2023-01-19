package net.minecraft.world.level.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;

public class HashMapPalette<T> implements Palette<T> {
   private final IdMap<T> registry;
   private final CrudeIncrementalIntIdentityHashBiMap<T> values;
   private final PaletteResize<T> resizeHandler;
   private final int bits;

   public HashMapPalette(IdMap<T> var1, int var2, PaletteResize<T> var3, List<T> var4) {
      this(var1, var2, var3);
      var4.forEach(this.values::add);
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
      return new HashMapPalette<>(var1, var0, var2, var3);
   }

   @Override
   public int idFor(T var1) {
      int var2 = this.values.getId((T)var1);
      if (var2 == -1) {
         var2 = this.values.add((T)var1);
         if (var2 >= 1 << this.bits) {
            var2 = this.resizeHandler.onResize(this.bits + 1, (T)var1);
         }
      }

      return var2;
   }

   @Override
   public boolean maybeHas(Predicate<T> var1) {
      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         if (var1.test(this.values.byId(var2))) {
            return true;
         }
      }

      return false;
   }

   @Override
   public T valueFor(int var1) {
      Object var2 = this.values.byId(var1);
      if (var2 == null) {
         throw new MissingPaletteEntryException(var1);
      } else {
         return (T)var2;
      }
   }

   @Override
   public void read(FriendlyByteBuf var1) {
      this.values.clear();
      int var2 = var1.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.values.add(this.registry.byIdOrThrow(var1.readVarInt()));
      }
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      int var2 = this.getSize();
      var1.writeVarInt(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.writeVarInt(this.registry.getId(this.values.byId(var3)));
      }
   }

   @Override
   public int getSerializedSize() {
      int var1 = FriendlyByteBuf.getVarIntSize(this.getSize());

      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         var1 += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values.byId(var2)));
      }

      return var1;
   }

   public List<T> getEntries() {
      ArrayList var1 = new ArrayList();
      this.values.iterator().forEachRemaining(var1::add);
      return var1;
   }

   @Override
   public int getSize() {
      return this.values.size();
   }

   @Override
   public Palette<T> copy() {
      return new HashMapPalette<>(this.registry, this.bits, this.resizeHandler, this.values.copy());
   }
}
