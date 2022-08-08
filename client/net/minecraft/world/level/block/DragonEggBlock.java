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
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DragonEggBlock extends FallingBlock {
   protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

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
      WorldBorder var4 = var2.getWorldBorder();

      for(int var5 = 0; var5 < 1000; ++var5) {
         BlockPos var6 = var3.offset(var2.random.nextInt(16) - var2.random.nextInt(16), var2.random.nextInt(8) - var2.random.nextInt(8), var2.random.nextInt(16) - var2.random.nextInt(16));
         if (var2.getBlockState(var6).isAir() && var4.isWithinBounds(var6)) {
            if (var2.isClientSide) {
               for(int var7 = 0; var7 < 128; ++var7) {
                  double var8 = var2.random.nextDouble();
                  float var10 = (var2.random.nextFloat() - 0.5F) * 0.2F;
                  float var11 = (var2.random.nextFloat() - 0.5F) * 0.2F;
                  float var12 = (var2.random.nextFloat() - 0.5F) * 0.2F;
                  double var13 = Mth.lerp(var8, (double)var6.getX(), (double)var3.getX()) + (var2.random.nextDouble() - 0.5) + 0.5;
                  double var15 = Mth.lerp(var8, (double)var6.getY(), (double)var3.getY()) + var2.random.nextDouble() - 0.5;
                  double var17 = Mth.lerp(var8, (double)var6.getZ(), (double)var3.getZ()) + (var2.random.nextDouble() - 0.5) + 0.5;
                  var2.addParticle(ParticleTypes.PORTAL, var13, var15, var17, (double)var10, (double)var11, (double)var12);
               }
            } else {
               var2.setBlock(var6, var1, 2);
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
