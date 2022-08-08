package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class HarvestFarmland extends Behavior<Villager> {
   private static final int HARVEST_DURATION = 200;
   public static final float SPEED_MODIFIER = 0.5F;
   @Nullable
   private BlockPos aboveFarmlandPos;
   private long nextOkStartTime;
   private int timeWorkedSoFar;
   private final List<BlockPos> validFarmlandAroundVillager = Lists.newArrayList();

   public HarvestFarmland() {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      if (!var1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         return false;
      } else if (var2.getVillagerData().getProfession() != VillagerProfession.FARMER) {
         return false;
      } else {
         BlockPos.MutableBlockPos var3 = var2.blockPosition().mutable();
         this.validFarmlandAroundVillager.clear();

         for(int var4 = -1; var4 <= 1; ++var4) {
            for(int var5 = -1; var5 <= 1; ++var5) {
               for(int var6 = -1; var6 <= 1; ++var6) {
                  var3.set(var2.getX() + (double)var4, var2.getY() + (double)var5, var2.getZ() + (double)var6);
                  if (this.validPos(var3, var1)) {
                     this.validFarmlandAroundVillager.add(new BlockPos(var3));
                  }
               }
            }
         }

         this.aboveFarmlandPos = this.getValidFarmland(var1);
         return this.aboveFarmlandPos != null;
      }
   }

   @Nullable
   private BlockPos getValidFarmland(ServerLevel var1) {
      return this.validFarmlandAroundVillager.isEmpty() ? null : (BlockPos)this.validFarmlandAroundVillager.get(var1.getRandom().nextInt(this.validFarmlandAroundVillager.size()));
   }

   private boolean validPos(BlockPos var1, ServerLevel var2) {
      BlockState var3 = var2.getBlockState(var1);
      Block var4 = var3.getBlock();
      Block var5 = var2.getBlockState(var1.below()).getBlock();
      return var4 instanceof CropBlock && ((CropBlock)var4).isMaxAge(var3) || var3.isAir() && var5 instanceof FarmBlock;
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      if (var3 > this.nextOkStartTime && this.aboveFarmlandPos != null) {
         var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosTracker(this.aboveFarmlandPos)));
         var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new BlockPosTracker(this.aboveFarmlandPos), 0.5F, 1)));
      }

   }

   protected void stop(ServerLevel var1, Villager var2, long var3) {
      var2.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
      var2.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      this.timeWorkedSoFar = 0;
      this.nextOkStartTime = var3 + 40L;
   }

   protected void tick(ServerLevel var1, Villager var2, long var3) {
      if (this.aboveFarmlandPos == null || this.aboveFarmlandPos.closerToCenterThan(var2.position(), 1.0)) {
         if (this.aboveFarmlandPos != null && var3 > this.nextOkStartTime) {
            BlockState var5 = var1.getBlockState(this.aboveFarmlandPos);
            Block var6 = var5.getBlock();
            Block var7 = var1.getBlockState(this.aboveFarmlandPos.below()).getBlock();
            if (var6 instanceof CropBlock && ((CropBlock)var6).isMaxAge(var5)) {
               var1.destroyBlock(this.aboveFarmlandPos, true, var2);
            }

            if (var5.isAir() && var7 instanceof FarmBlock && var2.hasFarmSeeds()) {
               SimpleContainer var8 = var2.getInventory();

               for(int var9 = 0; var9 < var8.getContainerSize(); ++var9) {
                  ItemStack var10 = var8.getItem(var9);
                  boolean var11 = false;
                  if (!var10.isEmpty()) {
                     BlockState var12;
                     if (var10.is(Items.WHEAT_SEEDS)) {
                        var12 = Blocks.WHEAT.defaultBlockState();
                        var1.setBlockAndUpdate(this.aboveFarmlandPos, var12);
                        var1.gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of(var2, var12));
                        var11 = true;
                     } else if (var10.is(Items.POTATO)) {
                        var12 = Blocks.POTATOES.defaultBlockState();
                        var1.setBlockAndUpdate(this.aboveFarmlandPos, var12);
                        var1.gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of(var2, var12));
                        var11 = true;
                     } else if (var10.is(Items.CARROT)) {
                        var12 = Blocks.CARROTS.defaultBlockState();
                        var1.setBlockAndUpdate(this.aboveFarmlandPos, var12);
                        var1.gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of(var2, var12));
                        var11 = true;
                     } else if (var10.is(Items.BEETROOT_SEEDS)) {
                        var12 = Blocks.BEETROOTS.defaultBlockState();
                        var1.setBlockAndUpdate(this.aboveFarmlandPos, var12);
                        var1.gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of(var2, var12));
                        var11 = true;
                     }
                  }

                  if (var11) {
                     var1.playSound((Player)null, (double)this.aboveFarmlandPos.getX(), (double)this.aboveFarmlandPos.getY(), (double)this.aboveFarmlandPos.getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                     var10.shrink(1);
                     if (var10.isEmpty()) {
                        var8.setItem(var9, ItemStack.EMPTY);
                     }
                     break;
                  }
               }
            }

            if (var6 instanceof CropBlock && !((CropBlock)var6).isMaxAge(var5)) {
               this.validFarmlandAroundVillager.remove(this.aboveFarmlandPos);
               this.aboveFarmlandPos = this.getValidFarmland(var1);
               if (this.aboveFarmlandPos != null) {
                  this.nextOkStartTime = var3 + 20L;
                  var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new BlockPosTracker(this.aboveFarmlandPos), 0.5F, 1)));
                  var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosTracker(this.aboveFarmlandPos)));
               }
            }
         }

         ++this.timeWorkedSoFar;
      }
   }

   protected boolean canStillUse(ServerLevel var1, Villager var2, long var3) {
      return this.timeWorkedSoFar < 200;
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void tick(ServerLevel var1, LivingEntity var2, long var3) {
      this.tick(var1, (Villager)var2, var3);
   }
}
