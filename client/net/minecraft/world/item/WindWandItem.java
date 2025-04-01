package net.minecraft.world.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.level.Level;

public class WindWandItem extends Item {
   public WindWandItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      if (var1 instanceof ServerLevel var4) {
         Projectile.spawnProjectileFromRotation((var2x, var3x, var4x) -> new WindCharge(var2, var1, var2.position().x(), var2.getEyePosition().y(), var2.position().z()), var4, Items.WIND_CHARGE.getDefaultInstance().copy(), var2, 0.0F, WindChargeItem.PROJECTILE_SHOOT_POWER, 0.0F);
      }

      return InteractionResult.SUCCESS;
   }
}
