package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockCrops extends BlockBush implements IGrowable {
   public static final IntegerProperty field_176488_a;
   private static final VoxelShape[] field_196393_a;

   protected BlockCrops(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(this.func_185524_e(), 0));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196393_a[(Integer)var1.func_177229_b(this.func_185524_e())];
   }

   protected boolean func_200014_a_(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var1.func_177230_c() == Blocks.field_150458_ak;
   }

   public IntegerProperty func_185524_e() {
      return field_176488_a;
   }

   public int func_185526_g() {
      return 7;
   }

   protected int func_185527_x(IBlockState var1) {
      return (Integer)var1.func_177229_b(this.func_185524_e());
   }

   public IBlockState func_185528_e(int var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(this.func_185524_e(), var1);
   }

   public boolean func_185525_y(IBlockState var1) {
      return (Integer)var1.func_177229_b(this.func_185524_e()) >= this.func_185526_g();
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      super.func_196267_b(var1, var2, var3, var4);
      if (var2.func_201669_a(var3.func_177984_a(), 0) >= 9) {
         int var5 = this.func_185527_x(var1);
         if (var5 < this.func_185526_g()) {
            float var6 = func_180672_a(this, var2, var3);
            if (var4.nextInt((int)(25.0F / var6) + 1) == 0) {
               var2.func_180501_a(var3, this.func_185528_e(var5 + 1), 2);
            }
         }
      }

   }

   public void func_176487_g(World var1, BlockPos var2, IBlockState var3) {
      int var4 = this.func_185527_x(var3) + this.func_185529_b(var1);
      int var5 = this.func_185526_g();
      if (var4 > var5) {
         var4 = var5;
      }

      var1.func_180501_a(var2, this.func_185528_e(var4), 2);
   }

   protected int func_185529_b(World var1) {
      return MathHelper.func_76136_a(var1.field_73012_v, 2, 5);
   }

   protected static float func_180672_a(Block var0, IBlockReader var1, BlockPos var2) {
      float var3 = 1.0F;
      BlockPos var4 = var2.func_177977_b();

      for(int var5 = -1; var5 <= 1; ++var5) {
         for(int var6 = -1; var6 <= 1; ++var6) {
            float var7 = 0.0F;
            IBlockState var8 = var1.func_180495_p(var4.func_177982_a(var5, 0, var6));
            if (var8.func_177230_c() == Blocks.field_150458_ak) {
               var7 = 1.0F;
               if ((Integer)var8.func_177229_b(BlockFarmland.field_176531_a) > 0) {
                  var7 = 3.0F;
               }
            }

            if (var5 != 0 || var6 != 0) {
               var7 /= 4.0F;
            }

            var3 += var7;
         }
      }

      BlockPos var12 = var2.func_177978_c();
      BlockPos var13 = var2.func_177968_d();
      BlockPos var15 = var2.func_177976_e();
      BlockPos var14 = var2.func_177974_f();
      boolean var9 = var0 == var1.func_180495_p(var15).func_177230_c() || var0 == var1.func_180495_p(var14).func_177230_c();
      boolean var10 = var0 == var1.func_180495_p(var12).func_177230_c() || var0 == var1.func_180495_p(var13).func_177230_c();
      if (var9 && var10) {
         var3 /= 2.0F;
      } else {
         boolean var11 = var0 == var1.func_180495_p(var15.func_177978_c()).func_177230_c() || var0 == var1.func_180495_p(var14.func_177978_c()).func_177230_c() || var0 == var1.func_180495_p(var14.func_177968_d()).func_177230_c() || var0 == var1.func_180495_p(var15.func_177968_d()).func_177230_c();
         if (var11) {
            var3 /= 2.0F;
         }
      }

      return var3;
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      return (var2.func_201669_a(var3, 0) >= 8 || var2.func_175678_i(var3)) && super.func_196260_a(var1, var2, var3);
   }

   protected IItemProvider func_199772_f() {
      return Items.field_151014_N;
   }

   protected IItemProvider func_199773_g() {
      return Items.field_151015_O;
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      super.func_196255_a(var1, var2, var3, var4, 0);
      if (!var2.field_72995_K) {
         int var6 = this.func_185527_x(var1);
         if (var6 >= this.func_185526_g()) {
            int var7 = 3 + var5;

            for(int var8 = 0; var8 < var7; ++var8) {
               if (var2.field_73012_v.nextInt(2 * this.func_185526_g()) <= var6) {
                  func_180635_a(var2, var3, new ItemStack(this.func_199772_f()));
               }
            }
         }

      }
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return this.func_185525_y(var1) ? this.func_199773_g() : this.func_199772_f();
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return new ItemStack(this.func_199772_f());
   }

   public boolean func_176473_a(IBlockReader var1, BlockPos var2, IBlockState var3, boolean var4) {
      return !this.func_185525_y(var3);
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      this.func_176487_g(var1, var3, var4);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176488_a);
   }

   static {
      field_176488_a = BlockStateProperties.field_208170_W;
      field_196393_a = new VoxelShape[]{Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
   }
}
