package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.OptionalBox;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

public class InteractWithDoor {
   private static final int COOLDOWN_BEFORE_RERUNNING_IN_SAME_NODE = 20;
   private static final double SKIP_CLOSING_DOOR_IF_FURTHER_AWAY_THAN = 3.0;
   private static final double MAX_DISTANCE_TO_HOLD_DOOR_OPEN_FOR_OTHER_MOBS = 2.0;

   public InteractWithDoor() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      MutableObject var0 = new MutableObject((Object)null);
      MutableInt var1 = new MutableInt(0);
      return BehaviorBuilder.create((Function)((var2) -> var2.group(var2.present(MemoryModuleType.PATH), var2.registered(MemoryModuleType.DOORS_TO_CLOSE), var2.registered(MemoryModuleType.NEAREST_LIVING_ENTITIES)).apply(var2, (var3, var4, var5) -> (var6, var7, var8) -> {
               Path var10 = (Path)var2.get(var3);
               Optional var11 = var2.tryGet(var4);
               if (!var10.notStarted() && !var10.isDone()) {
                  if (Objects.equals(var0.getValue(), var10.getNextNode())) {
                     var1.setValue(20);
                  } else if (var1.decrementAndGet() > 0) {
                     return false;
                  }

                  var0.setValue(var10.getNextNode());
                  Node var12 = var10.getPreviousNode();
                  Node var13 = var10.getNextNode();
                  BlockPos var14 = var12.asBlockPos();
                  BlockState var15 = var6.getBlockState(var14);
                  if (var15.is(BlockTags.MOB_INTERACTABLE_DOORS, (var0x) -> var0x.getBlock() instanceof DoorBlock)) {
                     DoorBlock var16 = (DoorBlock)var15.getBlock();
                     if (!var16.isOpen(var15)) {
                        var16.setOpen(var7, var6, var15, var14, true);
                     }

                     var11 = rememberDoorToClose(var4, var11, var6, var14);
                  }

                  BlockPos var19 = var13.asBlockPos();
                  BlockState var17 = var6.getBlockState(var19);
                  if (var17.is(BlockTags.MOB_INTERACTABLE_DOORS, (var0x) -> var0x.getBlock() instanceof DoorBlock)) {
                     DoorBlock var18 = (DoorBlock)var17.getBlock();
                     if (!var18.isOpen(var17)) {
                        var18.setOpen(var7, var6, var17, var19, true);
                        var11 = rememberDoorToClose(var4, var11, var6, var19);
                     }
                  }

                  var11.ifPresent((var6x) -> closeDoorsThatIHaveOpenedOrPassedThrough(var6, var7, var12, var13, var6x, var2.tryGet(var5)));
                  return true;
               } else {
                  return false;
               }
            })));
   }

   public static void closeDoorsThatIHaveOpenedOrPassedThrough(ServerLevel var0, LivingEntity var1, @Nullable Node var2, @Nullable Node var3, Set<GlobalPos> var4, Optional<List<LivingEntity>> var5) {
      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         GlobalPos var7 = (GlobalPos)var6.next();
         BlockPos var8 = var7.pos();
         if ((var2 == null || !var2.asBlockPos().equals(var8)) && (var3 == null || !var3.asBlockPos().equals(var8))) {
            if (isDoorTooFarAway(var0, var1, var7)) {
               var6.remove();
            } else {
               BlockState var9 = var0.getBlockState(var8);
               if (!var9.is(BlockTags.MOB_INTERACTABLE_DOORS, (var0x) -> var0x.getBlock() instanceof DoorBlock)) {
                  var6.remove();
               } else {
                  DoorBlock var10 = (DoorBlock)var9.getBlock();
                  if (!var10.isOpen(var9)) {
                     var6.remove();
                  } else if (areOtherMobsComingThroughDoor(var1, var8, var5)) {
                     var6.remove();
                  } else {
                     var10.setOpen(var1, var0, var9, var8, false);
                     var6.remove();
                  }
               }
            }
         }
      }

   }

   private static boolean areOtherMobsComingThroughDoor(LivingEntity var0, BlockPos var1, Optional<List<LivingEntity>> var2) {
      return var2.isEmpty() ? false : ((List)var2.get()).stream().filter((var1x) -> var1x.getType() == var0.getType()).filter((var1x) -> var1.closerToCenterThan(var1x.position(), 2.0)).anyMatch((var1x) -> isMobComingThroughDoor(var1x.getBrain(), var1));
   }

   private static boolean isMobComingThroughDoor(Brain<?> var0, BlockPos var1) {
      if (!var0.hasMemoryValue(MemoryModuleType.PATH)) {
         return false;
      } else {
         Path var2 = (Path)var0.getMemory(MemoryModuleType.PATH).get();
         if (var2.isDone()) {
            return false;
         } else {
            Node var3 = var2.getPreviousNode();
            if (var3 == null) {
               return false;
            } else {
               Node var4 = var2.getNextNode();
               return var1.equals(var3.asBlockPos()) || var1.equals(var4.asBlockPos());
            }
         }
      }
   }

   private static boolean isDoorTooFarAway(ServerLevel var0, LivingEntity var1, GlobalPos var2) {
      return var2.dimension() != var0.dimension() || !var2.pos().closerToCenterThan(var1.position(), 3.0);
   }

   private static Optional<Set<GlobalPos>> rememberDoorToClose(MemoryAccessor<OptionalBox.Mu, Set<GlobalPos>> var0, Optional<Set<GlobalPos>> var1, ServerLevel var2, BlockPos var3) {
      GlobalPos var4 = GlobalPos.of(var2.dimension(), var3);
      return Optional.of((Set)var1.map((var1x) -> {
         var1x.add(var4);
         return var1x;
      }).orElseGet(() -> {
         HashSet var2 = Sets.newHashSet(new GlobalPos[]{var4});
         var0.set(var2);
         return var2;
      }));
   }
}
