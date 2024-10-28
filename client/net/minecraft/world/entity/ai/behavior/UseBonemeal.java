package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

public class UseBonemeal extends Behavior<Villager> {
   private static final int BONEMEALING_DURATION = 80;
   private long nextWorkCycleTime;
   private long lastBonemealingSession;
   private int timeWorkedSoFar;
   private Optional<BlockPos> cropPos = Optional.empty();

   public UseBonemeal() {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      if (var2.tickCount % 10 == 0 && (this.lastBonemealingSession == 0L || this.lastBonemealingSession + 160L <= (long)var2.tickCount)) {
         if (var2.getInventory().countItem(Items.BONE_MEAL) <= 0) {
            return false;
         } else {
            this.cropPos = this.pickNextTarget(var1, var2);
            return this.cropPos.isPresent();
         }
      } else {
         return false;
      }
   }

   protected boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      return this.timeWorkedSoFar < 80 && this.cropPos.isPresent();
   }

   private Optional<BlockPos> pickNextTarget(ServerLevel var1, Villager var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
      Optional var4 = Optional.empty();
      int var5 = 0;

      for(int var6 = -1; var6 <= 1; ++var6) {
         for(int var7 = -1; var7 <= 1; ++var7) {
            for(int var8 = -1; var8 <= 1; ++var8) {
               var3.setWithOffset(var2.blockPosition(), var6, var7, var8);
               if (this.validPos(var3, var1)) {
                  ++var5;
                  if (var1.random.nextInt(var5) == 0) {
                     var4 = Optional.of(var3.immutable());
                  }
               }
            }
         }
      }

      return var4;
   }

   private boolean validPos(BlockPos var1, ServerLevel var2) {
      BlockState var3 = var2.getBlockState(var1);
      Block var4 = var3.getBlock();
      return var4 instanceof CropBlock && !((CropBlock)var4).isMaxAge(var3);
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      this.setCurrentCropAsTarget(var2);
      var2.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BONE_MEAL));
      this.nextWorkCycleTime = var3;
      this.timeWorkedSoFar = 0;
   }

   private void setCurrentCropAsTarget(Villager var1) {
      this.cropPos.ifPresent((var1x) -> {
         BlockPosTracker var2 = new BlockPosTracker(var1x);
         var1.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)var2);
         var1.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var2, 0.5F, 1)));
      });
   }

   protected void stop(ServerLevel var1, Villager var2, long var3) {
      var2.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      this.lastBonemealingSession = (long)var2.tickCount;
   }

   protected void tick(ServerLevel var1, Villager var2, long var3) {
      BlockPos var5 = (BlockPos)this.cropPos.get();
      if (var3 >= this.nextWorkCycleTime && var5.closerToCenterThan(var2.position(), 1.0)) {
         ItemStack var6 = ItemStack.EMPTY;
         SimpleContainer var7 = var2.getInventory();
         int var8 = var7.getContainerSize();

         for(int var9 = 0; var9 < var8; ++var9) {
            ItemStack var10 = var7.getItem(var9);
            if (var10.is(Items.BONE_MEAL)) {
               var6 = var10;
               break;
            }
         }

         if (!var6.isEmpty() && BoneMealItem.growCrop(var6, var1, var5)) {
            var1.levelEvent(1505, var5, 15);
            this.cropPos = this.pickNextTarget(var1, var2);
            this.setCurrentCropAsTarget(var2);
            this.nextWorkCycleTime = var3 + 40L;
         }

         ++this.timeWorkedSoFar;
      }
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Villager)var2, var3);
   }
}
