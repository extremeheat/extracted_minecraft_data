package net.minecraft.world.level.chunk;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import org.apache.commons.lang3.Validate;

public class LinearPalette<T> implements Palette<T> {
   private final T[] values;
   private final int bits;
   private int size;

   private LinearPalette(int var1, List<T> var2) {
      super();
      this.values = (T[])(new Object[1 << var1]);
      this.bits = var1;
      Validate.isTrue(var2.size() <= this.values.length, "Can't initialize LinearPalette of size %d with %d entries", new Object[]{this.values.length, var2.size()});

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         this.values[var3] = var2.get(var3);
      }

      this.size = var2.size();
   }

   private LinearPalette(T[] var1, int var2, int var3) {
      super();
      this.values = (T[])var1;
      this.bits = var2;
      this.size = var3;
   }

   public static <A> Palette<A> create(int var0, List<A> var1) {
      return new LinearPalette<A>(var0, var1);
   }

   public int idFor(T var1, PaletteResize<T> var2) {
      for(int var3 = 0; var3 < this.size; ++var3) {
         if (this.values[var3] == var1) {
            return var3;
         }
      }

      int var4 = this.size;
      if (var4 < this.values.length) {
         this.values[var4] = var1;
         ++this.size;
         return var4;
      } else {
         return var2.onResize(this.bits + 1, var1);
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
         return (T)this.values[var1];
      } else {
         throw new MissingPaletteEntryException(var1);
      }
   }

   public void read(FriendlyByteBuf var1, IdMap<T> var2) {
      this.size = var1.readVarInt();

      for(int var3 = 0; var3 < this.size; ++var3) {
         this.values[var3] = var2.byIdOrThrow(var1.readVarInt());
      }

   }

   public void write(FriendlyByteBuf var1, IdMap<T> var2) {
      var1.writeVarInt(this.size);

      for(int var3 = 0; var3 < this.size; ++var3) {
         var1.writeVarInt(var2.getId(this.values[var3]));
      }

   }

   public int getSerializedSize(IdMap<T> var1) {
      int var2 = VarInt.getByteSize(this.getSize());

      for(int var3 = 0; var3 < this.getSize(); ++var3) {
         var2 += VarInt.getByteSize(var1.getId(this.values[var3]));
      }

      return var2;
   }

   public int getSize() {
      return this.size;
   }

   public Palette<T> copy() {
      return new LinearPalette<T>(this.values.clone(), this.bits, this.size);
   }
}
