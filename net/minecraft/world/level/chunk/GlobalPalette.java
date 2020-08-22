package net.minecraft.world.level.chunk;

import net.minecraft.core.IdMapper;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public class GlobalPalette implements Palette {
   private final IdMapper registry;
   private final Object defaultValue;

   public GlobalPalette(IdMapper var1, Object var2) {
      this.registry = var1;
      this.defaultValue = var2;
   }

   public int idFor(Object var1) {
      int var2 = this.registry.getId(var1);
      return var2 == -1 ? 0 : var2;
   }

   public boolean maybeHas(Object var1) {
      return true;
   }

   public Object valueFor(int var1) {
      Object var2 = this.registry.byId(var1);
      return var2 == null ? this.defaultValue : var2;
   }

   public void read(FriendlyByteBuf var1) {
   }

   public void write(FriendlyByteBuf var1) {
   }

   public int getSerializedSize() {
      return FriendlyByteBuf.getVarIntSize(0);
   }

   public void read(ListTag var1) {
   }
}
