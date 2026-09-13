package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPane extends Block {
   private final String field_150100_a;
   private final boolean field_150099_b;
   private final String field_150101_M;
   private IIcon field_150102_N;

   protected BlockPane(String var1, String var2, Material var3, boolean var4) {
      super(var3);
      this.field_150100_a = var2;
      this.field_150099_b = var4;
      this.field_150101_M = var1;
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return !this.field_150099_b ? null : super.func_149650_a(var1, var2, var3);
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
   public int func_149645_b() {
      return this.field_149764_J == Material.field_151592_s ? 41 : 18;
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return var1.func_147439_a(var2, var3, var4) == this ? false : super.func_149646_a(var1, var2, var3, var4, var5);
   }

   @Override
   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
      boolean var8 = this.func_150098_a(var1.func_147439_a(var2, var3, var4 - 1));
      boolean var9 = this.func_150098_a(var1.func_147439_a(var2, var3, var4 + 1));
      boolean var10 = this.func_150098_a(var1.func_147439_a(var2 - 1, var3, var4));
      boolean var11 = this.func_150098_a(var1.func_147439_a(var2 + 1, var3, var4));
      if ((!var10 || !var11) && (var10 || var11 || var8 || var9)) {
         if (var10 && !var11) {
            this.func_149676_a(0.0F, 0.0F, 0.4375F, 0.5F, 1.0F, 0.5625F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
         } else if (!var10 && var11) {
            this.func_149676_a(0.5F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
         }
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
         super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      }

      if ((!var8 || !var9) && (var10 || var11 || var8 || var9)) {
         if (var8 && !var9) {
            this.func_149676_a(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
         } else if (!var8 && var9) {
            this.func_149676_a(0.4375F, 0.0F, 0.5F, 0.5625F, 1.0F, 1.0F);
            super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
         }
      } else {
         this.func_149676_a(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 1.0F);
         super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   @Override
   public void func_149683_g() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      float var5 = 0.4375F;
      float var6 = 0.5625F;
      float var7 = 0.4375F;
      float var8 = 0.5625F;
      boolean var9 = this.func_150098_a(var1.func_147439_a(var2, var3, var4 - 1));
      boolean var10 = this.func_150098_a(var1.func_147439_a(var2, var3, var4 + 1));
      boolean var11 = this.func_150098_a(var1.func_147439_a(var2 - 1, var3, var4));
      boolean var12 = this.func_150098_a(var1.func_147439_a(var2 + 1, var3, var4));
      if ((!var11 || !var12) && (var11 || var12 || var9 || var10)) {
         if (var11 && !var12) {
            var5 = 0.0F;
         } else if (!var11 && var12) {
            var6 = 1.0F;
         }
      } else {
         var5 = 0.0F;
         var6 = 1.0F;
      }

      if ((!var9 || !var10) && (var11 || var12 || var9 || var10)) {
         if (var9 && !var10) {
            var7 = 0.0F;
         } else if (!var9 && var10) {
            var8 = 1.0F;
         }
      } else {
         var7 = 0.0F;
         var8 = 1.0F;
      }

      this.func_149676_a(var5, 0.0F, var7, var6, 1.0F, var8);
   }

   public IIcon func_150097_e() {
      return this.field_150102_N;
   }

   public final boolean func_150098_a(Block var1) {
      return var1.func_149730_j()
         || var1 == this
         || var1 == Blocks.field_150359_w
         || var1 == Blocks.field_150399_cn
         || var1 == Blocks.field_150397_co
         || var1 instanceof BlockPane;
   }

   @Override
   protected boolean func_149700_E() {
      return true;
   }

   @Override
   protected ItemStack func_149644_j(int var1) {
      return new ItemStack(Item.func_150898_a(this), 1, var1);
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.field_150101_M);
      this.field_150102_N = var1.func_94245_a(this.field_150100_a);
   }
}
