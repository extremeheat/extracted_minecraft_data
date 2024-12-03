package net.minecraft.world.entity.vehicle;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MinecartFurnace extends AbstractMinecart {
   private static final EntityDataAccessor<Boolean> DATA_ID_FUEL;
   private static final int FUEL_TICKS_PER_ITEM = 3600;
   private static final int MAX_FUEL_TICKS = 32000;
   private int fuel;
   public Vec3 push;

   public MinecartFurnace(EntityType<? extends MinecartFurnace> var1, Level var2) {
      super(var1, var2);
      this.push = Vec3.ZERO;
   }

   public boolean isFurnace() {
      return true;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_ID_FUEL, false);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.fuel > 0) {
            --this.fuel;
         }

         if (this.fuel <= 0) {
            this.push = Vec3.ZERO;
         }

         this.setHasFuel(this.fuel > 0);
      }

      if (this.hasFuel() && this.random.nextInt(4) == 0) {
         this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8, this.getZ(), 0.0, 0.0, 0.0);
      }

   }

   protected double getMaxSpeed(ServerLevel var1) {
      return this.isInWater() ? super.getMaxSpeed(var1) * 0.75 : super.getMaxSpeed(var1) * 0.5;
   }

   protected Item getDropItem() {
      return Items.FURNACE_MINECART;
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.FURNACE_MINECART);
   }

   protected Vec3 applyNaturalSlowdown(Vec3 var1) {
      Vec3 var2;
      if (this.push.lengthSqr() > 1.0E-7) {
         this.push = this.calculateNewPushAlong(var1);
         var2 = var1.multiply(0.8, 0.0, 0.8).add(this.push);
         if (this.isInWater()) {
            var2 = var2.scale(0.1);
         }
      } else {
         var2 = var1.multiply(0.98, 0.0, 0.98);
      }

      return super.applyNaturalSlowdown(var2);
   }

   private Vec3 calculateNewPushAlong(Vec3 var1) {
      double var2 = 1.0E-4;
      double var4 = 0.001;
      return this.push.horizontalDistanceSqr() > 1.0E-4 && var1.horizontalDistanceSqr() > 0.001 ? this.push.projectedOn(var1).normalize().scale(this.push.length()) : this.push;
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(ItemTags.FURNACE_MINECART_FUEL) && this.fuel + 3600 <= 32000) {
         var3.consume(1, var1);
         this.fuel += 3600;
      }

      if (this.fuel > 0) {
         this.push = this.position().subtract(var1.position()).horizontal();
      }

      return InteractionResult.SUCCESS;
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putDouble("PushX", this.push.x);
      var1.putDouble("PushZ", this.push.z);
      var1.putShort("Fuel", (short)this.fuel);
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      double var2 = var1.getDouble("PushX");
      double var4 = var1.getDouble("PushZ");
      this.push = new Vec3(var2, 0.0, var4);
      this.fuel = var1.getShort("Fuel");
   }

   protected boolean hasFuel() {
      return (Boolean)this.entityData.get(DATA_ID_FUEL);
   }

   protected void setHasFuel(boolean var1) {
      this.entityData.set(DATA_ID_FUEL, var1);
   }

   public BlockState getDefaultDisplayBlockState() {
      return (BlockState)((BlockState)Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH)).setValue(FurnaceBlock.LIT, this.hasFuel());
   }

   static {
      DATA_ID_FUEL = SynchedEntityData.<Boolean>defineId(MinecartFurnace.class, EntityDataSerializers.BOOLEAN);
   }
}
