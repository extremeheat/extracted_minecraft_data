package net.minecraft.world.level.chunk;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.ArrayUtils;

public class LinearPalette implements Palette {
   private final IdMapper registry;
   private final Object[] values;
   private final PaletteResize resizeHandler;
   private final Function reader;
   private final int bits;
   private int size;

   public LinearPalette(IdMapper var1, int var2, PaletteResize var3, Function var4) {
      this.registry = var1;
      this.values = (Object[])(new Object[1 << var2]);
      this.bits = var2;
      this.resizeHandler = var3;
      this.reader = var4;
   }

   public int idFor(Object var1) {
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

   public boolean maybeHas(Object var1) {
      return ArrayUtils.contains(this.values, var1);
   }

   @Nullable
   public Object valueFor(int var1) {
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
