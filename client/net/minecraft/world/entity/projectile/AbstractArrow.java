package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.OminousItemSpawner;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractArrow extends Projectile {
   private static final double ARROW_BASE_DAMAGE = 2.0;
   private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
   private static final int FLAG_CRIT = 1;
   private static final int FLAG_NOPHYSICS = 2;
   @Nullable
   private BlockState lastState;
   protected boolean inGround;
   protected int inGroundTime;
   public AbstractArrow.Pickup pickup = AbstractArrow.Pickup.DISALLOWED;
   public int shakeTime;
   private int life;
   private double baseDamage = 2.0;
   private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();
   @Nullable
   private IntOpenHashSet piercingIgnoreEntityIds;
   @Nullable
   private List<Entity> piercedAndKilledEntities;
   private ItemStack pickupItemStack = this.getDefaultPickupItem();
   @Nullable
   private ItemStack firedFromWeapon = null;

   protected AbstractArrow(EntityType<? extends AbstractArrow> var1, Level var2) {
      super(var1, var2);
   }

   protected AbstractArrow(
      EntityType<? extends AbstractArrow> var1, double var2, double var4, double var6, Level var8, ItemStack var9, @Nullable ItemStack var10
   ) {
      this(var1, var8);
      this.pickupItemStack = var9.copy();
      this.setCustomName(var9.get(DataComponents.CUSTOM_NAME));
      Unit var11 = var9.remove(DataComponents.INTANGIBLE_PROJECTILE);
      if (var11 != null) {
         this.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
      }

      this.setPos(var2, var4, var6);
      if (var10 != null && var8 instanceof ServerLevel var12) {
         if (var10.isEmpty()) {
            throw new IllegalArgumentException("Invalid weapon firing an arrow");
         }

         this.firedFromWeapon = var10.copy();
         int var13 = EnchantmentHelper.getPiercingCount(var12, var10, this.pickupItemStack);
         if (var13 > 0) {
            this.setPierceLevel((byte)var13);
         }
      }
   }

   protected AbstractArrow(EntityType<? extends AbstractArrow> var1, LivingEntity var2, Level var3, ItemStack var4, @Nullable ItemStack var5) {
      this(var1, var2.getX(), var2.getEyeY() - 0.10000000149011612, var2.getZ(), var3, var4, var5);
      this.setOwner(var2);
   }

   public void setSoundEvent(SoundEvent var1) {
      this.soundEvent = var1;
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize() * 10.0;
      if (Double.isNaN(var3)) {
         var3 = 1.0;
      }

      var3 *= 64.0 * getViewScale();
      return var1 < var3 * var3;
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(ID_FLAGS, (byte)0);
      var1.define(PIERCE_LEVEL, (byte)0);
   }

   @Override
   public void shoot(double var1, double var3, double var5, float var7, float var8) {
      super.shoot(var1, var3, var5, var7, var8);
      this.life = 0;
   }

   @Override
   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9) {
      this.setPos(var1, var3, var5);
      this.setRot(var7, var8);
   }

   @Override
   public void lerpMotion(double var1, double var3, double var5) {
      super.lerpMotion(var1, var3, var5);
      this.life = 0;
   }

   @Override
   public void tick() {
      boolean var1 = !this.isNoPhysics();
      if (var1) {
         this.applyGravity();
      }

      this.applyInertia();
      Vec3 var2 = this.getDeltaMovement();
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         double var3 = var2.horizontalDistance();
         this.setYRot((float)(Mth.atan2(var2.x, var2.z) * 57.2957763671875));
         this.setXRot((float)(Mth.atan2(var2.y, var3) * 57.2957763671875));
         this.yRotO = this.getYRot();
         this.xRotO = this.getXRot();
      }

      BlockPos var9 = this.blockPosition();
      BlockState var4 = this.level().getBlockState(var9);
      if (!var4.isAir() && var1) {
         VoxelShape var5 = var4.getCollisionShape(this.level(), var9);
         if (!var5.isEmpty()) {
            Vec3 var6 = this.position();

            for (AABB var8 : var5.toAabbs()) {
               if (var8.move(var9).contains(var6)) {
                  this.inGround = true;
                  break;
               }
            }
         }
      }

      if (this.shakeTime > 0) {
         this.shakeTime--;
      }

      if (this.isInWaterOrRain() || var4.is(Blocks.POWDER_SNOW)) {
         this.clearFire();
      }

      if (this.inGround && var1) {
         if (this.lastState != var4 && this.shouldFall()) {
            this.startFalling();
         } else if (!this.level().isClientSide) {
            this.tickDespawn();
         }

         this.inGroundTime++;
      } else {
         this.inGroundTime = 0;
         Vec3 var10 = this.position();
         if (this.isInWater()) {
            this.addBubblePatricles(var10);
         }

         if (this.isCritArrow()) {
            for (int var11 = 0; var11 < 4; var11++) {
               this.level()
                  .addParticle(
                     ParticleTypes.CRIT,
                     var10.x + var2.x * (double)var11 / 4.0,
                     var10.y + var2.y * (double)var11 / 4.0,
                     var10.z + var2.z * (double)var11 / 4.0,
                     -var2.x,
                     -var2.y + 0.2,
                     -var2.z
                  );
            }
         }

         float var12;
         if (!var1) {
            var12 = (float)(Mth.atan2(-var2.x, -var2.z) * 57.2957763671875);
         } else {
            var12 = (float)(Mth.atan2(var2.x, var2.z) * 57.2957763671875);
         }

         float var13 = (float)(Mth.atan2(var2.y, var2.horizontalDistance()) * 57.2957763671875);
         this.setXRot(lerpRotation(this.getXRot(), var13));
         this.setYRot(lerpRotation(this.getYRot(), var12));
         if (var1) {
            BlockHitResult var14 = this.level()
               .clipIncludingBorder(new ClipContext(var10, var10.add(var2), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            this.stepMoveAndHit(var14);
         } else {
            this.setPos(var10.add(var2));
            this.applyEffectsFromBlocks();
         }

         super.tick();
      }
   }

   private void stepMoveAndHit(BlockHitResult var1) {
      while (this.isAlive()) {
         Vec3 var2 = this.position();
         EntityHitResult var3 = this.findHitEntity(var2, var1.getLocation());
         Vec3 var4 = Objects.requireNonNullElse(var3, var1).getLocation();
         this.setPos(var4);
         this.applyEffectsFromBlocks(var2, var4);
         if (this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
            this.handlePortal();
         }

         if (var3 == null) {
            if (this.isAlive() && var1.getType() != HitResult.Type.MISS) {
               this.hitTargetOrDeflectSelf(var1);
               this.hasImpulse = true;
            }
            break;
         } else if (this.isAlive() && !this.noPhysics) {
            ProjectileDeflection var5 = this.hitTargetOrDeflectSelf(var3);
            this.hasImpulse = true;
            if (this.getPierceLevel() > 0 && var5 == ProjectileDeflection.NONE) {
               continue;
            }
            break;
         }
      }
   }

   private void applyInertia() {
      Vec3 var1 = this.getDeltaMovement();
      float var2 = 0.99F;
      if (this.isInWater()) {
         var2 = this.getWaterInertia();
      }

      this.setDeltaMovement(var1.scale((double)var2));
   }

   private void addBubblePatricles(Vec3 var1) {
      Vec3 var2 = this.getDeltaMovement();

      for (int var3 = 0; var3 < 4; var3++) {
         float var4 = 0.25F;
         this.level().addParticle(ParticleTypes.BUBBLE, var1.x - var2.x * 0.25, var1.y - var2.y * 0.25, var1.z - var2.z * 0.25, var2.x, var2.y, var2.z);
      }
   }

   @Override
   protected double getDefaultGravity() {
      return 0.05;
   }

   private boolean shouldFall() {
      return this.inGround && this.level().noCollision(new AABB(this.position(), this.position()).inflate(0.06));
   }

   private void startFalling() {
      this.inGround = false;
      Vec3 var1 = this.getDeltaMovement();
      this.setDeltaMovement(
         var1.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F))
      );
      this.life = 0;
   }

   @Override
   public void move(MoverType var1, Vec3 var2) {
      super.move(var1, var2);
      if (var1 != MoverType.SELF && this.shouldFall()) {
         this.startFalling();
      }
   }

   protected void tickDespawn() {
      this.life++;
      if (this.life >= 1200) {
         this.discard();
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

   @Override
   protected void onItemBreak(Item var1) {
      this.firedFromWeapon = null;
   }

   @Override
   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      Entity var2 = var1.getEntity();
      float var3 = (float)this.getDeltaMovement().length();
      double var4 = this.baseDamage;
      Entity var6 = this.getOwner();
      DamageSource var7 = this.damageSources().arrow(this, (Entity)(var6 != null ? var6 : this));
      if (this.getWeaponItem() != null && this.level() instanceof ServerLevel var8) {
         var4 = (double)EnchantmentHelper.modifyDamage(var8, this.getWeaponItem(), var2, var7, (float)var4);
      }

      int var14 = Mth.ceil(Mth.clamp((double)var3 * var4, 0.0, 2.147483647E9));
      if (this.getPierceLevel() > 0) {
         if (this.piercingIgnoreEntityIds == null) {
            this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
         }

         if (this.piercedAndKilledEntities == null) {
            this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
         }

         if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
            this.discard();
            return;
         }

         this.piercingIgnoreEntityIds.add(var2.getId());
      }

      if (this.isCritArrow()) {
         long var15 = (long)this.random.nextInt(var14 / 2 + 2);
         var14 = (int)Math.min(var15 + (long)var14, 2147483647L);
      }

      if (var6 instanceof LivingEntity var16) {
         var16.setLastHurtMob(var2);
      }

      boolean var17 = var2.getType() == EntityType.ENDERMAN;
      int var10 = var2.getRemainingFireTicks();
      if (this.isOnFire() && !var17) {
         var2.igniteForSeconds(5.0F);
      }

      if (var2.hurtOrSimulate(var7, (float)var14)) {
         if (var17) {
            return;
         }

         if (var2 instanceof LivingEntity var11) {
            if (!this.level().isClientSide && this.getPierceLevel() <= 0) {
               var11.setArrowCount(var11.getArrowCount() + 1);
            }

            this.doKnockback(var11, var7);
            if (this.level() instanceof ServerLevel var12) {
               EnchantmentHelper.doPostAttackEffectsWithItemSource(var12, var11, var7, this.getWeaponItem());
            }

            this.doPostHurtEffects(var11);
            if (var11 != var6 && var11 instanceof Player && var6 instanceof ServerPlayer && !this.isSilent()) {
               ((ServerPlayer)var6).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
            }

            if (!var2.isAlive() && this.piercedAndKilledEntities != null) {
               this.piercedAndKilledEntities.add(var11);
            }

            if (!this.level().isClientSide && var6 instanceof ServerPlayer var19) {
               if (this.piercedAndKilledEntities != null) {
                  CriteriaTriggers.KILLED_BY_ARROW.trigger(var19, this.piercedAndKilledEntities, this.firedFromWeapon);
               } else if (!var2.isAlive()) {
                  CriteriaTriggers.KILLED_BY_ARROW.trigger(var19, List.of(var2), this.firedFromWeapon);
               }
            }
         }

         this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         if (this.getPierceLevel() <= 0) {
            this.discard();
         }
      } else {
         var2.setRemainingFireTicks(var10);
         this.deflect(ProjectileDeflection.REVERSE, var2, this.getOwner(), false);
         this.setDeltaMovement(this.getDeltaMovement().scale(0.2));
         if (this.level() instanceof ServerLevel var18 && this.getDeltaMovement().lengthSqr() < 1.0E-7) {
            if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
               this.spawnAtLocation(var18, this.getPickupItem(), 0.1F);
            }

            this.discard();
         }
      }
   }

   protected void doKnockback(LivingEntity var1, DamageSource var2) {
      double var3 = (double)(
         this.firedFromWeapon != null && this.level() instanceof ServerLevel var5
            ? EnchantmentHelper.modifyKnockback(var5, this.firedFromWeapon, var1, var2, 0.0F)
            : 0.0F
      );
      if (var3 > 0.0) {
         double var8 = Math.max(0.0, 1.0 - var1.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
         Vec3 var7 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(var3 * 0.6 * var8);
         if (var7.lengthSqr() > 0.0) {
            var1.push(var7.x, 0.1, var7.z);
         }
      }
   }

   @Override
   protected void onHitBlock(BlockHitResult var1) {
      this.lastState = this.level().getBlockState(var1.getBlockPos());
      super.onHitBlock(var1);
      ItemStack var2 = this.getWeaponItem();
      if (this.level() instanceof ServerLevel var3 && var2 != null) {
         this.hitBlockEnchantmentEffects(var3, var1, var2);
      }

      Vec3 var6 = this.getDeltaMovement();
      Vec3 var7 = new Vec3(Math.signum(var6.x), Math.signum(var6.y), Math.signum(var6.z));
      Vec3 var5 = var7.scale(0.05000000074505806);
      this.setPos(this.position().subtract(var5));
      this.setDeltaMovement(Vec3.ZERO);
      this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
      this.inGround = true;
      this.shakeTime = 7;
      this.setCritArrow(false);
      this.setPierceLevel((byte)0);
      this.setSoundEvent(SoundEvents.ARROW_HIT);
      this.resetPiercedEntities();
   }

   protected void hitBlockEnchantmentEffects(ServerLevel var1, BlockHitResult var2, ItemStack var3) {
      Vec3 var4 = var2.getBlockPos().clampLocationWithin(var2.getLocation());
      EnchantmentHelper.onHitBlock(
         var1,
         var3,
         this.getOwner() instanceof LivingEntity var5 ? var5 : null,
         this,
         null,
         var4,
         var1.getBlockState(var2.getBlockPos()),
         var1x -> this.firedFromWeapon = null
      );
   }

   @Override
   public ItemStack getWeaponItem() {
      return this.firedFromWeapon;
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
      return ProjectileUtil.getEntityHitResult(
         this.level(), this, var1, var2, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity
      );
   }

   @Override
   protected boolean canHitEntity(Entity var1) {
      return var1 instanceof Player && this.getOwner() instanceof Player var2 && !var2.canHarmPlayer((Player)var1)
         ? false
         : super.canHitEntity(var1) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(var1.getId()));
   }

   @Override
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
      var1.putString("SoundEvent", BuiltInRegistries.SOUND_EVENT.getKey(this.soundEvent).toString());
      var1.put("item", this.pickupItemStack.save(this.registryAccess()));
      if (this.firedFromWeapon != null) {
         var1.put("weapon", this.firedFromWeapon.save(this.registryAccess(), new CompoundTag()));
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.life = var1.getShort("life");
      if (var1.contains("inBlockState", 10)) {
         this.lastState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), var1.getCompound("inBlockState"));
      }

      this.shakeTime = var1.getByte("shake") & 255;
      this.inGround = var1.getBoolean("inGround");
      if (var1.contains("damage", 99)) {
         this.baseDamage = var1.getDouble("damage");
      }

      this.pickup = AbstractArrow.Pickup.byOrdinal(var1.getByte("pickup"));
      this.setCritArrow(var1.getBoolean("crit"));
      this.setPierceLevel(var1.getByte("PierceLevel"));
      if (var1.contains("SoundEvent", 8)) {
         this.soundEvent = BuiltInRegistries.SOUND_EVENT
            .getOptional(ResourceLocation.parse(var1.getString("SoundEvent")))
            .orElse(this.getDefaultHitGroundSoundEvent());
      }

      if (var1.contains("item", 10)) {
         this.setPickupItemStack(ItemStack.parse(this.registryAccess(), var1.getCompound("item")).orElse(this.getDefaultPickupItem()));
      } else {
         this.setPickupItemStack(this.getDefaultPickupItem());
      }

      if (var1.contains("weapon", 10)) {
         this.firedFromWeapon = ItemStack.parse(this.registryAccess(), var1.getCompound("weapon")).orElse(null);
      } else {
         this.firedFromWeapon = null;
      }
   }

   @Override
   public void setOwner(@Nullable Entity var1) {
      super.setOwner((Entity)var1);

      this.pickup = switch (var1) {
         case null, default -> this.pickup;
         case Player var4 when this.pickup == AbstractArrow.Pickup.DISALLOWED -> AbstractArrow.Pickup.ALLOWED;
         case OminousItemSpawner var5 -> AbstractArrow.Pickup.DISALLOWED;
      };
   }

   @Override
   public void playerTouch(Player var1) {
      if (!this.level().isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
         if (this.tryPickup(var1)) {
            var1.take(this, 1);
            this.discard();
         }
      }
   }

   protected boolean tryPickup(Player var1) {
      return switch (this.pickup) {
         case DISALLOWED -> false;
         case ALLOWED -> var1.getInventory().add(this.getPickupItem());
         case CREATIVE_ONLY -> var1.hasInfiniteMaterials();
      };
   }

   protected ItemStack getPickupItem() {
      return this.pickupItemStack.copy();
   }

   protected abstract ItemStack getDefaultPickupItem();

   @Override
   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   public ItemStack getPickupItemStackOrigin() {
      return this.pickupItemStack;
   }

   public void setBaseDamage(double var1) {
      this.baseDamage = var1;
   }

   public double getBaseDamage() {
      return this.baseDamage;
   }

   @Override
   public boolean isAttackable() {
      return this.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE);
   }

   public void setCritArrow(boolean var1) {
      this.setFlag(1, var1);
   }

   private void setPierceLevel(byte var1) {
      this.entityData.set(PIERCE_LEVEL, var1);
   }

   private void setFlag(int var1, boolean var2) {
      byte var3 = this.entityData.get(ID_FLAGS);
      if (var2) {
         this.entityData.set(ID_FLAGS, (byte)(var3 | var1));
      } else {
         this.entityData.set(ID_FLAGS, (byte)(var3 & ~var1));
      }
   }

   protected void setPickupItemStack(ItemStack var1) {
      if (!var1.isEmpty()) {
         this.pickupItemStack = var1;
      } else {
         this.pickupItemStack = this.getDefaultPickupItem();
      }
   }

   public boolean isCritArrow() {
      byte var1 = this.entityData.get(ID_FLAGS);
      return (var1 & 1) != 0;
   }

   public byte getPierceLevel() {
      return this.entityData.get(PIERCE_LEVEL);
   }

   public void setBaseDamageFromMob(float var1) {
      this.setBaseDamage((double)(var1 * 2.0F) + this.random.triangle((double)this.level().getDifficulty().getId() * 0.11, 0.57425));
   }

   protected float getWaterInertia() {
      return 0.6F;
   }

   public void setNoPhysics(boolean var1) {
      this.noPhysics = var1;
      this.setFlag(2, var1);
   }

   public boolean isNoPhysics() {
      return !this.level().isClientSide ? this.noPhysics : (this.entityData.get(ID_FLAGS) & 2) != 0;
   }

   @Override
   public boolean isPickable() {
      return super.isPickable() && !this.inGround;
   }

   @Override
   public SlotAccess getSlot(int var1) {
      return var1 == 0 ? SlotAccess.of(this::getPickupItemStackOrigin, this::setPickupItemStack) : super.getSlot(var1);
   }

   @Override
   protected boolean shouldBounceOnWorldBorder() {
      return true;
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
