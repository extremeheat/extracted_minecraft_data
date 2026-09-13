package net.minecraft.tileentity;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEntityNote extends TileEntity {
   public byte field_145879_a;
   public boolean field_145880_i;

   public TileEntityNote() {
      super();
   }

   @Override
   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      var1.func_74774_a("note", this.field_145879_a);
   }

   @Override
   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_145879_a = var1.func_74771_c("note");
      if (this.field_145879_a < 0) {
         this.field_145879_a = 0;
      }

      if (this.field_145879_a > 24) {
         this.field_145879_a = 24;
      }
   }

   public void func_145877_a() {
      this.field_145879_a = (byte)((this.field_145879_a + 1) % 25);
      this.func_70296_d();
   }

   public void func_145878_a(World var1, int var2, int var3, int var4) {
      if (var1.func_147439_a(var2, var3 + 1, var4).func_149688_o() == Material.field_151579_a) {
         Material var5 = var1.func_147439_a(var2, var3 - 1, var4).func_149688_o();
         byte var6 = 0;
         if (var5 == Material.field_151576_e) {
            var6 = 1;
         }

         if (var5 == Material.field_151595_p) {
            var6 = 2;
         }

         if (var5 == Material.field_151592_s) {
            var6 = 3;
         }

         if (var5 == Material.field_151575_d) {
            var6 = 4;
         }

         var1.func_147452_c(var2, var3, var4, Blocks.field_150323_B, var6, this.field_145879_a);
      }
   }
}
