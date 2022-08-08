package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class TryLaySpawnOnWaterNearLand extends Behavior<Frog> {
   private final Block spawnBlock;
   private final MemoryModuleType<?> memoryModule;

   public TryLaySpawnOnWaterNearLand(Block var1, MemoryModuleType<?> var2) {
      super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.IS_PREGNANT, MemoryStatus.VALUE_PRESENT));
      this.spawnBlock = var1;
      this.memoryModule = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Frog var2) {
      return !var2.isInWater() && var2.isOnGround();
   }

   protected void start(ServerLevel var1, Frog var2, long var3) {
      BlockPos var5 = var2.blockPosition().below();
      Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

      while(var6.hasNext()) {
         Direction var7 = (Direction)var6.next();
         BlockPos var8 = var5.relative(var7);
         if (var1.getBlockState(var8).is(Blocks.WATER)) {
            BlockPos var9 = var8.above();
            if (var1.getBlockState(var9).isAir()) {
               var1.setBlock(var9, this.spawnBlock.defaultBlockState(), 3);
               var1.playSound((Player)null, var2, SoundEvents.FROG_LAY_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
               var2.getBrain().eraseMemory(this.memoryModule);
               return;
            }
         }
      }

   }
}
