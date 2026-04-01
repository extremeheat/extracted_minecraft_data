package net.minecraft.world.entity.livingblock.interact;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class TeleportPlayerRandomly implements OnInteract {
   public TeleportPlayerRandomly() {
      super();
   }

   public InteractionResult apply(final Player player, final InteractionHand interactionHand, final Vec3 vec3, final LivingBlock livingBlock) {
      Vec3 position = player.position();
      RandomSource random = player.getRandom();
      double x = position.x() + (random.nextDouble() - 0.5) * 64.0;
      double y = position.y() + (double)(random.nextInt(64) - 32);
      double z = position.z() + (random.nextDouble() - 0.5) * 64.0;
      player.randomTeleport(x, y, z, true);
      player.level().playSound((Entity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS);
      return InteractionResult.SUCCESS;
   }
}
