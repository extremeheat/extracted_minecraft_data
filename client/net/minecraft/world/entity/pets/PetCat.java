package net.minecraft.world.entity.pets;

import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.CatVariants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class PetCat extends AbstractPet {
   private static final EntityDataAccessor<Holder<CatVariant>> DATA_VARIANT_ID;
   private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR;
   private static final ResourceKey<CatVariant> DEFAULT_VARIANT;
   private static final DyeColor DEFAULT_COLLAR_COLOR;

   public PetCat(EntityType<? extends PetCat> var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 2.0));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
   }

   public Holder<CatVariant> getVariant() {
      return (Holder)this.entityData.get(DATA_VARIANT_ID);
   }

   private void setVariant(Holder<CatVariant> var1) {
      this.entityData.set(DATA_VARIANT_ID, var1);
   }

   @Nullable
   public <T> T get(DataComponentType<? extends T> var1) {
      if (var1 == DataComponents.CAT_VARIANT) {
         return (T)castComponentValue(var1, this.getVariant());
      } else {
         return (T)(var1 == DataComponents.CAT_COLLAR ? castComponentValue(var1, this.getCollarColor()) : super.get(var1));
      }
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.CAT_VARIANT);
      this.applyImplicitComponentIfPresent(var1, DataComponents.CAT_COLLAR);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.CAT_VARIANT) {
         this.setVariant((Holder)castComponentValue(DataComponents.CAT_VARIANT, var2));
         return true;
      } else if (var1 == DataComponents.CAT_COLLAR) {
         this.setCollarColor((DyeColor)castComponentValue(DataComponents.CAT_COLLAR, var2));
         return true;
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   public DyeColor getCollarColor() {
      return DyeColor.byId((Integer)this.entityData.get(DATA_COLLAR_COLOR));
   }

   private void setCollarColor(DyeColor var1) {
      this.entityData.set(DATA_COLLAR_COLOR, var1.getId());
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_VARIANT_ID, VariantUtils.getDefaultOrAny(this.registryAccess(), DEFAULT_VARIANT));
      var1.define(DATA_COLLAR_COLOR, DEFAULT_COLLAR_COLOR.getId());
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      VariantUtils.writeVariant(var1, this.getVariant());
      var1.store("CollarColor", DyeColor.LEGACY_ID_CODEC, this.getCollarColor());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      VariantUtils.readVariant(var1, this.registryAccess(), Registries.CAT_VARIANT).ifPresent(this::setVariant);
      this.setCollarColor((DyeColor)var1.read("CollarColor", DyeColor.LEGACY_ID_CODEC).orElse(DEFAULT_COLLAR_COLOR));
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isTame()) {
         if (this.isInLove()) {
            return SoundEvents.CAT_PURR;
         } else {
            return this.random.nextInt(4) == 0 ? SoundEvents.CAT_PURREOW : SoundEvents.CAT_AMBIENT;
         }
      } else {
         return SoundEvents.CAT_STRAY_AMBIENT;
      }
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public void hiss() {
      this.makeSound(SoundEvents.CAT_HISS);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224).add(Attributes.ATTACK_DAMAGE, 3.0);
   }

   public void tick() {
      super.tick();
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      var4 = super.finalizeSpawn(var1, var2, var3, var4);
      CatVariants.selectVariantToSpawn(this.random, this.registryAccess(), SpawnContext.create(var1, this.blockPosition())).ifPresent(this::setVariant);
      return var4;
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.CAT_FOOD);
   }

   public boolean removeWhenFarAway(double var1) {
      return false;
   }

   static {
      DATA_VARIANT_ID = SynchedEntityData.<Holder<CatVariant>>defineId(PetCat.class, EntityDataSerializers.CAT_VARIANT);
      DATA_COLLAR_COLOR = SynchedEntityData.<Integer>defineId(PetCat.class, EntityDataSerializers.INT);
      DEFAULT_VARIANT = CatVariants.BLACK;
      DEFAULT_COLLAR_COLOR = DyeColor.RED;
   }
}
