package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockBannerWall extends BlockAbstractBanner {
   public static final DirectionProperty field_196290_a;
   private static final Map<EnumFacing, VoxelShape> field_196291_b;

   public BlockBannerWall(EnumDyeColor var1, Block.Properties var2) {
      super(var1, var2);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196290_a, EnumFacing.NORTH));
   }

   public String func_149739_a() {
      return this.func_199767_j().func_77658_a();
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      return var2.func_180495_p(var3.func_177972_a(((EnumFacing)var1.func_177229_b(field_196290_a)).func_176734_d())).func_185904_a().func_76220_a();
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var2 == ((EnumFacing)var1.func_177229_b(field_196290_a)).func_176734_d() && !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return (VoxelShape)field_196291_b.get(var1.func_177229_b(field_196290_a));
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = this.func_176223_P();
      World var3 = var1.func_195991_k();
      BlockPos var4 = var1.func_195995_a();
      EnumFacing[] var5 = var1.func_196009_e();
      EnumFacing[] var6 = var5;
      int var7 = var5.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         EnumFacing var9 = var6[var8];
         if (var9.func_176740_k().func_176722_c()) {
            EnumFacing var10 = var9.func_176734_d();
            var2 = (IBlockState)var2.func_206870_a(field_196290_a, var10);
            if (var2.func_196955_c(var3, var4)) {
               return var2;
            }
         }
      }

      return null;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_196290_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_196290_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_196290_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196290_a);
   }

   static {
      field_196290_a = BlockHorizontal.field_185512_D;
      field_196291_b = Maps.newEnumMap(ImmutableMap.of(EnumFacing.NORTH, Block.func_208617_a(0.0D, 0.0D, 14.0D, 16.0D, 12.5D, 16.0D), EnumFacing.SOUTH, Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 12.5D, 2.0D), EnumFacing.WEST, Block.func_208617_a(14.0D, 0.0D, 0.0D, 16.0D, 12.5D, 16.0D), EnumFacing.EAST, Block.func_208617_a(0.0D, 0.0D, 0.0D, 2.0D, 12.5D, 16.0D)));
   }
}
