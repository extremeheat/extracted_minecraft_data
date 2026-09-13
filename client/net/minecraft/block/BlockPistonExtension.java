package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonExtension extends Block {
   private IIcon field_150088_a;

   public BlockPistonExtension() {
      super(Material.field_76233_E);
      this.func_149672_a(field_149780_i);
      this.func_149711_c(0.5F);
   }

   public void func_150086_a(IIcon var1) {
      this.field_150088_a = var1;
   }

   public void func_150087_e() {
      this.field_150088_a = null;
   }

   @Override
   public void func_149681_a(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
      if (var6.field_71075_bZ.field_75098_d) {
         int var7 = func_150085_b(var5);
         Block var8 = var1.func_147439_a(var2 - Facing.field_71586_b[var7], var3 - Facing.field_71587_c[var7], var4 - Facing.field_71585_d[var7]);
         if (var8 == Blocks.field_150331_J || var8 == Blocks.field_150320_F) {
            var1.func_147468_f(var2 - Facing.field_71586_b[var7], var3 - Facing.field_71587_c[var7], var4 - Facing.field_71585_d[var7]);
         }
      }

      super.func_149681_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      super.func_149749_a(var1, var2, var3, var4, var5, var6);
      int var7 = Facing.field_71588_a[func_150085_b(var6)];
      var2 += Facing.field_71586_b[var7];
      var3 += Facing.field_71587_c[var7];
      var4 += Facing.field_71585_d[var7];
      Block var8 = var1.func_147439_a(var2, var3, var4);
      if (var8 == Blocks.field_150331_J || var8 == Blocks.field_150320_F) {
         var6 = var1.func_72805_g(var2, var3, var4);
         if (BlockPistonBase.func_150075_c(var6)) {
            var8.func_149697_b(var1, var2, var3, var4, var6, 0);
            var1.func_147468_f(var2, var3, var4);
         }
      }
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      int var3 = func_150085_b(var2);
      if (var1 == var3) {
         if (this.field_150088_a != null) {
            return this.field_150088_a;
         } else {
            return (var2 & 8) != 0 ? BlockPistonBase.func_150074_e("piston_top_sticky") : BlockPistonBase.func_150074_e("piston_top_normal");
         }
      } else {
         return var3 < 6 && var1 == Facing.field_71588_a[var3]
            ? BlockPistonBase.func_150074_e("piston_top_normal")
            : BlockPistonBase.func_150074_e("piston_side");
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
   }

   @Override
   public int func_149645_b() {
      return 17;
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
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return false;
   }

   @Override
   public boolean func_149707_d(World var1, int var2, int var3, int var4, int var5) {
      return false;
   }

   @Override
   public int func_149745_a(Random var1) {
      return 0;
   }

   @Override
   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
      int var8 = var1.func_72805_g(var2, var3, var4);
      float var9 = 0.25F;
      float var10 = 0.375F;
      float var11 = 0.625F;
      float var12 = 0.25F;
      float var13 = 0.75F;
      switch(func_150085_b(var8)) {
         case 0:
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
            this.func_149676_a(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
            break;
         case 1:
            this.func_149676_a(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
            this.func_149676_a(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
            break;
         case 2:
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
            this.func_149676_a(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
            break;
         case 3:
            this.func_149676_a(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
            this.func_149676_a(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
            break;
         case 4:
            this.func_149676_a(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
            this.func_149676_a(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
            break;
         case 5:
            this.func_149676_a(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
            this.func_149676_a(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      }

      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      float var6 = 0.25F;
      switch(func_150085_b(var5)) {
         case 0:
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
            break;
         case 1:
            this.func_149676_a(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
            break;
         case 2:
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
            break;
         case 3:
            this.func_149676_a(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
            break;
         case 4:
            this.func_149676_a(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
            break;
         case 5:
            this.func_149676_a(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      int var6 = func_150085_b(var1.func_72805_g(var2, var3, var4));
      Block var7 = var1.func_147439_a(var2 - Facing.field_71586_b[var6], var3 - Facing.field_71587_c[var6], var4 - Facing.field_71585_d[var6]);
      if (var7 != Blocks.field_150331_J && var7 != Blocks.field_150320_F) {
         var1.func_147468_f(var2, var3, var4);
      } else {
         var7.func_149695_a(var1, var2 - Facing.field_71586_b[var6], var3 - Facing.field_71587_c[var6], var4 - Facing.field_71585_d[var6], var5);
      }
   }

   public static int func_150085_b(int var0) {
      return MathHelper.func_76125_a(var0 & 7, 0, Facing.field_71586_b.length - 1);
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      return (var5 & 8) != 0 ? Item.func_150898_a(Blocks.field_150320_F) : Item.func_150898_a(Blocks.field_150331_J);
   }
}
