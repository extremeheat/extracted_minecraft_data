package net.minecraft.world.entity.livingblock.interact;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class FlipLeverInteraction implements OnInteract {
   public FlipLeverInteraction() {
      super();
   }

   public InteractionResult apply(final Player player, final InteractionHand interactionHand, final Vec3 vec3, final LivingBlock livingBlock) {
      if (interactionHand == InteractionHand.MAIN_HAND) {
         livingBlock.setBlockState((BlockState)livingBlock.getBlockState().cycle(BlockStateProperties.POWERED));
         LeverBlock.playSound(player, livingBlock.level(), livingBlock.blockPosition(), livingBlock.getBlockState());
         return InteractionResult.SUCCESS_SERVER;
      } else {
         return InteractionResult.FAIL;
      }
   }
}
