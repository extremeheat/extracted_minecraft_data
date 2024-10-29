package net.minecraft.world.entity;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Scoreboard;

public enum ConversionType {
   SINGLE(true) {
      void convert(Mob var1, Mob var2, ConversionParams var3) {
         Entity var4 = var1.getFirstPassenger();
         var2.copyPosition(var1);
         var2.setDeltaMovement(var1.getDeltaMovement());
         Entity var6;
         if (var4 != null) {
            var4.stopRiding();
            var4.boardingCooldown = 0;
            Iterator var5 = var2.getPassengers().iterator();

            while(var5.hasNext()) {
               var6 = (Entity)var5.next();
               var6.stopRiding();
               var6.remove(Entity.RemovalReason.DISCARDED);
            }

            var4.startRiding(var2);
         }

         Entity var9 = var1.getVehicle();
         if (var9 != null) {
            var1.stopRiding();
            var2.startRiding(var9);
         }

         if (var3.keepEquipment()) {
            Iterator var10 = EquipmentSlot.VALUES.iterator();

            while(var10.hasNext()) {
               EquipmentSlot var7 = (EquipmentSlot)var10.next();
               ItemStack var8 = var1.getItemBySlot(var7);
               if (!var8.isEmpty()) {
                  var2.setItemSlot(var7, var8.copyAndClear());
                  var2.setDropChance(var7, var1.getEquipmentDropChance(var7));
               }
            }
         }

         var2.fallDistance = var1.fallDistance;
         var2.setSharedFlag(7, var1.isFallFlying());
         var2.lastHurtByPlayerTime = var1.lastHurtByPlayerTime;
         var2.hurtTime = var1.hurtTime;
         var2.yBodyRot = var1.yBodyRot;
         var2.setOnGround(var1.onGround());
         Optional var10000 = var1.getSleepingPos();
         Objects.requireNonNull(var2);
         var10000.ifPresent(var2::setSleepingPos);
         var6 = var1.getLeashHolder();
         if (var6 != null) {
            var2.setLeashedTo(var6, true);
         }

         this.convertCommon(var1, var2, var3);
      }
   },
   SPLIT_ON_DEATH(false) {
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

   ConversionType(final boolean var3) {
      this.discardAfterConversion = var3;
   }

   public boolean shouldDiscardAfterConversion() {
      return this.discardAfterConversion;
   }

   abstract void convert(Mob var1, Mob var2, ConversionParams var3);

   void convertCommon(Mob var1, Mob var2, ConversionParams var3) {
      var2.setAbsorptionAmount(var1.getAbsorptionAmount());
      Iterator var4 = var1.getActiveEffects().iterator();

      while(var4.hasNext()) {
         MobEffectInstance var5 = (MobEffectInstance)var4.next();
         var2.addEffect(new MobEffectInstance(var5));
      }

      if (var1.isBaby()) {
         var2.setBaby(true);
      }

      if (var1 instanceof AgeableMob var8) {
         if (var2 instanceof AgeableMob var10) {
            var10.setAge(var8.getAge());
            var10.forcedAge = var8.forcedAge;
            var10.forcedAgeTimer = var8.forcedAgeTimer;
         }
      }

      Brain var9 = var1.getBrain();
      Brain var11 = var2.getBrain();
      if (var9.checkMemory(MemoryModuleType.ANGRY_AT, MemoryStatus.REGISTERED) && var9.hasMemoryValue(MemoryModuleType.ANGRY_AT)) {
         var11.setMemory(MemoryModuleType.ANGRY_AT, var9.getMemory(MemoryModuleType.ANGRY_AT));
      }

      if (var3.preserveCanPickUpLoot()) {
         var2.setCanPickUpLoot(var1.canPickUpLoot());
      }

      var2.setLeftHanded(var1.isLeftHanded());
      var2.setNoAi(var1.isNoAi());
      if (var1.isPersistenceRequired()) {
         var2.setPersistenceRequired();
      }

      if (var1.hasCustomName()) {
         var2.setCustomName(var1.getCustomName());
         var2.setCustomNameVisible(var1.isCustomNameVisible());
      }

      var2.setSharedFlagOnFire(var1.isOnFire());
      var2.setInvulnerable(var1.isInvulnerable());
      var2.setNoGravity(var1.isNoGravity());
      var2.setPortalCooldown(var1.getPortalCooldown());
      var2.setSilent(var1.isSilent());
      Set var10000 = var1.getTags();
      Objects.requireNonNull(var2);
      var10000.forEach(var2::addTag);
      if (var3.team() != null) {
         Scoreboard var6 = var2.level().getScoreboard();
         var6.addPlayerToTeam(var2.getStringUUID(), var3.team());
         if (var1.getTeam() != null && var1.getTeam() == var3.team()) {
            var6.removePlayerFromTeam(var1.getStringUUID(), var1.getTeam());
         }
      }

      if (var1 instanceof Zombie var12) {
         if (var12.canBreakDoors() && var2 instanceof Zombie var7) {
            var7.setCanBreakDoors(true);
         }
      }

   }

   // $FF: synthetic method
   private static ConversionType[] $values() {
      return new ConversionType[]{SINGLE, SPLIT_ON_DEATH};
   }
}
