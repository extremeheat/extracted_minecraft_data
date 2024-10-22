package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartSpawner extends AbstractMinecart {
   private final BaseSpawner spawner = new BaseSpawner() {
      @Override
      public void broadcastEvent(Level var1, BlockPos var2, int var3) {
         var1.broadcastEntityEvent(MinecartSpawner.this, (byte)var3);
      }
   };
   private final Runnable ticker;

   public MinecartSpawner(EntityType<? extends MinecartSpawner> var1, Level var2) {
      super(var1, var2);
      this.ticker = this.createTicker(var2);
   }

   @Override
   protected Item getDropItem() {
      return Items.MINECART;
   }

   @Override
   public ItemStack getPickResult() {
      return new ItemStack(Items.MINECART);
   }

   private Runnable createTicker(Level var1) {
      return var1 instanceof ServerLevel
         ? () -> this.spawner.serverTick((ServerLevel)var1, this.blockPosition())
         : () -> this.spawner.clientTick(var1, this.blockPosition());
   }

   @Override
   public BlockState getDefaultDisplayBlockState() {
      return Blocks.SPAWNER.defaultBlockState();
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.spawner.load(this.level(), this.blockPosition(), var1);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.spawner.save(var1);
   }

   @Override
   public void handleEntityEvent(byte var1) {
      this.spawner.onEventTriggered(this.level(), var1);
   }

   @Override
   public void tick() {
      super.tick();
      this.ticker.run();
   }

   public BaseSpawner getSpawner() {
      return this.spawner;
   }

   @Override
   public boolean onlyOpCanSetNbt() {
      return true;
   }
}
