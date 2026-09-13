package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistryDefaulted;
import net.minecraft.world.World;

public class BlockDispenser extends BlockContainer {
   public static final IRegistry field_149943_a = new RegistryDefaulted(new BehaviorDefaultDispenseItem());
   protected Random field_149942_b = new Random();
   protected IIcon field_149944_M;
   protected IIcon field_149945_N;
   protected IIcon field_149946_O;

   protected BlockDispenser() {
      super(Material.field_151576_e);
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   @Override
   public int func_149738_a(World var1) {
      return 4;
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      super.func_149726_b(var1, var2, var3, var4);
      this.func_149938_m(var1, var2, var3, var4);
   }

   private void func_149938_m(World var1, int var2, int var3, int var4) {
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
      int var3 = var2 & 7;
      if (var1 == var3) {
         return var3 != 1 && var3 != 0 ? this.field_149945_N : this.field_149946_O;
      } else if (var3 == 1 || var3 == 0) {
         return this.field_149944_M;
      } else {
         return var1 != 1 && var1 != 0 ? this.field_149761_L : this.field_149944_M;
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a("furnace_side");
      this.field_149944_M = var1.func_94245_a("furnace_top");
      this.field_149945_N = var1.func_94245_a(this.func_149641_N() + "_front_horizontal");
      this.field_149946_O = var1.func_94245_a(this.func_149641_N() + "_front_vertical");
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntityDispenser var10 = (TileEntityDispenser)var1.func_147438_o(var2, var3, var4);
         if (var10 != null) {
            var5.func_146102_a(var10);
         }

         return true;
      }
   }

   protected void func_149941_e(World var1, int var2, int var3, int var4) {
      BlockSourceImpl var5 = new BlockSourceImpl(var1, var2, var3, var4);
      TileEntityDispenser var6 = (TileEntityDispenser)var5.func_150835_j();
      if (var6 != null) {
         int var7 = var6.func_146017_i();
         if (var7 < 0) {
            var1.func_72926_e(1001, var2, var3, var4, 0);
         } else {
            ItemStack var8 = var6.func_70301_a(var7);
            IBehaviorDispenseItem var9 = this.func_149940_a(var8);
            if (var9 != IBehaviorDispenseItem.field_82483_a) {
               ItemStack var10 = var9.func_82482_a(var5, var8);
               var6.func_70299_a(var7, var10.field_77994_a == 0 ? null : var10);
            }
         }
      }
   }

   protected IBehaviorDispenseItem func_149940_a(ItemStack var1) {
      return (IBehaviorDispenseItem)field_149943_a.func_82594_a(var1.func_77973_b());
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      boolean var6 = var1.func_72864_z(var2, var3, var4) || var1.func_72864_z(var2, var3 + 1, var4);
      int var7 = var1.func_72805_g(var2, var3, var4);
      boolean var8 = (var7 & 8) != 0;
      if (var6 && !var8) {
         var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
         var1.func_72921_c(var2, var3, var4, var7 | 8, 4);
      } else if (!var6 && var8) {
         var1.func_72921_c(var2, var3, var4, var7 & -9, 4);
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!var1.field_72995_K) {
         this.func_149941_e(var1, var2, var3, var4);
      }
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityDispenser();
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = BlockPistonBase.func_150071_a(var1, var2, var3, var4, var5);
      var1.func_72921_c(var2, var3, var4, var7, 2);
      if (var6.func_82837_s()) {
         ((TileEntityDispenser)var1.func_147438_o(var2, var3, var4)).func_146018_a(var6.func_82833_r());
      }
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      TileEntityDispenser var7 = (TileEntityDispenser)var1.func_147438_o(var2, var3, var4);
      if (var7 != null) {
         for(int var8 = 0; var8 < var7.func_70302_i_(); ++var8) {
            ItemStack var9 = var7.func_70301_a(var8);
            if (var9 != null) {
               float var10 = this.field_149942_b.nextFloat() * 0.8F + 0.1F;
               float var11 = this.field_149942_b.nextFloat() * 0.8F + 0.1F;
               float var12 = this.field_149942_b.nextFloat() * 0.8F + 0.1F;

               while(var9.field_77994_a > 0) {
                  int var13 = this.field_149942_b.nextInt(21) + 10;
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
                  var14.field_70159_w = (double)((float)this.field_149942_b.nextGaussian() * var15);
                  var14.field_70181_x = (double)((float)this.field_149942_b.nextGaussian() * var15 + 0.2F);
                  var14.field_70179_y = (double)((float)this.field_149942_b.nextGaussian() * var15);
                  var1.func_72838_d(var14);
               }
            }
         }

         var1.func_147453_f(var2, var3, var4, var5);
      }

      super.func_149749_a(var1, var2, var3, var4, var5, var6);
   }

   public static IPosition func_149939_a(IBlockSource var0) {
      EnumFacing var1 = func_149937_b(var0.func_82620_h());
      double var2 = var0.func_82615_a() + 0.7 * (double)var1.func_82601_c();
      double var4 = var0.func_82617_b() + 0.7 * (double)var1.func_96559_d();
      double var6 = var0.func_82616_c() + 0.7 * (double)var1.func_82599_e();
      return new PositionImpl(var2, var4, var6);
   }

   public static EnumFacing func_149937_b(int var0) {
      return EnumFacing.func_82600_a(var0 & 7);
   }

   @Override
   public boolean func_149740_M() {
      return true;
   }

   @Override
   public int func_149736_g(World var1, int var2, int var3, int var4, int var5) {
      return Container.func_94526_b((IInventory)var1.func_147438_o(var2, var3, var4));
   }
}
