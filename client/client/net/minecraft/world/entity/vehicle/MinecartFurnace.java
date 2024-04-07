package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MinecartFurnace extends AbstractMinecart {
   private static final EntityDataAccessor<Boolean> DATA_ID_FUEL = SynchedEntityData.defineId(MinecartFurnace.class, EntityDataSerializers.BOOLEAN);
   private int fuel;
   public double xPush;
   public double zPush;
   private static final Ingredient INGREDIENT = Ingredient.of(Items.COAL, Items.CHARCOAL);

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
      return (this.isInWater() ? 3.0 : 4.0) / 20.0;
   }

   @Override
   protected Item getDropItem() {
      return Items.FURNACE_MINECART;
   }

   @Override
   protected void moveAlongTrack(BlockPos var1, BlockState var2) {
      double var3 = 1.0E-4;
      double var5 = 0.001;
      super.moveAlongTrack(var1, var2);
      Vec3 var7 = this.getDeltaMovement();
      double var8 = var7.horizontalDistanceSqr();
      double var10 = this.xPush * this.xPush + this.zPush * this.zPush;
      if (var10 > 1.0E-4 && var8 > 0.001) {
         double var12 = Math.sqrt(var8);
         double var14 = Math.sqrt(var10);
         this.xPush = var7.x / var12 * var14;
         this.zPush = var7.z / var12 * var14;
      }
   }

   @Override
   protected void applyNaturalSlowdown() {
      double var1 = this.xPush * this.xPush + this.zPush * this.zPush;
      if (var1 > 1.0E-7) {
         var1 = Math.sqrt(var1);
         this.xPush /= var1;
         this.zPush /= var1;
         Vec3 var3 = this.getDeltaMovement().multiply(0.8, 0.0, 0.8).add(this.xPush, 0.0, this.zPush);
         if (this.isInWater()) {
            var3 = var3.scale(0.1);
         }

         this.setDeltaMovement(var3);
      } else {
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.98, 0.0, 0.98));
      }

      super.applyNaturalSlowdown();
   }

   @Override
   public InteractionResult interact(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (INGREDIENT.test(var3) && this.fuel + 3600 <= 32000) {
         var3.consume(1, var1);
         this.fuel += 3600;
      }

      if (this.fuel > 0) {
         this.xPush = this.getX() - var1.getX();
         this.zPush = this.getZ() - var1.getZ();
      }

      return InteractionResult.sidedSuccess(this.level().isClientSide);
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
