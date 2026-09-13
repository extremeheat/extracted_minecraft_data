package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockTNT extends Block {
   private IIcon field_150116_a;
   private IIcon field_150115_b;

   public BlockTNT() {
      super(Material.field_151590_u);
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 0) {
         return this.field_150115_b;
      } else {
         return var1 == 1 ? this.field_150116_a : this.field_149761_L;
      }
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      super.func_149726_b(var1, var2, var3, var4);
      if (var1.func_72864_z(var2, var3, var4)) {
         this.func_149664_b(var1, var2, var3, var4, 1);
         var1.func_147468_f(var2, var3, var4);
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (var1.func_72864_z(var2, var3, var4)) {
         this.func_149664_b(var1, var2, var3, var4, 1);
         var1.func_147468_f(var2, var3, var4);
      }
   }

   @Override
   public int func_149745_a(Random var1) {
      return 1;
   }

   @Override
   public void func_149723_a(World var1, int var2, int var3, int var4, Explosion var5) {
      if (!var1.field_72995_K) {
         EntityTNTPrimed var6 = new EntityTNTPrimed(
            var1, (double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), var5.func_94613_c()
         );
         var6.field_70516_a = var1.field_73012_v.nextInt(var6.field_70516_a / 4) + var6.field_70516_a / 8;
         var1.func_72838_d(var6);
      }
   }

   @Override
   public void func_149664_b(World var1, int var2, int var3, int var4, int var5) {
      this.func_150114_a(var1, var2, var3, var4, var5, null);
   }

   public void func_150114_a(World var1, int var2, int var3, int var4, int var5, EntityLivingBase var6) {
      if (!var1.field_72995_K) {
         if ((var5 & 1) == 1) {
            EntityTNTPrimed var7 = new EntityTNTPrimed(var1, (double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), var6);
            var1.func_72838_d(var7);
            var1.func_72956_a(var7, "game.tnt.primed", 1.0F, 1.0F);
         }
      }
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var5.func_71045_bC() != null && var5.func_71045_bC().func_77973_b() == Items.field_151033_d) {
         this.func_150114_a(var1, var2, var3, var4, 1, var5);
         var1.func_147468_f(var2, var3, var4);
         var5.func_71045_bC().func_77972_a(1, var5);
         return true;
      } else {
         return super.func_149727_a(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   @Override
   public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
      if (var5 instanceof EntityArrow && !var1.field_72995_K) {
         EntityArrow var6 = (EntityArrow)var5;
         if (var6.func_70027_ad()) {
            this.func_150114_a(var1, var2, var3, var4, 1, var6.field_70250_c instanceof EntityLivingBase ? (EntityLivingBase)var6.field_70250_c : null);
            var1.func_147468_f(var2, var3, var4);
         }
      }
   }

   @Override
   public boolean func_149659_a(Explosion var1) {
      return false;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_side");
      this.field_150116_a = var1.func_94245_a(this.func_149641_N() + "_top");
      this.field_150115_b = var1.func_94245_a(this.func_149641_N() + "_bottom");
   }
}
