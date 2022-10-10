package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockLever extends BlockHorizontalFace {
   public static final BooleanProperty field_176359_b;
   protected static final VoxelShape field_185692_c;
   protected static final VoxelShape field_185693_d;
   protected static final VoxelShape field_185694_e;
   protected static final VoxelShape field_185695_f;
   protected static final VoxelShape field_209348_r;
   protected static final VoxelShape field_209349_s;
   protected static final VoxelShape field_209350_t;
   protected static final VoxelShape field_209351_u;

   protected BlockLever(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_185512_D, EnumFacing.NORTH)).func_206870_a(field_176359_b, false)).func_206870_a(field_196366_M, AttachFace.WALL));
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      switch((AttachFace)var1.func_177229_b(field_196366_M)) {
      case FLOOR:
         switch(((EnumFacing)var1.func_177229_b(field_185512_D)).func_176740_k()) {
         case X:
            return field_209349_s;
         case Z:
         default:
            return field_209348_r;
         }
      case WALL:
         switch((EnumFacing)var1.func_177229_b(field_185512_D)) {
         case EAST:
            return field_185695_f;
         case WEST:
            return field_185694_e;
         case SOUTH:
            return field_185693_d;
         case NORTH:
         default:
            return field_185692_c;
         }
      case CEILING:
      default:
         switch(((EnumFacing)var1.func_177229_b(field_185512_D)).func_176740_k()) {
         case X:
            return field_209351_u;
         case Z:
         default:
            return field_209350_t;
         }
      }
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      var1 = (IBlockState)var1.func_177231_a(field_176359_b);
      boolean var10 = (Boolean)var1.func_177229_b(field_176359_b);
      if (var2.field_72995_K) {
         if (var10) {
            func_196379_a(var1, var2, var3, 1.0F);
         }

         return true;
      } else {
         var2.func_180501_a(var3, var1, 3);
         float var11 = var10 ? 0.6F : 0.5F;
         var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_187750_dc, SoundCategory.BLOCKS, 0.3F, var11);
         this.func_196378_d(var1, var2, var3);
         return true;
      }
   }

   private static void func_196379_a(IBlockState var0, IWorld var1, BlockPos var2, float var3) {
      EnumFacing var4 = ((EnumFacing)var0.func_177229_b(field_185512_D)).func_176734_d();
      EnumFacing var5 = func_196365_i(var0).func_176734_d();
      double var6 = (double)var2.func_177958_n() + 0.5D + 0.1D * (double)var4.func_82601_c() + 0.2D * (double)var5.func_82601_c();
      double var8 = (double)var2.func_177956_o() + 0.5D + 0.1D * (double)var4.func_96559_d() + 0.2D * (double)var5.func_96559_d();
      double var10 = (double)var2.func_177952_p() + 0.5D + 0.1D * (double)var4.func_82599_e() + 0.2D * (double)var5.func_82599_e();
      var1.func_195594_a(new RedstoneParticleData(1.0F, 0.0F, 0.0F, var3), var6, var8, var10, 0.0D, 0.0D, 0.0D);
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.func_177229_b(field_176359_b) && var4.nextFloat() < 0.25F) {
         func_196379_a(var1, var2, var3, 0.5F);
      }

   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (!var5 && var1.func_177230_c() != var4.func_177230_c()) {
         if ((Boolean)var1.func_177229_b(field_176359_b)) {
            this.func_196378_d(var1, var2, var3);
         }

         super.func_196243_a(var1, var2, var3, var4, var5);
      }
   }

   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return (Boolean)var1.func_177229_b(field_176359_b) ? 15 : 0;
   }

   public int func_176211_b(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return (Boolean)var1.func_177229_b(field_176359_b) && func_196365_i(var1) == var4 ? 15 : 0;
   }

   public boolean func_149744_f(IBlockState var1) {
      return true;
   }

   private void func_196378_d(IBlockState var1, World var2, BlockPos var3) {
      var2.func_195593_d(var3, this);
      var2.func_195593_d(var3.func_177972_a(func_196365_i(var1).func_176734_d()), this);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196366_M, field_185512_D, field_176359_b);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   static {
      field_176359_b = BlockStateProperties.field_208194_u;
      field_185692_c = Block.func_208617_a(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
      field_185693_d = Block.func_208617_a(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
      field_185694_e = Block.func_208617_a(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
      field_185695_f = Block.func_208617_a(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
      field_209348_r = Block.func_208617_a(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
      field_209349_s = Block.func_208617_a(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
      field_209350_t = Block.func_208617_a(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
      field_209351_u = Block.func_208617_a(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);
   }
}
