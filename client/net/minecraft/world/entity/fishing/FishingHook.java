package net.minecraft.world.entity.fishing;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FishingHook extends Entity {
   private static final EntityDataAccessor<Integer> DATA_HOOKED_ENTITY;
   private boolean inGround;
   private int life;
   private final Player owner;
   private int flightTime;
   private int nibble;
   private int timeUntilLured;
   private int timeUntilHooked;
   private float fishAngle;
   public Entity hookedIn;
   private FishingHook.FishHookState currentState;
   private final int luck;
   private final int lureSpeed;

   private FishingHook(Level var1, Player var2, int var3, int var4) {
      super(EntityType.FISHING_BOBBER, var1);
      this.currentState = FishingHook.FishHookState.FLYING;
      this.noCulling = true;
      this.owner = var2;
      this.owner.fishing = this;
      this.luck = Math.max(0, var3);
      this.lureSpeed = Math.max(0, var4);
   }

   public FishingHook(Level var1, Player var2, double var3, double var5, double var7) {
      this((Level)var1, (Player)var2, 0, 0);
      this.setPos(var3, var5, var7);
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
   }

   public FishingHook(Player var1, Level var2, int var3, int var4) {
      this(var2, var1, var3, var4);
      float var5 = this.owner.xRot;
      float var6 = this.owner.yRot;
      float var7 = Mth.cos(-var6 * 0.017453292F - 3.1415927F);
      float var8 = Mth.sin(-var6 * 0.017453292F - 3.1415927F);
      float var9 = -Mth.cos(-var5 * 0.017453292F);
      float var10 = Mth.sin(-var5 * 0.017453292F);
      double var11 = this.owner.x - (double)var8 * 0.3D;
      double var13 = this.owner.y + (double)this.owner.getEyeHeight();
      double var15 = this.owner.z - (double)var7 * 0.3D;
      this.moveTo(var11, var13, var15, var6, var5);
      Vec3 var17 = new Vec3((double)(-var8), (double)Mth.clamp(-(var10 / var9), -5.0F, 5.0F), (double)(-var7));
      double var18 = var17.length();
      var17 = var17.multiply(0.6D / var18 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / var18 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / var18 + 0.5D + this.random.nextGaussian() * 0.0045D);
      this.setDeltaMovement(var17);
      this.yRot = (float)(Mth.atan2(var17.x, var17.z) * 57.2957763671875D);
      this.xRot = (float)(Mth.atan2(var17.y, (double)Mth.sqrt(getHorizontalDistanceSqr(var17))) * 57.2957763671875D);
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_HOOKED_ENTITY, 0);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_HOOKED_ENTITY.equals(var1)) {
         int var2 = (Integer)this.getEntityData().get(DATA_HOOKED_ENTITY);
         this.hookedIn = var2 > 0 ? this.level.getEntity(var2 - 1) : null;
      }

      super.onSyncedDataUpdated(var1);
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = 64.0D;
      return var1 < 4096.0D;
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
   }

   public void tick() {
      super.tick();
      if (this.owner == null) {
         this.remove();
      } else if (this.level.isClientSide || !this.shouldStopFishing()) {
         if (this.inGround) {
            ++this.life;
            if (this.life >= 1200) {
               this.remove();
               return;
            }
         }

         float var1 = 0.0F;
         BlockPos var2 = new BlockPos(this);
         FluidState var3 = this.level.getFluidState(var2);
         if (var3.is(FluidTags.WATER)) {
            var1 = var3.getHeight(this.level, var2);
         }

         if (this.currentState == FishingHook.FishHookState.FLYING) {
            if (this.hookedIn != null) {
               this.setDeltaMovement(Vec3.ZERO);
               this.currentState = FishingHook.FishHookState.HOOKED_IN_ENTITY;
               return;
            }

            if (var1 > 0.0F) {
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.3D, 0.2D, 0.3D));
               this.currentState = FishingHook.FishHookState.BOBBING;
               return;
            }

            if (!this.level.isClientSide) {
               this.checkCollision();
            }

            if (!this.inGround && !this.onGround && !this.horizontalCollision) {
               ++this.flightTime;
            } else {
               this.flightTime = 0;
               this.setDeltaMovement(Vec3.ZERO);
            }
         } else {
            if (this.currentState == FishingHook.FishHookState.HOOKED_IN_ENTITY) {
               if (this.hookedIn != null) {
                  if (this.hookedIn.removed) {
                     this.hookedIn = null;
                     this.currentState = FishingHook.FishHookState.FLYING;
                  } else {
                     this.x = this.hookedIn.x;
                     this.y = this.hookedIn.getBoundingBox().minY + (double)this.hookedIn.getBbHeight() * 0.8D;
                     this.z = this.hookedIn.z;
                     this.setPos(this.x, this.y, this.z);
                  }
               }

               return;
            }

            if (this.currentState == FishingHook.FishHookState.BOBBING) {
               Vec3 var4 = this.getDeltaMovement();
               double var5 = this.y + var4.y - (double)var2.getY() - (double)var1;
               if (Math.abs(var5) < 0.01D) {
                  var5 += Math.signum(var5) * 0.1D;
               }

               this.setDeltaMovement(var4.x * 0.9D, var4.y - var5 * (double)this.random.nextFloat() * 0.2D, var4.z * 0.9D);
               if (!this.level.isClientSide && var1 > 0.0F) {
                  this.catchingFish(var2);
               }
            }
         }

         if (!var3.is(FluidTags.WATER)) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
         }

         this.move(MoverType.SELF, this.getDeltaMovement());
         this.updateRotation();
         double var7 = 0.92D;
         this.setDeltaMovement(this.getDeltaMovement().scale(0.92D));
         this.setPos(this.x, this.y, this.z);
      }
   }

   private boolean shouldStopFishing() {
      ItemStack var1 = this.owner.getMainHandItem();
      ItemStack var2 = this.owner.getOffhandItem();
      boolean var3 = var1.getItem() == Items.FISHING_ROD;
      boolean var4 = var2.getItem() == Items.FISHING_ROD;
      if (!this.owner.removed && this.owner.isAlive() && (var3 || var4) && this.distanceToSqr(this.owner) <= 1024.0D) {
         return false;
      } else {
         this.remove();
         return true;
      }
   }

   private void updateRotation() {
      Vec3 var1 = this.getDeltaMovement();
      float var2 = Mth.sqrt(getHorizontalDistanceSqr(var1));
      this.yRot = (float)(Mth.atan2(var1.x, var1.z) * 57.2957763671875D);

      for(this.xRot = (float)(Mth.atan2(var1.y, (double)var2) * 57.2957763671875D); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
      }

      while(this.xRot - this.xRotO >= 180.0F) {
         this.xRotO += 360.0F;
      }

      while(this.yRot - this.yRotO < -180.0F) {
         this.yRotO -= 360.0F;
      }

      while(this.yRot - this.yRotO >= 180.0F) {
         this.yRotO += 360.0F;
      }

      this.xRot = Mth.lerp(0.2F, this.xRotO, this.xRot);
      this.yRot = Mth.lerp(0.2F, this.yRotO, this.yRot);
   }

   private void checkCollision() {
      HitResult var1 = ProjectileUtil.getHitResult(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (var1x) -> {
         return !var1x.isSpectator() && (var1x.isPickable() || var1x instanceof ItemEntity) && (var1x != this.owner || this.flightTime >= 5);
      }, ClipContext.Block.COLLIDER, true);
      if (var1.getType() != HitResult.Type.MISS) {
         if (var1.getType() == HitResult.Type.ENTITY) {
            this.hookedIn = ((EntityHitResult)var1).getEntity();
            this.setHookedEntity();
         } else {
            this.inGround = true;
         }
      }

   }

   private void setHookedEntity() {
      this.getEntityData().set(DATA_HOOKED_ENTITY, this.hookedIn.getId() + 1);
   }

   private void catchingFish(BlockPos var1) {
      ServerLevel var2 = (ServerLevel)this.level;
      int var3 = 1;
      BlockPos var4 = var1.above();
      if (this.random.nextFloat() < 0.25F && this.level.isRainingAt(var4)) {
         ++var3;
      }

      if (this.random.nextFloat() < 0.5F && !this.level.canSeeSky(var4)) {
         --var3;
      }

      if (this.nibble > 0) {
         --this.nibble;
         if (this.nibble <= 0) {
            this.timeUntilLured = 0;
            this.timeUntilHooked = 0;
         } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.2D * (double)this.random.nextFloat() * (double)this.random.nextFloat(), 0.0D));
         }
      } else {
         float var5;
         float var6;
         float var7;
         double var8;
         double var10;
         double var12;
         Block var14;
         if (this.timeUntilHooked > 0) {
            this.timeUntilHooked -= var3;
            if (this.timeUntilHooked > 0) {
               this.fishAngle = (float)((double)this.fishAngle + this.random.nextGaussian() * 4.0D);
               var5 = this.fishAngle * 0.017453292F;
               var6 = Mth.sin(var5);
               var7 = Mth.cos(var5);
               var8 = this.x + (double)(var6 * (float)this.timeUntilHooked * 0.1F);
               var10 = (double)((float)Mth.floor(this.getBoundingBox().minY) + 1.0F);
               var12 = this.z + (double)(var7 * (float)this.timeUntilHooked * 0.1F);
               var14 = var2.getBlockState(new BlockPos(var8, var10 - 1.0D, var12)).getBlock();
               if (var14 == Blocks.WATER) {
                  if (this.random.nextFloat() < 0.15F) {
                     var2.sendParticles(ParticleTypes.BUBBLE, var8, var10 - 0.10000000149011612D, var12, 1, (double)var6, 0.1D, (double)var7, 0.0D);
                  }

                  float var15 = var6 * 0.04F;
                  float var16 = var7 * 0.04F;
                  var2.sendParticles(ParticleTypes.FISHING, var8, var10, var12, 0, (double)var16, 0.01D, (double)(-var15), 1.0D);
                  var2.sendParticles(ParticleTypes.FISHING, var8, var10, var12, 0, (double)(-var16), 0.01D, (double)var15, 1.0D);
               }
            } else {
               Vec3 var17 = this.getDeltaMovement();
               this.setDeltaMovement(var17.x, (double)(-0.4F * Mth.nextFloat(this.random, 0.6F, 1.0F)), var17.z);
               this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
               double var18 = this.getBoundingBox().minY + 0.5D;
               var2.sendParticles(ParticleTypes.BUBBLE, this.x, var18, this.z, (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), 0.0D, (double)this.getBbWidth(), 0.20000000298023224D);
               var2.sendParticles(ParticleTypes.FISHING, this.x, var18, this.z, (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), 0.0D, (double)this.getBbWidth(), 0.20000000298023224D);
               this.nibble = Mth.nextInt(this.random, 20, 40);
            }
         } else if (this.timeUntilLured > 0) {
            this.timeUntilLured -= var3;
            var5 = 0.15F;
            if (this.timeUntilLured < 20) {
               var5 = (float)((double)var5 + (double)(20 - this.timeUntilLured) * 0.05D);
            } else if (this.timeUntilLured < 40) {
               var5 = (float)((double)var5 + (double)(40 - this.timeUntilLured) * 0.02D);
            } else if (this.timeUntilLured < 60) {
               var5 = (float)((double)var5 + (double)(60 - this.timeUntilLured) * 0.01D);
            }

            if (this.random.nextFloat() < var5) {
               var6 = Mth.nextFloat(this.random, 0.0F, 360.0F) * 0.017453292F;
               var7 = Mth.nextFloat(this.random, 25.0F, 60.0F);
               var8 = this.x + (double)(Mth.sin(var6) * var7 * 0.1F);
               var10 = (double)((float)Mth.floor(this.getBoundingBox().minY) + 1.0F);
               var12 = this.z + (double)(Mth.cos(var6) * var7 * 0.1F);
               var14 = var2.getBlockState(new BlockPos(var8, var10 - 1.0D, var12)).getBlock();
               if (var14 == Blocks.WATER) {
                  var2.sendParticles(ParticleTypes.SPLASH, var8, var10, var12, 2 + this.random.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
               }
            }

            if (this.timeUntilLured <= 0) {
               this.fishAngle = Mth.nextFloat(this.random, 0.0F, 360.0F);
               this.timeUntilHooked = Mth.nextInt(this.random, 20, 80);
            }
         } else {
            this.timeUntilLured = Mth.nextInt(this.random, 100, 600);
            this.timeUntilLured -= this.lureSpeed * 20 * 5;
         }
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
   }

   public void readAdditionalSaveData(CompoundTag var1) {
   }

   public int retrieve(ItemStack var1) {
      if (!this.level.isClientSide && this.owner != null) {
         int var2 = 0;
         if (this.hookedIn != null) {
            this.bringInHookedEntity();
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)this.owner, var1, this, Collections.emptyList());
            this.level.broadcastEntityEvent(this, (byte)31);
            var2 = this.hookedIn instanceof ItemEntity ? 3 : 5;
         } else if (this.nibble > 0) {
            LootContext.Builder var3 = (new LootContext.Builder((ServerLevel)this.level)).withParameter(LootContextParams.BLOCK_POS, new BlockPos(this)).withParameter(LootContextParams.TOOL, var1).withRandom(this.random).withLuck((float)this.luck + this.owner.getLuck());
            LootTable var4 = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING);
            List var5 = var4.getRandomItems(var3.create(LootContextParamSets.FISHING));
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)this.owner, var1, this, var5);
            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
               ItemStack var7 = (ItemStack)var6.next();
               ItemEntity var8 = new ItemEntity(this.level, this.x, this.y, this.z, var7);
               double var9 = this.owner.x - this.x;
               double var11 = this.owner.y - this.y;
               double var13 = this.owner.z - this.z;
               double var15 = 0.1D;
               var8.setDeltaMovement(var9 * 0.1D, var11 * 0.1D + Math.sqrt(Math.sqrt(var9 * var9 + var11 * var11 + var13 * var13)) * 0.08D, var13 * 0.1D);
               this.level.addFreshEntity(var8);
               this.owner.level.addFreshEntity(new ExperienceOrb(this.owner.level, this.owner.x, this.owner.y + 0.5D, this.owner.z + 0.5D, this.random.nextInt(6) + 1));
               if (var7.getItem().is(ItemTags.FISHES)) {
                  this.owner.awardStat((ResourceLocation)Stats.FISH_CAUGHT, 1);
               }
            }

            var2 = 1;
         }

         if (this.inGround) {
            var2 = 2;
         }

         this.remove();
         return var2;
      } else {
         return 0;
      }
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 31 && this.level.isClientSide && this.hookedIn instanceof Player && ((Player)this.hookedIn).isLocalPlayer()) {
         this.bringInHookedEntity();
      }

      super.handleEntityEvent(var1);
   }

   protected void bringInHookedEntity() {
      if (this.owner != null) {
         Vec3 var1 = (new Vec3(this.owner.x - this.x, this.owner.y - this.y, this.owner.z - this.z)).scale(0.1D);
         this.hookedIn.setDeltaMovement(this.hookedIn.getDeltaMovement().add(var1));
      }
   }

   protected boolean makeStepSound() {
      return false;
   }

   public void remove() {
      super.remove();
      if (this.owner != null) {
         this.owner.fishing = null;
      }

   }

   @Nullable
   public Player getOwner() {
      return this.owner;
   }

   public boolean canChangeDimensions() {
      return false;
   }

   public Packet<?> getAddEntityPacket() {
      Player var1 = this.getOwner();
      return new ClientboundAddEntityPacket(this, var1 == null ? this.getId() : var1.getId());
   }

   static {
      DATA_HOOKED_ENTITY = SynchedEntityData.defineId(FishingHook.class, EntityDataSerializers.INT);
   }

   static enum FishHookState {
      FLYING,
      HOOKED_IN_ENTITY,
      BOBBING;

      private FishHookState() {
      }
   }
}
