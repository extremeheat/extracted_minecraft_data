package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;

public class InteractWithDoor extends Behavior {
   public InteractWithDoor() {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_PRESENT, MemoryModuleType.INTERACTABLE_DOORS, MemoryStatus.VALUE_PRESENT, MemoryModuleType.OPENED_DOORS, MemoryStatus.REGISTERED));
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Brain var5 = var2.getBrain();
      Path var6 = (Path)var5.getMemory(MemoryModuleType.PATH).get();
      List var7 = (List)var5.getMemory(MemoryModuleType.INTERACTABLE_DOORS).get();
      List var8 = (List)var6.getNodes().stream().map((var0) -> {
         return new BlockPos(var0.x, var0.y, var0.z);
      }).collect(Collectors.toList());
      Set var9 = this.getDoorsThatAreOnMyPath(var1, var7, var8);
      int var10 = var6.getIndex() - 1;
      this.openOrCloseDoors(var1, var8, var9, var10, var2, var5);
   }

   private Set getDoorsThatAreOnMyPath(ServerLevel var1, List var2, List var3) {
      Stream var10000 = var2.stream().filter((var1x) -> {
         return var1x.dimension() == var1.getDimension().getType();
      }).map(GlobalPos::pos);
      var3.getClass();
      return (Set)var10000.filter(var3::contains).collect(Collectors.toSet());
   }

   private void openOrCloseDoors(ServerLevel var1, List var2, Set var3, int var4, LivingEntity var5, Brain var6) {
      var3.forEach((var4x) -> {
         int var5 = var2.indexOf(var4x);
         BlockState var6x = var1.getBlockState(var4x);
         Block var7 = var6x.getBlock();
         if (BlockTags.WOODEN_DOORS.contains(var7) && var7 instanceof DoorBlock) {
            boolean var8 = var5 >= var4;
            ((DoorBlock)var7).setOpen(var1, var4x, var8);
            GlobalPos var9 = GlobalPos.of(var1.getDimension().getType(), var4x);
            if (!var6.getMemory(MemoryModuleType.OPENED_DOORS).isPresent() && var8) {
               var6.setMemory(MemoryModuleType.OPENED_DOORS, (Object)Sets.newHashSet(new GlobalPos[]{var9}));
            } else {
               var6.getMemory(MemoryModuleType.OPENED_DOORS).ifPresent((var2x) -> {
                  if (var8) {
                     var2x.add(var9);
                  } else {
                     var2x.remove(var9);
                  }

               });
            }
         }

      });
      closeAllOpenedDoors(var1, var2, var4, var5, var6);
   }

   public static void closeAllOpenedDoors(ServerLevel var0, List var1, int var2, LivingEntity var3, Brain var4) {
      var4.getMemory(MemoryModuleType.OPENED_DOORS).ifPresent((var4x) -> {
         Iterator var5 = var4x.iterator();

         while(var5.hasNext()) {
            GlobalPos var6 = (GlobalPos)var5.next();
            BlockPos var7 = var6.pos();
            int var8 = var1.indexOf(var7);
            if (var0.getDimension().getType() != var6.dimension()) {
               var5.remove();
            } else {
               BlockState var9 = var0.getBlockState(var7);
               Block var10 = var9.getBlock();
               if (BlockTags.WOODEN_DOORS.contains(var10) && var10 instanceof DoorBlock && var8 < var2 && var7.closerThan(var3.position(), 4.0D)) {
                  ((DoorBlock)var10).setOpen(var0, var7, false);
                  var5.remove();
               }
            }
         }

      });
   }
}
