package net.minecraft.world.entity.livingblock.interact;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class ThrowEyeOfEnder implements OnInteract {
   public ThrowEyeOfEnder() {
      super();
   }

   public InteractionResult apply(final Player player, final InteractionHand interactionHand, final Vec3 vec3, final LivingBlock livingBlock) {
      InteractionResult result = ((EnderEyeItem)Items.ENDER_EYE).throwEnderEye(player.level(), player, interactionHand, livingBlock.getItemStack());
      if (result == InteractionResult.SUCCESS_SERVER) {
         livingBlock.discard();
      }

      return result;
   }
}
