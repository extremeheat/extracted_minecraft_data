package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableLong;

public class TryFindWater {
   public TryFindWater() {
      super();
   }

   public static BehaviorControl<PathfinderMob> create(int var0, float var1) {
      MutableLong var2 = new MutableLong(0L);
      return BehaviorBuilder.create(
         var3 -> var3.group(
                  var3.absent(MemoryModuleType.ATTACK_TARGET), var3.absent(MemoryModuleType.WALK_TARGET), var3.registered(MemoryModuleType.LOOK_TARGET)
               )
               .apply(var3, (var3x, var4, var5) -> (var5x, var6, var7) -> {
                     if (var5x.getFluidState(var6.blockPosition()).is(FluidTags.WATER)) {
                        return false;
                     } else if (var7 < var2.getValue()) {
                        var2.setValue(var7 + 20L + 2L);
                        return true;
                     } else {
                        BlockPos var9 = null;
                        BlockPos var10 = null;
                        BlockPos var11 = var6.blockPosition();
      
                        for(BlockPos var14 : BlockPos.withinManhattan(var11, var0, var0, var0)) {
                           if (var14.getX() != var11.getX() || var14.getZ() != var11.getZ()) {
                              BlockState var15 = var6.level().getBlockState(var14.above());
                              BlockState var16 = var6.level().getBlockState(var14);
                              if (var16.is(Blocks.WATER)) {
                                 if (var15.isAir()) {
                                    var9 = var14.immutable();
                                    break;
                                 }
      
                                 if (var10 == null && !var14.closerToCenterThan(var6.position(), 1.5)) {
                                    var10 = var14.immutable();
                                 }
                              }
                           }
                        }
      
                        if (var9 == null) {
                           var9 = var10;
                        }
      
                        if (var9 != null) {
                           var5.set(new BlockPosTracker(var9));
                           var4.set(new WalkTarget(new BlockPosTracker(var9), var1, 0));
                        }
      
                        var2.setValue(var7 + 40L);
                        return true;
                     }
                  })
      );
   }
}