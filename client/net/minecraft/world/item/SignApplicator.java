package net.minecraft.world.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SignTextSlot;

public interface SignApplicator {
   boolean tryApplyToSign(Level level, SignBlockEntity sign, SignTextSlot slot, ItemStack item, Player player);

   default boolean canApplyToSign(final SignText text, final ItemStack item, final Player player) {
      return text.hasMessage(player.isTextFilteringEnabled());
   }
}
