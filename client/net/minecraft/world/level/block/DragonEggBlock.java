package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DragonEggBlock extends FallingBlock {
   protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public DragonEggBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      this.teleport(var1, var2, var3);
      return InteractionResult.sidedSuccess(var2.isClientSide);
   }

   public void attack(BlockState var1, Level var2, BlockPos var3, Player var4) {
      this.teleport(var1, var2, var3);
   }

   private void teleport(BlockState var1, Level var2, BlockPos var3) {
      for(int var4 = 0; var4 < 1000; ++var4) {
         BlockPos var5 = var3.offset(var2.random.nextInt(16) - var2.random.nextInt(16), var2.random.nextInt(8) - var2.random.nextInt(8), var2.random.nextInt(16) - var2.random.nextInt(16));
         if (var2.getBlockState(var5).isAir()) {
            if (var2.isClientSide) {
               for(int var6 = 0; var6 < 128; ++var6) {
                  double var7 = var2.random.nextDouble();
                  float var9 = (var2.random.nextFloat() - 0.5F) * 0.2F;
                  float var10 = (var2.random.nextFloat() - 0.5F) * 0.2F;
                  float var11 = (var2.random.nextFloat() - 0.5F) * 0.2F;
                  double var12 = Mth.lerp(var7, (double)var5.getX(), (double)var3.getX()) + (var2.random.nextDouble() - 0.5D) + 0.5D;
                  double var14 = Mth.lerp(var7, (double)var5.getY(), (double)var3.getY()) + var2.random.nextDouble() - 0.5D;
                  double var16 = Mth.lerp(var7, (double)var5.getZ(), (double)var3.getZ()) + (var2.random.nextDouble() - 0.5D) + 0.5D;
                  var2.addParticle(ParticleTypes.PORTAL, var12, var14, var16, (double)var9, (double)var10, (double)var11);
               }
            } else {
               var2.setBlock(var5, var1, 2);
               var2.removeBlock(var3, false);
            }

            return;
         }
      }

   }

   protected int getDelayAfterPlace() {
      return 5;
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }
}
