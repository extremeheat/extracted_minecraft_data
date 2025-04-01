package net.minecraft.world.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerUnlocks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FireWandItem extends Item {
   public FireWandItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      if (var1 instanceof ServerLevel) {
         Vec3 var4 = var2.getViewVector(1.0F);
         if (var2 instanceof ServerPlayer) {
            ServerPlayer var5 = (ServerPlayer)var2;
            if (var5.isActive(PlayerUnlocks.SORCERER_SUPREME) && var5.isActive(PlayerUnlocks.DRAGON_FIRE)) {
               DragonFireball var7 = new DragonFireball(var1, var2, var4.normalize());
               var7.accelerationPower = 1.0;
               var7.setPos(var2.getX() + var4.x * 4.0, var2.getY(0.5), var7.getZ() + var4.z * 4.0);
               var1.addFreshEntity(var7);
            } else {
               LargeFireball var6 = new LargeFireball(var1, var2, var4.normalize(), 1);
               var6.accelerationPower = 1.0;
               var6.setPos(var2.getX() + var4.x * 4.0, var2.getY(0.5), var6.getZ() + var4.z * 4.0);
               var1.addFreshEntity(var6);
            }
         }
      }

      return InteractionResult.SUCCESS;
   }
}
