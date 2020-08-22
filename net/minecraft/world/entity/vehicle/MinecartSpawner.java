package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartSpawner extends AbstractMinecart {
   private final BaseSpawner spawner = new BaseSpawner() {
      public void broadcastEvent(int var1) {
         MinecartSpawner.this.level.broadcastEntityEvent(MinecartSpawner.this, (byte)var1);
      }

      public Level getLevel() {
         return MinecartSpawner.this.level;
      }

      public BlockPos getPos() {
         return new BlockPos(MinecartSpawner.this);
      }
   };

   public MinecartSpawner(EntityType var1, Level var2) {
      super(var1, var2);
   }

   public MinecartSpawner(Level var1, double var2, double var4, double var6) {
      super(EntityType.SPAWNER_MINECART, var1, var2, var4, var6);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.SPAWNER;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.SPAWNER.defaultBlockState();
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.spawner.load(var1);
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.spawner.save(var1);
   }

   public void handleEntityEvent(byte var1) {
      this.spawner.onEventTriggered(var1);
   }

   public void tick() {
      super.tick();
      this.spawner.tick();
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }
}
