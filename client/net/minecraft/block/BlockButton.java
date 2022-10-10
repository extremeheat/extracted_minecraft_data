package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockButton extends BlockHorizontalFace {
   public static final BooleanProperty field_176584_b;
   protected static final VoxelShape field_196370_b;
   protected static final VoxelShape field_196371_c;
   protected static final VoxelShape field_196376_y;
   protected static final VoxelShape field_196377_z;
   protected static final VoxelShape field_185622_d;
   protected static final VoxelShape field_185624_e;
   protected static final VoxelShape field_185626_f;
   protected static final VoxelShape field_185628_g;
   protected static final VoxelShape field_196372_E;
   protected static final VoxelShape field_196373_F;
   protected static final VoxelShape field_196374_G;
   protected static final VoxelShape field_196375_H;
   protected static final VoxelShape field_185623_D;
   protected static final VoxelShape field_185625_E;
   protected static final VoxelShape field_185627_F;
   protected static final VoxelShape field_185629_G;
   private final boolean field_150047_a;

   protected BlockButton(boolean var1, Block.Properties var2) {
      super(var2);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_185512_D, EnumFacing.NORTH)).func_206870_a(field_176584_b, false)).func_206870_a(field_196366_M, AttachFace.WALL));
      this.field_150047_a = var1;
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return this.field_150047_a ? 30 : 20;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      EnumFacing var4 = (EnumFacing)var1.func_177229_b(field_185512_D);
      boolean var5 = (Boolean)var1.func_177229_b(field_176584_b);
      switch((AttachFace)var1.func_177229_b(field_196366_M)) {
      case FLOOR:
         if (var4.func_176740_k() == EnumFacing.Axis.X) {
            return var5 ? field_196374_G : field_196376_y;
         }

         return var5 ? field_196375_H : field_196377_z;
      case WALL:
         switch(var4) {
         case EAST:
            return var5 ? field_185629_G : field_185628_g;
         case WEST:
            return var5 ? field_185627_F : field_185626_f;
         case SOUTH:
            return var5 ? field_185625_E : field_185624_e;
         case NORTH:
         default:
            return var5 ? field_185623_D : field_185622_d;
         }
      case CEILING:
      default:
         if (var4.func_176740_k() == EnumFacing.Axis.X) {
            return var5 ? field_196372_E : field_196370_b;
         } else {
            return var5 ? field_196373_F : field_196371_c;
         }
      }
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if ((Boolean)var1.func_177229_b(field_176584_b)) {
         return true;
      } else {
         var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176584_b, true), 3);
         this.func_196367_a(var4, var2, var3, true);
         this.func_196368_e(var1, var2, var3);
         var2.func_205220_G_().func_205360_a(var3, this, this.func_149738_a(var2));
         return true;
      }
   }

   protected void func_196367_a(@Nullable EntityPlayer var1, IWorld var2, BlockPos var3, boolean var4) {
      var2.func_184133_a(var4 ? var1 : null, var3, this.func_196369_b(var4), SoundCategory.BLOCKS, 0.3F, var4 ? 0.6F : 0.5F);
   }

   protected abstract SoundEvent func_196369_b(boolean var1);

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (!var5 && var1.func_177230_c() != var4.func_177230_c()) {
         if ((Boolean)var1.func_177229_b(field_176584_b)) {
            this.func_196368_e(var1, var2, var3);
         }

         super.func_196243_a(var1, var2, var3, var4, var5);
      }
   }

   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return (Boolean)var1.func_177229_b(field_176584_b) ? 15 : 0;
   }

   public int func_176211_b(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return (Boolean)var1.func_177229_b(field_176584_b) && func_196365_i(var1) == var4 ? 15 : 0;
   }

   public boolean func_149744_f(IBlockState var1) {
      return true;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var2.field_72995_K && (Boolean)var1.func_177229_b(field_176584_b)) {
         if (this.field_150047_a) {
            this.func_185616_e(var1, var2, var3);
         } else {
            var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176584_b, false), 3);
            this.func_196368_e(var1, var2, var3);
            this.func_196367_a((EntityPlayer)null, var2, var3, false);
         }

      }
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      if (!var2.field_72995_K && this.field_150047_a && !(Boolean)var1.func_177229_b(field_176584_b)) {
         this.func_185616_e(var1, var2, var3);
      }
   }

   private void func_185616_e(IBlockState var1, World var2, BlockPos var3) {
      List var4 = var2.func_72872_a(EntityArrow.class, var1.func_196954_c(var2, var3).func_197752_a().func_186670_a(var3));
      boolean var5 = !var4.isEmpty();
      boolean var6 = (Boolean)var1.func_177229_b(field_176584_b);
      if (var5 != var6) {
         var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176584_b, var5), 3);
         this.func_196368_e(var1, var2, var3);
         this.func_196367_a((EntityPlayer)null, var2, var3, var5);
      }

      if (var5) {
         var2.func_205220_G_().func_205360_a(new BlockPos(var3), this, this.func_149738_a(var2));
      }

   }

   private void func_196368_e(IBlockState var1, World var2, BlockPos var3) {
      var2.func_195593_d(var3, this);
      var2.func_195593_d(var3.func_177972_a(func_196365_i(var1).func_176734_d()), this);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_185512_D, field_176584_b, field_196366_M);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   static {
      field_176584_b = BlockStateProperties.field_208194_u;
      field_196370_b = Block.func_208617_a(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
      field_196371_c = Block.func_208617_a(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
      field_196376_y = Block.func_208617_a(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
      field_196377_z = Block.func_208617_a(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
      field_185622_d = Block.func_208617_a(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
      field_185624_e = Block.func_208617_a(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
      field_185626_f = Block.func_208617_a(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
      field_185628_g = Block.func_208617_a(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
      field_196372_E = Block.func_208617_a(6.0D, 15.0D, 5.0D, 10.0D, 16.0D, 11.0D);
      field_196373_F = Block.func_208617_a(5.0D, 15.0D, 6.0D, 11.0D, 16.0D, 10.0D);
      field_196374_G = Block.func_208617_a(6.0D, 0.0D, 5.0D, 10.0D, 1.0D, 11.0D);
      field_196375_H = Block.func_208617_a(5.0D, 0.0D, 6.0D, 11.0D, 1.0D, 10.0D);
      field_185623_D = Block.func_208617_a(5.0D, 6.0D, 15.0D, 11.0D, 10.0D, 16.0D);
      field_185625_E = Block.func_208617_a(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 1.0D);
      field_185627_F = Block.func_208617_a(15.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
      field_185629_G = Block.func_208617_a(0.0D, 6.0D, 5.0D, 1.0D, 10.0D, 11.0D);
   }
}
