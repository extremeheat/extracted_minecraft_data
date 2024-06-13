package net.minecraft.world.effect;

import java.util.ArrayList;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

class WeavingMobEffect extends MobEffect {
   private final ToIntFunction<RandomSource> maxCobwebs;

   protected WeavingMobEffect(MobEffectCategory var1, int var2, ToIntFunction<RandomSource> var3) {
      super(var1, var2, ParticleTypes.ITEM_COBWEB);
      this.maxCobwebs = var3;
   }

   @Override
   public void onMobRemoved(LivingEntity var1, int var2, Entity.RemovalReason var3) {
      if (var3 == Entity.RemovalReason.KILLED) {
         this.spawnCobwebsRandomlyAround(var1.level(), var1.getRandom(), var1.getOnPos());
      }
   }

   private void spawnCobwebsRandomlyAround(Level var1, RandomSource var2, BlockPos var3) {
      ArrayList var4 = new ArrayList();
      int var5 = this.maxCobwebs.applyAsInt(var2);

      for (BlockPos var7 : BlockPos.randomInCube(var2, 10, var3, 3)) {
         BlockPos var8 = var7.below();
         if (var1.getBlockState(var7).isAir() && var1.getBlockState(var8).isFaceSturdy(var1, var8, Direction.UP)) {
            var4.add(var7.immutable());
            if (var4.size() >= var5) {
               break;
            }
         }
      }

      for (BlockPos var10 : var4) {
         var1.setBlock(var10, Blocks.COBWEB.defaultBlockState(), 3);
         var1.levelEvent(3018, var10, 0);
      }
   }
}
