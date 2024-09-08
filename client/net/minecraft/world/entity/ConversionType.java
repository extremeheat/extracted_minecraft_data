package net.minecraft.world.entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Scoreboard;

public enum ConversionType {
   SINGLE(true) {
      @Override
      void convert(Mob var1, Mob var2, ConversionParams var3) {
         Entity var4 = var1.getFirstPassenger();
         if (var4 != null) {
            var4.stopRiding();
            var4.boardingCooldown = 0;
            var4.startRiding(var2);
         }

         if (var3.keepEquipment()) {
            for (EquipmentSlot var6 : EquipmentSlot.VALUES) {
               ItemStack var7 = var1.getItemBySlot(var6);
               if (!var7.isEmpty()) {
                  var2.setItemSlot(var6, var7.copyAndClear());
                  var2.setDropChance(var6, var1.getEquipmentDropChance(var6));
               }
            }
         }

         var2.getAttributes().assignAllValues(var1.getAttributes());
         var2.fallDistance = var1.fallDistance;
         var2.setSharedFlag(7, var1.isFallFlying());
         float var8 = var1.getHealth() / var1.getMaxHealth();
         var2.setHealth(var2.getMaxHealth() * var8);
         var2.lastHurtByPlayerTime = var1.lastHurtByPlayerTime;
         var2.hurtTime = var1.hurtTime;
         var2.yBodyRot = var1.yBodyRot;
         var2.copyPosition(var1);
         var2.setDeltaMovement(var1.getDeltaMovement());
         var2.setOnGround(var1.onGround());
         var1.getSleepingPos().ifPresent(var2::setSleepingPos);
         Entity var9 = var1.getLeashHolder();
         if (var9 != null) {
            var2.setLeashedTo(var9, true);
         }

         this.convertCommon(var1, var2, var3);
      }
   },
   SPLIT_ON_DEATH(false) {
      @Override
      void convert(Mob var1, Mob var2, ConversionParams var3) {
         Entity var4 = var1.getFirstPassenger();
         if (var4 != null) {
            var4.stopRiding();
         }

         Entity var5 = var1.getLeashHolder();
         if (var5 != null) {
            var1.dropLeash(true, true);
         }

         this.convertCommon(var1, var2, var3);
      }
   };

   private final boolean discardAfterConversion;

   ConversionType(final boolean nullxx) {
      this.discardAfterConversion = nullxx;
   }

   public boolean shouldDiscardAfterConversion() {
      return this.discardAfterConversion;
   }

   abstract void convert(Mob var1, Mob var2, ConversionParams var3);

   void convertCommon(Mob var1, Mob var2, ConversionParams var3) {
      var2.setAbsorptionAmount(var1.getAbsorptionAmount());

      for (MobEffectInstance var5 : var1.getActiveEffects()) {
         var2.addEffect(var5);
      }

      if (var1.isBaby()) {
         var2.setBaby(true);
      }

      if (var1 instanceof AgeableMob var7 && var2 instanceof AgeableMob var9) {
         var9.setAge(var7.getAge());
         var9.forcedAge = var7.forcedAge;
         var9.forcedAgeTimer = var7.forcedAgeTimer;
      }

      Brain var8 = var1.getBrain();
      Brain var10 = var2.getBrain();
      if (var8.checkMemory(MemoryModuleType.ANGRY_AT, MemoryStatus.REGISTERED) && var8.hasMemoryValue(MemoryModuleType.ANGRY_AT)) {
         var10.setMemory(MemoryModuleType.ANGRY_AT, var8.getMemory(MemoryModuleType.ANGRY_AT));
      }

      if (var3.preserveCanPickUpLoot()) {
         var2.setCanPickUpLoot(var1.canPickUpLoot());
      }

      var2.setLeftHanded(var1.isLeftHanded());
      var2.setNoAi(var1.isNoAi());
      if (var1.isPersistenceRequired()) {
         var2.setPersistenceRequired();
      }

      var2.setLootTable(var1.getLootTable());
      var2.setLootTableSeed(var1.getLootTableSeed());
      if (var1.hasCustomName()) {
         var2.setCustomName(var1.getCustomName());
         var2.setCustomNameVisible(var1.isCustomNameVisible());
      }

      var2.setSharedFlagOnFire(var1.isOnFire());
      var2.setInvulnerable(var1.isInvulnerable());
      var2.setNoGravity(var1.isNoGravity());
      var2.setPortalCooldown(var1.getPortalCooldown());
      var2.setSilent(var1.isSilent());
      var1.getTags().forEach(var2::addTag);
      if (var3.team() != null) {
         Scoreboard var6 = var2.level().getScoreboard();
         var6.addPlayerToTeam(var2.getStringUUID(), var3.team());
         if (var1.getTeam() != null && var1.getTeam() == var3.team()) {
            var6.removePlayerFromTeam(var1.getStringUUID(), var1.getTeam());
         }
      }
   }
}
