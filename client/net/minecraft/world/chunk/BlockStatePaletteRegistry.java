package net.minecraft.world.chunk;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ObjectIntIdentityMap;

public class BlockStatePaletteRegistry<T> implements IBlockStatePalette<T> {
   private final ObjectIntIdentityMap<T> field_205505_a;
   private final T field_205506_b;

   public BlockStatePaletteRegistry(ObjectIntIdentityMap<T> var1, T var2) {
      super();
      this.field_205505_a = var1;
      this.field_205506_b = var2;
   }

   public int func_186041_a(T var1) {
      int var2 = this.field_205505_a.func_148747_b(var1);
      return var2 == -1 ? 0 : var2;
   }

   public T func_186039_a(int var1) {
      Object var2 = this.field_205505_a.func_148745_a(var1);
      return var2 == null ? this.field_205506_b : var2;
   }

   public void func_186038_a(PacketBuffer var1) {
   }

   public void func_186037_b(PacketBuffer var1) {
   }

   public int func_186040_a() {
      return PacketBuffer.func_150790_a(0);
   }

   public void func_196968_a(NBTTagList var1) {
   }
}
