package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Cow extends Animal {
   public Cow(EntityType var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(Items.WHEAT), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.COW_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.COW_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COW_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   public boolean mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.getItem() == Items.BUCKET && !var1.abilities.instabuild && !this.isBaby()) {
         var1.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
         var3.shrink(1);
         if (var3.isEmpty()) {
            var1.setItemInHand(var2, new ItemStack(Items.MILK_BUCKET));
         } else if (!var1.inventory.add(new ItemStack(Items.MILK_BUCKET))) {
            var1.drop(new ItemStack(Items.MILK_BUCKET), false);
         }

         return true;
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   public Cow getBreedOffspring(AgableMob var1) {
      return (Cow)EntityType.COW.create(this.level);
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return this.isBaby() ? var2.height * 0.95F : 1.3F;
   }

   // $FF: synthetic method
   public AgableMob getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }
}
