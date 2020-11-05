package net.minecraft.world.entity.monster;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class Creeper extends Monster implements PowerableMob {
   private static final EntityDataAccessor<Integer> DATA_SWELL_DIR;
   private static final EntityDataAccessor<Boolean> DATA_IS_POWERED;
   private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED;
   private int oldSwell;
   private int swell;
   private int maxSwell = 30;
   private int explosionRadius = 3;
   private int droppedSkulls;

   public Creeper(EntityType<? extends Creeper> var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new SwellGoal(this));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Ocelot.class, 6.0F, 1.0D, 1.2D));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, Cat.class, 6.0F, 1.0D, 1.2D));
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D);
   }

   public int getMaxFallDistance() {
      return this.getTarget() == null ? 3 : 3 + (int)(this.getHealth() - 1.0F);
   }

   public boolean causeFallDamage(float var1, float var2) {
      boolean var3 = super.causeFallDamage(var1, var2);
      this.swell = (int)((float)this.swell + var1 * 1.5F);
      if (this.swell > this.maxSwell - 5) {
         this.swell = this.maxSwell - 5;
      }

      return var3;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SWELL_DIR, -1);
      this.entityData.define(DATA_IS_POWERED, false);
      this.entityData.define(DATA_IS_IGNITED, false);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if ((Boolean)this.entityData.get(DATA_IS_POWERED)) {
         var1.putBoolean("powered", true);
      }

      var1.putShort("Fuse", (short)this.maxSwell);
      var1.putByte("ExplosionRadius", (byte)this.explosionRadius);
      var1.putBoolean("ignited", this.isIgnited());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.entityData.set(DATA_IS_POWERED, var1.getBoolean("powered"));
      if (var1.contains("Fuse", 99)) {
         this.maxSwell = var1.getShort("Fuse");
      }

      if (var1.contains("ExplosionRadius", 99)) {
         this.explosionRadius = var1.getByte("ExplosionRadius");
      }

      if (var1.getBoolean("ignited")) {
         this.ignite();
      }

   }

   public void tick() {
      if (this.isAlive()) {
         this.oldSwell = this.swell;
         if (this.isIgnited()) {
            this.setSwellDir(1);
         }

         int var1 = this.getSwellDir();
         if (var1 > 0 && this.swell == 0) {
            this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
         }

         this.swell += var1;
         if (this.swell < 0) {
            this.swell = 0;
         }

         if (this.swell >= this.maxSwell) {
            this.swell = this.maxSwell;
            this.explodeCreeper();
         }
      }

      super.tick();
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.CREEPER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.CREEPER_DEATH;
   }

   protected void dropCustomDeathLoot(DamageSource var1, int var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);
      Entity var4 = var1.getEntity();
      if (var4 != this && var4 instanceof Creeper) {
         Creeper var5 = (Creeper)var4;
         if (var5.canDropMobsSkull()) {
            var5.increaseDroppedSkulls();
            this.spawnAtLocation(Items.CREEPER_HEAD);
         }
      }

   }

   public boolean doHurtTarget(Entity var1) {
      return true;
   }

   public boolean isPowered() {
      return (Boolean)this.entityData.get(DATA_IS_POWERED);
   }

   public float getSwelling(float var1) {
      return Mth.lerp(var1, (float)this.oldSwell, (float)this.swell) / (float)(this.maxSwell - 2);
   }

   public int getSwellDir() {
      return (Integer)this.entityData.get(DATA_SWELL_DIR);
   }

   public void setSwellDir(int var1) {
      this.entityData.set(DATA_SWELL_DIR, var1);
   }

   public void thunderHit(ServerLevel var1, LightningBolt var2) {
      super.thunderHit(var1, var2);
      this.entityData.set(DATA_IS_POWERED, true);
   }

   protected InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.getItem() == Items.FLINT_AND_STEEL) {
         this.level.playSound(var1, this.getX(), this.getY(), this.getZ(), SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
         if (!this.level.isClientSide) {
            this.ignite();
            var3.hurtAndBreak(1, var1, (var1x) -> {
               var1x.broadcastBreakEvent(var2);
            });
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   private void explodeCreeper() {
      if (!this.level.isClientSide) {
         Explosion.BlockInteraction var1 = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
         float var2 = this.isPowered() ? 2.0F : 1.0F;
         this.dead = true;
         this.level.explode(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * var2, var1);
         this.remove();
         this.spawnLingeringCloud();
      }

   }

   private void spawnLingeringCloud() {
      Collection var1 = this.getActiveEffects();
      if (!var1.isEmpty()) {
         AreaEffectCloud var2 = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
         var2.setRadius(2.5F);
         var2.setRadiusOnUse(-0.5F);
         var2.setWaitTime(10);
         var2.setDuration(var2.getDuration() / 2);
         var2.setRadiusPerTick(-var2.getRadius() / (float)var2.getDuration());
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            MobEffectInstance var4 = (MobEffectInstance)var3.next();
            var2.addEffect(new MobEffectInstance(var4));
         }

         this.level.addFreshEntity(var2);
      }

   }

   public boolean isIgnited() {
      return (Boolean)this.entityData.get(DATA_IS_IGNITED);
   }

   public void ignite() {
      this.entityData.set(DATA_IS_IGNITED, true);
   }

   public boolean canDropMobsSkull() {
      return this.isPowered() && this.droppedSkulls < 1;
   }

   public void increaseDroppedSkulls() {
      ++this.droppedSkulls;
   }

   static {
      DATA_SWELL_DIR = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.INT);
      DATA_IS_POWERED = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
      DATA_IS_IGNITED = SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BOOLEAN);
   }
}
