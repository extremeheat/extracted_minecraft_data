package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SporeBlossomBlock extends Block {
   public static final MapCodec<SporeBlossomBlock> CODEC = simpleCodec(SporeBlossomBlock::new);
   private static final VoxelShape SHAPE = Block.box(2.0, 13.0, 2.0, 14.0, 16.0, 14.0);
   private static final int ADD_PARTICLE_ATTEMPTS = 14;
   private static final int PARTICLE_XZ_RADIUS = 10;
   private static final int PARTICLE_Y_MAX = 10;

   public MapCodec<SporeBlossomBlock> codec() {
      return CODEC;
   }

   public SporeBlossomBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return Block.canSupportCenter(var2, var3.above(), Direction.DOWN) && !var2.isWaterAt(var3);
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == Direction.UP && !this.canSurvive(var1, var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      int var5 = var3.getX();
      int var6 = var3.getY();
      int var7 = var3.getZ();
      double var8 = (double)var5 + var4.nextDouble();
      double var10 = (double)var6 + 0.7;
      double var12 = (double)var7 + var4.nextDouble();
      var2.addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, var8, var10, var12, 0.0, 0.0, 0.0);
      BlockPos.MutableBlockPos var14 = new BlockPos.MutableBlockPos();

      for(int var15 = 0; var15 < 14; ++var15) {
         var14.set(var5 + Mth.nextInt(var4, -10, 10), var6 - var4.nextInt(10), var7 + Mth.nextInt(var4, -10, 10));
         BlockState var16 = var2.getBlockState(var14);
         if (!var16.isCollisionShapeFullBlock(var2, var14)) {
            var2.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, (double)var14.getX() + var4.nextDouble(), (double)var14.getY() + var4.nextDouble(), (double)var14.getZ() + var4.nextDouble(), 0.0, 0.0, 0.0);
         }
      }

   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }
}
