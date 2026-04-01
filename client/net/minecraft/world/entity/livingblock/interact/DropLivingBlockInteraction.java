package net.minecraft.world.entity.livingblock.interact;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.livingblock.behavior.DispenseLivingBlockInteraction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DropperBlock;

public class DropLivingBlockInteraction extends DispenseLivingBlockInteraction {
   public DropLivingBlockInteraction() {
      super();
   }

   public DispenseItemBehavior getDispenseItemBehavior(final ServerLevel level, final ItemStack itemStack) {
      return DropperBlock.DISPENSE_BEHAVIOUR;
   }
}
