package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPumpkin extends BlockDirectional {
   private boolean field_149985_a;
   private IIcon field_149984_b;
   private IIcon field_149986_M;

   protected BlockPumpkin(boolean var1) {
      super(Material.field_151572_C);
      this.func_149675_a(true);
      this.field_149985_a = var1;
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 1) {
         return this.field_149984_b;
      } else if (var1 == 0) {
         return this.field_149984_b;
      } else if (var2 == 2 && var1 == 2) {
         return this.field_149986_M;
      } else if (var2 == 3 && var1 == 5) {
         return this.field_149986_M;
      } else if (var2 == 0 && var1 == 3) {
         return this.field_149986_M;
      } else {
         return var2 == 1 && var1 == 4 ? this.field_149986_M : this.field_149761_L;
      }
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      super.func_149726_b(var1, var2, var3, var4);
      if (var1.func_147439_a(var2, var3 - 1, var4) == Blocks.field_150433_aE && var1.func_147439_a(var2, var3 - 2, var4) == Blocks.field_150433_aE) {
         if (!var1.field_72995_K) {
            var1.func_147465_d(var2, var3, var4, func_149729_e(0), 0, 2);
            var1.func_147465_d(var2, var3 - 1, var4, func_149729_e(0), 0, 2);
            var1.func_147465_d(var2, var3 - 2, var4, func_149729_e(0), 0, 2);
            EntitySnowman var9 = new EntitySnowman(var1);
            var9.func_70012_b((double)var2 + 0.5, (double)var3 - 1.95, (double)var4 + 0.5, 0.0F, 0.0F);
            var1.func_72838_d(var9);
            var1.func_147444_c(var2, var3, var4, func_149729_e(0));
            var1.func_147444_c(var2, var3 - 1, var4, func_149729_e(0));
            var1.func_147444_c(var2, var3 - 2, var4, func_149729_e(0));
         }

         for(int var10 = 0; var10 < 120; ++var10) {
            var1.func_72869_a(
               "snowshovel",
               (double)var2 + var1.field_73012_v.nextDouble(),
               (double)(var3 - 2) + var1.field_73012_v.nextDouble() * 2.5,
               (double)var4 + var1.field_73012_v.nextDouble(),
               0.0,
               0.0,
               0.0
            );
         }
      } else if (var1.func_147439_a(var2, var3 - 1, var4) == Blocks.field_150339_S && var1.func_147439_a(var2, var3 - 2, var4) == Blocks.field_150339_S) {
         boolean var5 = var1.func_147439_a(var2 - 1, var3 - 1, var4) == Blocks.field_150339_S
            && var1.func_147439_a(var2 + 1, var3 - 1, var4) == Blocks.field_150339_S;
         boolean var6 = var1.func_147439_a(var2, var3 - 1, var4 - 1) == Blocks.field_150339_S
            && var1.func_147439_a(var2, var3 - 1, var4 + 1) == Blocks.field_150339_S;
         if (var5 || var6) {
            var1.func_147465_d(var2, var3, var4, func_149729_e(0), 0, 2);
            var1.func_147465_d(var2, var3 - 1, var4, func_149729_e(0), 0, 2);
            var1.func_147465_d(var2, var3 - 2, var4, func_149729_e(0), 0, 2);
            if (var5) {
               var1.func_147465_d(var2 - 1, var3 - 1, var4, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2 + 1, var3 - 1, var4, func_149729_e(0), 0, 2);
            } else {
               var1.func_147465_d(var2, var3 - 1, var4 - 1, func_149729_e(0), 0, 2);
               var1.func_147465_d(var2, var3 - 1, var4 + 1, func_149729_e(0), 0, 2);
            }

            EntityIronGolem var7 = new EntityIronGolem(var1);
            var7.func_70849_f(true);
            var7.func_70012_b((double)var2 + 0.5, (double)var3 - 1.95, (double)var4 + 0.5, 0.0F, 0.0F);
            var1.func_72838_d(var7);

            for(int var8 = 0; var8 < 120; ++var8) {
               var1.func_72869_a(
                  "snowballpoof",
                  (double)var2 + var1.field_73012_v.nextDouble(),
                  (double)(var3 - 2) + var1.field_73012_v.nextDouble() * 3.9,
                  (double)var4 + var1.field_73012_v.nextDouble(),
                  0.0,
                  0.0,
                  0.0
               );
            }

            var1.func_147444_c(var2, var3, var4, func_149729_e(0));
            var1.func_147444_c(var2, var3 - 1, var4, func_149729_e(0));
            var1.func_147444_c(var2, var3 - 2, var4, func_149729_e(0));
            if (var5) {
               var1.func_147444_c(var2 - 1, var3 - 1, var4, func_149729_e(0));
               var1.func_147444_c(var2 + 1, var3 - 1, var4, func_149729_e(0));
            } else {
               var1.func_147444_c(var2, var3 - 1, var4 - 1, func_149729_e(0));
               var1.func_147444_c(var2, var3 - 1, var4 + 1, func_149729_e(0));
            }
         }
      }
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return var1.func_147439_a(var2, var3, var4).field_149764_J.func_76222_j() && World.func_147466_a(var1, var2, var3 - 1, var4);
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 2.5) & 3;
      var1.func_72921_c(var2, var3, var4, var7, 2);
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149986_M = var1.func_94245_a(this.func_149641_N() + "_face_" + (this.field_149985_a ? "on" : "off"));
      this.field_149984_b = var1.func_94245_a(this.func_149641_N() + "_top");
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_side");
   }
}
