package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class RingBell {
   private static final float BELL_RING_CHANCE = 0.95F;
   public static final int RING_BELL_FROM_DISTANCE = 3;

   public RingBell() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((var0) -> var0.group(var0.present(MemoryModuleType.MEETING_POINT)).apply(var0, (var1) -> (var2, var3, var4) -> {
               if (var2.random.nextFloat() <= 0.95F) {
                  return false;
               } else {
                  BlockPos var6 = ((GlobalPos)var0.get(var1)).pos();
                  if (var6.closerThan(var3.blockPosition(), 3.0)) {
                     BlockState var7 = var2.getBlockState(var6);
                     if (var7.is(Blocks.BELL)) {
                        BellBlock var8 = (BellBlock)var7.getBlock();
                        var8.attemptToRing(var3, var2, var6, (Direction)null);
                     }
                  }

                  return true;
               }
            })));
   }
}
