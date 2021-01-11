package net.minecraft.block;

import java.util.Iterator;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class BlockChest extends BlockContainer {
   public static final PropertyDirection field_176459_a;
   public final int field_149956_a;

   protected BlockChest(int var1) {
      super(Material.field_151575_d);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176459_a, EnumFacing.NORTH));
      this.field_149956_a = var1;
      this.func_149647_a(CreativeTabs.field_78031_c);
      this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public int func_149645_b() {
      return 2;
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      if (var1.func_180495_p(var2.func_177978_c()).func_177230_c() == this) {
         this.func_149676_a(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
      } else if (var1.func_180495_p(var2.func_177968_d()).func_177230_c() == this) {
         this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
      } else if (var1.func_180495_p(var2.func_177976_e()).func_177230_c() == this) {
         this.func_149676_a(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
      } else if (var1.func_180495_p(var2.func_177974_f()).func_177230_c() == this) {
         this.func_149676_a(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
      } else {
         this.func_149676_a(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
      }

   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      this.func_176455_e(var1, var2, var3);
      Iterator var4 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         EnumFacing var5 = (EnumFacing)var4.next();
         BlockPos var6 = var2.func_177972_a(var5);
         IBlockState var7 = var1.func_180495_p(var6);
         if (var7.func_177230_c() == this) {
            this.func_176455_e(var1, var6, var7);
         }
      }

   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P().func_177226_a(field_176459_a, var8.func_174811_aO());
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      EnumFacing var6 = EnumFacing.func_176731_b(MathHelper.func_76128_c((double)(var4.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3).func_176734_d();
      var3 = var3.func_177226_a(field_176459_a, var6);
      BlockPos var7 = var2.func_177978_c();
      BlockPos var8 = var2.func_177968_d();
      BlockPos var9 = var2.func_177976_e();
      BlockPos var10 = var2.func_177974_f();
      boolean var11 = this == var1.func_180495_p(var7).func_177230_c();
      boolean var12 = this == var1.func_180495_p(var8).func_177230_c();
      boolean var13 = this == var1.func_180495_p(var9).func_177230_c();
      boolean var14 = this == var1.func_180495_p(var10).func_177230_c();
      if (!var11 && !var12 && !var13 && !var14) {
         var1.func_180501_a(var2, var3, 3);
      } else if (var6.func_176740_k() == EnumFacing.Axis.X && (var11 || var12)) {
         if (var11) {
            var1.func_180501_a(var7, var3, 3);
         } else {
            var1.func_180501_a(var8, var3, 3);
         }

         var1.func_180501_a(var2, var3, 3);
      } else if (var6.func_176740_k() == EnumFacing.Axis.Z && (var13 || var14)) {
         if (var13) {
            var1.func_180501_a(var9, var3, 3);
         } else {
            var1.func_180501_a(var10, var3, 3);
         }

         var1.func_180501_a(var2, var3, 3);
      }

      if (var5.func_82837_s()) {
         TileEntity var15 = var1.func_175625_s(var2);
         if (var15 instanceof TileEntityChest) {
            ((TileEntityChest)var15).func_145976_a(var5.func_82833_r());
         }
      }

   }

   public IBlockState func_176455_e(World var1, BlockPos var2, IBlockState var3) {
      if (var1.field_72995_K) {
         return var3;
      } else {
         IBlockState var4 = var1.func_180495_p(var2.func_177978_c());
         IBlockState var5 = var1.func_180495_p(var2.func_177968_d());
         IBlockState var6 = var1.func_180495_p(var2.func_177976_e());
         IBlockState var7 = var1.func_180495_p(var2.func_177974_f());
         EnumFacing var8 = (EnumFacing)var3.func_177229_b(field_176459_a);
         Block var9 = var4.func_177230_c();
         Block var10 = var5.func_177230_c();
         Block var11 = var6.func_177230_c();
         Block var12 = var7.func_177230_c();
         if (var9 != this && var10 != this) {
            boolean var21 = var9.func_149730_j();
            boolean var22 = var10.func_149730_j();
            if (var11 == this || var12 == this) {
               BlockPos var23 = var11 == this ? var2.func_177976_e() : var2.func_177974_f();
               IBlockState var24 = var1.func_180495_p(var23.func_177978_c());
               IBlockState var25 = var1.func_180495_p(var23.func_177968_d());
               var8 = EnumFacing.SOUTH;
               EnumFacing var26;
               if (var11 == this) {
                  var26 = (EnumFacing)var6.func_177229_b(field_176459_a);
               } else {
                  var26 = (EnumFacing)var7.func_177229_b(field_176459_a);
               }

               if (var26 == EnumFacing.NORTH) {
                  var8 = EnumFacing.NORTH;
               }

               Block var19 = var24.func_177230_c();
               Block var20 = var25.func_177230_c();
               if ((var21 || var19.func_149730_j()) && !var22 && !var20.func_149730_j()) {
                  var8 = EnumFacing.SOUTH;
               }

               if ((var22 || var20.func_149730_j()) && !var21 && !var19.func_149730_j()) {
                  var8 = EnumFacing.NORTH;
               }
            }
         } else {
            BlockPos var13 = var9 == this ? var2.func_177978_c() : var2.func_177968_d();
            IBlockState var14 = var1.func_180495_p(var13.func_177976_e());
            IBlockState var15 = var1.func_180495_p(var13.func_177974_f());
            var8 = EnumFacing.EAST;
            EnumFacing var16;
            if (var9 == this) {
               var16 = (EnumFacing)var4.func_177229_b(field_176459_a);
            } else {
               var16 = (EnumFacing)var5.func_177229_b(field_176459_a);
            }

            if (var16 == EnumFacing.WEST) {
               var8 = EnumFacing.WEST;
            }

            Block var17 = var14.func_177230_c();
            Block var18 = var15.func_177230_c();
            if ((var11.func_149730_j() || var17.func_149730_j()) && !var12.func_149730_j() && !var18.func_149730_j()) {
               var8 = EnumFacing.EAST;
            }

            if ((var12.func_149730_j() || var18.func_149730_j()) && !var11.func_149730_j() && !var17.func_149730_j()) {
               var8 = EnumFacing.WEST;
            }
         }

         var3 = var3.func_177226_a(field_176459_a, var8);
         var1.func_180501_a(var2, var3, 3);
         return var3;
      }
   }

   public IBlockState func_176458_f(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = null;
      Iterator var5 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var5.hasNext()) {
         EnumFacing var6 = (EnumFacing)var5.next();
         IBlockState var7 = var1.func_180495_p(var2.func_177972_a(var6));
         if (var7.func_177230_c() == this) {
            return var3;
         }

         if (var7.func_177230_c().func_149730_j()) {
            if (var4 != null) {
               var4 = null;
               break;
            }

            var4 = var6;
         }
      }

      if (var4 != null) {
         return var3.func_177226_a(field_176459_a, var4.func_176734_d());
      } else {
         EnumFacing var8 = (EnumFacing)var3.func_177229_b(field_176459_a);
         if (var1.func_180495_p(var2.func_177972_a(var8)).func_177230_c().func_149730_j()) {
            var8 = var8.func_176734_d();
         }

         if (var1.func_180495_p(var2.func_177972_a(var8)).func_177230_c().func_149730_j()) {
            var8 = var8.func_176746_e();
         }

         if (var1.func_180495_p(var2.func_177972_a(var8)).func_177230_c().func_149730_j()) {
            var8 = var8.func_176734_d();
         }

         return var3.func_177226_a(field_176459_a, var8);
      }
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      int var3 = 0;
      BlockPos var4 = var2.func_177976_e();
      BlockPos var5 = var2.func_177974_f();
      BlockPos var6 = var2.func_177978_c();
      BlockPos var7 = var2.func_177968_d();
      if (var1.func_180495_p(var4).func_177230_c() == this) {
         if (this.func_176454_e(var1, var4)) {
            return false;
         }

         ++var3;
      }

      if (var1.func_180495_p(var5).func_177230_c() == this) {
         if (this.func_176454_e(var1, var5)) {
            return false;
         }

         ++var3;
      }

      if (var1.func_180495_p(var6).func_177230_c() == this) {
         if (this.func_176454_e(var1, var6)) {
            return false;
         }

         ++var3;
      }

      if (var1.func_180495_p(var7).func_177230_c() == this) {
         if (this.func_176454_e(var1, var7)) {
            return false;
         }

         ++var3;
      }

      return var3 <= 1;
   }

   private boolean func_176454_e(World var1, BlockPos var2) {
      if (var1.func_180495_p(var2).func_177230_c() != this) {
         return false;
      } else {
         Iterator var3 = EnumFacing.Plane.HORIZONTAL.iterator();

         EnumFacing var4;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = (EnumFacing)var3.next();
         } while(var1.func_180495_p(var2.func_177972_a(var4)).func_177230_c() != this);

         return true;
      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      super.func_176204_a(var1, var2, var3, var4);
      TileEntity var5 = var1.func_175625_s(var2);
      if (var5 instanceof TileEntityChest) {
         var5.func_145836_u();
      }

   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      TileEntity var4 = var1.func_175625_s(var2);
      if (var4 instanceof IInventory) {
         InventoryHelper.func_180175_a(var1, var2, (IInventory)var4);
         var1.func_175666_e(var2, this);
      }

      super.func_180663_b(var1, var2, var3);
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         ILockableContainer var9 = this.func_180676_d(var1, var2);
         if (var9 != null) {
            var4.func_71007_a(var9);
            if (this.field_149956_a == 0) {
               var4.func_71029_a(StatList.field_181723_aa);
            } else if (this.field_149956_a == 1) {
               var4.func_71029_a(StatList.field_181737_U);
            }
         }

         return true;
      }
   }

   public ILockableContainer func_180676_d(World var1, BlockPos var2) {
      TileEntity var3 = var1.func_175625_s(var2);
      if (!(var3 instanceof TileEntityChest)) {
         return null;
      } else {
         Object var4 = (TileEntityChest)var3;
         if (this.func_176457_m(var1, var2)) {
            return null;
         } else {
            Iterator var5 = EnumFacing.Plane.HORIZONTAL.iterator();

            while(true) {
               while(true) {
                  EnumFacing var6;
                  TileEntity var9;
                  do {
                     BlockPos var7;
                     Block var8;
                     do {
                        if (!var5.hasNext()) {
                           return (ILockableContainer)var4;
                        }

                        var6 = (EnumFacing)var5.next();
                        var7 = var2.func_177972_a(var6);
                        var8 = var1.func_180495_p(var7).func_177230_c();
                     } while(var8 != this);

                     if (this.func_176457_m(var1, var7)) {
                        return null;
                     }

                     var9 = var1.func_175625_s(var7);
                  } while(!(var9 instanceof TileEntityChest));

                  if (var6 != EnumFacing.WEST && var6 != EnumFacing.NORTH) {
                     var4 = new InventoryLargeChest("container.chestDouble", (ILockableContainer)var4, (TileEntityChest)var9);
                  } else {
                     var4 = new InventoryLargeChest("container.chestDouble", (TileEntityChest)var9, (ILockableContainer)var4);
                  }
               }
            }
         }
      }
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityChest();
   }

   public boolean func_149744_f() {
      return this.field_149956_a == 1;
   }

   public int func_180656_a(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      if (!this.func_149744_f()) {
         return 0;
      } else {
         int var5 = 0;
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityChest) {
            var5 = ((TileEntityChest)var6).field_145987_o;
         }

         return MathHelper.func_76125_a(var5, 0, 15);
      }
   }

   public int func_176211_b(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return var4 == EnumFacing.UP ? this.func_180656_a(var1, var2, var3, var4) : 0;
   }

   private boolean func_176457_m(World var1, BlockPos var2) {
      return this.func_176456_n(var1, var2) || this.func_176453_o(var1, var2);
   }

   private boolean func_176456_n(World var1, BlockPos var2) {
      return var1.func_180495_p(var2.func_177984_a()).func_177230_c().func_149721_r();
   }

   private boolean func_176453_o(World var1, BlockPos var2) {
      Iterator var3 = var1.func_72872_a(EntityOcelot.class, new AxisAlignedBB((double)var2.func_177958_n(), (double)(var2.func_177956_o() + 1), (double)var2.func_177952_p(), (double)(var2.func_177958_n() + 1), (double)(var2.func_177956_o() + 2), (double)(var2.func_177952_p() + 1))).iterator();

      EntityOcelot var5;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         Entity var4 = (Entity)var3.next();
         var5 = (EntityOcelot)var4;
      } while(!var5.func_70906_o());

      return true;
   }

   public boolean func_149740_M() {
      return true;
   }

   public int func_180641_l(World var1, BlockPos var2) {
      return Container.func_94526_b(this.func_180676_d(var1, var2));
   }

   public IBlockState func_176203_a(int var1) {
      EnumFacing var2 = EnumFacing.func_82600_a(var1);
      if (var2.func_176740_k() == EnumFacing.Axis.Y) {
         var2 = EnumFacing.NORTH;
      }

      return this.func_176223_P().func_177226_a(field_176459_a, var2);
   }

   public int func_176201_c(IBlockState var1) {
      return ((EnumFacing)var1.func_177229_b(field_176459_a)).func_176745_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176459_a});
   }

   static {
      field_176459_a = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
   }
}
