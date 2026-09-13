package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockEndPortalFrame extends Block {
   private IIcon field_150023_a;
   private IIcon field_150022_b;

   public BlockEndPortalFrame() {
      super(Material.field_151576_e);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 1) {
         return this.field_150023_a;
      } else {
         return var1 == 0 ? Blocks.field_150377_bs.func_149733_h(var1) : this.field_149761_L;
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_side");
      this.field_150023_a = var1.func_94245_a(this.func_149641_N() + "_top");
      this.field_150022_b = var1.func_94245_a(this.func_149641_N() + "_eye");
   }

   public IIcon func_150021_e() {
      return this.field_150022_b;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 26;
   }

   @Override
   public void func_149683_g() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
   }

   @Override
   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      int var8 = var1.func_72805_g(var2, var3, var4);
      if (func_150020_b(var8)) {
         this.func_149676_a(0.3125F, 0.8125F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
         super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      }

      this.func_149683_g();
   }

   public static boolean func_150020_b(int var0) {
      return (var0 & 4) != 0;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return null;
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = ((MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 3) + 2) % 4;
      var1.func_72921_c(var2, var3, var4, var7, 2);
   }

   @Override
   public boolean func_149740_M() {
      return true;
   }

   @Override
   public int func_149736_g(World var1, int var2, int var3, int var4, int var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      return func_150020_b(var6) ? 15 : 0;
   }
}
