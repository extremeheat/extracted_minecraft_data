package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFarmland extends Block {
   public static final PropertyInteger field_176531_a = PropertyInteger.func_177719_a("moisture", 0, 7);

   protected BlockFarmland() {
      super(Material.field_151578_c);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176531_a, 0));
      this.func_149675_a(true);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
      this.func_149713_g(255);
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return new AxisAlignedBB((double)var2.func_177958_n(), (double)var2.func_177956_o(), (double)var2.func_177952_p(), (double)(var2.func_177958_n() + 1), (double)(var2.func_177956_o() + 1), (double)(var2.func_177952_p() + 1));
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      int var5 = (Integer)var3.func_177229_b(field_176531_a);
      if (!this.func_176530_e(var1, var2) && !var1.func_175727_C(var2.func_177984_a())) {
         if (var5 > 0) {
            var1.func_180501_a(var2, var3.func_177226_a(field_176531_a, var5 - 1), 2);
         } else if (!this.func_176529_d(var1, var2)) {
            var1.func_175656_a(var2, Blocks.field_150346_d.func_176223_P());
         }
      } else if (var5 < 7) {
         var1.func_180501_a(var2, var3.func_177226_a(field_176531_a, 7), 2);
      }

   }

   public void func_180658_a(World var1, BlockPos var2, Entity var3, float var4) {
      if (var3 instanceof EntityLivingBase) {
         if (!var1.field_72995_K && var1.field_73012_v.nextFloat() < var4 - 0.5F) {
            if (!(var3 instanceof EntityPlayer) && !var1.func_82736_K().func_82766_b("mobGriefing")) {
               return;
            }

            var1.func_175656_a(var2, Blocks.field_150346_d.func_176223_P());
         }

         super.func_180658_a(var1, var2, var3, var4);
      }
   }

   private boolean func_176529_d(World var1, BlockPos var2) {
      Block var3 = var1.func_180495_p(var2.func_177984_a()).func_177230_c();
      return var3 instanceof BlockCrops || var3 instanceof BlockStem;
   }

   private boolean func_176530_e(World var1, BlockPos var2) {
      Iterator var3 = BlockPos.func_177975_b(var2.func_177982_a(-4, 0, -4), var2.func_177982_a(4, 1, 4)).iterator();

      BlockPos.MutableBlockPos var4;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         var4 = (BlockPos.MutableBlockPos)var3.next();
      } while(var1.func_180495_p(var4).func_177230_c().func_149688_o() != Material.field_151586_h);

      return true;
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      super.func_176204_a(var1, var2, var3, var4);
      if (var1.func_180495_p(var2.func_177984_a()).func_177230_c().func_149688_o().func_76220_a()) {
         var1.func_175656_a(var2, Blocks.field_150346_d.func_176223_P());
      }

   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      switch(var3) {
      case UP:
         return true;
      case NORTH:
      case SOUTH:
      case WEST:
      case EAST:
         Block var4 = var1.func_180495_p(var2).func_177230_c();
         return !var4.func_149662_c() && var4 != Blocks.field_150458_ak;
      default:
         return super.func_176225_a(var1, var2, var3);
      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Blocks.field_150346_d.func_180660_a(Blocks.field_150346_d.func_176223_P().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.DIRT), var2, var3);
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Item.func_150898_a(Blocks.field_150346_d);
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176531_a, var1 & 7);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176531_a);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176531_a});
   }
}
