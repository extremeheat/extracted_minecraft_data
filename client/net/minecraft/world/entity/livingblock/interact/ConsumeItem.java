package net.minecraft.world.entity.livingblock.interact;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.phys.Vec3;

public class ConsumeItem implements OnInteract {
   public ConsumeItem() {
      super();
   }

   public InteractionResult apply(final Player player, final InteractionHand interactionHand, final Vec3 vec3, final LivingBlock livingBlock) {
      ItemStack itemStack = livingBlock.getItemStack();
      Consumable consumable = (Consumable)itemStack.get(DataComponents.CONSUMABLE);
      if (consumable != null && consumable.canConsume(player, itemStack)) {
         if (consumable.onConsume(player.level(), player, itemStack).isEmpty()) {
            livingBlock.discard();
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }
}
