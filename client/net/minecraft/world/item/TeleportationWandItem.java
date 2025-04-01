package net.minecraft.world.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;

public class TeleportationWandItem extends Item {
   public TeleportationWandItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      if (var1 instanceof ServerLevel var4) {
         Projectile.spawnProjectileFromRotation((var0, var1x, var2x) -> new ThrownEnderpearl(var0, var1x, var2x, true), var4, Items.ENDER_PEARL.getDefaultInstance().copy(), var2, 0.0F, 10.0F, 0.0F);
      }

      return InteractionResult.SUCCESS;
   }
}
