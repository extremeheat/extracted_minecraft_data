package net.minecraft.world.entity;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Scoreboard;

public enum ConversionType {
   SINGLE(true) {
      void convert(final Mob from, final Mob to, final ConversionParams params) {
         Entity rootPassenger = from.getFirstPassenger();
         to.copyPosition(from);
         to.setDeltaMovement(from.getDeltaMovement());
         if (rootPassenger != null) {
            rootPassenger.stopRiding();
            rootPassenger.boardingCooldown = 0;

            for(Entity passenger : to.getPassengers()) {
               passenger.stopRiding();
               passenger.remove(Entity.RemovalReason.DISCARDED);
            }

            rootPassenger.startRiding(to);
         }

         Entity vehicle = from.getVehicle();
         if (vehicle != null) {
            from.stopRiding();
            to.startRiding(vehicle, false, false);
         }

         if (params.keepEquipment()) {
            for(EquipmentSlot slot : EquipmentSlot.VALUES) {
               ItemStack itemStack = from.getItemBySlot(slot);
               if (!itemStack.isEmpty()) {
                  to.setItemSlot(slot, itemStack.copyAndClear());
                  to.setDropChance(slot, from.getDropChances().byEquipment(slot));
               }
            }
         }

         to.fallDistance = from.fallDistance;
         to.setSharedFlag(7, from.isFallFlying());
         to.lastHurtByPlayerMemoryTime = from.lastHurtByPlayerMemoryTime;
         to.hurtTime = from.hurtTime;
         to.yBodyRot = from.yBodyRot;
         to.setOnGround(from.onGround());
         Optional var10000 = from.getSleepingPos();
         Objects.requireNonNull(to);
         var10000.ifPresent(to::setSleepingPos);
         Entity leashHolder = from.getLeashHolder();
         if (leashHolder != null) {
            to.setLeashedTo(leashHolder, true);
         }

         this.convertCommon(from, to, params);
      }
   },
   SPLIT_ON_DEATH(false) {
      void convert(final Mob from, final Mob to, final ConversionParams params) {
         Entity rootPassenger = from.getFirstPassenger();
         if (rootPassenger != null) {
            rootPassenger.stopRiding();
         }

         Entity leashHolder = from.getLeashHolder();
         if (leashHolder != null) {
            from.dropLeash();
         }

         this.convertCommon(from, to, params);
      }
   };

   private static final Set<DataComponentType<?>> COMPONENTS_TO_COPY = Set.of(DataComponents.CUSTOM_NAME, DataComponents.CUSTOM_DATA);
   private final boolean discardAfterConversion;

   private ConversionType(final boolean discardAfterConversion) {
      this.discardAfterConversion = discardAfterConversion;
   }

   public boolean shouldDiscardAfterConversion() {
      return this.discardAfterConversion;
   }

   abstract void convert(final Mob from, final Mob to, final ConversionParams params);

   void convertCommon(final Mob from, final Mob to, final ConversionParams params) {
      to.setAbsorptionAmount(from.getAbsorptionAmount());

      for(MobEffectInstance effect : from.getActiveEffects()) {
         to.addEffect(new MobEffectInstance(effect));
      }

      if (from.isBaby()) {
         to.setBaby(true);
      }

      if (from instanceof AgeableMob oldAgeable) {
         if (to instanceof AgeableMob convertedAgeable) {
            convertedAgeable.setAge(oldAgeable.getAge());
            convertedAgeable.forcedAge = oldAgeable.forcedAge;
            convertedAgeable.forcedAgeTimer = oldAgeable.forcedAgeTimer;
         }
      }

      Brain<?> oldBrain = from.getBrain();
      Brain<?> convertedBrain = to.getBrain();
      if (oldBrain.checkMemory(MemoryModuleType.ANGRY_AT, MemoryStatus.REGISTERED) && oldBrain.hasMemoryValue(MemoryModuleType.ANGRY_AT)) {
         convertedBrain.setMemory(MemoryModuleType.ANGRY_AT, oldBrain.getMemory(MemoryModuleType.ANGRY_AT));
      }

      if (params.preserveCanPickUpLoot()) {
         to.setCanPickUpLoot(from.canPickUpLoot());
      }

      to.setLeftHanded(from.isLeftHanded());
      to.setNoAi(from.isNoAi());
      if (from.isPersistenceRequired()) {
         to.setPersistenceRequired();
      }

      to.setCustomNameVisible(from.isCustomNameVisible());
      to.setSharedFlagOnFire(from.isOnFire());
      to.setInvulnerable(from.isInvulnerable());
      to.setNoGravity(from.isNoGravity());
      to.setPortalCooldown(from.getPortalCooldown());
      to.setSilent(from.isSilent());
      Set var10000 = from.entityTags();
      Objects.requireNonNull(to);
      var10000.forEach(to::addTag);

      for(DataComponentType<?> component : COMPONENTS_TO_COPY) {
         copyComponent(from, to, component);
      }

      if (params.team() != null) {
         Scoreboard scoreboard = to.level().getScoreboard();
         scoreboard.addPlayerToTeam(to.getStringUUID(), params.team());
         if (from.getTeam() != null && from.getTeam() == params.team()) {
            scoreboard.removePlayerFromTeam(from.getStringUUID(), from.getTeam());
         }
      }

      if (from instanceof Zombie fromZombie) {
         if (fromZombie.canBreakDoors() && to instanceof Zombie toZombie) {
            toZombie.setCanBreakDoors(true);
         }
      }

   }

   private static <T> void copyComponent(final Mob from, final Mob to, final DataComponentType<T> componentType) {
      T value = (T)from.get(componentType);
      if (value != null) {
         to.setComponent(componentType, value);
      }

   }

   // $FF: synthetic method
   private static ConversionType[] $values() {
      return new ConversionType[]{SINGLE, SPLIT_ON_DEATH};
   }
}
