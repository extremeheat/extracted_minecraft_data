package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PiglinSpecificSensor extends Sensor<LivingEntity> {
   public PiglinSpecificSensor() {
      super();
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, new MemoryModuleType[]{MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_REPELLENT});
   }

   protected void doTick(ServerLevel var1, LivingEntity var2) {
      Brain var3 = var2.getBrain();
      var3.setMemory(MemoryModuleType.NEAREST_REPELLENT, findNearestRepellent(var1, var2));
      Optional var4 = Optional.empty();
      Optional var5 = Optional.empty();
      Optional var6 = Optional.empty();
      Optional var7 = Optional.empty();
      Optional var8 = Optional.empty();
      Optional var9 = Optional.empty();
      Optional var10 = Optional.empty();
      int var11 = 0;
      ArrayList var12 = Lists.newArrayList();
      ArrayList var13 = Lists.newArrayList();
      List var14 = (List)var3.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).orElse(ImmutableList.of());
      Iterator var15 = var14.iterator();

      while(true) {
         while(true) {
            while(var15.hasNext()) {
               LivingEntity var16 = (LivingEntity)var15.next();
               if (var16 instanceof Hoglin) {
                  Hoglin var21 = (Hoglin)var16;
                  if (var21.isBaby() && !var6.isPresent()) {
                     var6 = Optional.of(var21);
                  } else if (var21.isAdult()) {
                     ++var11;
                     if (!var5.isPresent() && var21.canBeHunted()) {
                        var5 = Optional.of(var21);
                     }
                  }
               } else if (var16 instanceof PiglinBrute) {
                  var12.add((PiglinBrute)var16);
               } else if (var16 instanceof Piglin) {
                  Piglin var20 = (Piglin)var16;
                  if (var20.isBaby() && !var7.isPresent()) {
                     var7 = Optional.of(var20);
                  } else if (var20.isAdult()) {
                     var12.add(var20);
                  }
               } else if (var16 instanceof Player) {
                  Player var17 = (Player)var16;
                  if (!var9.isPresent() && EntitySelector.ATTACK_ALLOWED.test(var16) && !PiglinAi.isWearingGold(var17)) {
                     var9 = Optional.of(var17);
                  }

                  if (!var10.isPresent() && !var17.isSpectator() && PiglinAi.isPlayerHoldingLovedItem(var17)) {
                     var10 = Optional.of(var17);
                  }
               } else if (var4.isPresent() || !(var16 instanceof WitherSkeleton) && !(var16 instanceof WitherBoss)) {
                  if (!var8.isPresent() && PiglinAi.isZombified(var16.getType())) {
                     var8 = Optional.of(var16);
                  }
               } else {
                  var4 = Optional.of((Mob)var16);
               }
            }

            List var18 = (List)var3.getMemory(MemoryModuleType.LIVING_ENTITIES).orElse(ImmutableList.of());
            Iterator var19 = var18.iterator();

            while(var19.hasNext()) {
               LivingEntity var22 = (LivingEntity)var19.next();
               if (var22 instanceof AbstractPiglin && ((AbstractPiglin)var22).isAdult()) {
                  var13.add((AbstractPiglin)var22);
               }
            }

            var3.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, var4);
            var3.setMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, var5);
            var3.setMemory(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, var6);
            var3.setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, var8);
            var3.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, var9);
            var3.setMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, var10);
            var3.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, (Object)var13);
            var3.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, (Object)var12);
            var3.setMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, (Object)var12.size());
            var3.setMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, (Object)var11);
            return;
         }
      }
   }

   private static Optional<BlockPos> findNearestRepellent(ServerLevel var0, LivingEntity var1) {
      return BlockPos.findClosestMatch(var1.blockPosition(), 8, 4, (var1x) -> {
         return isValidRepellent(var0, var1x);
      });
   }

   private static boolean isValidRepellent(ServerLevel var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      boolean var3 = var2.is(BlockTags.PIGLIN_REPELLENTS);
      return var3 && var2.is(Blocks.SOUL_CAMPFIRE) ? CampfireBlock.isLitCampfire(var2) : var3;
   }
}
