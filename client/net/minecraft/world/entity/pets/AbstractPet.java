package net.minecraft.world.entity.pets;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerUnlocks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPet extends TamableAnimal {
   private static final float START_HEALTH = 8.0F;

   public AbstractPet(EntityType<? extends AbstractPet> var1, Level var2) {
      super(var1, var2);
      this.setTame(true, false);
      this.setInvulnerable(true);
      this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
      this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
   }

   public boolean shouldTryTeleportToOwner() {
      LivingEntity var1 = this.getOwner();
      return var1 != null && this.distanceToSqr(this.getOwner()) >= 400.0;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.FLYING_SPEED, 0.30000001192092896).add(Attributes.MAX_HEALTH, 8.0).add(Attributes.ATTACK_DAMAGE, 4.0);
   }

   public boolean isAttackable() {
      return false;
   }

   public @Nullable AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return null;
   }

   public @NotNull InteractionResult mobInteract(Player var1, InteractionHand var2) {
      if (this.isOwnedBy(var1) && var1.isActive(PlayerUnlocks.CAN_PET)) {
         this.spawnPettingParticles();
      }

      return InteractionResult.SUCCESS;
   }

   public boolean isFood(ItemStack var1) {
      return false;
   }

   protected void spawnPettingParticles() {
      SimpleParticleType var1 = ParticleTypes.HEART;

      for(int var2 = 0; var2 < 7; ++var2) {
         double var3 = this.random.nextGaussian() * 0.02;
         double var5 = this.random.nextGaussian() * 0.02;
         double var7 = this.random.nextGaussian() * 0.02;
         this.level().addParticle(var1, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), var3, var5, var7);
      }

   }
}
