package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SmokerBlock extends AbstractFurnaceBlock {
   protected SmokerBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new SmokerBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createFurnaceTicker(var1, var3, BlockEntityType.SMOKER);
   }

   @Override
   protected void openContainer(Level var1, BlockPos var2, Player var3) {
      BlockEntity var4 = var1.getBlockEntity(var2);
      if (var4 instanceof SmokerBlockEntity) {
         var3.openMenu((MenuProvider)var4);
         var3.awardStat(Stats.INTERACT_WITH_SMOKER);
      }
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var1.getValue(LIT)) {
         double var5 = (double)var3.getX() + 0.5;
         double var7 = (double)var3.getY();
         double var9 = (double)var3.getZ() + 0.5;
         if (var4.nextDouble() < 0.1) {
            var2.playLocalSound(var5, var7, var9, SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         var2.addParticle(ParticleTypes.SMOKE, var5, var7 + 1.1, var9, 0.0, 0.0, 0.0);
      }
   }
}
