package net.minecraft.world.entity.decoration;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemFrame extends HangingEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final EntityDataAccessor<ItemStack> DATA_ITEM;
   private static final EntityDataAccessor<Integer> DATA_ROTATION;
   private float dropChance = 1.0F;

   public ItemFrame(EntityType<? extends ItemFrame> var1, Level var2) {
      super(var1, var2);
   }

   public ItemFrame(Level var1, BlockPos var2, Direction var3) {
      super(EntityType.ITEM_FRAME, var1, var2);
      this.setDirection(var3);
   }

   protected float getEyeHeight(Pose var1, EntityDimensions var2) {
      return 0.0F;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
      this.getEntityData().define(DATA_ROTATION, 0);
   }

   protected void setDirection(Direction var1) {
      Validate.notNull(var1);
      this.direction = var1;
      if (var1.getAxis().isHorizontal()) {
         this.xRot = 0.0F;
         this.yRot = (float)(this.direction.get2DDataValue() * 90);
      } else {
         this.xRot = (float)(-90 * var1.getAxisDirection().getStep());
         this.yRot = 0.0F;
      }

      this.xRotO = this.xRot;
      this.yRotO = this.yRot;
      this.recalculateBoundingBox();
   }

   protected void recalculateBoundingBox() {
      if (this.direction != null) {
         double var1 = 0.46875D;
         this.x = (double)this.pos.getX() + 0.5D - (double)this.direction.getStepX() * 0.46875D;
         this.y = (double)this.pos.getY() + 0.5D - (double)this.direction.getStepY() * 0.46875D;
         this.z = (double)this.pos.getZ() + 0.5D - (double)this.direction.getStepZ() * 0.46875D;
         double var3 = (double)this.getWidth();
         double var5 = (double)this.getHeight();
         double var7 = (double)this.getWidth();
         Direction.Axis var9 = this.direction.getAxis();
         switch(var9) {
         case X:
            var3 = 1.0D;
            break;
         case Y:
            var5 = 1.0D;
            break;
         case Z:
            var7 = 1.0D;
         }

         var3 /= 32.0D;
         var5 /= 32.0D;
         var7 /= 32.0D;
         this.setBoundingBox(new AABB(this.x - var3, this.y - var5, this.z - var7, this.x + var3, this.y + var5, this.z + var7));
      }
   }

   public boolean survives() {
      if (!this.level.noCollision(this)) {
         return false;
      } else {
         BlockState var1 = this.level.getBlockState(this.pos.relative(this.direction.getOpposite()));
         return var1.getMaterial().isSolid() || this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(var1) ? this.level.getEntities((Entity)this, this.getBoundingBox(), HANGING_ENTITY).isEmpty() : false;
      }
   }

   public float getPickRadius() {
      return 0.0F;
   }

   public void kill() {
      this.removeFramedMap(this.getItem());
      super.kill();
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (!var1.isExplosion() && !this.getItem().isEmpty()) {
         if (!this.level.isClientSide) {
            this.dropItem(var1.getEntity(), false);
            this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.hurt(var1, var2);
      }
   }

   public int getWidth() {
      return 12;
   }

   public int getHeight() {
      return 12;
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = 16.0D;
      var3 *= 64.0D * getViewScale();
      return var1 < var3 * var3;
   }

   public void dropItem(@Nullable Entity var1) {
      this.playSound(SoundEvents.ITEM_FRAME_BREAK, 1.0F, 1.0F);
      this.dropItem(var1, true);
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.ITEM_FRAME_PLACE, 1.0F, 1.0F);
   }

   private void dropItem(@Nullable Entity var1, boolean var2) {
      if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         if (var1 == null) {
            this.removeFramedMap(this.getItem());
         }

      } else {
         ItemStack var3 = this.getItem();
         this.setItem(ItemStack.EMPTY);
         if (var1 instanceof Player) {
            Player var4 = (Player)var1;
            if (var4.abilities.instabuild) {
               this.removeFramedMap(var3);
               return;
            }
         }

         if (var2) {
            this.spawnAtLocation(Items.ITEM_FRAME);
         }

         if (!var3.isEmpty()) {
            var3 = var3.copy();
            this.removeFramedMap(var3);
            if (this.random.nextFloat() < this.dropChance) {
               this.spawnAtLocation(var3);
            }
         }

      }
   }

   private void removeFramedMap(ItemStack var1) {
      if (var1.getItem() == Items.FILLED_MAP) {
         MapItemSavedData var2 = MapItem.getOrCreateSavedData(var1, this.level);
         var2.removedFromFrame(this.pos, this.getId());
         var2.setDirty(true);
      }

      var1.setFramed((ItemFrame)null);
   }

   public ItemStack getItem() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM);
   }

   public void setItem(ItemStack var1) {
      this.setItem(var1, true);
   }

   public void setItem(ItemStack var1, boolean var2) {
      if (!var1.isEmpty()) {
         var1 = var1.copy();
         var1.setCount(1);
         var1.setFramed(this);
      }

      this.getEntityData().set(DATA_ITEM, var1);
      if (!var1.isEmpty()) {
         this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
      }

      if (var2 && this.pos != null) {
         this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }

   }

   public boolean setSlot(int var1, ItemStack var2) {
      if (var1 == 0) {
         this.setItem(var2);
         return true;
      } else {
         return false;
      }
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (var1.equals(DATA_ITEM)) {
         ItemStack var2 = this.getItem();
         if (!var2.isEmpty() && var2.getFrame() != this) {
            var2.setFramed(this);
         }
      }

   }

   public int getRotation() {
      return (Integer)this.getEntityData().get(DATA_ROTATION);
   }

   public void setRotation(int var1) {
      this.setRotation(var1, true);
   }

   private void setRotation(int var1, boolean var2) {
      this.getEntityData().set(DATA_ROTATION, var1 % 8);
      if (var2 && this.pos != null) {
         this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (!this.getItem().isEmpty()) {
         var1.put("Item", this.getItem().save(new CompoundTag()));
         var1.putByte("ItemRotation", (byte)this.getRotation());
         var1.putFloat("ItemDropChance", this.dropChance);
      }

      var1.putByte("Facing", (byte)this.direction.get3DDataValue());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      CompoundTag var2 = var1.getCompound("Item");
      if (var2 != null && !var2.isEmpty()) {
         ItemStack var3 = ItemStack.of(var2);
         if (var3.isEmpty()) {
            LOGGER.warn("Unable to load item from: {}", var2);
         }

         ItemStack var4 = this.getItem();
         if (!var4.isEmpty() && !ItemStack.matches(var3, var4)) {
            this.removeFramedMap(var4);
         }

         this.setItem(var3, false);
         this.setRotation(var1.getByte("ItemRotation"), false);
         if (var1.contains("ItemDropChance", 99)) {
            this.dropChance = var1.getFloat("ItemDropChance");
         }
      }

      this.setDirection(Direction.from3DDataValue(var1.getByte("Facing")));
   }

   public boolean interact(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (!this.level.isClientSide) {
         if (this.getItem().isEmpty()) {
            if (!var3.isEmpty()) {
               this.setItem(var3);
               if (!var1.abilities.instabuild) {
                  var3.shrink(1);
               }
            }
         } else {
            this.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
            this.setRotation(this.getRotation() + 1);
         }
      }

      return true;
   }

   public int getAnalogOutput() {
      return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, this.getType(), this.direction.get3DDataValue(), this.getPos());
   }

   static {
      DATA_ITEM = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.ITEM_STACK);
      DATA_ROTATION = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.INT);
   }
}
