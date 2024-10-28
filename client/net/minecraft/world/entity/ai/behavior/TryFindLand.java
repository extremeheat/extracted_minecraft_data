package net.minecraft.world.entity.ai.behavior;

import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.apache.commons.lang3.mutable.MutableLong;

public class TryFindLand {
   private static final int COOLDOWN_TICKS = 60;

   public TryFindLand() {
      super();
   }

   public static BehaviorControl<PathfinderMob> create(int var0, float var1) {
      MutableLong var2 = new MutableLong(0L);
      return BehaviorBuilder.create((var3) -> {
         return var3.group(var3.absent(MemoryModuleType.ATTACK_TARGET), var3.absent(MemoryModuleType.WALK_TARGET), var3.registered(MemoryModuleType.LOOK_TARGET)).apply(var3, (var3x, var4, var5) -> {
            return (var5x, var6, var7) -> {
               if (!var5x.getFluidState(var6.blockPosition()).is(FluidTags.WATER)) {
                  return false;
               } else if (var7 < var2.getValue()) {
                  var2.setValue(var7 + 60L);
                  return true;
               } else {
                  BlockPos var9 = var6.blockPosition();
                  BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();
                  CollisionContext var11 = CollisionContext.of(var6);
                  Iterator var12 = BlockPos.withinManhattan(var9, var0, var0, var0).iterator();

                  while(var12.hasNext()) {
                     BlockPos var13 = (BlockPos)var12.next();
                     if (var13.getX() != var9.getX() || var13.getZ() != var9.getZ()) {
                        BlockState var14 = var5x.getBlockState(var13);
                        BlockState var15 = var5x.getBlockState(var10.setWithOffset(var13, (Direction)Direction.DOWN));
                        if (!var14.is(Blocks.WATER) && var5x.getFluidState(var13).isEmpty() && var14.getCollisionShape(var5x, var13, var11).isEmpty() && var15.isFaceSturdy(var5x, var10, Direction.UP)) {
                           BlockPos var16 = var13.immutable();
                           var5.set(new BlockPosTracker(var16));
                           var4.set(new WalkTarget(new BlockPosTracker(var16), var1, 1));
                           break;
                        }
                     }
                  }

                  var2.setValue(var7 + 60L);
                  return true;
               }
            };
         });
      });
   }
}
