package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

public class InteractWithDoor extends Behavior<LivingEntity> {
   private static final int COOLDOWN_BEFORE_RERUNNING_IN_SAME_NODE = 20;
   private static final double SKIP_CLOSING_DOOR_IF_FURTHER_AWAY_THAN = 2.0;
   private static final double MAX_DISTANCE_TO_HOLD_DOOR_OPEN_FOR_OTHER_MOBS = 2.0;
   @Nullable
   private Node lastCheckedNode;
   private int remainingCooldown;

   public InteractWithDoor() {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_PRESENT, MemoryModuleType.DOORS_TO_CLOSE, MemoryStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      Path var3 = (Path)var2.getBrain().getMemory(MemoryModuleType.PATH).get();
      if (!var3.notStarted() && !var3.isDone()) {
         if (!Objects.equals(this.lastCheckedNode, var3.getNextNode())) {
            this.remainingCooldown = 20;
            return true;
         } else {
            if (this.remainingCooldown > 0) {
               --this.remainingCooldown;
            }

            return this.remainingCooldown == 0;
         }
      } else {
         return false;
      }
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Path var5 = (Path)var2.getBrain().getMemory(MemoryModuleType.PATH).get();
      this.lastCheckedNode = var5.getNextNode();
      Node var6 = var5.getPreviousNode();
      Node var7 = var5.getNextNode();
      BlockPos var8 = var6.asBlockPos();
      BlockState var9 = var1.getBlockState(var8);
      if (var9.is(BlockTags.WOODEN_DOORS, (var0) -> {
         return var0.getBlock() instanceof DoorBlock;
      })) {
         DoorBlock var10 = (DoorBlock)var9.getBlock();
         if (!var10.isOpen(var9)) {
            var10.setOpen(var2, var1, var9, var8, true);
         }

         this.rememberDoorToClose(var1, var2, var8);
      }

      BlockPos var13 = var7.asBlockPos();
      BlockState var11 = var1.getBlockState(var13);
      if (var11.is(BlockTags.WOODEN_DOORS, (var0) -> {
         return var0.getBlock() instanceof DoorBlock;
      })) {
         DoorBlock var12 = (DoorBlock)var11.getBlock();
         if (!var12.isOpen(var11)) {
            var12.setOpen(var2, var1, var11, var13, true);
            this.rememberDoorToClose(var1, var2, var13);
         }
      }

      closeDoorsThatIHaveOpenedOrPassedThrough(var1, var2, var6, var7);
   }

   public static void closeDoorsThatIHaveOpenedOrPassedThrough(ServerLevel var0, LivingEntity var1, @Nullable Node var2, @Nullable Node var3) {
      Brain var4 = var1.getBrain();
      if (var4.hasMemoryValue(MemoryModuleType.DOORS_TO_CLOSE)) {
         Iterator var5 = ((Set)var4.getMemory(MemoryModuleType.DOORS_TO_CLOSE).get()).iterator();

         while(true) {
            GlobalPos var6;
            BlockPos var7;
            do {
               do {
                  if (!var5.hasNext()) {
                     return;
                  }

                  var6 = (GlobalPos)var5.next();
                  var7 = var6.pos();
               } while(var2 != null && var2.asBlockPos().equals(var7));
            } while(var3 != null && var3.asBlockPos().equals(var7));

            if (isDoorTooFarAway(var0, var1, var6)) {
               var5.remove();
            } else {
               BlockState var8 = var0.getBlockState(var7);
               if (!var8.is(BlockTags.WOODEN_DOORS, (var0x) -> {
                  return var0x.getBlock() instanceof DoorBlock;
               })) {
                  var5.remove();
               } else {
                  DoorBlock var9 = (DoorBlock)var8.getBlock();
                  if (!var9.isOpen(var8)) {
                     var5.remove();
                  } else if (areOtherMobsComingThroughDoor(var0, var1, var7)) {
                     var5.remove();
                  } else {
                     var9.setOpen(var1, var0, var8, var7, false);
                     var5.remove();
                  }
               }
            }
         }
      }
   }

   private static boolean areOtherMobsComingThroughDoor(ServerLevel var0, LivingEntity var1, BlockPos var2) {
      Brain var3 = var1.getBrain();
      return !var3.hasMemoryValue(MemoryModuleType.NEAREST_LIVING_ENTITIES) ? false : ((List)var3.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).get()).stream().filter((var1x) -> {
         return var1x.getType() == var1.getType();
      }).filter((var1x) -> {
         return var2.closerToCenterThan(var1x.position(), 2.0);
      }).anyMatch((var2x) -> {
         return isMobComingThroughDoor(var0, var2x, var2);
      });
   }

   private static boolean isMobComingThroughDoor(ServerLevel var0, LivingEntity var1, BlockPos var2) {
      if (!var1.getBrain().hasMemoryValue(MemoryModuleType.PATH)) {
         return false;
      } else {
         Path var3 = (Path)var1.getBrain().getMemory(MemoryModuleType.PATH).get();
         if (var3.isDone()) {
            return false;
         } else {
            Node var4 = var3.getPreviousNode();
            if (var4 == null) {
               return false;
            } else {
               Node var5 = var3.getNextNode();
               return var2.equals(var4.asBlockPos()) || var2.equals(var5.asBlockPos());
            }
         }
      }
   }

   private static boolean isDoorTooFarAway(ServerLevel var0, LivingEntity var1, GlobalPos var2) {
      return var2.dimension() != var0.dimension() || !var2.pos().closerToCenterThan(var1.position(), 2.0);
   }

   private void rememberDoorToClose(ServerLevel var1, LivingEntity var2, BlockPos var3) {
      Brain var4 = var2.getBrain();
      GlobalPos var5 = GlobalPos.of(var1.dimension(), var3);
      if (var4.getMemory(MemoryModuleType.DOORS_TO_CLOSE).isPresent()) {
         ((Set)var4.getMemory(MemoryModuleType.DOORS_TO_CLOSE).get()).add(var5);
      } else {
         var4.setMemory(MemoryModuleType.DOORS_TO_CLOSE, (Object)Sets.newHashSet(new GlobalPos[]{var5}));
      }

   }
}
