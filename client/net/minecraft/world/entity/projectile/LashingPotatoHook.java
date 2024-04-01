package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LashingPotatoHook extends Projectile {
   public static final EntityDataAccessor<Boolean> IN_BLOCK = SynchedEntityData.defineId(LashingPotatoHook.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<Float> LENGTH = SynchedEntityData.defineId(LashingPotatoHook.class, EntityDataSerializers.FLOAT);
   private static final float MAX_RANGE = 100.0F;
   private static final double SPEED = 5.0;

   public LashingPotatoHook(EntityType<? extends LashingPotatoHook> var1, Level var2) {
      super(var1, var2);
      this.noCulling = true;
   }

   public LashingPotatoHook(Level var1, Player var2) {
      this(EntityType.LASHING_POTATO_HOOK, var1);
      this.setOwner(var2);
      this.setPos(var2.getX(), var2.getEyeY() - 0.1, var2.getZ());
      this.setDeltaMovement(var2.getViewVector(1.0F).scale(5.0));
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(IN_BLOCK, false);
      var1.define(LENGTH, 0.0F);
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double var1) {
      return true;
   }

   @Override
   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9) {
   }

   @Override
   public void tick() {
      super.tick();
      Player var1 = this.getPlayerOwner();
      if (var1 != null && (this.level().isClientSide() || !this.shouldRetract(var1))) {
         HitResult var2 = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
         if (var2.getType() != HitResult.Type.MISS) {
            this.onHit(var2);
         }

         this.setPos(var2.getLocation());
         this.checkInsideBlocks();
      } else {
         this.discard();
      }
   }

   private boolean shouldRetract(Player var1) {
      if (!var1.isRemoved() && var1.isAlive() && var1.isHolding(Items.LASHING_POTATO) && !(this.distanceToSqr(var1) > 10000.0)) {
         return false;
      } else {
         this.discard();
         return true;
      }
   }

   @Override
   protected boolean canHitEntity(Entity var1) {
      return false;
   }

   @Override
   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      this.setDeltaMovement(Vec3.ZERO);
      this.setInBlock(true);
      Player var2 = this.getPlayerOwner();
      if (var2 != null) {
         double var3 = var2.getEyePosition().subtract(var1.getLocation()).length();
         this.setLength(Math.max((float)var3 * 0.5F - 3.0F, 1.5F));
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      var1.putBoolean("in_block", this.inBlock());
      var1.putFloat("length", this.length());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      this.setInBlock(var1.getBoolean("in_block"));
      this.setLength(var1.getFloat("length"));
   }

   private void setInBlock(boolean var1) {
      this.getEntityData().set(IN_BLOCK, var1);
   }

   private void setLength(float var1) {
      this.getEntityData().set(LENGTH, var1);
   }

   public boolean inBlock() {
      return this.getEntityData().get(IN_BLOCK);
   }

   public float length() {
      return this.getEntityData().get(LENGTH);
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

   private void updateOwnerInfo(@Nullable LashingPotatoHook var1) {
      Player var2 = this.getPlayerOwner();
      if (var2 != null) {
         var2.grappling = var1;
      }
   }

   @Nullable
   public Player getPlayerOwner() {
      Entity var1 = this.getOwner();
      return var1 instanceof Player ? (Player)var1 : null;
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
         this.kill();
      }
   }
}
