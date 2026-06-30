package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DragonEggBlock extends FallingBlock {
   private static final VoxelShape SHAPE = Block.column(14.0, 0.0, 16.0);

   public DragonEggBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      this.teleport(state, level, pos);
      return InteractionResult.SUCCESS;
   }

   protected void attack(final BlockState state, final Level level, final BlockPos pos, final Player player) {
      this.teleport(state, level, pos);
   }

   private void teleport(final BlockState state, final Level level, final BlockPos pos) {
      WorldBorder worldBorder = level.getWorldBorder();
      RandomSource random = level.getRandom();

      for(int i = 0; i < 1000; ++i) {
         BlockPos testPos = pos.offset(random.nextInt(16) - random.nextInt(16), random.nextInt(8) - random.nextInt(8), random.nextInt(16) - random.nextInt(16));
         if (level.getBlockState(testPos).isAir() && !level.getBlockState(testPos.below()).isAir() && worldBorder.isWithinBounds(testPos) && level.isInsideBuildHeight(testPos)) {
            if (level.isClientSide()) {
               for(int j = 0; j < 128; ++j) {
                  double d = random.nextDouble();
                  float xa = (random.nextFloat() - 0.5F) * 0.2F;
                  float ya = (random.nextFloat() - 0.5F) * 0.2F;
                  float za = (random.nextFloat() - 0.5F) * 0.2F;
                  double x = Mth.lerp(d, (double)testPos.getX(), (double)pos.getX()) + (random.nextDouble() - 0.5) + 0.5;
                  double y = Mth.lerp(d, (double)testPos.getY(), (double)pos.getY()) + random.nextDouble() - 0.5;
                  double z = Mth.lerp(d, (double)testPos.getZ(), (double)pos.getZ()) + (random.nextDouble() - 0.5) + 0.5;
                  level.addParticle(ParticleTypes.PORTAL, x, y, z, (double)xa, (double)ya, (double)za);
               }
            } else {
               level.setBlock(testPos, state, 2);
               level.removeBlock(pos, false);
            }

            return;
         }
      }

   }

   protected int getDelayAfterPlace() {
      return 5;
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   public int getDustColor(final BlockState blockState, final BlockGetter level, final BlockPos pos) {
      return -16777216;
   }
}
