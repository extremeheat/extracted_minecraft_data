package net.minecraft.world.entity.projectile;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class FishingHook extends Projectile {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final RandomSource syncronizedRandom = RandomSource.create();
   private boolean biting;
   private int outOfWaterTime;
   private static final int MAX_OUT_OF_WATER_TIME = 10;
   private static final EntityDataAccessor<Integer> DATA_HOOKED_ENTITY = SynchedEntityData.defineId(FishingHook.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_BITING = SynchedEntityData.defineId(FishingHook.class, EntityDataSerializers.BOOLEAN);
   private int life;
   private int nibble;
   private int timeUntilLured;
   private int timeUntilHooked;
   private float fishAngle;
   private boolean openWater = true;
   @Nullable
   private Entity hookedIn;
   private FishingHook.FishHookState currentState = FishingHook.FishHookState.FLYING;
   private final int luck;
   private final int lureSpeed;

   private FishingHook(EntityType<? extends FishingHook> var1, Level var2, int var3, int var4) {
      super(var1, var2);
      this.noCulling = true;
      this.luck = Math.max(0, var3);
      this.lureSpeed = Math.max(0, var4);
   }

   public FishingHook(EntityType<? extends FishingHook> var1, Level var2) {
      this(var1, var2, 0, 0);
   }

   public FishingHook(Player var1, Level var2, int var3, int var4) {
      this(EntityType.FISHING_BOBBER, var2, var3, var4);
      this.setOwner(var1);
      float var5 = var1.getXRot();
      float var6 = var1.getYRot();
      float var7 = Mth.cos(-var6 * 0.017453292F - 3.1415927F);
      float var8 = Mth.sin(-var6 * 0.017453292F - 3.1415927F);
      float var9 = -Mth.cos(-var5 * 0.017453292F);
      float var10 = Mth.sin(-var5 * 0.017453292F);
      double var11 = var1.getX() - (double)var8 * 0.3;
      double var13 = var1.getEyeY();
      double var15 = var1.getZ() - (double)var7 * 0.3;
      this.moveTo(var11, var13, var15, var6, var5);
      Vec3 var17 = new Vec3((double)(-var8), (double)Mth.clamp(-(var10 / var9), -5.0F, 5.0F), (double)(-var7));
      double var18 = var17.length();
      var17 = var17.multiply(
         0.6 / var18 + this.random.triangle(0.5, 0.0103365),
         0.6 / var18 + this.random.triangle(0.5, 0.0103365),
         0.6 / var18 + this.random.triangle(0.5, 0.0103365)
      );
      this.setDeltaMovement(var17);
      this.setYRot((float)(Mth.atan2(var17.x, var17.z) * 57.2957763671875));
      this.setXRot((float)(Mth.atan2(var17.y, var17.horizontalDistance()) * 57.2957763671875));
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_HOOKED_ENTITY, 0);
      var1.define(DATA_BITING, false);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_HOOKED_ENTITY.equals(var1)) {
         int var2 = this.getEntityData().get(DATA_HOOKED_ENTITY);
         this.hookedIn = var2 > 0 ? this.level().getEntity(var2 - 1) : null;
      }

      if (DATA_BITING.equals(var1)) {
         this.biting = this.getEntityData().get(DATA_BITING);
         if (this.biting) {
            this.setDeltaMovement(this.getDeltaMovement().x, (double)(-0.4F * Mth.nextFloat(this.syncronizedRandom, 0.6F, 1.0F)), this.getDeltaMovement().z);
         }
      }

      super.onSyncedDataUpdated(var1);
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = 64.0;
      return var1 < 4096.0;
   }

   @Override
   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9) {
   }

   @Override
   public void tick() {
      this.syncronizedRandom.setSeed(this.getUUID().getLeastSignificantBits() ^ this.level().getGameTime());
      super.tick();
      Player var1 = this.getPlayerOwner();
      if (var1 == null) {
         this.discard();
      } else if (this.level().isClientSide || !this.shouldStopFishing(var1)) {
         if (this.onGround()) {
            this.life++;
            if (this.life >= 1200) {
               this.discard();
               return;
            }
         } else {
            this.life = 0;
         }

         float var2 = 0.0F;
         BlockPos var3 = this.blockPosition();
         FluidState var4 = this.level().getFluidState(var3);
         if (var4.is(FluidTags.WATER)) {
            var2 = var4.getHeight(this.level(), var3);
         }

         boolean var5 = var2 > 0.0F;
         if (this.currentState == FishingHook.FishHookState.FLYING) {
            if (this.hookedIn != null) {
               this.setDeltaMovement(Vec3.ZERO);
               this.currentState = FishingHook.FishHookState.HOOKED_IN_ENTITY;
               return;
            }

            if (var5) {
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.3, 0.2, 0.3));
               this.currentState = FishingHook.FishHookState.BOBBING;
               return;
            }

            this.checkCollision();
         } else {
            if (this.currentState == FishingHook.FishHookState.HOOKED_IN_ENTITY) {
               if (this.hookedIn != null) {
                  if (!this.hookedIn.isRemoved() && this.hookedIn.level().dimension() == this.level().dimension()) {
                     this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8), this.hookedIn.getZ());
                  } else {
                     this.setHookedEntity(null);
                     this.currentState = FishingHook.FishHookState.FLYING;
                  }
               }

               return;
            }

            if (this.currentState == FishingHook.FishHookState.BOBBING) {
               Vec3 var6 = this.getDeltaMovement();
               double var7 = this.getY() + var6.y - (double)var3.getY() - (double)var2;
               if (Math.abs(var7) < 0.01) {
                  var7 += Math.signum(var7) * 0.1;
               }

               this.setDeltaMovement(var6.x * 0.9, var6.y - var7 * (double)this.random.nextFloat() * 0.2, var6.z * 0.9);
               if (this.nibble <= 0 && this.timeUntilHooked <= 0) {
                  this.openWater = true;
               } else {
                  this.openWater = this.openWater && this.outOfWaterTime < 10 && this.calculateOpenWater(var3);
               }

               if (var5) {
                  this.outOfWaterTime = Math.max(0, this.outOfWaterTime - 1);
                  if (this.biting) {
                     this.setDeltaMovement(
                        this.getDeltaMovement().add(0.0, -0.1 * (double)this.syncronizedRandom.nextFloat() * (double)this.syncronizedRandom.nextFloat(), 0.0)
                     );
                  }

                  if (!this.level().isClientSide) {
                     this.catchingFish(var3);
                  }
               } else {
                  this.outOfWaterTime = Math.min(10, this.outOfWaterTime + 1);
               }
            }
         }

         if (!var4.is(FluidTags.WATER)) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.03, 0.0));
         }

         this.move(MoverType.SELF, this.getDeltaMovement());
         this.updateRotation();
         if (this.currentState == FishingHook.FishHookState.FLYING && (this.onGround() || this.horizontalCollision)) {
            this.setDeltaMovement(Vec3.ZERO);
         }

         double var9 = 0.92;
         this.setDeltaMovement(this.getDeltaMovement().scale(0.92));
         this.reapplyPosition();
      }
   }

   private boolean shouldStopFishing(Player var1) {
      ItemStack var2 = var1.getMainHandItem();
      ItemStack var3 = var1.getOffhandItem();
      boolean var4 = var2.is(Items.FISHING_ROD);
      boolean var5 = var3.is(Items.FISHING_ROD);
      if (!var1.isRemoved() && var1.isAlive() && (var4 || var5) && !(this.distanceToSqr(var1) > 1024.0)) {
         return false;
      } else {
         this.discard();
         return true;
      }
   }

   private void checkCollision() {
      HitResult var1 = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      this.hitOrDeflect(var1);
   }

   @Override
   protected boolean canHitEntity(Entity var1) {
      return super.canHitEntity(var1) || var1.isAlive() && var1 instanceof ItemEntity;
   }

   @Override
   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      if (!this.level().isClientSide) {
         this.setHookedEntity(var1.getEntity());
      }
   }

   @Override
   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      this.setDeltaMovement(this.getDeltaMovement().normalize().scale(var1.distanceTo(this)));
   }

   private void setHookedEntity(@Nullable Entity var1) {
      this.hookedIn = var1;
      this.getEntityData().set(DATA_HOOKED_ENTITY, var1 == null ? 0 : var1.getId() + 1);
   }

   private void catchingFish(BlockPos var1) {
      ServerLevel var2 = (ServerLevel)this.level();
      int var3 = 1;
      BlockPos var4 = var1.above();
      if (this.random.nextFloat() < 0.25F && this.level().isRainingAt(var4)) {
         var3++;
      }

      if (this.random.nextFloat() < 0.5F && !this.level().canSeeSky(var4)) {
         var3--;
      }

      if (this.nibble > 0) {
         this.nibble--;
         if (this.nibble <= 0) {
            this.timeUntilLured = 0;
            this.timeUntilHooked = 0;
            this.getEntityData().set(DATA_BITING, false);
         }
      } else if (this.timeUntilHooked > 0) {
         this.timeUntilHooked -= var3;
         if (this.timeUntilHooked > 0) {
            this.fishAngle = this.fishAngle + (float)this.random.triangle(0.0, 9.188);
            float var5 = this.fishAngle * 0.017453292F;
            float var6 = Mth.sin(var5);
            float var7 = Mth.cos(var5);
            double var8 = this.getX() + (double)(var6 * (float)this.timeUntilHooked * 0.1F);
            double var10 = (double)((float)Mth.floor(this.getY()) + 1.0F);
            double var12 = this.getZ() + (double)(var7 * (float)this.timeUntilHooked * 0.1F);
            BlockState var14 = var2.getBlockState(BlockPos.containing(var8, var10 - 1.0, var12));
            if (var14.is(Blocks.WATER)) {
               if (this.random.nextFloat() < 0.15F) {
                  var2.sendParticles(ParticleTypes.BUBBLE, var8, var10 - 0.10000000149011612, var12, 1, (double)var6, 0.1, (double)var7, 0.0);
               }

               float var15 = var6 * 0.04F;
               float var16 = var7 * 0.04F;
               var2.sendParticles(ParticleTypes.FISHING, var8, var10, var12, 0, (double)var16, 0.01, (double)(-var15), 1.0);
               var2.sendParticles(ParticleTypes.FISHING, var8, var10, var12, 0, (double)(-var16), 0.01, (double)var15, 1.0);
            }
         } else {
            this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
            double var17 = this.getY() + 0.5;
            var2.sendParticles(
               ParticleTypes.BUBBLE,
               this.getX(),
               var17,
               this.getZ(),
               (int)(1.0F + this.getBbWidth() * 20.0F),
               (double)this.getBbWidth(),
               0.0,
               (double)this.getBbWidth(),
               0.20000000298023224
            );
            var2.sendParticles(
               ParticleTypes.FISHING,
               this.getX(),
               var17,
               this.getZ(),
               (int)(1.0F + this.getBbWidth() * 20.0F),
               (double)this.getBbWidth(),
               0.0,
               (double)this.getBbWidth(),
               0.20000000298023224
            );
            this.nibble = Mth.nextInt(this.random, 20, 40);
            this.getEntityData().set(DATA_BITING, true);
         }
      } else if (this.timeUntilLured > 0) {
         this.timeUntilLured -= var3;
         float var18 = 0.15F;
         if (this.timeUntilLured < 20) {
            var18 += (float)(20 - this.timeUntilLured) * 0.05F;
         } else if (this.timeUntilLured < 40) {
            var18 += (float)(40 - this.timeUntilLured) * 0.02F;
         } else if (this.timeUntilLured < 60) {
            var18 += (float)(60 - this.timeUntilLured) * 0.01F;
         }

         if (this.random.nextFloat() < var18) {
            float var19 = Mth.nextFloat(this.random, 0.0F, 360.0F) * 0.017453292F;
            float var20 = Mth.nextFloat(this.random, 25.0F, 60.0F);
            double var21 = this.getX() + (double)(Mth.sin(var19) * var20) * 0.1;
            double var22 = (double)((float)Mth.floor(this.getY()) + 1.0F);
            double var23 = this.getZ() + (double)(Mth.cos(var19) * var20) * 0.1;
            BlockState var24 = var2.getBlockState(BlockPos.containing(var21, var22 - 1.0, var23));
            if (var24.is(Blocks.WATER)) {
               var2.sendParticles(ParticleTypes.SPLASH, var21, var22, var23, 2 + this.random.nextInt(2), 0.10000000149011612, 0.0, 0.10000000149011612, 0.0);
            }
         }

         if (this.timeUntilLured <= 0) {
            this.fishAngle = Mth.nextFloat(this.random, 0.0F, 360.0F);
            this.timeUntilHooked = Mth.nextInt(this.random, 20, 80);
         }
      } else {
         this.timeUntilLured = Mth.nextInt(this.random, 100, 600);
         this.timeUntilLured = this.timeUntilLured - this.lureSpeed * 20 * 5;
      }
   }

   private boolean calculateOpenWater(BlockPos var1) {
      FishingHook.OpenWaterType var2 = FishingHook.OpenWaterType.INVALID;

      for (int var3 = -1; var3 <= 2; var3++) {
         FishingHook.OpenWaterType var4 = this.getOpenWaterTypeForArea(var1.offset(-2, var3, -2), var1.offset(2, var3, 2));
         switch (var4) {
            case ABOVE_WATER:
               if (var2 == FishingHook.OpenWaterType.INVALID) {
                  return false;
               }
               break;
            case INSIDE_WATER:
               if (var2 == FishingHook.OpenWaterType.ABOVE_WATER) {
                  return false;
               }
               break;
            case INVALID:
               return false;
         }

         var2 = var4;
      }

      return true;
   }

   private FishingHook.OpenWaterType getOpenWaterTypeForArea(BlockPos var1, BlockPos var2) {
      return BlockPos.betweenClosedStream(var1, var2)
         .map(this::getOpenWaterTypeForBlock)
         .reduce((var0, var1x) -> var0 == var1x ? var0 : FishingHook.OpenWaterType.INVALID)
         .orElse(FishingHook.OpenWaterType.INVALID);
   }

   private FishingHook.OpenWaterType getOpenWaterTypeForBlock(BlockPos var1) {
      BlockState var2 = this.level().getBlockState(var1);
      if (!var2.isAir() && !var2.is(Blocks.LILY_PAD)) {
         FluidState var3 = var2.getFluidState();
         return var3.is(FluidTags.WATER) && var3.isSource() && var2.getCollisionShape(this.level(), var1).isEmpty()
            ? FishingHook.OpenWaterType.INSIDE_WATER
            : FishingHook.OpenWaterType.INVALID;
      } else {
         return FishingHook.OpenWaterType.ABOVE_WATER;
      }
   }

   public boolean isOpenWaterFishing() {
      return this.openWater;
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
   }

   public int retrieve(ItemStack var1) {
      Player var2 = this.getPlayerOwner();
      if (!this.level().isClientSide && var2 != null && !this.shouldStopFishing(var2)) {
         int var3 = 0;
         if (this.hookedIn != null) {
            this.pullEntity(this.hookedIn);
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)var2, var1, this, Collections.emptyList());
            this.level().broadcastEntityEvent(this, (byte)31);
            var3 = this.hookedIn instanceof ItemEntity ? 3 : 5;
         } else if (this.nibble > 0) {
            LootParams var4 = new LootParams.Builder((ServerLevel)this.level())
               .withParameter(LootContextParams.ORIGIN, this.position())
               .withParameter(LootContextParams.TOOL, var1)
               .withParameter(LootContextParams.THIS_ENTITY, this)
               .withLuck((float)this.luck + var2.getLuck())
               .create(LootContextParamSets.FISHING);
            LootTable var5 = this.level().getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);
            ObjectArrayList var6 = var5.getRandomItems(var4);
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)var2, var1, this, var6);

            for (ItemStack var8 : var6) {
               ItemEntity var9 = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), var8);
               double var10 = var2.getX() - this.getX();
               double var12 = var2.getY() - this.getY();
               double var14 = var2.getZ() - this.getZ();
               double var16 = 0.1;
               var9.setDeltaMovement(var10 * 0.1, var12 * 0.1 + Math.sqrt(Math.sqrt(var10 * var10 + var12 * var12 + var14 * var14)) * 0.08, var14 * 0.1);
               this.level().addFreshEntity(var9);
               var2.level().addFreshEntity(new ExperienceOrb(var2.level(), var2.getX(), var2.getY() + 0.5, var2.getZ() + 0.5, this.random.nextInt(6) + 1));
               if (var8.is(ItemTags.FISHES)) {
                  var2.awardStat(Stats.FISH_CAUGHT, 1);
               }
            }

            var3 = 1;
         }

         if (this.onGround()) {
            var3 = 2;
         }

         this.discard();
         return var3;
      } else {
         return 0;
      }
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 31 && this.level().isClientSide && this.hookedIn instanceof Player && ((Player)this.hookedIn).isLocalPlayer()) {
         this.pullEntity(this.hookedIn);
      }

      super.handleEntityEvent(var1);
   }

   protected void pullEntity(Entity var1) {
      Entity var2 = this.getOwner();
      if (var2 != null) {
         Vec3 var3 = new Vec3(var2.getX() - this.getX(), var2.getY() - this.getY(), var2.getZ() - this.getZ()).scale(0.1);
         var1.setDeltaMovement(var1.getDeltaMovement().add(var3));
      }
   }

   @Override
   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   @Override
   public void remove(Entity.RemovalReason var1) {
      this.updateOwnerInfo(null);
      super.remove(var1);
   }

   @Override
   public void onClientRemoval() {
      this.updateOwnerInfo(null);
   }

   @Override
   public void setOwner(@Nullable Entity var1) {
      super.setOwner(var1);
      this.updateOwnerInfo(this);
   }

   private void updateOwnerInfo(@Nullable FishingHook var1) {
      Player var2 = this.getPlayerOwner();
      if (var2 != null) {
         var2.fishing = var1;
      }
   }

   @Nullable
   public Player getPlayerOwner() {
      Entity var1 = this.getOwner();
      return var1 instanceof Player ? (Player)var1 : null;
   }

   @Nullable
   public Entity getHookedIn() {
      return this.hookedIn;
   }

   @Override
   public boolean canChangeDimensions() {
      return false;
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      Entity var1 = this.getOwner();
      return new ClientboundAddEntityPacket(this, var1 == null ? this.getId() : var1.getId());
   }

   @Override
   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      if (this.getPlayerOwner() == null) {
         int var2 = var1.getData();
         LOGGER.error("Failed to recreate fishing hook on client. {} (id: {}) is not a valid owner.", this.level().getEntity(var2), var2);
         this.kill();
      }
   }

   static enum FishHookState {
      FLYING,
      HOOKED_IN_ENTITY,
      BOBBING;

      private FishHookState() {
      }
   }

   static enum OpenWaterType {
      ABOVE_WATER,
      INSIDE_WATER,
      INVALID;

      private OpenWaterType() {
      }
   }
}
