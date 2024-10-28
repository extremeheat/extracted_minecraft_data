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
import net.minecraft.world.phys.shapes.CollisionContext;
import org.apache.commons.lang3.mutable.MutableLong;

public class TryFindLandNearWater {
   public TryFindLandNearWater() {
      super();
   }

   public static BehaviorControl<PathfinderMob> create(int var0, float var1) {
      MutableLong var2 = new MutableLong(0L);
      return BehaviorBuilder.create((var3) -> {
         return var3.group(var3.absent(MemoryModuleType.ATTACK_TARGET), var3.absent(MemoryModuleType.WALK_TARGET), var3.registered(MemoryModuleType.LOOK_TARGET)).apply(var3, (var3x, var4, var5) -> {
            return (var5x, var6, var7) -> {
               if (var5x.getFluidState(var6.blockPosition()).is(FluidTags.WATER)) {
                  return false;
               } else if (var7 < var2.getValue()) {
                  var2.setValue(var7 + 40L);
                  return true;
               } else {
                  CollisionContext var9 = CollisionContext.of(var6);
                  BlockPos var10 = var6.blockPosition();
                  BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
                  Iterator var12 = BlockPos.withinManhattan(var10, var0, var0, var0).iterator();

                  label45:
                  while(var12.hasNext()) {
                     BlockPos var13 = (BlockPos)var12.next();
                     if ((var13.getX() != var10.getX() || var13.getZ() != var10.getZ()) && var5x.getBlockState(var13).getCollisionShape(var5x, var13, var9).isEmpty() && !var5x.getBlockState(var11.setWithOffset(var13, (Direction)Direction.DOWN)).getCollisionShape(var5x, var13, var9).isEmpty()) {
                        Iterator var14 = Direction.Plane.HORIZONTAL.iterator();

                        while(var14.hasNext()) {
                           Direction var15 = (Direction)var14.next();
                           var11.setWithOffset(var13, (Direction)var15);
                           if (var5x.getBlockState(var11).isAir() && var5x.getBlockState(var11.move(Direction.DOWN)).is(Blocks.WATER)) {
                              var5.set(new BlockPosTracker(var13));
                              var4.set(new WalkTarget(new BlockPosTracker(var13), var1, 0));
                              break label45;
                           }
                        }
                     }
                  }

                  var2.setValue(var7 + 40L);
                  return true;
               }
            };
         });
      });
   }
}
