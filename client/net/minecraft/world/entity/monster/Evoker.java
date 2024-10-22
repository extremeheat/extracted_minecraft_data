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
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
import net.minecraft.world.entity.monster.creaking.Creaking;
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

   @Override
   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new Evoker.EvokerCastingSpellGoal());
      this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6, 1.0));
      this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Creaking.class, 8.0F, 1.0, 1.2));
      this.goalSelector.addGoal(4, new Evoker.EvokerSummonSpellGoal());
      this.goalSelector.addGoal(5, new Evoker.EvokerAttackSpellGoal());
      this.goalSelector.addGoal(6, new Evoker.EvokerWololoSpellGoal());
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.5).add(Attributes.FOLLOW_RANGE, 12.0).add(Attributes.MAX_HEALTH, 24.0);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
   }

   @Override
   public SoundEvent getCelebrateSound() {
      return SoundEvents.EVOKER_CELEBRATE;
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
   }

   @Override
   protected boolean considersEntityAsAlly(Entity var1) {
      if (var1 == this) {
         return true;
      } else if (super.considersEntityAsAlly(var1)) {
         return true;
      } else {
         if (var1 instanceof Vex var2 && var2.getOwner() != null) {
            return this.considersEntityAsAlly(var2.getOwner());
         }

         return false;
      }
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.EVOKER_AMBIENT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.EVOKER_DEATH;
   }

   @Override
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

   @Override
   protected SoundEvent getCastingSoundEvent() {
      return SoundEvents.EVOKER_CAST_SPELL;
   }

   @Override
   public void applyRaidBuffs(ServerLevel var1, int var2, boolean var3) {
   }

   class EvokerAttackSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
      EvokerAttackSpellGoal() {
         super();
      }

      @Override
      protected int getCastingTime() {
         return 40;
      }

      @Override
      protected int getCastingInterval() {
         return 100;
      }

      @Override
      protected void performSpellCasting() {
         LivingEntity var1 = Evoker.this.getTarget();
         double var2 = Math.min(var1.getY(), Evoker.this.getY());
         double var4 = Math.max(var1.getY(), Evoker.this.getY()) + 1.0;
         float var6 = (float)Mth.atan2(var1.getZ() - Evoker.this.getZ(), var1.getX() - Evoker.this.getX());
         if (Evoker.this.distanceToSqr(var1) < 9.0) {
            for (int var7 = 0; var7 < 5; var7++) {
               float var8 = var6 + (float)var7 * 3.1415927F * 0.4F;
               this.createSpellEntity(Evoker.this.getX() + (double)Mth.cos(var8) * 1.5, Evoker.this.getZ() + (double)Mth.sin(var8) * 1.5, var2, var4, var8, 0);
            }

            for (int var11 = 0; var11 < 8; var11++) {
               float var13 = var6 + (float)var11 * 3.1415927F * 2.0F / 8.0F + 1.2566371F;
               this.createSpellEntity(
                  Evoker.this.getX() + (double)Mth.cos(var13) * 2.5, Evoker.this.getZ() + (double)Mth.sin(var13) * 2.5, var2, var4, var13, 3
               );
            }
         } else {
            for (int var12 = 0; var12 < 16; var12++) {
               double var14 = 1.25 * (double)(var12 + 1);
               int var10 = 1 * var12;
               this.createSpellEntity(
                  Evoker.this.getX() + (double)Mth.cos(var6) * var14, Evoker.this.getZ() + (double)Mth.sin(var6) * var14, var2, var4, var6, var10
               );
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
         } while (var11.getY() >= Mth.floor(var5) - 1);

         if (var12) {
            Evoker.this.level().addFreshEntity(new EvokerFangs(Evoker.this.level(), var1, (double)var11.getY() + var13, var3, var9, var10, Evoker.this));
            Evoker.this.level().gameEvent(GameEvent.ENTITY_PLACE, new Vec3(var1, (double)var11.getY() + var13, var3), GameEvent.Context.of(Evoker.this));
         }
      }

      @Override
      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.EVOKER_PREPARE_ATTACK;
      }

      @Override
      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.FANGS;
      }
   }

   class EvokerCastingSpellGoal extends SpellcasterIllager.SpellcasterCastingSpellGoal {
      EvokerCastingSpellGoal() {
         super();
      }

      @Override
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

      @Override
      public boolean canUse() {
         if (!super.canUse()) {
            return false;
         } else {
            int var1 = getServerLevel(Evoker.this.level())
               .getNearbyEntities(Vex.class, this.vexCountTargeting, Evoker.this, Evoker.this.getBoundingBox().inflate(16.0))
               .size();
            return Evoker.this.random.nextInt(8) + 1 > var1;
         }
      }

      @Override
      protected int getCastingTime() {
         return 100;
      }

      @Override
      protected int getCastingInterval() {
         return 340;
      }

      @Override
      protected void performSpellCasting() {
         ServerLevel var1 = (ServerLevel)Evoker.this.level();
         PlayerTeam var2 = Evoker.this.getTeam();

         for (int var3 = 0; var3 < 3; var3++) {
            BlockPos var4 = Evoker.this.blockPosition().offset(-2 + Evoker.this.random.nextInt(5), 1, -2 + Evoker.this.random.nextInt(5));
            Vex var5 = EntityType.VEX.create(Evoker.this.level(), EntitySpawnReason.MOB_SUMMONED);
            if (var5 != null) {
               var5.moveTo(var4, 0.0F, 0.0F);
               var5.finalizeSpawn(var1, Evoker.this.level().getCurrentDifficultyAt(var4), EntitySpawnReason.MOB_SUMMONED, null);
               var5.setOwner(Evoker.this);
               var5.setBoundOrigin(var4);
               var5.setLimitedLife(20 * (30 + Evoker.this.random.nextInt(90)));
               if (var2 != null) {
                  var1.getScoreboard().addPlayerToTeam(var5.getScoreboardName(), var2);
               }

               var1.addFreshEntityWithPassengers(var5);
               var1.gameEvent(GameEvent.ENTITY_PLACE, var4, GameEvent.Context.of(Evoker.this));
            }
         }
      }

      @Override
      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.EVOKER_PREPARE_SUMMON;
      }

      @Override
      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.SUMMON_VEX;
      }
   }

   public class EvokerWololoSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
      private final TargetingConditions wololoTargeting = TargetingConditions.forNonCombat()
         .range(16.0)
         .selector((var0, var1) -> ((Sheep)var0).getColor() == DyeColor.BLUE);

      public EvokerWololoSpellGoal() {
         super();
      }

      @Override
      public boolean canUse() {
         if (Evoker.this.getTarget() != null) {
            return false;
         } else if (Evoker.this.isCastingSpell()) {
            return false;
         } else if (Evoker.this.tickCount < this.nextAttackTickCount) {
            return false;
         } else {
            ServerLevel var1 = getServerLevel(Evoker.this.level());
            if (!var1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
               return false;
            } else {
               List var2 = var1.getNearbyEntities(Sheep.class, this.wololoTargeting, Evoker.this, Evoker.this.getBoundingBox().inflate(16.0, 4.0, 16.0));
               if (var2.isEmpty()) {
                  return false;
               } else {
                  Evoker.this.setWololoTarget((Sheep)var2.get(Evoker.this.random.nextInt(var2.size())));
                  return true;
               }
            }
         }
      }

      @Override
      public boolean canContinueToUse() {
         return Evoker.this.getWololoTarget() != null && this.attackWarmupDelay > 0;
      }

      @Override
      public void stop() {
         super.stop();
         Evoker.this.setWololoTarget(null);
      }

      @Override
      protected void performSpellCasting() {
         Sheep var1 = Evoker.this.getWololoTarget();
         if (var1 != null && var1.isAlive()) {
            var1.setColor(DyeColor.RED);
         }
      }

      @Override
      protected int getCastWarmupTime() {
         return 40;
      }

      @Override
      protected int getCastingTime() {
         return 60;
      }

      @Override
      protected int getCastingInterval() {
         return 140;
      }

      @Override
      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.EVOKER_PREPARE_WOLOLO;
      }

      @Override
      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.WOLOLO;
      }
   }
}
