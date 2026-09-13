package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.world.World;

public class BlockNote extends BlockContainer {
   public BlockNote() {
      super(Material.field_151575_d);
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      boolean var6 = var1.func_72864_z(var2, var3, var4);
      TileEntityNote var7 = (TileEntityNote)var1.func_147438_o(var2, var3, var4);
      if (var7 != null && var7.field_145880_i != var6) {
         if (var6) {
            var7.func_145878_a(var1, var2, var3, var4);
         }

         var7.field_145880_i = var6;
      }
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntityNote var10 = (TileEntityNote)var1.func_147438_o(var2, var3, var4);
         if (var10 != null) {
            var10.func_145877_a();
            var10.func_145878_a(var1, var2, var3, var4);
         }

         return true;
      }
   }

   @Override
   public void func_149699_a(World var1, int var2, int var3, int var4, EntityPlayer var5) {
      if (!var1.field_72995_K) {
         TileEntityNote var6 = (TileEntityNote)var1.func_147438_o(var2, var3, var4);
         if (var6 != null) {
            var6.func_145878_a(var1, var2, var3, var4);
         }
      }
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityNote();
   }

   @Override
   public boolean func_149696_a(World var1, int var2, int var3, int var4, int var5, int var6) {
      float var7 = (float)Math.pow(2.0, (double)(var6 - 12) / 12.0);
      String var8 = "harp";
      if (var5 == 1) {
         var8 = "bd";
      }

      if (var5 == 2) {
         var8 = "snare";
      }

      if (var5 == 3) {
         var8 = "hat";
      }

      if (var5 == 4) {
         var8 = "bassattack";
      }

      var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.5, (double)var4 + 0.5, "note." + var8, 3.0F, var7);
      var1.func_72869_a("note", (double)var2 + 0.5, (double)var3 + 1.2, (double)var4 + 0.5, (double)var6 / 24.0, 0.0, 0.0);
      return true;
   }
}
