package net.minecraft.world.entity.ai.behavior;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class TryLaySpawnOnWaterNearLand {
   public TryLaySpawnOnWaterNearLand() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(Block var0) {
      return BehaviorBuilder.create((var1) -> {
         return var1.group(var1.absent(MemoryModuleType.ATTACK_TARGET), var1.present(MemoryModuleType.WALK_TARGET), var1.present(MemoryModuleType.IS_PREGNANT)).apply(var1, (var1x, var2, var3) -> {
            return (var2x, var3x, var4) -> {
               if (!var3x.isInWater() && var3x.onGround()) {
                  BlockPos var6 = var3x.blockPosition().below();
                  Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

                  while(var7.hasNext()) {
                     Direction var8 = (Direction)var7.next();
                     BlockPos var9 = var6.relative(var8);
                     if (var2x.getBlockState(var9).getCollisionShape(var2x, var9).getFaceShape(Direction.UP).isEmpty() && var2x.getFluidState(var9).is((Fluid)Fluids.WATER)) {
                        BlockPos var10 = var9.above();
                        if (var2x.getBlockState(var10).isAir()) {
                           BlockState var11 = var0.defaultBlockState();
                           var2x.setBlock(var10, var11, 3);
                           var2x.gameEvent(GameEvent.BLOCK_PLACE, var10, GameEvent.Context.of(var3x, var11));
                           var2x.playSound((Player)null, var3x, SoundEvents.FROG_LAY_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                           var3.erase();
                           return true;
                        }
                     }
                  }

                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }
}
