package net.minecraft.world.entity.livingblock.interact;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class LeashToPlayer implements OnInteract {
   public LeashToPlayer() {
      super();
   }

   public InteractionResult apply(final Player player, final InteractionHand interactionHand, final Vec3 vec3, final LivingBlock livingBlock) {
      if (livingBlock.isLeashed()) {
         livingBlock.removeLeash();
         livingBlock.playSound(SoundEvents.LEAD_UNTIED);
         return InteractionResult.SUCCESS;
      } else {
         livingBlock.setLeashedTo(player, true);
         livingBlock.playSound(SoundEvents.LEAD_TIED);
         return InteractionResult.SUCCESS;
      }
   }
}
