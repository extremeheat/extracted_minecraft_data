package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockChest extends BlockContainer {
   private final Random field_149955_b = new Random();
   public final int field_149956_a;

   protected BlockChest(int var1) {
      super(Material.field_151575_d);
      this.field_149956_a = var1;
      this.func_149647_a(CreativeTabs.field_78031_c);
      this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
      return 22;
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      if (var1.func_147439_a(var2, var3, var4 - 1) == this) {
         this.func_149676_a(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
      } else if (var1.func_147439_a(var2, var3, var4 + 1) == this) {
         this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
      } else if (var1.func_147439_a(var2 - 1, var3, var4) == this) {
         this.func_149676_a(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
      } else if (var1.func_147439_a(var2 + 1, var3, var4) == this) {
         this.func_149676_a(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
      } else {
         this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
      }
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      super.func_149726_b(var1, var2, var3, var4);
      this.func_149954_e(var1, var2, var3, var4);
      Block var5 = var1.func_147439_a(var2, var3, var4 - 1);
      Block var6 = var1.func_147439_a(var2, var3, var4 + 1);
      Block var7 = var1.func_147439_a(var2 - 1, var3, var4);
      Block var8 = var1.func_147439_a(var2 + 1, var3, var4);
      if (var5 == this) {
         this.func_149954_e(var1, var2, var3, var4 - 1);
      }

      if (var6 == this) {
         this.func_149954_e(var1, var2, var3, var4 + 1);
      }

      if (var7 == this) {
         this.func_149954_e(var1, var2 - 1, var3, var4);
      }

      if (var8 == this) {
         this.func_149954_e(var1, var2 + 1, var3, var4);
      }
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      Block var7 = var1.func_147439_a(var2, var3, var4 - 1);
      Block var8 = var1.func_147439_a(var2, var3, var4 + 1);
      Block var9 = var1.func_147439_a(var2 - 1, var3, var4);
      Block var10 = var1.func_147439_a(var2 + 1, var3, var4);
      byte var11 = 0;
      int var12 = MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 3;
      if (var12 == 0) {
         var11 = 2;
      }

      if (var12 == 1) {
         var11 = 5;
      }

      if (var12 == 2) {
         var11 = 3;
      }

      if (var12 == 3) {
         var11 = 4;
      }

      if (var7 != this && var8 != this && var9 != this && var10 != this) {
         var1.func_72921_c(var2, var3, var4, var11, 3);
      } else {
         if ((var7 == this || var8 == this) && (var11 == 4 || var11 == 5)) {
            if (var7 == this) {
               var1.func_72921_c(var2, var3, var4 - 1, var11, 3);
            } else {
               var1.func_72921_c(var2, var3, var4 + 1, var11, 3);
            }

            var1.func_72921_c(var2, var3, var4, var11, 3);
         }

         if ((var9 == this || var10 == this) && (var11 == 2 || var11 == 3)) {
            if (var9 == this) {
               var1.func_72921_c(var2 - 1, var3, var4, var11, 3);
            } else {
               var1.func_72921_c(var2 + 1, var3, var4, var11, 3);
            }

            var1.func_72921_c(var2, var3, var4, var11, 3);
         }
      }

      if (var6.func_82837_s()) {
         ((TileEntityChest)var1.func_147438_o(var2, var3, var4)).func_145976_a(var6.func_82833_r());
      }
   }

   public void func_149954_e(World var1, int var2, int var3, int var4) {
      if (!var1.field_72995_K) {
         Block var5 = var1.func_147439_a(var2, var3, var4 - 1);
         Block var6 = var1.func_147439_a(var2, var3, var4 + 1);
         Block var7 = var1.func_147439_a(var2 - 1, var3, var4);
         Block var8 = var1.func_147439_a(var2 + 1, var3, var4);
         byte var9 = 4;
         if (var5 != this && var6 != this) {
            if (var7 != this && var8 != this) {
               var9 = 3;
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
            } else {
               int var16 = var7 == this ? var2 - 1 : var2 + 1;
               Block var17 = var1.func_147439_a(var16, var3, var4 - 1);
               int var18 = var7 == this ? var2 - 1 : var2 + 1;
               Block var19 = var1.func_147439_a(var18, var3, var4 + 1);
               var9 = 3;
               int var21 = -1;
               if (var7 == this) {
                  var21 = var1.func_72805_g(var2 - 1, var3, var4);
               } else {
                  var21 = var1.func_72805_g(var2 + 1, var3, var4);
               }

               if (var21 == 2) {
                  var9 = 2;
               }

               if ((var5.func_149730_j() || var17.func_149730_j()) && !var6.func_149730_j() && !var19.func_149730_j()) {
                  var9 = 3;
               }

               if ((var6.func_149730_j() || var19.func_149730_j()) && !var5.func_149730_j() && !var17.func_149730_j()) {
                  var9 = 2;
               }
            }
         } else {
            int var10 = var5 == this ? var4 - 1 : var4 + 1;
            Block var11 = var1.func_147439_a(var2 - 1, var3, var10);
            int var12 = var5 == this ? var4 - 1 : var4 + 1;
            Block var13 = var1.func_147439_a(var2 + 1, var3, var12);
            var9 = 5;
            int var14 = -1;
            if (var5 == this) {
               var14 = var1.func_72805_g(var2, var3, var4 - 1);
            } else {
               var14 = var1.func_72805_g(var2, var3, var4 + 1);
            }

            if (var14 == 4) {
               var9 = 4;
            }

            if ((var7.func_149730_j() || var11.func_149730_j()) && !var8.func_149730_j() && !var13.func_149730_j()) {
               var9 = 5;
            }

            if ((var8.func_149730_j() || var13.func_149730_j()) && !var7.func_149730_j() && !var11.func_149730_j()) {
               var9 = 4;
            }
         }

         var1.func_72921_c(var2, var3, var4, var9, 3);
      }
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      int var5 = 0;
      if (var1.func_147439_a(var2 - 1, var3, var4) == this) {
         ++var5;
      }

      if (var1.func_147439_a(var2 + 1, var3, var4) == this) {
         ++var5;
      }

      if (var1.func_147439_a(var2, var3, var4 - 1) == this) {
         ++var5;
      }

      if (var1.func_147439_a(var2, var3, var4 + 1) == this) {
         ++var5;
      }

      if (var5 > 1) {
         return false;
      } else if (this.func_149952_n(var1, var2 - 1, var3, var4)) {
         return false;
      } else if (this.func_149952_n(var1, var2 + 1, var3, var4)) {
         return false;
      } else if (this.func_149952_n(var1, var2, var3, var4 - 1)) {
         return false;
      } else {
         return !this.func_149952_n(var1, var2, var3, var4 + 1);
      }
   }

   private boolean func_149952_n(World var1, int var2, int var3, int var4) {
      if (var1.func_147439_a(var2, var3, var4) != this) {
         return false;
      } else if (var1.func_147439_a(var2 - 1, var3, var4) == this) {
         return true;
      } else if (var1.func_147439_a(var2 + 1, var3, var4) == this) {
         return true;
      } else if (var1.func_147439_a(var2, var3, var4 - 1) == this) {
         return true;
      } else {
         return var1.func_147439_a(var2, var3, var4 + 1) == this;
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      super.func_149695_a(var1, var2, var3, var4, var5);
      TileEntityChest var6 = (TileEntityChest)var1.func_147438_o(var2, var3, var4);
      if (var6 != null) {
         var6.func_145836_u();
      }
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      TileEntityChest var7 = (TileEntityChest)var1.func_147438_o(var2, var3, var4);
      if (var7 != null) {
         for(int var8 = 0; var8 < var7.func_70302_i_(); ++var8) {
            ItemStack var9 = var7.func_70301_a(var8);
            if (var9 != null) {
               float var10 = this.field_149955_b.nextFloat() * 0.8F + 0.1F;
               float var11 = this.field_149955_b.nextFloat() * 0.8F + 0.1F;

               EntityItem var14;
               for(float var12 = this.field_149955_b.nextFloat() * 0.8F + 0.1F; var9.field_77994_a > 0; var1.func_72838_d(var14)) {
                  int var13 = this.field_149955_b.nextInt(21) + 10;
                  if (var13 > var9.field_77994_a) {
                     var13 = var9.field_77994_a;
                  }

                  var9.field_77994_a -= var13;
                  var14 = new EntityItem(
                     var1,
                     (double)((float)var2 + var10),
                     (double)((float)var3 + var11),
                     (double)((float)var4 + var12),
                     new ItemStack(var9.func_77973_b(), var13, var9.func_77960_j())
                  );
                  float var15 = 0.05F;
                  var14.field_70159_w = (double)((float)this.field_149955_b.nextGaussian() * var15);
                  var14.field_70181_x = (double)((float)this.field_149955_b.nextGaussian() * var15 + 0.2F);
                  var14.field_70179_y = (double)((float)this.field_149955_b.nextGaussian() * var15);
                  if (var9.func_77942_o()) {
                     var14.func_92059_d().func_77982_d((NBTTagCompound)var9.func_77978_p().func_74737_b());
                  }
               }
            }
         }

         var1.func_147453_f(var2, var3, var4, var5);
      }

      super.func_149749_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         IInventory var10 = this.func_149951_m(var1, var2, var3, var4);
         if (var10 != null) {
            var5.func_71007_a(var10);
         }

         return true;
      }
   }

   public IInventory func_149951_m(World var1, int var2, int var3, int var4) {
      Object var5 = (TileEntityChest)var1.func_147438_o(var2, var3, var4);
      if (var5 == null) {
         return null;
      } else if (var1.func_147439_a(var2, var3 + 1, var4).func_149721_r()) {
         return null;
      } else if (func_149953_o(var1, var2, var3, var4)) {
         return null;
      } else if (var1.func_147439_a(var2 - 1, var3, var4) != this
         || !var1.func_147439_a(var2 - 1, var3 + 1, var4).func_149721_r() && !func_149953_o(var1, var2 - 1, var3, var4)) {
         if (var1.func_147439_a(var2 + 1, var3, var4) != this
            || !var1.func_147439_a(var2 + 1, var3 + 1, var4).func_149721_r() && !func_149953_o(var1, var2 + 1, var3, var4)) {
            if (var1.func_147439_a(var2, var3, var4 - 1) != this
               || !var1.func_147439_a(var2, var3 + 1, var4 - 1).func_149721_r() && !func_149953_o(var1, var2, var3, var4 - 1)) {
               if (var1.func_147439_a(var2, var3, var4 + 1) != this
                  || !var1.func_147439_a(var2, var3 + 1, var4 + 1).func_149721_r() && !func_149953_o(var1, var2, var3, var4 + 1)) {
                  if (var1.func_147439_a(var2 - 1, var3, var4) == this) {
                     var5 = new InventoryLargeChest("container.chestDouble", (TileEntityChest)var1.func_147438_o(var2 - 1, var3, var4), (IInventory)var5);
                  }

                  if (var1.func_147439_a(var2 + 1, var3, var4) == this) {
                     var5 = new InventoryLargeChest("container.chestDouble", (IInventory)var5, (TileEntityChest)var1.func_147438_o(var2 + 1, var3, var4));
                  }

                  if (var1.func_147439_a(var2, var3, var4 - 1) == this) {
                     var5 = new InventoryLargeChest("container.chestDouble", (TileEntityChest)var1.func_147438_o(var2, var3, var4 - 1), (IInventory)var5);
                  }

                  if (var1.func_147439_a(var2, var3, var4 + 1) == this) {
                     var5 = new InventoryLargeChest("container.chestDouble", (IInventory)var5, (TileEntityChest)var1.func_147438_o(var2, var3, var4 + 1));
                  }

                  return (IInventory)var5;
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityChest();
   }

   @Override
   public boolean func_149744_f() {
      return this.field_149956_a == 1;
   }

   @Override
   public int func_149709_b(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      if (!this.func_149744_f()) {
         return 0;
      } else {
         int var6 = ((TileEntityChest)var1.func_147438_o(var2, var3, var4)).field_145987_o;
         return MathHelper.func_76125_a(var6, 0, 15);
      }
   }

   @Override
   public int func_149748_c(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return var5 == 1 ? this.func_149709_b(var1, var2, var3, var4, var5) : 0;
   }

   private static boolean func_149953_o(World var0, int var1, int var2, int var3) {
      for(Entity var5 : var0.func_72872_a(
         EntityOcelot.class,
         AxisAlignedBB.func_72330_a((double)var1, (double)(var2 + 1), (double)var3, (double)(var1 + 1), (double)(var2 + 2), (double)(var3 + 1))
      )) {
         EntityOcelot var6 = (EntityOcelot)var5;
         if (var6.func_70906_o()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean func_149740_M() {
      return true;
   }

   @Override
   public int func_149736_g(World var1, int var2, int var3, int var4, int var5) {
      return Container.func_94526_b(this.func_149951_m(var1, var2, var3, var4));
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a("planks_oak");
   }
}
