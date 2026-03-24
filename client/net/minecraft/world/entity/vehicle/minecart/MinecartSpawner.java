package net.minecraft.world.entity.vehicle.minecart;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MinecartSpawner extends AbstractMinecart {
   private final BaseSpawner spawner = new BaseSpawner() {
      {
         Objects.requireNonNull(MinecartSpawner.this);
      }

      public void broadcastEvent(final Level level, final BlockPos pos, final int id) {
         level.broadcastEntityEvent(MinecartSpawner.this, (byte)id);
      }
   };
   private final Runnable ticker;

   public MinecartSpawner(final EntityType<? extends MinecartSpawner> type, final Level level) {
      super(type, level);
      this.ticker = this.createTicker(level);
   }

   protected Item getDropItem() {
      return Items.MINECART;
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.MINECART);
   }

   private Runnable createTicker(final Level level) {
      return level instanceof ServerLevel ? () -> this.spawner.serverTick((ServerLevel)level, this.blockPosition()) : () -> this.spawner.clientTick(level, this.blockPosition());
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.SPAWNER.defaultBlockState();
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.spawner.load(this.level(), this.blockPosition(), input);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      this.spawner.save(output);
   }

   public void handleEntityEvent(final byte id) {
      this.spawner.onEventTriggered(this.level(), id);
   }

   public void tick() {
      super.tick();
      this.ticker.run();
   }

   public BaseSpawner getSpawner() {
      return this.spawner;
   }
}
