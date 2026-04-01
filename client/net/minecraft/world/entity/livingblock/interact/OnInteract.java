package net.minecraft.world.entity.livingblock.interact;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface OnInteract {
   InteractionResult apply(final Player player, final InteractionHand interactionHand, final Vec3 vec3, final LivingBlock livingBlock);
}
