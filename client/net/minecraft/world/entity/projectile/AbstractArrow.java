package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractArrow extends Projectile {
   private static final EntityDataAccessor<Byte> ID_FLAGS;
   private static final EntityDataAccessor<Byte> PIERCE_LEVEL;
   @Nullable
   private BlockState lastState;
   protected boolean inGround;
   protected int inGroundTime;
   public AbstractArrow.Pickup pickup;
   public int shakeTime;
   private int life;
   private double baseDamage;
   private int knockback;
   private SoundEvent soundEvent;
   private IntOpenHashSet piercingIgnoreEntityIds;
   private List<Entity> piercedAndKilledEntities;

   protected AbstractArrow(EntityType<? extends AbstractArrow> var1, Level var2) {
      super(var1, var2);
      this.pickup = AbstractArrow.Pickup.DISALLOWED;
      this.baseDamage = 2.0D;
      this.soundEvent = this.getDefaultHitGroundSoundEvent();
   }

   protected AbstractArrow(EntityType<? extends AbstractArrow> var1, double var2, double var4, double var6, Level var8) {
      this(var1, var8);
      this.setPos(var2, var4, var6);
   }

   protected AbstractArrow(EntityType<? extends AbstractArrow> var1, LivingEntity var2, Level var3) {
      this(var1, var2.getX(), var2.getEyeY() - 0.10000000149011612D, var2.getZ(), var3);
      this.setOwner(var2);
      if (var2 instanceof Player) {
         this.pickup = AbstractArrow.Pickup.ALLOWED;
      }

   }

   public void setSoundEvent(SoundEvent var1) {
      this.soundEvent = var1;
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize() * 10.0D;
      if (Double.isNaN(var3)) {
         var3 = 1.0D;
      }

      var3 *= 64.0D * getViewScale();
      return var1 < var3 * var3;
   }

   protected void defineSynchedData() {
      this.entityData.define(ID_FLAGS, (byte)0);
      this.entityData.define(PIERCE_LEVEL, (byte)0);
   }

   public void shoot(double var1, double var3, double var5, float var7, float var8) {
      super.shoot(var1, var3, var5, var7, var8);
      this.life = 0;
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.setPos(var1, var3, var5);
      this.setRot(var7, var8);
   }

   public void lerpMotion(double var1, double var3, double var5) {
      super.lerpMotion(var1, var3, var5);
      this.life = 0;
   }

   public void tick() {
      super.tick();
      boolean var1 = this.isNoPhysics();
      Vec3 var2 = this.getDeltaMovement();
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float var3 = Mth.sqrt(getHorizontalDistanceSqr(var2));
         this.yRot = (float)(Mth.atan2(var2.x, var2.z) * 57.2957763671875D);
         this.xRot = (float)(Mth.atan2(var2.y, (double)var3) * 57.2957763671875D);
         this.yRotO = this.yRot;
         this.xRotO = this.xRot;
      }

      BlockPos var25 = this.blockPosition();
      BlockState var4 = this.level.getBlockState(var25);
      Vec3 var6;
      if (!var4.isAir() && !var1) {
         VoxelShape var5 = var4.getCollisionShape(this.level, var25);
         if (!var5.isEmpty()) {
            var6 = this.position();
            Iterator var7 = var5.toAabbs().iterator();

            while(var7.hasNext()) {
               AABB var8 = (AABB)var7.next();
               if (var8.move(var25).contains(var6)) {
                  this.inGround = true;
                  break;
               }
            }
         }
      }

      if (this.shakeTime > 0) {
         --this.shakeTime;
      }

      if (this.isInWaterOrRain()) {
         this.clearFire();
      }

      if (this.inGround && !var1) {
         if (this.lastState != var4 && this.shouldFall()) {
            this.startFalling();
         } else if (!this.level.isClientSide) {
            this.tickDespawn();
         }

         ++this.inGroundTime;
      } else {
         this.inGroundTime = 0;
         Vec3 var26 = this.position();
         var6 = var26.add(var2);
         Object var27 = this.level.clip(new ClipContext(var26, var6, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
         if (((HitResult)var27).getType() != HitResult.Type.MISS) {
            var6 = ((HitResult)var27).getLocation();
         }

         while(!this.removed) {
            EntityHitResult var28 = this.findHitEntity(var26, var6);
            if (var28 != null) {
               var27 = var28;
            }

            if (var27 != null && ((HitResult)var27).getType() == HitResult.Type.ENTITY) {
               Entity var9 = ((EntityHitResult)var27).getEntity();
               Entity var10 = this.getOwner();
               if (var9 instanceof Player && var10 instanceof Player && !((Player)var10).canHarmPlayer((Player)var9)) {
                  var27 = null;
                  var28 = null;
               }
            }

            if (var27 != null && !var1) {
               this.onHit((HitResult)var27);
               this.hasImpulse = true;
            }

            if (var28 == null || this.getPierceLevel() <= 0) {
               break;
            }

            var27 = null;
         }

         var2 = this.getDeltaMovement();
         double var29 = var2.x;
         double var30 = var2.y;
         double var12 = var2.z;
         if (this.isCritArrow()) {
            for(int var14 = 0; var14 < 4; ++var14) {
               this.level.addParticle(ParticleTypes.CRIT, this.getX() + var29 * (double)var14 / 4.0D, this.getY() + var30 * (double)var14 / 4.0D, this.getZ() + var12 * (double)var14 / 4.0D, -var29, -var30 + 0.2D, -var12);
            }
         }

         double var31 = this.getX() + var29;
         double var16 = this.getY() + var30;
         double var18 = this.getZ() + var12;
         float var20 = Mth.sqrt(getHorizontalDistanceSqr(var2));
         if (var1) {
            this.yRot = (float)(Mth.atan2(-var29, -var12) * 57.2957763671875D);
         } else {
            this.yRot = (float)(Mth.atan2(var29, var12) * 57.2957763671875D);
         }

         this.xRot = (float)(Mth.atan2(var30, (double)var20) * 57.2957763671875D);
         this.xRot = lerpRotation(this.xRotO, this.xRot);
         this.yRot = lerpRotation(this.yRotO, this.yRot);
         float var21 = 0.99F;
         float var22 = 0.05F;
         if (this.isInWater()) {
            for(int var23 = 0; var23 < 4; ++var23) {
               float var24 = 0.25F;
               this.level.addParticle(ParticleTypes.BUBBLE, var31 - var29 * 0.25D, var16 - var30 * 0.25D, var18 - var12 * 0.25D, var29, var30, var12);
            }

            var21 = this.getWaterInertia();
         }

         this.setDeltaMovement(var2.scale((double)var21));
         if (!this.isNoGravity() && !var1) {
            Vec3 var32 = this.getDeltaMovement();
            this.setDeltaMovement(var32.x, var32.y - 0.05000000074505806D, var32.z);
         }

         this.setPos(var31, var16, var18);
         this.checkInsideBlocks();
      }
   }

   private boolean shouldFall() {
      return this.inGround && this.level.noCollision((new AABB(this.position(), this.position())).inflate(0.06D));
   }

   private void startFalling() {
      this.inGround = false;
      Vec3 var1 = this.getDeltaMovement();
      this.setDeltaMovement(var1.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
      this.life = 0;
   }

   public void move(MoverType var1, Vec3 var2) {
      super.move(var1, var2);
      if (var1 != MoverType.SELF && this.shouldFall()) {
         this.startFalling();
      }

   }

   protected void tickDespawn() {
      ++this.life;
      if (this.life >= 1200) {
         this.remove();
      }

   }

   private void resetPiercedEntities() {
      if (this.piercedAndKilledEntities != null) {
         this.piercedAndKilledEntities.clear();
      }

      if (this.piercingIgnoreEntityIds != null) {
         this.piercingIgnoreEntityIds.clear();
      }

   }

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      Entity var2 = var1.getEntity();
      float var3 = (float)this.getDeltaMovement().length();
      int var4 = Mth.ceil(Mth.clamp((double)var3 * this.baseDamage, 0.0D, 2.147483647E9D));
      if (this.getPierceLevel() > 0) {
         if (this.piercingIgnoreEntityIds == null) {
            this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
         }

         if (this.piercedAndKilledEntities == null) {
            this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
         }

         if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
            this.remove();
            return;
         }

         this.piercingIgnoreEntityIds.add(var2.getId());
      }

      if (this.isCritArrow()) {
         long var5 = (long)this.random.nextInt(var4 / 2 + 2);
         var4 = (int)Math.min(var5 + (long)var4, 2147483647L);
      }

      Entity var6 = this.getOwner();
      DamageSource var11;
      if (var6 == null) {
         var11 = DamageSource.arrow(this, this);
      } else {
         var11 = DamageSource.arrow(this, var6);
         if (var6 instanceof LivingEntity) {
            ((LivingEntity)var6).setLastHurtMob(var2);
         }
      }

      boolean var7 = var2.getType() == EntityType.ENDERMAN;
      int var8 = var2.getRemainingFireTicks();
      if (this.isOnFire() && !var7) {
         var2.setSecondsOnFire(5);
      }

      if (var2.hurt(var11, (float)var4)) {
         if (var7) {
            return;
         }

         if (var2 instanceof LivingEntity) {
            LivingEntity var9 = (LivingEntity)var2;
            if (!this.level.isClientSide && this.getPierceLevel() <= 0) {
               var9.setArrowCount(var9.getArrowCount() + 1);
            }

            if (this.knockback > 0) {
               Vec3 var10 = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knockback * 0.6D);
               if (var10.lengthSqr() > 0.0D) {
                  var9.push(var10.x, 0.1D, var10.z);
               }
            }

            if (!this.level.isClientSide && var6 instanceof LivingEntity) {
               EnchantmentHelper.doPostHurtEffects(var9, var6);
               EnchantmentHelper.doPostDamageEffects((LivingEntity)var6, var9);
            }

            this.doPostHurtEffects(var9);
            if (var6 != null && var9 != var6 && var9 instanceof Player && var6 instanceof ServerPlayer && !this.isSilent()) {
               ((ServerPlayer)var6).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
            }

            if (!var2.isAlive() && this.piercedAndKilledEntities != null) {
               this.piercedAndKilledEntities.add(var9);
            }

            if (!this.level.isClientSide && var6 instanceof ServerPlayer) {
               ServerPlayer var12 = (ServerPlayer)var6;
               if (this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
                  CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(var12, this.piercedAndKilledEntities);
               } else if (!var2.isAlive() && this.shotFromCrossbow()) {
                  CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(var12, Arrays.asList(var2));
               }
            }
         }

         this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         if (this.getPierceLevel() <= 0) {
            this.remove();
         }
      } else {
         var2.setRemainingFireTicks(var8);
         this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
         this.yRot += 180.0F;
         this.yRotO += 180.0F;
         if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
               this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }

            this.remove();
         }
      }

   }

   protected void onHitBlock(BlockHitResult var1) {
      this.lastState = this.level.getBlockState(var1.getBlockPos());
      super.onHitBlock(var1);
      Vec3 var2 = var1.getLocation().subtract(this.getX(), this.getY(), this.getZ());
      this.setDeltaMovement(var2);
      Vec3 var3 = var2.normalize().scale(0.05000000074505806D);
      this.setPosRaw(this.getX() - var3.x, this.getY() - var3.y, this.getZ() - var3.z);
      this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
      this.inGround = true;
      this.shakeTime = 7;
      this.setCritArrow(false);
      this.setPierceLevel((byte)0);
      this.setSoundEvent(SoundEvents.ARROW_HIT);
      this.setShotFromCrossbow(false);
      this.resetPiercedEntities();
   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.ARROW_HIT;
   }

   protected final SoundEvent getHitGroundSoundEvent() {
      return this.soundEvent;
   }

   protected void doPostHurtEffects(LivingEntity var1) {
   }

   @Nullable
   protected EntityHitResult findHitEntity(Vec3 var1, Vec3 var2) {
      return ProjectileUtil.getEntityHitResult(this.level, this, var1, var2, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
   }

   protected boolean canHitEntity(Entity var1) {
      return super.canHitEntity(var1) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(var1.getId()));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putShort("life", (short)this.life);
      if (this.lastState != null) {
         var1.put("inBlockState", NbtUtils.writeBlockState(this.lastState));
      }

      var1.putByte("shake", (byte)this.shakeTime);
      var1.putBoolean("inGround", this.inGround);
      var1.putByte("pickup", (byte)this.pickup.ordinal());
      var1.putDouble("damage", this.baseDamage);
      var1.putBoolean("crit", this.isCritArrow());
      var1.putByte("PierceLevel", this.getPierceLevel());
      var1.putString("SoundEvent", Registry.SOUND_EVENT.getKey(this.soundEvent).toString());
      var1.putBoolean("ShotFromCrossbow", this.shotFromCrossbow());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.life = var1.getShort("life");
      if (var1.contains("inBlockState", 10)) {
         this.lastState = NbtUtils.readBlockState(var1.getCompound("inBlockState"));
      }

      this.shakeTime = var1.getByte("shake") & 255;
      this.inGround = var1.getBoolean("inGround");
      if (var1.contains("damage", 99)) {
         this.baseDamage = var1.getDouble("damage");
      }

      if (var1.contains("pickup", 99)) {
         this.pickup = AbstractArrow.Pickup.byOrdinal(var1.getByte("pickup"));
      } else if (var1.contains("player", 99)) {
         this.pickup = var1.getBoolean("player") ? AbstractArrow.Pickup.ALLOWED : AbstractArrow.Pickup.DISALLOWED;
      }

      this.setCritArrow(var1.getBoolean("crit"));
      this.setPierceLevel(var1.getByte("PierceLevel"));
      if (var1.contains("SoundEvent", 8)) {
         this.soundEvent = (SoundEvent)Registry.SOUND_EVENT.getOptional(new ResourceLocation(var1.getString("SoundEvent"))).orElse(this.getDefaultHitGroundSoundEvent());
      }

      this.setShotFromCrossbow(var1.getBoolean("ShotFromCrossbow"));
   }

   public void setOwner(@Nullable Entity var1) {
      super.setOwner(var1);
      if (var1 instanceof Player) {
         this.pickup = ((Player)var1).abilities.instabuild ? AbstractArrow.Pickup.CREATIVE_ONLY : AbstractArrow.Pickup.ALLOWED;
      }

   }

   public void playerTouch(Player var1) {
      if (!this.level.isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
         boolean var2 = this.pickup == AbstractArrow.Pickup.ALLOWED || this.pickup == AbstractArrow.Pickup.CREATIVE_ONLY && var1.abilities.instabuild || this.isNoPhysics() && this.getOwner().getUUID() == var1.getUUID();
         if (this.pickup == AbstractArrow.Pickup.ALLOWED && !var1.inventory.add(this.getPickupItem())) {
            var2 = false;
         }

         if (var2) {
            var1.take(this, 1);
            this.remove();
         }

      }
   }

   protected abstract ItemStack getPickupItem();

   protected boolean isMovementNoisy() {
      return false;
   }

   public void setBaseDamage(double var1) {
      this.baseDamage = var1;
   }

   public double getBaseDamage() {
      return this.baseDamage;
   }

   public void setKnockback(int var1) {
      this.knockback = var1;
   }

   public boolean isAttackable() {
      return false;
   }

   protected float getEyeHeight(Pose var1, EntityDimensions var2) {
      return 0.13F;
   }

   public void setCritArrow(boolean var1) {
      this.setFlag(1, var1);
   }

   public void setPierceLevel(byte var1) {
      this.entityData.set(PIERCE_LEVEL, var1);
   }

   private void setFlag(int var1, boolean var2) {
      byte var3 = (Byte)this.entityData.get(ID_FLAGS);
      if (var2) {
         this.entityData.set(ID_FLAGS, (byte)(var3 | var1));
      } else {
         this.entityData.set(ID_FLAGS, (byte)(var3 & ~var1));
      }

   }

   public boolean isCritArrow() {
      byte var1 = (Byte)this.entityData.get(ID_FLAGS);
      return (var1 & 1) != 0;
   }

   public boolean shotFromCrossbow() {
      byte var1 = (Byte)this.entityData.get(ID_FLAGS);
      return (var1 & 4) != 0;
   }

   public byte getPierceLevel() {
      return (Byte)this.entityData.get(PIERCE_LEVEL);
   }

   public void setEnchantmentEffectsFromEntity(LivingEntity var1, float var2) {
      int var3 = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, var1);
      int var4 = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, var1);
      this.setBaseDamage((double)(var2 * 2.0F) + this.random.nextGaussian() * 0.25D + (double)((float)this.level.getDifficulty().getId() * 0.11F));
      if (var3 > 0) {
         this.setBaseDamage(this.getBaseDamage() + (double)var3 * 0.5D + 0.5D);
      }

      if (var4 > 0) {
         this.setKnockback(var4);
      }

      if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, var1) > 0) {
         this.setSecondsOnFire(100);
      }

   }

   protected float getWaterInertia() {
      return 0.6F;
   }

   public void setNoPhysics(boolean var1) {
      this.noPhysics = var1;
      this.setFlag(2, var1);
   }

   public boolean isNoPhysics() {
      if (!this.level.isClientSide) {
         return this.noPhysics;
      } else {
         return ((Byte)this.entityData.get(ID_FLAGS) & 2) != 0;
      }
   }

   public void setShotFromCrossbow(boolean var1) {
      this.setFlag(4, var1);
   }

   public Packet<?> getAddEntityPacket() {
      Entity var1 = this.getOwner();
      return new ClientboundAddEntityPacket(this, var1 == null ? 0 : var1.getId());
   }

   static {
      ID_FLAGS = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
      PIERCE_LEVEL = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
   }

   public static enum Pickup {
      DISALLOWED,
      ALLOWED,
      CREATIVE_ONLY;

      private Pickup() {
      }

      public static AbstractArrow.Pickup byOrdinal(int var0) {
         if (var0 < 0 || var0 > values().length) {
            var0 = 0;
         }

         return values()[var0];
      }
   }
}
