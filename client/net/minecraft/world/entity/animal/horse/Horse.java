package net.minecraft.world.entity.animal.horse;

import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class Horse extends AbstractHorse {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT;
   private static final EntityDimensions BABY_DIMENSIONS;
   private static final int DEFAULT_VARIANT = 0;

   public Horse(EntityType<? extends Horse> var1, Level var2) {
      super(var1, var2);
   }

   protected void randomizeAttributes(RandomSource var1) {
      AttributeInstance var10000 = this.getAttribute(Attributes.MAX_HEALTH);
      Objects.requireNonNull(var1);
      var10000.setBaseValue((double)generateMaxHealth(var1::nextInt));
      var10000 = this.getAttribute(Attributes.MOVEMENT_SPEED);
      Objects.requireNonNull(var1);
      var10000.setBaseValue(generateSpeed(var1::nextDouble));
      var10000 = this.getAttribute(Attributes.JUMP_STRENGTH);
      Objects.requireNonNull(var1);
      var10000.setBaseValue(generateJumpStrength(var1::nextDouble));
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_ID_TYPE_VARIANT, 0);
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Variant", this.getTypeVariant());
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.setTypeVariant(var1.getIntOr("Variant", 0));
   }

   private void setTypeVariant(int var1) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, var1);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   private void setVariantAndMarkings(Variant var1, Markings var2) {
      this.setTypeVariant(var1.getId() & 255 | var2.getId() << 8 & '\uff00');
   }

   public Variant getVariant() {
      return Variant.byId(this.getTypeVariant() & 255);
   }

   private void setVariant(Variant var1) {
      this.setTypeVariant(var1.getId() & 255 | this.getTypeVariant() & -256);
   }

   public <T> @Nullable T get(DataComponentType<? extends T> var1) {
      return (T)(var1 == DataComponents.HORSE_VARIANT ? castComponentValue(var1, this.getVariant()) : super.get(var1));
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.HORSE_VARIANT);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.HORSE_VARIANT) {
         this.setVariant((Variant)castComponentValue(DataComponents.HORSE_VARIANT, var2));
         return true;
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   public Markings getMarkings() {
      return Markings.byId((this.getTypeVariant() & '\uff00') >> 8);
   }

   protected void playGallopSound(SoundType var1) {
      super.playGallopSound(var1);
      if (this.random.nextInt(10) == 0) {
         this.playSound(SoundEvents.HORSE_BREATHE, var1.getVolume() * 0.6F, var1.getPitch());
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HORSE_DEATH;
   }

   protected SoundEvent getEatingSound() {
      return SoundEvents.HORSE_EAT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.HORSE_ANGRY;
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      boolean var3 = !this.isBaby() && this.isTamed() && var1.isSecondaryUseActive();
      if (!this.isVehicle() && !var3) {
         ItemStack var4 = var1.getItemInHand(var2);
         if (!var4.isEmpty()) {
            if (this.isFood(var4)) {
               return this.fedFood(var1, var4);
            }

            if (!this.isTamed()) {
               this.makeMad();
               return InteractionResult.SUCCESS;
            }
         }

         return super.mobInteract(var1, var2);
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   public boolean canMate(Animal var1) {
      if (var1 == this) {
         return false;
      } else if (!(var1 instanceof Donkey) && !(var1 instanceof Horse)) {
         return false;
      } else {
         return this.canParent() && ((AbstractHorse)var1).canParent();
      }
   }

   public @Nullable AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      if (var2 instanceof Donkey) {
         Mule var9 = EntityType.MULE.create(var1, EntitySpawnReason.BREEDING);
         if (var9 != null) {
            this.setOffspringAttributes(var2, var9);
         }

         return var9;
      } else {
         Horse var3 = (Horse)var2;
         Horse var4 = EntityType.HORSE.create(var1, EntitySpawnReason.BREEDING);
         if (var4 != null) {
            int var6 = this.random.nextInt(9);
            Variant var5;
            if (var6 < 4) {
               var5 = this.getVariant();
            } else if (var6 < 8) {
               var5 = var3.getVariant();
            } else {
               var5 = (Variant)Util.getRandom(Variant.values(), this.random);
            }

            int var8 = this.random.nextInt(5);
            Markings var7;
            if (var8 < 2) {
               var7 = this.getMarkings();
            } else if (var8 < 4) {
               var7 = var3.getMarkings();
            } else {
               var7 = (Markings)Util.getRandom(Markings.values(), this.random);
            }

            var4.setVariantAndMarkings(var5, var7);
            this.setOffspringAttributes(var2, var4);
         }

         return var4;
      }
   }

   public boolean canUseSlot(EquipmentSlot var1) {
      return true;
   }

   protected void hurtArmor(DamageSource var1, float var2) {
      this.doHurtEquipment(var1, var2, new EquipmentSlot[]{EquipmentSlot.BODY});
   }

   public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      RandomSource var5 = var1.getRandom();
      Variant var6;
      if (var4 instanceof HorseGroupData) {
         var6 = ((HorseGroupData)var4).variant;
      } else {
         var6 = (Variant)Util.getRandom(Variant.values(), var5);
         var4 = new HorseGroupData(var6);
      }

      this.setVariantAndMarkings(var6, (Markings)Util.getRandom(Markings.values(), var5));
      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4);
   }

   public EntityDimensions getDefaultDimensions(Pose var1) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(var1);
   }

   static {
      DATA_ID_TYPE_VARIANT = SynchedEntityData.<Integer>defineId(Horse.class, EntityDataSerializers.INT);
      BABY_DIMENSIONS = EntityType.HORSE.getDimensions().withAttachments(EntityAttachments.builder().attach(EntityAttachment.PASSENGER, 0.0F, EntityType.HORSE.getHeight() + 0.125F, 0.0F)).scale(0.5F);
   }

   public static class HorseGroupData extends AgeableMob.AgeableMobGroupData {
      public final Variant variant;

      public HorseGroupData(Variant var1) {
         super(true);
         this.variant = var1;
      }
   }
}
