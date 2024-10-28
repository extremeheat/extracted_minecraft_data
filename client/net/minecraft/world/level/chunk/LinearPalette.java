package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import org.apache.commons.lang3.Validate;

public class LinearPalette<T> implements Palette<T> {
   private final IdMap<T> registry;
   private final T[] values;
   private final PaletteResize<T> resizeHandler;
   private final int bits;
   private int size;

   private LinearPalette(IdMap<T> var1, int var2, PaletteResize<T> var3, List<T> var4) {
      super();
      this.registry = var1;
      this.values = new Object[1 << var2];
      this.bits = var2;
      this.resizeHandler = var3;
      Validate.isTrue(var4.size() <= this.values.length, "Can't initialize LinearPalette of size %d with %d entries", new Object[]{this.values.length, var4.size()});

      for(int var5 = 0; var5 < var4.size(); ++var5) {
         this.values[var5] = var4.get(var5);
      }

      this.size = var4.size();
   }

   private LinearPalette(IdMap<T> var1, T[] var2, PaletteResize<T> var3, int var4, int var5) {
      super();
      this.registry = var1;
      this.values = var2;
      this.resizeHandler = var3;
      this.bits = var4;
      this.size = var5;
   }

   public static <A> Palette<A> create(int var0, IdMap<A> var1, PaletteResize<A> var2, List<A> var3) {
      return new LinearPalette(var1, var0, var2, var3);
   }

   public int idFor(T var1) {
      int var2;
      for(var2 = 0; var2 < this.size; ++var2) {
         if (this.values[var2] == var1) {
            return var2;
         }
      }

      var2 = this.size;
      if (var2 < this.values.length) {
         this.values[var2] = var1;
         ++this.size;
         return var2;
      } else {
         return this.resizeHandler.onResize(this.bits + 1, var1);
      }
   }

   public boolean maybeHas(Predicate<T> var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         if (var1.test(this.values[var2])) {
            return true;
         }
      }

      return false;
   }

   public T valueFor(int var1) {
      if (var1 >= 0 && var1 < this.size) {
         return this.values[var1];
      } else {
         throw new MissingPaletteEntryException(var1);
      }
   }

   public void read(FriendlyByteBuf var1) {
      this.size = var1.readVarInt();

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.values[var2] = this.registry.byIdOrThrow(var1.readVarInt());
      }

   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.size);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeVarInt(this.registry.getId(this.values[var2]));
      }

   }

   public int getSerializedSize() {
      int var1 = VarInt.getByteSize(this.getSize());

      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         var1 += VarInt.getByteSize(this.registry.getId(this.values[var2]));
      }

      return var1;
   }

   public int getSize() {
      return this.size;
   }

   public Palette<T> copy() {
      return new LinearPalette(this.registry, (Object[])this.values.clone(), this.resizeHandler, this.bits, this.size);
   }
}
