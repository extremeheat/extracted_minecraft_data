package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockFurnace extends BlockContainer {
   private final Random field_149933_a = new Random();
   private final boolean field_149932_b;
   private static boolean field_149934_M;
   private IIcon field_149935_N;
   private IIcon field_149936_O;

   protected BlockFurnace(boolean var1) {
      super(Material.field_151576_e);
      this.field_149932_b = var1;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150460_al);
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      super.func_149726_b(var1, var2, var3, var4);
      this.func_149930_e(var1, var2, var3, var4);
   }

   private void func_149930_e(World var1, int var2, int var3, int var4) {
      if (!var1.field_72995_K) {
         Block var5 = var1.func_147439_a(var2, var3, var4 - 1);
         Block var6 = var1.func_147439_a(var2, var3, var4 + 1);
         Block var7 = var1.func_147439_a(var2 - 1, var3, var4);
         Block var8 = var1.func_147439_a(var2 + 1, var3, var4);
         byte var9 = 3;
         if (var5.func_149730_j() && !var6.func_149730_j()) {
            var9 = 3;
         }

         if (var6.func_149730_j() && !var5.func_149730_j()) {
            var9 = 2;
         }

         if (var7.func_149730_j() && !var8.func_149730_j()) {
            var9 = 5;
         }

         if (var8.func_149730_j() && !var7.func_149730_j()) {
            var9 = 4;
         }

         var1.func_72921_c(var2, var3, var4, var9, 2);
      }
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 1) {
         return this.field_149935_N;
      } else if (var1 == 0) {
         return this.field_149935_N;
      } else {
         return var1 != var2 ? this.field_149761_L : this.field_149936_O;
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a("furnace_side");
      this.field_149936_O = var1.func_94245_a(this.field_149932_b ? "furnace_front_on" : "furnace_front_off");
      this.field_149935_N = var1.func_94245_a("furnace_top");
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      if (this.field_149932_b) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         float var7 = (float)var2 + 0.5F;
         float var8 = (float)var3 + 0.0F + var5.nextFloat() * 6.0F / 16.0F;
         float var9 = (float)var4 + 0.5F;
         float var10 = 0.52F;
         float var11 = var5.nextFloat() * 0.6F - 0.3F;
         if (var6 == 4) {
            var1.func_72869_a("smoke", (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
            var1.func_72869_a("flame", (double)(var7 - var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
         } else if (var6 == 5) {
            var1.func_72869_a("smoke", (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
            var1.func_72869_a("flame", (double)(var7 + var10), (double)var8, (double)(var9 + var11), 0.0, 0.0, 0.0);
         } else if (var6 == 2) {
            var1.func_72869_a("smoke", (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0, 0.0, 0.0);
            var1.func_72869_a("flame", (double)(var7 + var11), (double)var8, (double)(var9 - var10), 0.0, 0.0, 0.0);
         } else if (var6 == 3) {
            var1.func_72869_a("smoke", (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0, 0.0, 0.0);
            var1.func_72869_a("flame", (double)(var7 + var11), (double)var8, (double)(var9 + var10), 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntityFurnace var10 = (TileEntityFurnace)var1.func_147438_o(var2, var3, var4);
         if (var10 != null) {
            var5.func_146101_a(var10);
         }

         return true;
      }
   }

   public static void func_149931_a(boolean var0, World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      TileEntity var6 = var1.func_147438_o(var2, var3, var4);
      field_149934_M = true;
      if (var0) {
         var1.func_147449_b(var2, var3, var4, Blocks.field_150470_am);
      } else {
         var1.func_147449_b(var2, var3, var4, Blocks.field_150460_al);
      }

      field_149934_M = false;
      var1.func_72921_c(var2, var3, var4, var5, 2);
      if (var6 != null) {
         var6.func_145829_t();
         var1.func_147455_a(var2, var3, var4, var6);
      }
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityFurnace();
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 3;
      if (var7 == 0) {
         var1.func_72921_c(var2, var3, var4, 2, 2);
      }

      if (var7 == 1) {
         var1.func_72921_c(var2, var3, var4, 5, 2);
      }

      if (var7 == 2) {
         var1.func_72921_c(var2, var3, var4, 3, 2);
      }

      if (var7 == 3) {
         var1.func_72921_c(var2, var3, var4, 4, 2);
      }

      if (var6.func_82837_s()) {
         ((TileEntityFurnace)var1.func_147438_o(var2, var3, var4)).func_145951_a(var6.func_82833_r());
      }
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      if (!field_149934_M) {
         TileEntityFurnace var7 = (TileEntityFurnace)var1.func_147438_o(var2, var3, var4);
         if (var7 != null) {
            for(int var8 = 0; var8 < var7.func_70302_i_(); ++var8) {
               ItemStack var9 = var7.func_70301_a(var8);
               if (var9 != null) {
                  float var10 = this.field_149933_a.nextFloat() * 0.8F + 0.1F;
                  float var11 = this.field_149933_a.nextFloat() * 0.8F + 0.1F;
                  float var12 = this.field_149933_a.nextFloat() * 0.8F + 0.1F;

                  while(var9.field_77994_a > 0) {
                     int var13 = this.field_149933_a.nextInt(21) + 10;
                     if (var13 > var9.field_77994_a) {
                        var13 = var9.field_77994_a;
                     }

                     var9.field_77994_a -= var13;
                     EntityItem var14 = new EntityItem(
                        var1,
                        (double)((float)var2 + var10),
                        (double)((float)var3 + var11),
                        (double)((float)var4 + var12),
                        new ItemStack(var9.func_77973_b(), var13, var9.func_77960_j())
                     );
                     if (var9.func_77942_o()) {
                        var14.func_92059_d().func_77982_d((NBTTagCompound)var9.func_77978_p().func_74737_b());
                     }

                     float var15 = 0.05F;
                     var14.field_70159_w = (double)((float)this.field_149933_a.nextGaussian() * var15);
                     var14.field_70181_x = (double)((float)this.field_149933_a.nextGaussian() * var15 + 0.2F);
                     var14.field_70179_y = (double)((float)this.field_149933_a.nextGaussian() * var15);
                     var1.func_72838_d(var14);
                  }
               }
            }

            var1.func_147453_f(var2, var3, var4, var5);
         }
      }

      super.func_149749_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean func_149740_M() {
      return true;
   }

   @Override
   public int func_149736_g(World var1, int var2, int var3, int var4, int var5) {
      return Container.func_94526_b((IInventory)var1.func_147438_o(var2, var3, var4));
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Item.func_150898_a(Blocks.field_150460_al);
   }
}
