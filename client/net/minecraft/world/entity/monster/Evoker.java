package net.minecraft.world.entity.monster;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.PlayerTeam;

public class Evoker extends SpellcasterIllager {
   @Nullable
   private Sheep wololoTarget;

   public Evoker(EntityType<? extends Evoker> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 10;
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EvokerCastingSpellGoal());
      this.goalSelector.addGoal(2, new AvoidEntityGoal(this, Player.class, 8.0F, 0.6, 1.0));
      this.goalSelector.addGoal(4, new EvokerSummonSpellGoal());
      this.goalSelector.addGoal(5, new EvokerAttackSpellGoal());
      this.goalSelector.addGoal(6, new EvokerWololoSpellGoal());
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{Raider.class})).setAlertOthers());
      this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal(this, Player.class, true)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, false));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.5).add(Attributes.FOLLOW_RANGE, 12.0).add(Attributes.MAX_HEALTH, 24.0);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.EVOKER_CELEBRATE;
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
   }

   public boolean isAlliedTo(Entity var1) {
      if (var1 == null) {
         return false;
      } else if (var1 == this) {
         return true;
      } else if (super.isAlliedTo(var1)) {
         return true;
      } else if (var1 instanceof Vex) {
         Vex var2 = (Vex)var1;
         return this.isAlliedTo(var2.getOwner());
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.EVOKER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.EVOKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.EVOKER_HURT;
   }

   void setWololoTarget(@Nullable Sheep var1) {
      this.wololoTarget = var1;
   }

   @Nullable
   Sheep getWololoTarget() {
      return this.wololoTarget;
   }

   protected SoundEvent getCastingSoundEvent() {
      return SoundEvents.EVOKER_CAST_SPELL;
   }

   public void applyRaidBuffs(ServerLevel var1, int var2, boolean var3) {
   }

   class EvokerCastingSpellGoal extends SpellcasterIllager.SpellcasterCastingSpellGoal {
      EvokerCastingSpellGoal() {
         super();
      }

      public void tick() {
         if (Evoker.this.getTarget() != null) {
            Evoker.this.getLookControl().setLookAt(Evoker.this.getTarget(), (float)Evoker.this.getMaxHeadYRot(), (float)Evoker.this.getMaxHeadXRot());
         } else if (Evoker.this.getWololoTarget() != null) {
            Evoker.this.getLookControl().setLookAt(Evoker.this.getWololoTarget(), (float)Evoker.this.getMaxHeadYRot(), (float)Evoker.this.getMaxHeadXRot());
         }

      }
   }

   class EvokerSummonSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
      private final TargetingConditions vexCountTargeting = TargetingConditions.forNonCombat().range(16.0).ignoreLineOfSight().ignoreInvisibilityTesting();

      EvokerSummonSpellGoal() {
         super();
      }

      public boolean canUse() {
         if (!super.canUse()) {
            return false;
         } else {
            int var1 = Evoker.this.level().getNearbyEntities(Vex.class, this.vexCountTargeting, Evoker.this, Evoker.this.getBoundingBox().inflate(16.0)).size();
            return Evoker.this.random.nextInt(8) + 1 > var1;
         }
      }

      protected int getCastingTime() {
         return 100;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void performSpellCasting() {
         ServerLevel var1 = (ServerLevel)Evoker.this.level();
         PlayerTeam var2 = Evoker.this.getTeam();

         for(int var3 = 0; var3 < 3; ++var3) {
            BlockPos var4 = Evoker.this.blockPosition().offset(-2 + Evoker.this.random.nextInt(5), 1, -2 + Evoker.this.random.nextInt(5));
            Vex var5 = (Vex)EntityType.VEX.create(Evoker.this.level());
            if (var5 != null) {
               var5.moveTo(var4, 0.0F, 0.0F);
               var5.finalizeSpawn(var1, Evoker.this.level().getCurrentDifficultyAt(var4), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null);
               var5.setOwner(Evoker.this);
               var5.setBoundOrigin(var4);
               var5.setLimitedLife(20 * (30 + Evoker.this.random.nextInt(90)));
               if (var2 != null) {
                  var1.getScoreboard().addPlayerToTeam(var5.getScoreboardName(), var2);
               }

               var1.addFreshEntityWithPassengers(var5);
               var1.gameEvent(GameEvent.ENTITY_PLACE, var4, GameEvent.Context.of((Entity)Evoker.this));
            }
         }

      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.EVOKER_PREPARE_SUMMON;
      }

      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.SUMMON_VEX;
      }
   }

   class EvokerAttackSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
      EvokerAttackSpellGoal() {
         super();
      }

      protected int getCastingTime() {
         return 40;
      }

      protected int getCastingInterval() {
         return 100;
      }

      protected void performSpellCasting() {
         LivingEntity var1 = Evoker.this.getTarget();
         double var2 = Math.min(var1.getY(), Evoker.this.getY());
         double var4 = Math.max(var1.getY(), Evoker.this.getY()) + 1.0;
         float var6 = (float)Mth.atan2(var1.getZ() - Evoker.this.getZ(), var1.getX() - Evoker.this.getX());
         int var7;
         if (Evoker.this.distanceToSqr(var1) < 9.0) {
            float var8;
            for(var7 = 0; var7 < 5; ++var7) {
               var8 = var6 + (float)var7 * 3.1415927F * 0.4F;
               this.createSpellEntity(Evoker.this.getX() + (double)Mth.cos(var8) * 1.5, Evoker.this.getZ() + (double)Mth.sin(var8) * 1.5, var2, var4, var8, 0);
            }

            for(var7 = 0; var7 < 8; ++var7) {
               var8 = var6 + (float)var7 * 3.1415927F * 2.0F / 8.0F + 1.2566371F;
               this.createSpellEntity(Evoker.this.getX() + (double)Mth.cos(var8) * 2.5, Evoker.this.getZ() + (double)Mth.sin(var8) * 2.5, var2, var4, var8, 3);
            }
         } else {
            for(var7 = 0; var7 < 16; ++var7) {
               double var11 = 1.25 * (double)(var7 + 1);
               int var10 = 1 * var7;
               this.createSpellEntity(Evoker.this.getX() + (double)Mth.cos(var6) * var11, Evoker.this.getZ() + (double)Mth.sin(var6) * var11, var2, var4, var6, var10);
            }
         }

      }

      private void createSpellEntity(double var1, double var3, double var5, double var7, float var9, int var10) {
         BlockPos var11 = BlockPos.containing(var1, var7, var3);
         boolean var12 = false;
         double var13 = 0.0;

         do {
            BlockPos var15 = var11.below();
            BlockState var16 = Evoker.this.level().getBlockState(var15);
            if (var16.isFaceSturdy(Evoker.this.level(), var15, Direction.UP)) {
               if (!Evoker.this.level().isEmptyBlock(var11)) {
                  BlockState var17 = Evoker.this.level().getBlockState(var11);
                  VoxelShape var18 = var17.getCollisionShape(Evoker.this.level(), var11);
                  if (!var18.isEmpty()) {
                     var13 = var18.max(Direction.Axis.Y);
                  }
               }

               var12 = true;
               break;
            }

            var11 = var11.below();
         } while(var11.getY() >= Mth.floor(var5) - 1);

         if (var12) {
            Evoker.this.level().addFreshEntity(new EvokerFangs(Evoker.this.level(), var1, (double)var11.getY() + var13, var3, var9, var10, Evoker.this));
            Evoker.this.level().gameEvent(GameEvent.ENTITY_PLACE, new Vec3(var1, (double)var11.getY() + var13, var3), GameEvent.Context.of((Entity)Evoker.this));
         }

      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.EVOKER_PREPARE_ATTACK;
      }

      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.FANGS;
      }
   }

   public class EvokerWololoSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
      private final TargetingConditions wololoTargeting = TargetingConditions.forNonCombat().range(16.0).selector((var0) -> {
         return ((Sheep)var0).getColor() == DyeColor.BLUE;
      });

      public EvokerWololoSpellGoal() {
         super();
      }

      public boolean canUse() {
         if (Evoker.this.getTarget() != null) {
            return false;
         } else if (Evoker.this.isCastingSpell()) {
            return false;
         } else if (Evoker.this.tickCount < this.nextAttackTickCount) {
            return false;
         } else if (!Evoker.this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
         } else {
            List var1 = Evoker.this.level().getNearbyEntities(Sheep.class, this.wololoTargeting, Evoker.this, Evoker.this.getBoundingBox().inflate(16.0, 4.0, 16.0));
            if (var1.isEmpty()) {
               return false;
            } else {
               Evoker.this.setWololoTarget((Sheep)var1.get(Evoker.this.random.nextInt(var1.size())));
               return true;
            }
         }
      }

      public boolean canContinueToUse() {
         return Evoker.this.getWololoTarget() != null && this.attackWarmupDelay > 0;
      }

      public void stop() {
         super.stop();
         Evoker.this.setWololoTarget((Sheep)null);
      }

      protected void performSpellCasting() {
         Sheep var1 = Evoker.this.getWololoTarget();
         if (var1 != null && var1.isAlive()) {
            var1.setColor(DyeColor.RED);
         }

      }

      protected int getCastWarmupTime() {
         return 40;
      }

      protected int getCastingTime() {
         return 60;
      }

      protected int getCastingInterval() {
         return 140;
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.EVOKER_PREPARE_WOLOLO;
      }

      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.WOLOLO;
      }
   }
}
