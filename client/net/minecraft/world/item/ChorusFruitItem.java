package net.minecraft.world.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class ChorusFruitItem extends Item {
   public ChorusFruitItem(Item.Properties var1) {
      super(var1);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      ItemStack var4 = super.finishUsingItem(var1, var2, var3);
      if (!var2.isClientSide) {
         for(int var5 = 0; var5 < 16; ++var5) {
            double var6 = var3.getX() + (var3.getRandom().nextDouble() - 0.5) * 16.0;
            double var8 = Mth.clamp(var3.getY() + (double)(var3.getRandom().nextInt(16) - 8), (double)var2.getMinBuildHeight(), (double)(var2.getMinBuildHeight() + ((ServerLevel)var2).getLogicalHeight() - 1));
            double var10 = var3.getZ() + (var3.getRandom().nextDouble() - 0.5) * 16.0;
            if (var3.isPassenger()) {
               var3.stopRiding();
            }

            Vec3 var12 = var3.position();
            if (var3.randomTeleport(var6, var8, var10, true)) {
               var2.gameEvent(GameEvent.TELEPORT, var12, GameEvent.Context.of((Entity)var3));
               SoundSource var13;
               SoundEvent var14;
               if (var3 instanceof Fox) {
                  var14 = SoundEvents.FOX_TELEPORT;
                  var13 = SoundSource.NEUTRAL;
               } else {
                  var14 = SoundEvents.CHORUS_FRUIT_TELEPORT;
                  var13 = SoundSource.PLAYERS;
               }

               var2.playSound((Player)null, var3.getX(), var3.getY(), var3.getZ(), var14, var13);
               var3.resetFallDistance();
               break;
            }
         }

         if (var3 instanceof Player) {
            Player var15 = (Player)var3;
            var15.getCooldowns().addCooldown(this, 20);
         }
      }

      return var4;
   }
}
