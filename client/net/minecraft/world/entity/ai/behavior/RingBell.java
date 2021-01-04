package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RingBell extends Behavior<LivingEntity> {
   public RingBell() {
      super(ImmutableMap.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      return var1.random.nextFloat() > 0.95F;
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Brain var5 = var2.getBrain();
      BlockPos var6 = ((GlobalPos)var5.getMemory(MemoryModuleType.MEETING_POINT).get()).pos();
      if (var6.closerThan(new BlockPos(var2), 3.0D)) {
         BlockState var7 = var1.getBlockState(var6);
         if (var7.getBlock() == Blocks.BELL) {
            BellBlock var8 = (BellBlock)var7.getBlock();
            Iterator var9 = Direction.Plane.HORIZONTAL.iterator();

            while(var9.hasNext()) {
               Direction var10 = (Direction)var9.next();
               if (var8.onHit(var1, var7, var1.getBlockEntity(var6), new BlockHitResult(new Vec3(0.5D, 0.5D, 0.5D), var10, var6, false), (Player)null, false)) {
                  break;
               }
            }
         }
      }

   }
}
