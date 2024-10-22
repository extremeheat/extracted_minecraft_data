package net.minecraft.world.entity.animal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Cow extends Animal {
   private static final EntityDimensions BABY_DIMENSIONS = EntityType.COW.getDimensions().scale(0.5F).withEyeHeight(0.665F);

   public Cow(EntityType<? extends Cow> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, var0 -> var0.is(ItemTags.COW_FOOD), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.COW_FOOD);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.COW_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.COW_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.COW_DEATH;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F;
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(Items.BUCKET) && !this.isBaby()) {
         var1.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
         ItemStack var4 = ItemUtils.createFilledResult(var3, var1, Items.MILK_BUCKET.getDefaultInstance());
         var1.setItemInHand(var2, var4);
         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   @Nullable
   public Cow getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return EntityType.COW.create(var1, EntitySpawnReason.BREEDING);
   }

   @Override
   public EntityDimensions getDefaultDimensions(Pose var1) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(var1);
   }
}
