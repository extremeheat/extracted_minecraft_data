package net.minecraft.world.level.chunk;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public class LinearPalette<T> implements Palette<T> {
   private final IdMapper<T> registry;
   private final T[] values;
   private final PaletteResize<T> resizeHandler;
   private final Function<CompoundTag, T> reader;
   private final int bits;
   private int size;

   public LinearPalette(IdMapper<T> var1, int var2, PaletteResize<T> var3, Function<CompoundTag, T> var4) {
      super();
      this.registry = var1;
      this.values = (Object[])(new Object[1 << var2]);
      this.bits = var2;
      this.resizeHandler = var3;
      this.reader = var4;
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

   @Nullable
   public T valueFor(int var1) {
      return var1 >= 0 && var1 < this.size ? this.values[var1] : null;
   }

   public void read(FriendlyByteBuf var1) {
      this.size = var1.readVarInt();

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.values[var2] = this.registry.byId(var1.readVarInt());
      }

   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.size);

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeVarInt(this.registry.getId(this.values[var2]));
      }

   }

   public int getSerializedSize() {
      int var1 = FriendlyByteBuf.getVarIntSize(this.getSize());

      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         var1 += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values[var2]));
      }

      return var1;
   }

   public int getSize() {
      return this.size;
   }

   public void read(ListTag var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.values[var2] = this.reader.apply(var1.getCompound(var2));
      }

      this.size = var1.size();
   }
}
