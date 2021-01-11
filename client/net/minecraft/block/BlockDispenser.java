package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.RegistryDefaulted;
import net.minecraft.world.World;

public class BlockDispenser extends BlockContainer {
   public static final PropertyDirection field_176441_a = PropertyDirection.func_177714_a("facing");
   public static final PropertyBool field_176440_b = PropertyBool.func_177716_a("triggered");
   public static final RegistryDefaulted<Item, IBehaviorDispenseItem> field_149943_a = new RegistryDefaulted(new BehaviorDefaultDispenseItem());
   protected Random field_149942_b = new Random();

   protected BlockDispenser() {
      super(Material.field_151576_e);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176441_a, EnumFacing.NORTH).func_177226_a(field_176440_b, false));
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   public int func_149738_a(World var1) {
      return 4;
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      super.func_176213_c(var1, var2, var3);
      this.func_176438_e(var1, var2, var3);
   }

   private void func_176438_e(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.field_72995_K) {
         EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176441_a);
         boolean var5 = var1.func_180495_p(var2.func_177978_c()).func_177230_c().func_149730_j();
         boolean var6 = var1.func_180495_p(var2.func_177968_d()).func_177230_c().func_149730_j();
         if (var4 == EnumFacing.NORTH && var5 && !var6) {
            var4 = EnumFacing.SOUTH;
         } else if (var4 == EnumFacing.SOUTH && var6 && !var5) {
            var4 = EnumFacing.NORTH;
         } else {
            boolean var7 = var1.func_180495_p(var2.func_177976_e()).func_177230_c().func_149730_j();
            boolean var8 = var1.func_180495_p(var2.func_177974_f()).func_177230_c().func_149730_j();
            if (var4 == EnumFacing.WEST && var7 && !var8) {
               var4 = EnumFacing.EAST;
            } else if (var4 == EnumFacing.EAST && var8 && !var7) {
               var4 = EnumFacing.WEST;
            }
         }

         var1.func_180501_a(var2, var3.func_177226_a(field_176441_a, var4).func_177226_a(field_176440_b, false), 2);
      }
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntity var9 = var1.func_175625_s(var2);
         if (var9 instanceof TileEntityDispenser) {
            var4.func_71007_a((TileEntityDispenser)var9);
            if (var9 instanceof TileEntityDropper) {
               var4.func_71029_a(StatList.field_181731_O);
            } else {
               var4.func_71029_a(StatList.field_181733_Q);
            }
         }

         return true;
      }
   }

   protected void func_176439_d(World var1, BlockPos var2) {
      BlockSourceImpl var3 = new BlockSourceImpl(var1, var2);
      TileEntityDispenser var4 = (TileEntityDispenser)var3.func_150835_j();
      if (var4 != null) {
         int var5 = var4.func_146017_i();
         if (var5 < 0) {
            var1.func_175718_b(1001, var2, 0);
         } else {
            ItemStack var6 = var4.func_70301_a(var5);
            IBehaviorDispenseItem var7 = this.func_149940_a(var6);
            if (var7 != IBehaviorDispenseItem.field_82483_a) {
               ItemStack var8 = var7.func_82482_a(var3, var6);
               var4.func_70299_a(var5, var8.field_77994_a <= 0 ? null : var8);
            }

         }
      }
   }

   protected IBehaviorDispenseItem func_149940_a(ItemStack var1) {
      return (IBehaviorDispenseItem)field_149943_a.func_82594_a(var1 == null ? null : var1.func_77973_b());
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      boolean var5 = var1.func_175640_z(var2) || var1.func_175640_z(var2.func_177984_a());
      boolean var6 = (Boolean)var3.func_177229_b(field_176440_b);
      if (var5 && !var6) {
         var1.func_175684_a(var2, this, this.func_149738_a(var1));
         var1.func_180501_a(var2, var3.func_177226_a(field_176440_b, true), 4);
      } else if (!var5 && var6) {
         var1.func_180501_a(var2, var3.func_177226_a(field_176440_b, false), 4);
      }

   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.field_72995_K) {
         this.func_176439_d(var1, var2);
      }

   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityDispenser();
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P().func_177226_a(field_176441_a, BlockPistonBase.func_180695_a(var1, var2, var8)).func_177226_a(field_176440_b, false);
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      var1.func_180501_a(var2, var3.func_177226_a(field_176441_a, BlockPistonBase.func_180695_a(var1, var2, var4)), 2);
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityDispenser) {
            ((TileEntityDispenser)var6).func_146018_a(var5.func_82833_r());
         }
      }

   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      TileEntity var4 = var1.func_175625_s(var2);
      if (var4 instanceof TileEntityDispenser) {
         InventoryHelper.func_180175_a(var1, var2, (TileEntityDispenser)var4);
         var1.func_175666_e(var2, this);
      }

      super.func_180663_b(var1, var2, var3);
   }

   public static IPosition func_149939_a(IBlockSource var0) {
      EnumFacing var1 = func_149937_b(var0.func_82620_h());
      double var2 = var0.func_82615_a() + 0.7D * (double)var1.func_82601_c();
      double var4 = var0.func_82617_b() + 0.7D * (double)var1.func_96559_d();
      double var6 = var0.func_82616_c() + 0.7D * (double)var1.func_82599_e();
      return new PositionImpl(var2, var4, var6);
   }

   public static EnumFacing func_149937_b(int var0) {
      return EnumFacing.func_82600_a(var0 & 7);
   }

   public boolean func_149740_M() {
      return true;
   }

   public int func_180641_l(World var1, BlockPos var2) {
      return Container.func_178144_a(var1.func_175625_s(var2));
   }

   public int func_149645_b() {
      return 3;
   }

   public IBlockState func_176217_b(IBlockState var1) {
      return this.func_176223_P().func_177226_a(field_176441_a, EnumFacing.SOUTH);
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176441_a, func_149937_b(var1)).func_177226_a(field_176440_b, (var1 & 8) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176441_a)).func_176745_a();
      if ((Boolean)var1.func_177229_b(field_176440_b)) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176441_a, field_176440_b});
   }
}
