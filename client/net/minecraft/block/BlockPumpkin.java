package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockPumpkin extends BlockDirectional {
   private BlockPattern field_176394_a;
   private BlockPattern field_176393_b;
   private BlockPattern field_176395_M;
   private BlockPattern field_176396_O;
   private static final Predicate<IBlockState> field_181085_Q = new Predicate<IBlockState>() {
      public boolean apply(IBlockState var1) {
         return var1 != null && (var1.func_177230_c() == Blocks.field_150423_aK || var1.func_177230_c() == Blocks.field_150428_aP);
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((IBlockState)var1);
      }
   };

   protected BlockPumpkin() {
      super(Material.field_151572_C, MapColor.field_151676_q);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176387_N, EnumFacing.NORTH));
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      super.func_176213_c(var1, var2, var3);
      this.func_180673_e(var1, var2);
   }

   public boolean func_176390_d(World var1, BlockPos var2) {
      return this.func_176392_j().func_177681_a(var1, var2) != null || this.func_176389_S().func_177681_a(var1, var2) != null;
   }

   private void func_180673_e(World var1, BlockPos var2) {
      BlockPattern.PatternHelper var3;
      int var4;
      int var6;
      if ((var3 = this.func_176391_l().func_177681_a(var1, var2)) != null) {
         for(var4 = 0; var4 < this.func_176391_l().func_177685_b(); ++var4) {
            BlockWorldState var5 = var3.func_177670_a(0, var4, 0);
            var1.func_180501_a(var5.func_177508_d(), Blocks.field_150350_a.func_176223_P(), 2);
         }

         EntitySnowman var9 = new EntitySnowman(var1);
         BlockPos var10 = var3.func_177670_a(0, 2, 0).func_177508_d();
         var9.func_70012_b((double)var10.func_177958_n() + 0.5D, (double)var10.func_177956_o() + 0.05D, (double)var10.func_177952_p() + 0.5D, 0.0F, 0.0F);
         var1.func_72838_d(var9);

         for(var6 = 0; var6 < 120; ++var6) {
            var1.func_175688_a(EnumParticleTypes.SNOW_SHOVEL, (double)var10.func_177958_n() + var1.field_73012_v.nextDouble(), (double)var10.func_177956_o() + var1.field_73012_v.nextDouble() * 2.5D, (double)var10.func_177952_p() + var1.field_73012_v.nextDouble(), 0.0D, 0.0D, 0.0D);
         }

         for(var6 = 0; var6 < this.func_176391_l().func_177685_b(); ++var6) {
            BlockWorldState var7 = var3.func_177670_a(0, var6, 0);
            var1.func_175722_b(var7.func_177508_d(), Blocks.field_150350_a);
         }
      } else if ((var3 = this.func_176388_T().func_177681_a(var1, var2)) != null) {
         for(var4 = 0; var4 < this.func_176388_T().func_177684_c(); ++var4) {
            for(int var12 = 0; var12 < this.func_176388_T().func_177685_b(); ++var12) {
               var1.func_180501_a(var3.func_177670_a(var4, var12, 0).func_177508_d(), Blocks.field_150350_a.func_176223_P(), 2);
            }
         }

         BlockPos var11 = var3.func_177670_a(1, 2, 0).func_177508_d();
         EntityIronGolem var13 = new EntityIronGolem(var1);
         var13.func_70849_f(true);
         var13.func_70012_b((double)var11.func_177958_n() + 0.5D, (double)var11.func_177956_o() + 0.05D, (double)var11.func_177952_p() + 0.5D, 0.0F, 0.0F);
         var1.func_72838_d(var13);

         for(var6 = 0; var6 < 120; ++var6) {
            var1.func_175688_a(EnumParticleTypes.SNOWBALL, (double)var11.func_177958_n() + var1.field_73012_v.nextDouble(), (double)var11.func_177956_o() + var1.field_73012_v.nextDouble() * 3.9D, (double)var11.func_177952_p() + var1.field_73012_v.nextDouble(), 0.0D, 0.0D, 0.0D);
         }

         for(var6 = 0; var6 < this.func_176388_T().func_177684_c(); ++var6) {
            for(int var14 = 0; var14 < this.func_176388_T().func_177685_b(); ++var14) {
               BlockWorldState var8 = var3.func_177670_a(var6, var14, 0);
               var1.func_175722_b(var8.func_177508_d(), Blocks.field_150350_a);
            }
         }
      }

   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return var1.func_180495_p(var2).func_177230_c().field_149764_J.func_76222_j() && World.func_175683_a(var1, var2.func_177977_b());
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P().func_177226_a(field_176387_N, var8.func_174811_aO().func_176734_d());
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176387_N, EnumFacing.func_176731_b(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((EnumFacing)var1.func_177229_b(field_176387_N)).func_176736_b();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176387_N});
   }

   protected BlockPattern func_176392_j() {
      if (this.field_176394_a == null) {
         this.field_176394_a = FactoryBlockPattern.func_177660_a().func_177659_a(" ", "#", "#").func_177662_a('#', BlockWorldState.func_177510_a(BlockStateHelper.func_177638_a(Blocks.field_150433_aE))).func_177661_b();
      }

      return this.field_176394_a;
   }

   protected BlockPattern func_176391_l() {
      if (this.field_176393_b == null) {
         this.field_176393_b = FactoryBlockPattern.func_177660_a().func_177659_a("^", "#", "#").func_177662_a('^', BlockWorldState.func_177510_a(field_181085_Q)).func_177662_a('#', BlockWorldState.func_177510_a(BlockStateHelper.func_177638_a(Blocks.field_150433_aE))).func_177661_b();
      }

      return this.field_176393_b;
   }

   protected BlockPattern func_176389_S() {
      if (this.field_176395_M == null) {
         this.field_176395_M = FactoryBlockPattern.func_177660_a().func_177659_a("~ ~", "###", "~#~").func_177662_a('#', BlockWorldState.func_177510_a(BlockStateHelper.func_177638_a(Blocks.field_150339_S))).func_177662_a('~', BlockWorldState.func_177510_a(BlockStateHelper.func_177638_a(Blocks.field_150350_a))).func_177661_b();
      }

      return this.field_176395_M;
   }

   protected BlockPattern func_176388_T() {
      if (this.field_176396_O == null) {
         this.field_176396_O = FactoryBlockPattern.func_177660_a().func_177659_a("~^~", "###", "~#~").func_177662_a('^', BlockWorldState.func_177510_a(field_181085_Q)).func_177662_a('#', BlockWorldState.func_177510_a(BlockStateHelper.func_177638_a(Blocks.field_150339_S))).func_177662_a('~', BlockWorldState.func_177510_a(BlockStateHelper.func_177638_a(Blocks.field_150350_a))).func_177661_b();
      }

      return this.field_176396_O;
   }
}
