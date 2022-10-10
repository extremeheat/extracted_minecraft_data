package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
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

public class BlockRedstoneTorchWall extends BlockRedstoneTorch {
   public static final DirectionProperty field_196530_b;
   public static final BooleanProperty field_196531_c;

   protected BlockRedstoneTorchWall(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196530_b, EnumFacing.NORTH)).func_206870_a(field_196531_c, true));
   }

   public String func_149739_a() {
      return this.func_199767_j().func_77658_a();
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return Blocks.field_196591_bQ.func_196244_b(var1, var2, var3);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      return Blocks.field_196591_bQ.func_196260_a(var1, var2, var3);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return Blocks.field_196591_bQ.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = Blocks.field_196591_bQ.func_196258_a(var1);
      return var2 == null ? null : (IBlockState)this.func_176223_P().func_206870_a(field_196530_b, var2.func_177229_b(field_196530_b));
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.func_177229_b(field_196531_c)) {
         EnumFacing var5 = ((EnumFacing)var1.func_177229_b(field_196530_b)).func_176734_d();
         double var6 = 0.27D;
         double var8 = (double)var3.func_177958_n() + 0.5D + (var4.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)var5.func_82601_c();
         double var10 = (double)var3.func_177956_o() + 0.7D + (var4.nextDouble() - 0.5D) * 0.2D + 0.22D;
         double var12 = (double)var3.func_177952_p() + 0.5D + (var4.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)var5.func_82599_e();
         var2.func_195594_a(RedstoneParticleData.field_197564_a, var8, var10, var12, 0.0D, 0.0D, 0.0D);
      }
   }

   protected boolean func_176597_g(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = ((EnumFacing)var3.func_177229_b(field_196530_b)).func_176734_d();
      return var1.func_175709_b(var2.func_177972_a(var4), var4);
   }

   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return (Boolean)var1.func_177229_b(field_196531_c) && var1.func_177229_b(field_196530_b) != var4 ? 15 : 0;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return Blocks.field_196591_bQ.func_185499_a(var1, var2);
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return Blocks.field_196591_bQ.func_185471_a(var1, var2);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196530_b, field_196531_c);
   }

   static {
      field_196530_b = BlockHorizontal.field_185512_D;
      field_196531_c = BlockRedstoneTorch.field_196528_a;
   }
}
