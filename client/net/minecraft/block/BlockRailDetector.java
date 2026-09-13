package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase {
   private IIcon[] field_150055_b;

   public BlockRailDetector() {
      super(true);
      this.func_149675_a(true);
   }

   @Override
   public int func_149738_a(World var1) {
      return 20;
   }

   @Override
   public boolean func_149744_f() {
      return true;
   }

   @Override
   public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
      if (!var1.field_72995_K) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         if ((var6 & 8) == 0) {
            this.func_150054_a(var1, var2, var3, var4, var6);
         }
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!var1.field_72995_K) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         if ((var6 & 8) != 0) {
            this.func_150054_a(var1, var2, var3, var4, var6);
         }
      }
   }

   @Override
   public int func_149709_b(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return (var1.func_72805_g(var2, var3, var4) & 8) != 0 ? 15 : 0;
   }

   @Override
   public int func_149748_c(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      if ((var1.func_72805_g(var2, var3, var4) & 8) == 0) {
         return 0;
      } else {
         return var5 == 1 ? 15 : 0;
      }
   }

   private void func_150054_a(World var1, int var2, int var3, int var4, int var5) {
      boolean var6 = (var5 & 8) != 0;
      boolean var7 = false;
      float var8 = 0.125F;
      List var9 = var1.func_72872_a(
         EntityMinecart.class,
         AxisAlignedBB.func_72330_a(
            (double)((float)var2 + var8),
            (double)var3,
            (double)((float)var4 + var8),
            (double)((float)(var2 + 1) - var8),
            (double)((float)(var3 + 1) - var8),
            (double)((float)(var4 + 1) - var8)
         )
      );
      if (!var9.isEmpty()) {
         var7 = true;
      }

      if (var7 && !var6) {
         var1.func_72921_c(var2, var3, var4, var5 | 8, 3);
         var1.func_147459_d(var2, var3, var4, this);
         var1.func_147459_d(var2, var3 - 1, var4, this);
         var1.func_147458_c(var2, var3, var4, var2, var3, var4);
      }

      if (!var7 && var6) {
         var1.func_72921_c(var2, var3, var4, var5 & 7, 3);
         var1.func_147459_d(var2, var3, var4, this);
         var1.func_147459_d(var2, var3 - 1, var4, this);
         var1.func_147458_c(var2, var3, var4, var2, var3, var4);
      }

      if (var7) {
         var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
      }

      var1.func_147453_f(var2, var3, var4, this);
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      super.func_149726_b(var1, var2, var3, var4);
      this.func_150054_a(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4));
   }

   @Override
   public boolean func_149740_M() {
      return true;
   }

   @Override
   public int func_149736_g(World var1, int var2, int var3, int var4, int var5) {
      if ((var1.func_72805_g(var2, var3, var4) & 8) > 0) {
         float var6 = 0.125F;
         List var7 = var1.func_72872_a(
            EntityMinecartCommandBlock.class,
            AxisAlignedBB.func_72330_a(
               (double)((float)var2 + var6),
               (double)var3,
               (double)((float)var4 + var6),
               (double)((float)(var2 + 1) - var6),
               (double)((float)(var3 + 1) - var6),
               (double)((float)(var4 + 1) - var6)
            )
         );
         if (var7.size() > 0) {
            return ((EntityMinecartCommandBlock)var7.get(0)).func_145822_e().func_145760_g();
         }

         List var8 = var1.func_82733_a(
            EntityMinecart.class,
            AxisAlignedBB.func_72330_a(
               (double)((float)var2 + var6),
               (double)var3,
               (double)((float)var4 + var6),
               (double)((float)(var2 + 1) - var6),
               (double)((float)(var3 + 1) - var6),
               (double)((float)(var4 + 1) - var6)
            ),
            IEntitySelector.field_96566_b
         );
         if (var8.size() > 0) {
            return Container.func_94526_b((IInventory)var8.get(0));
         }
      }

      return 0;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_150055_b = new IIcon[2];
      this.field_150055_b[0] = var1.func_94245_a(this.func_149641_N());
      this.field_150055_b[1] = var1.func_94245_a(this.func_149641_N() + "_powered");
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return (var2 & 8) != 0 ? this.field_150055_b[1] : this.field_150055_b[0];
   }
}
