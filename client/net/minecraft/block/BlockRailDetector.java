package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase {
   public static final PropertyEnum<BlockRailBase.EnumRailDirection> field_176573_b = PropertyEnum.func_177708_a("shape", BlockRailBase.EnumRailDirection.class, new Predicate<BlockRailBase.EnumRailDirection>() {
      public boolean apply(BlockRailBase.EnumRailDirection var1) {
         return var1 != BlockRailBase.EnumRailDirection.NORTH_EAST && var1 != BlockRailBase.EnumRailDirection.NORTH_WEST && var1 != BlockRailBase.EnumRailDirection.SOUTH_EAST && var1 != BlockRailBase.EnumRailDirection.SOUTH_WEST;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((BlockRailBase.EnumRailDirection)var1);
      }
   });
   public static final PropertyBool field_176574_M = PropertyBool.func_177716_a("powered");

   public BlockRailDetector() {
      super(true);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176574_M, false).func_177226_a(field_176573_b, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
      this.func_149675_a(true);
   }

   public int func_149738_a(World var1) {
      return 20;
   }

   public boolean func_149744_f() {
      return true;
   }

   public void func_180634_a(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      if (!var1.field_72995_K) {
         if (!(Boolean)var3.func_177229_b(field_176574_M)) {
            this.func_176570_e(var1, var2, var3);
         }
      }
   }

   public void func_180645_a(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.field_72995_K && (Boolean)var3.func_177229_b(field_176574_M)) {
         this.func_176570_e(var1, var2, var3);
      }
   }

   public int func_180656_a(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return (Boolean)var3.func_177229_b(field_176574_M) ? 15 : 0;
   }

   public int func_176211_b(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      if (!(Boolean)var3.func_177229_b(field_176574_M)) {
         return 0;
      } else {
         return var4 == EnumFacing.UP ? 15 : 0;
      }
   }

   private void func_176570_e(World var1, BlockPos var2, IBlockState var3) {
      boolean var4 = (Boolean)var3.func_177229_b(field_176574_M);
      boolean var5 = false;
      List var6 = this.func_176571_a(var1, var2, EntityMinecart.class);
      if (!var6.isEmpty()) {
         var5 = true;
      }

      if (var5 && !var4) {
         var1.func_180501_a(var2, var3.func_177226_a(field_176574_M, true), 3);
         var1.func_175685_c(var2, this);
         var1.func_175685_c(var2.func_177977_b(), this);
         var1.func_175704_b(var2, var2);
      }

      if (!var5 && var4) {
         var1.func_180501_a(var2, var3.func_177226_a(field_176574_M, false), 3);
         var1.func_175685_c(var2, this);
         var1.func_175685_c(var2.func_177977_b(), this);
         var1.func_175704_b(var2, var2);
      }

      if (var5) {
         var1.func_175684_a(var2, this, this.func_149738_a(var1));
      }

      var1.func_175666_e(var2, this);
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      super.func_176213_c(var1, var2, var3);
      this.func_176570_e(var1, var2, var3);
   }

   public IProperty<BlockRailBase.EnumRailDirection> func_176560_l() {
      return field_176573_b;
   }

   public boolean func_149740_M() {
      return true;
   }

   public int func_180641_l(World var1, BlockPos var2) {
      if ((Boolean)var1.func_180495_p(var2).func_177229_b(field_176574_M)) {
         List var3 = this.func_176571_a(var1, var2, EntityMinecartCommandBlock.class);
         if (!var3.isEmpty()) {
            return ((EntityMinecartCommandBlock)var3.get(0)).func_145822_e().func_145760_g();
         }

         List var4 = this.func_176571_a(var1, var2, EntityMinecart.class, EntitySelectors.field_96566_b);
         if (!var4.isEmpty()) {
            return Container.func_94526_b((IInventory)var4.get(0));
         }
      }

      return 0;
   }

   protected <T extends EntityMinecart> List<T> func_176571_a(World var1, BlockPos var2, Class<T> var3, Predicate<Entity>... var4) {
      AxisAlignedBB var5 = this.func_176572_a(var2);
      return var4.length != 1 ? var1.func_72872_a(var3, var5) : var1.func_175647_a(var3, var5, var4[0]);
   }

   private AxisAlignedBB func_176572_a(BlockPos var1) {
      float var2 = 0.2F;
      return new AxisAlignedBB((double)((float)var1.func_177958_n() + 0.2F), (double)var1.func_177956_o(), (double)((float)var1.func_177952_p() + 0.2F), (double)((float)(var1.func_177958_n() + 1) - 0.2F), (double)((float)(var1.func_177956_o() + 1) - 0.2F), (double)((float)(var1.func_177952_p() + 1) - 0.2F));
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176573_b, BlockRailBase.EnumRailDirection.func_177016_a(var1 & 7)).func_177226_a(field_176574_M, (var1 & 8) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((BlockRailBase.EnumRailDirection)var1.func_177229_b(field_176573_b)).func_177015_a();
      if ((Boolean)var1.func_177229_b(field_176574_M)) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176573_b, field_176574_M});
   }
}
