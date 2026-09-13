package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemLead;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFence extends Block {
   private final String field_149827_a;

   public BlockFence(String var1, Material var2) {
      super(var2);
      this.field_149827_a = var1;
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
      boolean var8 = this.func_149826_e(var1, var2, var3, var4 - 1);
      boolean var9 = this.func_149826_e(var1, var2, var3, var4 + 1);
      boolean var10 = this.func_149826_e(var1, var2 - 1, var3, var4);
      boolean var11 = this.func_149826_e(var1, var2 + 1, var3, var4);
      float var12 = 0.375F;
      float var13 = 0.625F;
      float var14 = 0.375F;
      float var15 = 0.625F;
      if (var8) {
         var14 = 0.0F;
      }

      if (var9) {
         var15 = 1.0F;
      }

      if (var8 || var9) {
         this.func_149676_a(var12, 0.0F, var14, var13, 1.5F, var15);
         super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      }

      var14 = 0.375F;
      var15 = 0.625F;
      if (var10) {
         var12 = 0.0F;
      }

      if (var11) {
         var13 = 1.0F;
      }

      if (var10 || var11 || !var8 && !var9) {
         this.func_149676_a(var12, 0.0F, var14, var13, 1.5F, var15);
         super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      }

      if (var8) {
         var14 = 0.0F;
      }

      if (var9) {
         var15 = 1.0F;
      }

      this.func_149676_a(var12, 0.0F, var14, var13, 1.0F, var15);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      boolean var5 = this.func_149826_e(var1, var2, var3, var4 - 1);
      boolean var6 = this.func_149826_e(var1, var2, var3, var4 + 1);
      boolean var7 = this.func_149826_e(var1, var2 - 1, var3, var4);
      boolean var8 = this.func_149826_e(var1, var2 + 1, var3, var4);
      float var9 = 0.375F;
      float var10 = 0.625F;
      float var11 = 0.375F;
      float var12 = 0.625F;
      if (var5) {
         var11 = 0.0F;
      }

      if (var6) {
         var12 = 1.0F;
      }

      if (var7) {
         var9 = 0.0F;
      }

      if (var8) {
         var10 = 1.0F;
      }

      this.func_149676_a(var9, 0.0F, var11, var10, 1.0F, var12);
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public boolean func_149655_b(IBlockAccess var1, int var2, int var3, int var4) {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 11;
   }

   public boolean func_149826_e(IBlockAccess var1, int var2, int var3, int var4) {
      Block var5 = var1.func_147439_a(var2, var3, var4);
      if (var5 == this || var5 == Blocks.field_150396_be) {
         return true;
      } else if (var5.field_149764_J.func_76218_k() && var5.func_149686_d()) {
         return var5.field_149764_J != Material.field_151572_C;
      } else {
         return false;
      }
   }

   public static boolean func_149825_a(Block var0) {
      return var0 == Blocks.field_150422_aJ || var0 == Blocks.field_150386_bk;
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return true;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.field_149827_a);
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         return ItemLead.func_150909_a(var5, var1, var2, var3, var4);
      }
   }
}
