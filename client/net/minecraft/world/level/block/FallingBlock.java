package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class FallingBlock extends Block implements Fallable {
   public FallingBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      var2.scheduleTick(var3, this, this.getDelayAfterPlace());
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      var4.scheduleTick(var5, this, this.getDelayAfterPlace());
      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (isFree(var2.getBlockState(var3.below())) && var3.getY() >= var2.getMinBuildHeight()) {
         FallingBlockEntity var5 = FallingBlockEntity.fall(var2, var3, var1);
         this.falling(var5);
      }
   }

   protected void falling(FallingBlockEntity var1) {
   }

   protected int getDelayAfterPlace() {
      return 2;
   }

   public static boolean isFree(BlockState var0) {
      Material var1 = var0.getMaterial();
      return var0.isAir() || var0.is(BlockTags.FIRE) || var1.isLiquid() || var1.isReplaceable();
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(16) == 0) {
         BlockPos var5 = var3.below();
         if (isFree(var2.getBlockState(var5))) {
            double var6 = (double)var3.getX() + var4.nextDouble();
            double var8 = (double)var3.getY() - 0.05;
            double var10 = (double)var3.getZ() + var4.nextDouble();
            var2.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, var1), var6, var8, var10, 0.0, 0.0, 0.0);
         }
      }
   }

   public int getDustColor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return -16777216;
   }
}
