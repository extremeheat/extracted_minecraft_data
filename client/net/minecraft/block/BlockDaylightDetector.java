package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockDaylightDetector extends BlockContainer {
   public static final IntegerProperty field_176436_a;
   public static final BooleanProperty field_196320_b;
   protected static final VoxelShape field_196321_c;

   public BlockDaylightDetector(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176436_a, 0)).func_206870_a(field_196320_b, false));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196321_c;
   }

   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return (Integer)var1.func_177229_b(field_176436_a);
   }

   public static void func_196319_d(IBlockState var0, World var1, BlockPos var2) {
      if (var1.field_73011_w.func_191066_m()) {
         int var3 = var1.func_175642_b(EnumLightType.SKY, var2) - var1.func_175657_ab();
         float var4 = var1.func_72929_e(1.0F);
         boolean var5 = (Boolean)var0.func_177229_b(field_196320_b);
         if (var5) {
            var3 = 15 - var3;
         } else if (var3 > 0) {
            float var6 = var4 < 3.1415927F ? 0.0F : 6.2831855F;
            var4 += (var6 - var4) * 0.2F;
            var3 = Math.round((float)var3 * MathHelper.func_76134_b(var4));
         }

         var3 = MathHelper.func_76125_a(var3, 0, 15);
         if ((Integer)var0.func_177229_b(field_176436_a) != var3) {
            var1.func_180501_a(var2, (IBlockState)var0.func_206870_a(field_176436_a, var3), 3);
         }

      }
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var4.func_175142_cm()) {
         if (var2.field_72995_K) {
            return true;
         } else {
            IBlockState var10 = (IBlockState)var1.func_177231_a(field_196320_b);
            var2.func_180501_a(var3, var10, 4);
            func_196319_d(var10, var2, var3);
            return true;
         }
      } else {
         return super.func_196250_a(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.MODEL;
   }

   public boolean func_149744_f(IBlockState var1) {
      return true;
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityDaylightDetector();
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176436_a, field_196320_b);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return var4 == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   static {
      field_176436_a = BlockStateProperties.field_208136_ak;
      field_196320_b = BlockStateProperties.field_208188_o;
      field_196321_c = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
   }
}
