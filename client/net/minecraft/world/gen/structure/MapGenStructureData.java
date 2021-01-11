package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class MapGenStructureData extends WorldSavedData {
   private NBTTagCompound field_143044_a = new NBTTagCompound();

   public MapGenStructureData(String var1) {
      super(var1);
   }

   public void func_76184_a(NBTTagCompound var1) {
      this.field_143044_a = var1.func_74775_l("Features");
   }

   public void func_76187_b(NBTTagCompound var1) {
      var1.func_74782_a("Features", this.field_143044_a);
   }

   public void func_143043_a(NBTTagCompound var1, int var2, int var3) {
      this.field_143044_a.func_74782_a(func_143042_b(var2, var3), var1);
   }

   public static String func_143042_b(int var0, int var1) {
      return "[" + var0 + "," + var1 + "]";
   }

   public NBTTagCompound func_143041_a() {
      return this.field_143044_a;
   }
}
