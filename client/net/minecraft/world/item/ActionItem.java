package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;

public class ActionItem extends Item {
   public ActionItem(final Item.Properties properties) {
      super(properties);
   }

   public InteractionResult interactLivingBlock(final Player player, final LivingBlock target) {
      return InteractionResult.PASS;
   }

   public boolean attackBlock(final Player player, final LivingBlock target) {
      return !player.isSpectator();
   }

   public boolean actionOnBlock(final Player player, final BlockPos pos, final Direction direction) {
      return false;
   }

   public boolean actionOnEntity(final Player player, final Entity entity) {
      return false;
   }

   public boolean actionOnNothing(final Player player) {
      return false;
   }
}
