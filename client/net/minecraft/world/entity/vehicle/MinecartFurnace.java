package net.minecraft.world.entity.vehicle;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
   private static final EntityDataAccessor<Boolean> DATA_ID_FUEL = SynchedEntityData.defineId(MinecartFurnace.class, EntityDataSerializers.BOOLEAN);
   private static final int FUEL_TICKS_PER_ITEM = 3600;
   private static final int MAX_FUEL_TICKS = 32000;
   private int fuel;
   public double xPush;
   public double zPush;

   public MinecartFurnace(EntityType<? extends MinecartFurnace> var1, Level var2) {
      super(var1, var2);
   }

   public MinecartFurnace(Level var1, double var2, double var4, double var6) {
      super(EntityType.FURNACE_MINECART, var1, var2, var4, var6);
   }

   @Override
   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.FURNACE;
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_ID_FUEL, false);
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.fuel > 0) {
            this.fuel--;
         }

         if (this.fuel <= 0) {
            this.xPush = 0.0;
            this.zPush = 0.0;
         }

         this.setHasFuel(this.fuel > 0);
      }

      if (this.hasFuel() && this.random.nextInt(4) == 0) {
         this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8, this.getZ(), 0.0, 0.0, 0.0);
      }
   }

   @Override
   protected double getMaxSpeed() {
      return this.isInWater() ? super.getMaxSpeed() * 0.75 : super.getMaxSpeed() * 0.5;
   }

   @Override
   protected Item getDropItem() {
      return Items.FURNACE_MINECART;
   }

   @Override
   protected void moveAlongTrack() {
      double var1 = 1.0E-4;
      double var3 = 0.001;
      super.moveAlongTrack();
      Vec3 var5 = this.getDeltaMovement();
      double var6 = var5.horizontalDistanceSqr();
      double var8 = this.xPush * this.xPush + this.zPush * this.zPush;
      if (var8 > 1.0E-4 && var6 > 0.001) {
         double var10 = Math.sqrt(var6);
         double var12 = Math.sqrt(var8);
         this.xPush = var5.x / var10 * var12;
         this.zPush = var5.z / var10 * var12;
      }
   }

   @Override
   protected Vec3 applyNaturalSlowdown(Vec3 var1) {
      double var2 = this.xPush * this.xPush + this.zPush * this.zPush;
      Vec3 var4;
      if (var2 > 1.0E-7) {
         var2 = Math.sqrt(var2);
         this.xPush /= var2;
         this.zPush /= var2;
         var4 = var1.multiply(0.8, 0.0, 0.8).add(this.xPush, 0.0, this.zPush);
         if (this.isInWater()) {
            var4 = var4.scale(0.1);
         }
      } else {
         var4 = var1.multiply(0.98, 0.0, 0.98);
      }

      return super.applyNaturalSlowdown(var4);
   }

   @Override
   public InteractionResult interact(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(ItemTags.FURNACE_MINECART_FUEL) && this.fuel + 3600 <= 32000) {
         var3.consume(1, var1);
         this.fuel += 3600;
      }

      if (this.fuel > 0) {
         this.xPush = this.getX() - var1.getX();
         this.zPush = this.getZ() - var1.getZ();
      }

      return InteractionResult.SUCCESS;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putDouble("PushX", this.xPush);
      var1.putDouble("PushZ", this.zPush);
      var1.putShort("Fuel", (short)this.fuel);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.xPush = var1.getDouble("PushX");
      this.zPush = var1.getDouble("PushZ");
      this.fuel = var1.getShort("Fuel");
   }

   protected boolean hasFuel() {
      return this.entityData.get(DATA_ID_FUEL);
   }

   protected void setHasFuel(boolean var1) {
      this.entityData.set(DATA_ID_FUEL, var1);
   }

   @Override
   public BlockState getDefaultDisplayBlockState() {
      return Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH).setValue(FurnaceBlock.LIT, Boolean.valueOf(this.hasFuel()));
   }
}
