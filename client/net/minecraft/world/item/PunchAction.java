package net.minecraft.world.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;

public class PunchAction extends ActionItem {
   public PunchAction(final Item.Properties properties) {
      super(properties);
   }

   public boolean attackBlock(final Player player, final LivingBlock target) {
      if (player.isSpectator()) {
         return false;
      } else {
         hurtTarget(player, target, 1.0F);
         target.setAttackedBy(player);
         return true;
      }
   }

   public boolean actionOnEntity(final Player player, final Entity entity) {
      if (player.isSpectator()) {
         return false;
      } else {
         int damage;
         if (entity instanceof LivingBlock) {
            LivingBlock livingBlock = (LivingBlock)entity;
            livingBlock.setAttackedBy(player);
            damage = 1;
         } else {
            damage = 2;
         }

         hurtTarget(player, entity, (float)damage);
         return true;
      }
   }

   private static void hurtTarget(final Player player, final Entity entity, final float damage) {
      if (player instanceof ServerPlayer serverPlayer) {
         entity.hurtServer(serverPlayer.level(), player.damageSources().playerAttack(serverPlayer), damage);
      }

   }
}
