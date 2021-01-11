package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockOldLeaf extends BlockLeaves {
   public static final PropertyEnum<BlockPlanks.EnumType> field_176239_P = PropertyEnum.func_177708_a("variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>() {
      public boolean apply(BlockPlanks.EnumType var1) {
         return var1.func_176839_a() < 4;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((BlockPlanks.EnumType)var1);
      }
   });

   public BlockOldLeaf() {
      super();
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176239_P, BlockPlanks.EnumType.OAK).func_177226_a(field_176236_b, true).func_177226_a(field_176237_a, true));
   }

   public int func_180644_h(IBlockState var1) {
      if (var1.func_177230_c() != this) {
         return super.func_180644_h(var1);
      } else {
         BlockPlanks.EnumType var2 = (BlockPlanks.EnumType)var1.func_177229_b(field_176239_P);
         if (var2 == BlockPlanks.EnumType.SPRUCE) {
            return ColorizerFoliage.func_77466_a();
         } else {
            return var2 == BlockPlanks.EnumType.BIRCH ? ColorizerFoliage.func_77469_b() : super.func_180644_h(var1);
         }
      }
   }

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      IBlockState var4 = var1.func_180495_p(var2);
      if (var4.func_177230_c() == this) {
         BlockPlanks.EnumType var5 = (BlockPlanks.EnumType)var4.func_177229_b(field_176239_P);
         if (var5 == BlockPlanks.EnumType.SPRUCE) {
            return ColorizerFoliage.func_77466_a();
         }

         if (var5 == BlockPlanks.EnumType.BIRCH) {
            return ColorizerFoliage.func_77469_b();
         }
      }

      return super.func_180662_a(var1, var2, var3);
   }

   protected void func_176234_a(World var1, BlockPos var2, IBlockState var3, int var4) {
      if (var3.func_177229_b(field_176239_P) == BlockPlanks.EnumType.OAK && var1.field_73012_v.nextInt(var4) == 0) {
         func_180635_a(var1, var2, new ItemStack(Items.field_151034_e, 1, 0));
      }

   }

   protected int func_176232_d(IBlockState var1) {
      return var1.func_177229_b(field_176239_P) == BlockPlanks.EnumType.JUNGLE ? 40 : super.func_176232_d(var1);
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, BlockPlanks.EnumType.OAK.func_176839_a()));
      var3.add(new ItemStack(var1, 1, BlockPlanks.EnumType.SPRUCE.func_176839_a()));
      var3.add(new ItemStack(var1, 1, BlockPlanks.EnumType.BIRCH.func_176839_a()));
      var3.add(new ItemStack(var1, 1, BlockPlanks.EnumType.JUNGLE.func_176839_a()));
   }

   protected ItemStack func_180643_i(IBlockState var1) {
      return new ItemStack(Item.func_150898_a(this), 1, ((BlockPlanks.EnumType)var1.func_177229_b(field_176239_P)).func_176839_a());
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176239_P, this.func_176233_b(var1)).func_177226_a(field_176237_a, (var1 & 4) == 0).func_177226_a(field_176236_b, (var1 & 8) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((BlockPlanks.EnumType)var1.func_177229_b(field_176239_P)).func_176839_a();
      if (!(Boolean)var1.func_177229_b(field_176237_a)) {
         var3 |= 4;
      }

      if ((Boolean)var1.func_177229_b(field_176236_b)) {
         var3 |= 8;
      }

      return var3;
   }

   public BlockPlanks.EnumType func_176233_b(int var1) {
      return BlockPlanks.EnumType.func_176837_a((var1 & 3) % 4);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176239_P, field_176236_b, field_176237_a});
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockPlanks.EnumType)var1.func_177229_b(field_176239_P)).func_176839_a();
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      if (!var1.field_72995_K && var2.func_71045_bC() != null && var2.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
         var2.func_71029_a(StatList.field_75934_C[Block.func_149682_b(this)]);
         func_180635_a(var1, var3, new ItemStack(Item.func_150898_a(this), 1, ((BlockPlanks.EnumType)var4.func_177229_b(field_176239_P)).func_176839_a()));
      } else {
         super.func_180657_a(var1, var2, var3, var4, var5);
      }
   }
}
