package net.minecraft.world.entity.ai.behavior;

import java.util.Comparator;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class BehaviorUtils {
   public static void lockGazeAndWalkToEachOther(LivingEntity var0, LivingEntity var1) {
      lookAtEachOther(var0, var1);
      walkToEachOther(var0, var1);
   }

   public static boolean entityIsVisible(Brain<?> var0, LivingEntity var1) {
      return var0.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).filter((var1x) -> {
         return var1x.contains(var1);
      }).isPresent();
   }

   public static boolean targetIsValid(Brain<?> var0, MemoryModuleType<? extends LivingEntity> var1, EntityType<?> var2) {
      return var0.getMemory(var1).filter((var1x) -> {
         return var1x.getType() == var2;
      }).filter(LivingEntity::isAlive).filter((var1x) -> {
         return entityIsVisible(var0, var1x);
      }).isPresent();
   }

   public static void lookAtEachOther(LivingEntity var0, LivingEntity var1) {
      lookAtEntity(var0, var1);
      lookAtEntity(var1, var0);
   }

   public static void lookAtEntity(LivingEntity var0, LivingEntity var1) {
      var0.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(var1)));
   }

   public static void walkToEachOther(LivingEntity var0, LivingEntity var1) {
      boolean var2 = true;
      walkToEntity(var0, var1, 2);
      walkToEntity(var1, var0, 2);
   }

   public static void walkToEntity(LivingEntity var0, LivingEntity var1, int var2) {
      float var3 = (float)var0.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
      EntityPosWrapper var4 = new EntityPosWrapper(var1);
      WalkTarget var5 = new WalkTarget(var4, var3, var2);
      var0.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)var4);
      var0.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)var5);
   }

   public static void throwItem(LivingEntity var0, ItemStack var1, LivingEntity var2) {
      double var3 = var0.y - 0.30000001192092896D + (double)var0.getEyeHeight();
      ItemEntity var5 = new ItemEntity(var0.level, var0.x, var3, var0.z, var1);
      BlockPos var6 = new BlockPos(var2);
      BlockPos var7 = new BlockPos(var0);
      float var8 = 0.3F;
      Vec3 var9 = new Vec3(var6.subtract(var7));
      var9 = var9.normalize().scale(0.30000001192092896D);
      var5.setDeltaMovement(var9);
      var5.setDefaultPickUpDelay();
      var0.level.addFreshEntity(var5);
   }

   public static SectionPos findSectionClosestToVillage(ServerLevel var0, SectionPos var1, int var2) {
      int var3 = var0.sectionsToVillage(var1);
      Stream var10000 = SectionPos.cube(var1, var2).filter((var2x) -> {
         return var0.sectionsToVillage(var2x) < var3;
      });
      var0.getClass();
      return (SectionPos)var10000.min(Comparator.comparingInt(var0::sectionsToVillage)).orElse(var1);
   }
}
