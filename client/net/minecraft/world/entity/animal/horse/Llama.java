package net.minecraft.world.entity.animal.horse;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LlamaFollowCaravanGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Llama extends AbstractChestedHorse implements VariantHolder<Llama.Variant>, RangedAttackMob {
   private static final int MAX_STRENGTH = 5;
   private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT, Blocks.HAY_BLOCK.asItem());
   private static final EntityDataAccessor<Integer> DATA_STRENGTH_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
   private static final EntityDimensions BABY_DIMENSIONS = EntityType.LLAMA
      .getDimensions()
      .withAttachments(EntityAttachments.builder().attach(EntityAttachment.PASSENGER, 0.0F, EntityType.LLAMA.getHeight() - 0.8125F, -0.3F))
      .scale(0.5F);
   boolean didSpit;
   @Nullable
   private Llama caravanHead;
   @Nullable
   private Llama caravanTail;

   public Llama(EntityType<? extends Llama> var1, Level var2) {
      super(var1, var2);
   }

   public boolean isTraderLlama() {
      return false;
   }

   private void setStrength(int var1) {
      this.entityData.set(DATA_STRENGTH_ID, Math.max(1, Math.min(5, var1)));
   }

   private void setRandomStrength(RandomSource var1) {
      int var2 = var1.nextFloat() < 0.04F ? 5 : 3;
      this.setStrength(1 + var1.nextInt(var2));
   }

   public int getStrength() {
      return this.entityData.get(DATA_STRENGTH_ID);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Variant", this.getVariant().id);
      var1.putInt("Strength", this.getStrength());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      this.setStrength(var1.getInt("Strength"));
      super.readAdditionalSaveData(var1);
      this.setVariant(Llama.Variant.byId(var1.getInt("Variant")));
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2));
      this.goalSelector.addGoal(2, new LlamaFollowCaravanGoal(this, 2.0999999046325684));
      this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25, 40, 20.0F));
      this.goalSelector.addGoal(3, new PanicGoal(this, 1.2));
      this.goalSelector.addGoal(4, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(5, new TemptGoal(this, 1.25, Ingredient.of(Items.HAY_BLOCK), false));
      this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.0));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new Llama.LlamaHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new Llama.LlamaAttackWolfGoal(this));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return createBaseChestedHorseAttributes().add(Attributes.FOLLOW_RANGE, 40.0);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_STRENGTH_ID, 0);
      var1.define(DATA_VARIANT_ID, 0);
   }

   public Llama.Variant getVariant() {
      return Llama.Variant.byId(this.entityData.get(DATA_VARIANT_ID));
   }

   public void setVariant(Llama.Variant var1) {
      this.entityData.set(DATA_VARIANT_ID, var1.id);
   }

   @Override
   protected int getInventorySize() {
      return this.hasChest() ? 1 + 3 * this.getInventoryColumns() : super.getInventorySize();
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return FOOD_ITEMS.test(var1);
   }

   @Override
   protected boolean handleEating(Player var1, ItemStack var2) {
      byte var3 = 0;
      byte var4 = 0;
      float var5 = 0.0F;
      boolean var6 = false;
      if (var2.is(Items.WHEAT)) {
         var3 = 10;
         var4 = 3;
         var5 = 2.0F;
      } else if (var2.is(Blocks.HAY_BLOCK.asItem())) {
         var3 = 90;
         var4 = 6;
         var5 = 10.0F;
         if (this.isTamed() && this.getAge() == 0 && this.canFallInLove()) {
            var6 = true;
            this.setInLove(var1);
         }
      }

      if (this.getHealth() < this.getMaxHealth() && var5 > 0.0F) {
         this.heal(var5);
         var6 = true;
      }

      if (this.isBaby() && var3 > 0) {
         this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
         if (!this.level().isClientSide) {
            this.ageUp(var3);
         }

         var6 = true;
      }

      if (var4 > 0 && (var6 || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
         var6 = true;
         if (!this.level().isClientSide) {
            this.modifyTemper(var4);
         }
      }

      if (var6 && !this.isSilent()) {
         SoundEvent var7 = this.getEatingSound();
         if (var7 != null) {
            this.level()
               .playSound(
                  null,
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  this.getEatingSound(),
                  this.getSoundSource(),
                  1.0F,
                  1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
               );
         }
      }

      return var6;
   }

   @Override
   public boolean isImmobile() {
      return this.isDeadOrDying() || this.isEating();
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      RandomSource var5 = var1.getRandom();
      this.setRandomStrength(var5);
      Llama.Variant var6;
      if (var4 instanceof Llama.LlamaGroupData) {
         var6 = ((Llama.LlamaGroupData)var4).variant;
      } else {
         var6 = Util.getRandom(Llama.Variant.values(), var5);
         var4 = new Llama.LlamaGroupData(var6);
      }

      this.setVariant(var6);
      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4);
   }

   @Override
   protected boolean canPerformRearing() {
      return false;
   }

   @Override
   protected SoundEvent getAngrySound() {
      return SoundEvents.LLAMA_ANGRY;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.LLAMA_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.LLAMA_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.LLAMA_DEATH;
   }

   @Nullable
   @Override
   protected SoundEvent getEatingSound() {
      return SoundEvents.LLAMA_EAT;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.LLAMA_STEP, 0.15F, 1.0F);
   }

   @Override
   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.LLAMA_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   @Override
   public int getInventoryColumns() {
      return this.getStrength();
   }

   @Override
   public boolean canWearBodyArmor() {
      return true;
   }

   @Override
   public boolean isBodyArmorItem(ItemStack var1) {
      return var1.is(ItemTags.WOOL_CARPETS);
   }

   @Override
   public boolean isSaddleable() {
      return false;
   }

   @Override
   public void containerChanged(Container var1) {
      DyeColor var2 = this.getSwag();
      super.containerChanged(var1);
      DyeColor var3 = this.getSwag();
      if (this.tickCount > 20 && var3 != null && var3 != var2) {
         this.playSound(SoundEvents.LLAMA_SWAG, 0.5F, 1.0F);
      }
   }

   @Nullable
   private static DyeColor getDyeColor(ItemStack var0) {
      Block var1 = Block.byItem(var0.getItem());
      return var1 instanceof WoolCarpetBlock ? ((WoolCarpetBlock)var1).getColor() : null;
   }

   @Nullable
   public DyeColor getSwag() {
      return getDyeColor(this.getItemBySlot(EquipmentSlot.BODY));
   }

   @Override
   public int getMaxTemper() {
      return 30;
   }

   @Override
   public boolean canMate(Animal var1) {
      return var1 != this && var1 instanceof Llama && this.canParent() && ((Llama)var1).canParent();
   }

   @Nullable
   public Llama getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Llama var3 = this.makeNewLlama();
      if (var3 != null) {
         this.setOffspringAttributes(var2, var3);
         Llama var4 = (Llama)var2;
         int var5 = this.random.nextInt(Math.max(this.getStrength(), var4.getStrength())) + 1;
         if (this.random.nextFloat() < 0.03F) {
            ++var5;
         }

         var3.setStrength(var5);
         var3.setVariant(this.random.nextBoolean() ? this.getVariant() : var4.getVariant());
      }

      return var3;
   }

   @Nullable
   protected Llama makeNewLlama() {
      return EntityType.LLAMA.create(this.level());
   }

   private void spit(LivingEntity var1) {
      LlamaSpit var2 = new LlamaSpit(this.level(), this);
      double var3 = var1.getX() - this.getX();
      double var5 = var1.getY(0.3333333333333333) - var2.getY();
      double var7 = var1.getZ() - this.getZ();
      double var9 = Math.sqrt(var3 * var3 + var7 * var7) * 0.20000000298023224;
      var2.shoot(var3, var5 + var9, var7, 1.5F, 10.0F);
      if (!this.isSilent()) {
         this.level()
            .playSound(
               null,
               this.getX(),
               this.getY(),
               this.getZ(),
               SoundEvents.LLAMA_SPIT,
               this.getSoundSource(),
               1.0F,
               1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
            );
      }

      this.level().addFreshEntity(var2);
      this.didSpit = true;
   }

   void setDidSpit(boolean var1) {
      this.didSpit = var1;
   }

   @Override
   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      int var4 = this.calculateFallDamage(var1, var2);
      if (var4 <= 0) {
         return false;
      } else {
         if (var1 >= 6.0F) {
            this.hurt(var3, (float)var4);
            if (this.isVehicle()) {
               for(Entity var6 : this.getIndirectPassengers()) {
                  var6.hurt(var3, (float)var4);
               }
            }
         }

         this.playBlockFallSound();
         return true;
      }
   }

   public void leaveCaravan() {
      if (this.caravanHead != null) {
         this.caravanHead.caravanTail = null;
      }

      this.caravanHead = null;
   }

   public void joinCaravan(Llama var1) {
      this.caravanHead = var1;
      this.caravanHead.caravanTail = this;
   }

   public boolean hasCaravanTail() {
      return this.caravanTail != null;
   }

   public boolean inCaravan() {
      return this.caravanHead != null;
   }

   @Nullable
   public Llama getCaravanHead() {
      return this.caravanHead;
   }

   @Override
   protected double followLeashSpeed() {
      return 2.0;
   }

   @Override
   protected void followMommy() {
      if (!this.inCaravan() && this.isBaby()) {
         super.followMommy();
      }
   }

   @Override
   public boolean canEatGrass() {
      return false;
   }

   @Override
   public void performRangedAttack(LivingEntity var1, float var2) {
      this.spit(var1);
   }

   @Override
   public Vec3 getLeashOffset() {
      return new Vec3(0.0, 0.75 * (double)this.getEyeHeight(), (double)this.getBbWidth() * 0.5);
   }

   @Override
   public EntityDimensions getDefaultDimensions(Pose var1) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(var1);
   }

   @Override
   protected Vec3 getPassengerAttachmentPoint(Entity var1, EntityDimensions var2, float var3) {
      return getDefaultPassengerAttachmentPoint(this, var1, var2.attachments());
   }

   static class LlamaAttackWolfGoal extends NearestAttackableTargetGoal<Wolf> {
      public LlamaAttackWolfGoal(Llama var1) {
         super(var1, Wolf.class, 16, false, true, var0 -> !((Wolf)var0).isTame());
      }

      @Override
      protected double getFollowDistance() {
         return super.getFollowDistance() * 0.25;
      }
   }

   static class LlamaGroupData extends AgeableMob.AgeableMobGroupData {
      public final Llama.Variant variant;

      LlamaGroupData(Llama.Variant var1) {
         super(true);
         this.variant = var1;
      }
   }

   static class LlamaHurtByTargetGoal extends HurtByTargetGoal {
      public LlamaHurtByTargetGoal(Llama var1) {
         super(var1);
      }

      // $VF: Could not properly define all variable types!
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      @Override
      public boolean canContinueToUse() {
         Mob var2 = this.mob;
         if (var2 instanceof Llama var1 && var1.didSpit) {
            var1.setDidSpit(false);
            return false;
         }

         return super.canContinueToUse();
      }
   }

   public static enum Variant implements StringRepresentable {
      CREAMY(0, "creamy"),
      WHITE(1, "white"),
      BROWN(2, "brown"),
      GRAY(3, "gray");

      public static final Codec<Llama.Variant> CODEC = StringRepresentable.fromEnum(Llama.Variant::values);
      private static final IntFunction<Llama.Variant> BY_ID = ByIdMap.continuous(Llama.Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
      final int id;
      private final String name;

      private Variant(int var3, String var4) {
         this.id = var3;
         this.name = var4;
      }

      public int getId() {
         return this.id;
      }

      public static Llama.Variant byId(int var0) {
         return BY_ID.apply(var0);
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
