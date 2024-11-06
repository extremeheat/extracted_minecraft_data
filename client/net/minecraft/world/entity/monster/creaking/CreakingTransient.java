package net.minecraft.world.entity.monster.creaking;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CreakingTransient extends Creaking {
   public static final int INVULNERABILITY_ANIMATION_DURATION = 8;
   public static final int TWITCH_DEATH_DURATION = 45;
   private static final int MAX_PLAYER_STUCK_COUNTER = 4;
   private static final EntityDataAccessor<Boolean> IS_TEARING_DOWN;
   private int invulnerabilityAnimationRemainingTicks;
   private boolean eyesGlowing;
   private int nextFlickerTime;
   @Nullable
   BlockPos homePos;
   private int playerStuckCounter;

   public CreakingTransient(EntityType<? extends Creaking> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.DAMAGE_OTHER, 8.0F);
      this.setPathfindingMalus(PathType.POWDER_SNOW, 8.0F);
      this.setPathfindingMalus(PathType.LAVA, 8.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
   }

   public void bindToCreakingHeart(BlockPos var1) {
      this.homePos = var1;
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.level().isClientSide) {
         return super.hurtServer(var1, var2, var3);
      } else if (var2.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return super.hurtServer(var1, var2, var3);
      } else if (!this.isInvulnerableTo(var1, var2) && this.invulnerabilityAnimationRemainingTicks <= 0) {
         if (this.isDeadOrDying()) {
            return false;
         } else {
            Player var4 = this.resolvePlayerResponsibleForDamage(var2);
            Entity var5 = var2.getDirectEntity();
            if (!(var5 instanceof LivingEntity) && !(var5 instanceof Projectile) && var4 == null) {
               return false;
            } else {
               this.invulnerabilityAnimationRemainingTicks = 8;
               this.level().broadcastEntityEvent(this, (byte)66);
               BlockEntity var7 = this.level().getBlockEntity(this.homePos);
               if (var7 instanceof CreakingHeartBlockEntity) {
                  CreakingHeartBlockEntity var6 = (CreakingHeartBlockEntity)var7;
                  if (var6.isProtector(this)) {
                     if (var4 != null) {
                        var6.creakingHurt();
                     }

                     this.playHurtSound(var2);
                  }
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(IS_TEARING_DOWN, false);
   }

   public void aiStep() {
      if (this.invulnerabilityAnimationRemainingTicks > 0) {
         --this.invulnerabilityAnimationRemainingTicks;
      }

      super.aiStep();
   }

   public void tick() {
      if (!this.level().isClientSide) {
         label18: {
            if (this.homePos != null) {
               BlockEntity var2 = this.level().getBlockEntity(this.homePos);
               if (var2 instanceof CreakingHeartBlockEntity) {
                  CreakingHeartBlockEntity var1 = (CreakingHeartBlockEntity)var2;
                  if (var1.isProtector(this)) {
                     break label18;
                  }
               }
            }

            this.setHealth(0.0F);
         }
      }

      super.tick();
      if (this.level().isClientSide) {
         this.setupAnimationStates();
         this.checkEyeBlink();
      }

   }

   protected void tickDeath() {
      if (this.isTearingDown()) {
         ++this.deathTime;
         if (this.deathTime > 45 && !this.level().isClientSide() && !this.isRemoved()) {
            this.tearDown();
         }
      } else {
         super.tickDeath();
      }

   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 66) {
         this.invulnerabilityAnimationRemainingTicks = 8;
         this.playHurtSound(this.damageSources().generic());
      } else {
         super.handleEntityEvent(var1);
      }

   }

   private void setupAnimationStates() {
      this.invulnerabilityAnimationState.animateWhen(this.invulnerabilityAnimationRemainingTicks > 0, this.tickCount);
      this.deathAnimationState.animateWhen(this.isTearingDown(), this.tickCount);
   }

   public void tearDown() {
      Level var2 = this.level();
      if (var2 instanceof ServerLevel var1) {
         AABB var10 = this.getBoundingBox();
         Vec3 var3 = var10.getCenter();
         double var4 = var10.getXsize() * 0.3;
         double var6 = var10.getYsize() * 0.3;
         double var8 = var10.getZsize() * 0.3;
         var1.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK_CRUMBLE, Blocks.PALE_OAK_WOOD.defaultBlockState()), var3.x, var3.y, var3.z, 100, var4, var6, var8, 0.0);
         var1.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK_CRUMBLE, (BlockState)Blocks.CREAKING_HEART.defaultBlockState().setValue(CreakingHeartBlock.ACTIVE, true)), var3.x, var3.y, var3.z, 10, var4, var6, var8, 0.0);
      }

      this.makeSound(this.getDeathSound());
      this.gameEvent(GameEvent.ENTITY_DIE);
      this.remove(Entity.RemovalReason.DISCARDED);
   }

   public void creakingDeathEffects(@Nullable DamageSource var1) {
      if (var1 != null) {
         Entity var3 = var1.getEntity();
         if (var3 instanceof LivingEntity) {
            LivingEntity var2 = (LivingEntity)var3;
            var2.awardKillScore(this, var1);
         }
      }

      this.makeSound(SoundEvents.CREAKING_TWITCH);
   }

   protected boolean canAddPassenger(Entity var1) {
      return false;
   }

   protected boolean couldAcceptPassenger() {
      return false;
   }

   protected void addPassenger(Entity var1) {
      throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
   }

   public boolean canUsePortal(boolean var1) {
      return false;
   }

   protected PathNavigation createNavigation(Level var1) {
      return new CreakingPathNavigation(this, var1);
   }

   public boolean playerIsStuckInYou() {
      List var1 = (List)this.brain.getMemory(MemoryModuleType.NEAREST_PLAYERS).orElse(List.of());
      if (var1.isEmpty()) {
         this.playerStuckCounter = 0;
         return false;
      } else {
         AABB var2 = this.getBoundingBox();
         Iterator var3 = var1.iterator();

         Player var4;
         do {
            if (!var3.hasNext()) {
               this.playerStuckCounter = 0;
               return false;
            }

            var4 = (Player)var3.next();
         } while(!var2.contains(var4.getEyePosition()));

         ++this.playerStuckCounter;
         return this.playerStuckCounter > 4;
      }
   }

   public void setTearingDown() {
      this.entityData.set(IS_TEARING_DOWN, true);
   }

   public boolean isTearingDown() {
      return (Boolean)this.entityData.get(IS_TEARING_DOWN);
   }

   public boolean hasGlowingEyes() {
      return this.eyesGlowing;
   }

   public void checkEyeBlink() {
      if (this.deathTime > this.nextFlickerTime) {
         this.nextFlickerTime = this.deathTime + this.getRandom().nextIntBetweenInclusive(this.eyesGlowing ? 2 : this.deathTime / 4, this.eyesGlowing ? 8 : this.deathTime / 2);
         this.eyesGlowing = !this.eyesGlowing;
      }

   }

   static {
      IS_TEARING_DOWN = SynchedEntityData.defineId(CreakingTransient.class, EntityDataSerializers.BOOLEAN);
   }

   class CreakingPathNavigation extends GroundPathNavigation {
      CreakingPathNavigation(final Creaking var2, final Level var3) {
         super(var2, var3);
      }

      public void tick() {
         if (CreakingTransient.this.canMove()) {
            super.tick();
         }

      }

      protected PathFinder createPathFinder(int var1) {
         this.nodeEvaluator = CreakingTransient.this.new HomeNodeEvaluator();
         this.nodeEvaluator.setCanPassDoors(true);
         return new PathFinder(this.nodeEvaluator, var1);
      }
   }

   class HomeNodeEvaluator extends WalkNodeEvaluator {
      private static final int MAX_DISTANCE_TO_HOME_SQ = 1024;

      HomeNodeEvaluator() {
         super();
      }

      public PathType getPathType(PathfindingContext var1, int var2, int var3, int var4) {
         BlockPos var5 = CreakingTransient.this.homePos;
         if (var5 == null) {
            return super.getPathType(var1, var2, var3, var4);
         } else {
            double var6 = var5.distSqr(new Vec3i(var2, var3, var4));
            return var6 > 1024.0 && var6 >= var5.distSqr(var1.mobPosition()) ? PathType.BLOCKED : super.getPathType(var1, var2, var3, var4);
         }
      }
   }
}
