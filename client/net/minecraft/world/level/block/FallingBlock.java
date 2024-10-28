package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class FallingBlock extends Block implements Fallable {
   public FallingBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected abstract MapCodec<? extends FallingBlock> codec();

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      var2.scheduleTick(var3, this, this.getDelayAfterPlace());
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      var4.scheduleTick(var5, (Block)this, this.getDelayAfterPlace());
      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
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
      return var0.isAir() || var0.is(BlockTags.FIRE) || var0.liquid() || var0.canBeReplaced();
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(16) == 0) {
         BlockPos var5 = var3.below();
         if (isFree(var2.getBlockState(var5))) {
            ParticleUtils.spawnParticleBelow(var2, var3, var4, new BlockParticleOption(ParticleTypes.FALLING_DUST, var1));
         }
      }

   }

   public int getDustColor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return -16777216;
   }
}
