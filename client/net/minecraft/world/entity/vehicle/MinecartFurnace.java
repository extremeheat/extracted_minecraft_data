package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MinecartFurnace extends AbstractMinecart {
   private static final EntityDataAccessor<Boolean> DATA_ID_FUEL;
   private int fuel;
   public double xPush;
   public double zPush;
   private static final Ingredient INGREDIENT;

   public MinecartFurnace(EntityType<? extends MinecartFurnace> var1, Level var2) {
      super(var1, var2);
   }

   public MinecartFurnace(Level var1, double var2, double var4, double var6) {
      super(EntityType.FURNACE_MINECART, var1, var2, var4, var6);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.FURNACE;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_FUEL, false);
   }

   public void tick() {
      super.tick();
      if (this.fuel > 0) {
         --this.fuel;
      }

      if (this.fuel <= 0) {
         this.xPush = 0.0D;
         this.zPush = 0.0D;
      }

      this.setHasFuel(this.fuel > 0);
      if (this.hasFuel() && this.random.nextInt(4) == 0) {
         this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.x, this.y + 0.8D, this.z, 0.0D, 0.0D, 0.0D);
      }

   }

   protected double getMaxSpeed() {
      return 0.2D;
   }

   public void destroy(DamageSource var1) {
      super.destroy(var1);
      if (!var1.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.spawnAtLocation(Blocks.FURNACE);
      }

   }

   protected void moveAlongTrack(BlockPos var1, BlockState var2) {
      super.moveAlongTrack(var1, var2);
      double var3 = this.xPush * this.xPush + this.zPush * this.zPush;
      Vec3 var5 = this.getDeltaMovement();
      if (var3 > 1.0E-4D && getHorizontalDistanceSqr(var5) > 0.001D) {
         var3 = (double)Mth.sqrt(var3);
         this.xPush /= var3;
         this.zPush /= var3;
         if (this.xPush * var5.x + this.zPush * var5.z < 0.0D) {
            this.xPush = 0.0D;
            this.zPush = 0.0D;
         } else {
            double var6 = var3 / this.getMaxSpeed();
            this.xPush *= var6;
            this.zPush *= var6;
         }
      }

   }

   protected void applyNaturalSlowdown() {
      double var1 = this.xPush * this.xPush + this.zPush * this.zPush;
      if (var1 > 1.0E-7D) {
         var1 = (double)Mth.sqrt(var1);
         this.xPush /= var1;
         this.zPush /= var1;
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.8D, 0.0D, 0.8D).add(this.xPush, 0.0D, this.zPush));
      } else {
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.98D, 0.0D, 0.98D));
      }

      super.applyNaturalSlowdown();
   }

   public boolean interact(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (INGREDIENT.test(var3) && this.fuel + 3600 <= 32000) {
         if (!var1.abilities.instabuild) {
            var3.shrink(1);
         }

         this.fuel += 3600;
      }

      this.xPush = this.x - var1.x;
      this.zPush = this.z - var1.z;
      return true;
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putDouble("PushX", this.xPush);
      var1.putDouble("PushZ", this.zPush);
      var1.putShort("Fuel", (short)this.fuel);
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.xPush = var1.getDouble("PushX");
      this.zPush = var1.getDouble("PushZ");
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
      DATA_ID_FUEL = SynchedEntityData.defineId(MinecartFurnace.class, EntityDataSerializers.BOOLEAN);
      INGREDIENT = Ingredient.of(Items.COAL, Items.CHARCOAL);
   }
}
