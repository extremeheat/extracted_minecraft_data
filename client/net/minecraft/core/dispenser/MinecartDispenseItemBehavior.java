package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

public class MinecartDispenseItemBehavior extends DefaultDispenseItemBehavior {
   private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
   private final EntityType<? extends AbstractMinecart> entityType;

   public MinecartDispenseItemBehavior(final EntityType<? extends AbstractMinecart> entityType) {
      super();
      this.entityType = entityType;
   }

   public ItemStack execute(final DispenseSource source, final ItemStack dispensed) {
      Direction direction = source.direction();
      ServerLevel level = source.level();
      Vec3 center = source.center();
      double spawnX = center.x() + (double)direction.getStepX() * 1.125;
      double spawnY = Math.floor(center.y()) + (double)direction.getStepY();
      double spawnZ = center.z() + (double)direction.getStepZ() * 1.125;
      BlockPos front = source.pos().relative(direction);
      BlockState blockFront = level.getBlockState(front);
      double yOffset;
      if (blockFront.is(BlockTags.RAILS)) {
         if (getRailShape(blockFront).isSlope()) {
            yOffset = 0.6;
         } else {
            yOffset = 0.1;
         }
      } else {
         if (!blockFront.isAir()) {
            return this.defaultDispenseItemBehavior.dispense(source, dispensed);
         }

         BlockState blockBelow = level.getBlockState(front.below());
         if (!blockBelow.is(BlockTags.RAILS)) {
            return this.defaultDispenseItemBehavior.dispense(source, dispensed);
         }

         if (direction != Direction.DOWN && getRailShape(blockBelow).isSlope()) {
            yOffset = -0.4;
         } else {
            yOffset = -0.9;
         }
      }

      Vec3 spawnPos = new Vec3(spawnX, spawnY + yOffset, spawnZ);
      AbstractMinecart minecart = AbstractMinecart.createMinecart(level, spawnPos.x, spawnPos.y, spawnPos.z, this.entityType, EntitySpawnReason.DISPENSER, dispensed, (Player)null);
      if (minecart != null) {
         level.addFreshEntity(minecart);
         dispensed.shrink(1);
      }

      return dispensed;
   }

   private static RailShape getRailShape(final BlockState blockFront) {
      Block var2 = blockFront.getBlock();
      RailShape var10000;
      if (var2 instanceof BaseRailBlock railBlock) {
         var10000 = (RailShape)blockFront.getValue(railBlock.getShapeProperty());
      } else {
         var10000 = RailShape.NORTH_SOUTH;
      }

      return var10000;
   }

   protected void playSound(final DispenseSource source) {
      source.level().levelEvent(1000, source.pos(), 0);
   }
}
