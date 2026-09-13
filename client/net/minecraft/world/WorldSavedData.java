package net.minecraft.world;

import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldSavedData {
   public final String field_76190_i;
   private boolean field_76189_a;

   public WorldSavedData(String var1) {
      super();
      this.field_76190_i = var1;
   }

   public abstract void func_76184_a(NBTTagCompound var1);

   public abstract void func_76187_b(NBTTagCompound var1);

   public void func_76185_a() {
      this.func_76186_a(true);
   }

   public void func_76186_a(boolean var1) {
      this.field_76189_a = var1;
   }

   public boolean func_76188_b() {
      return this.field_76189_a;
   }
}
