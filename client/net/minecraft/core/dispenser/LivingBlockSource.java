package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.item.ItemStack;

public record LivingBlockSource(ServerLevel level, LivingBlock livingBlock) implements DispenseSource {
   public LivingBlockSource {
      super();
   }

   public BlockPos pos() {
      return this.livingBlock.blockPosition();
   }

   public Direction direction() {
      return this.livingBlock.getNearestViewDirection().getOpposite();
   }

   public ItemStack insertItem(final ItemStack stack) {
      return stack;
   }
}
