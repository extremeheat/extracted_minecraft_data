package net.minecraft.world.entity.pets;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class PetTurtle extends AbstractPet {
   @Nullable
   BlockPos travelPos;

   public PetTurtle(EntityType<? extends PetTurtle> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.DOOR_IRON_CLOSED, -1.0F);
      this.setPathfindingMalus(PathType.DOOR_WOOD_CLOSED, -1.0F);
      this.setPathfindingMalus(PathType.DOOR_OPEN, -1.0F);
      this.moveControl = new TurtleMoveControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
      this.goalSelector.addGoal(8, new TurtleRandomStrollGoal(this, 1.0, 100));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.STEP_HEIGHT, 1.0);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public int getAmbientSoundInterval() {
      return 200;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return !this.isInWater() && this.onGround() && !this.isBaby() ? SoundEvents.TURTLE_AMBIENT_LAND : super.getAmbientSound();
   }

   protected void playSwimSound(float var1) {
      super.playSwimSound(var1 * 1.5F);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.TURTLE_SWIM;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      SoundEvent var3 = SoundEvents.TURTLE_SHAMBLE;
      this.playSound(var3, 0.15F, 1.0F);
   }

   protected float nextStep() {
      return this.moveDist + 0.15F;
   }

   protected PathNavigation createNavigation(Level var1) {
      return new PetTurtlePathNavigation(this, var1);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return EntityType.TURTLE.create(var1, EntitySpawnReason.BREEDING);
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.TURTLE_FOOD);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      if (var2.getFluidState(var1).is(FluidTags.WATER)) {
         return 10.0F;
      } else {
         return TurtleEggBlock.onSand(var2, var1) ? 10.0F : var2.getPathfindingCostFromLightLevels(var1);
      }
   }

   public void travel(Vec3 var1) {
      if (this.isInWater()) {
         this.moveRelative(0.1F, var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
         }
      } else {
         super.travel(var1);
      }

   }

   public boolean canBeLeashed() {
      return false;
   }

   static class TurtleRandomStrollGoal extends RandomStrollGoal {
      TurtleRandomStrollGoal(PetTurtle var1, double var2, int var4) {
         super(var1, var2, var4);
      }

      public boolean canUse() {
         return !this.mob.isInWater() ? super.canUse() : false;
      }
   }

   static class TurtleMoveControl extends MoveControl {
      private final PetTurtle turtle;

      TurtleMoveControl(PetTurtle var1) {
         super(var1);
         this.turtle = var1;
      }

      private void updateSpeed() {
         if (this.turtle.isInWater()) {
            this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0, 0.005, 0.0));
            if (this.turtle.isBaby()) {
               this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 3.0F, 0.06F));
            }
         } else if (this.turtle.onGround()) {
            this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0F, 0.06F));
         }

      }

      public void tick() {
         this.updateSpeed();
         if (this.operation == MoveControl.Operation.MOVE_TO && !this.turtle.getNavigation().isDone()) {
            double var1 = this.wantedX - this.turtle.getX();
            double var3 = this.wantedY - this.turtle.getY();
            double var5 = this.wantedZ - this.turtle.getZ();
            double var7 = Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
            if (var7 < 9.999999747378752E-6) {
               this.mob.setSpeed(0.0F);
            } else {
               var3 /= var7;
               float var9 = (float)(Mth.atan2(var5, var1) * 57.2957763671875) - 90.0F;
               this.turtle.setYRot(this.rotlerp(this.turtle.getYRot(), var9, 90.0F));
               this.turtle.yBodyRot = this.turtle.getYRot();
               float var10 = (float)(this.speedModifier * this.turtle.getAttributeValue(Attributes.MOVEMENT_SPEED));
               this.turtle.setSpeed(Mth.lerp(0.125F, this.turtle.getSpeed(), var10));
               this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0, (double)this.turtle.getSpeed() * var3 * 0.1, 0.0));
            }
         } else {
            this.turtle.setSpeed(0.0F);
         }
      }
   }

   static class PetTurtlePathNavigation extends AmphibiousPathNavigation {
      PetTurtlePathNavigation(PetTurtle var1, Level var2) {
         super(var1, var2);
      }

      public boolean isStableDestination(BlockPos var1) {
         Mob var3 = this.mob;
         if (var3 instanceof PetTurtle var2) {
            if (var2.travelPos != null) {
               return this.level.getBlockState(var1).is(Blocks.WATER);
            }
         }

         return !this.level.getBlockState(var1.below()).isAir();
      }
   }
}
