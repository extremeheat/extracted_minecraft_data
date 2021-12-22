package net.minecraft.world.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ChorusFruitItem extends Item {
   public ChorusFruitItem(Item.Properties var1) {
      super(var1);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      ItemStack var4 = super.finishUsingItem(var1, var2, var3);
      if (!var2.isClientSide) {
         double var5 = var3.getX();
         double var7 = var3.getY();
         double var9 = var3.getZ();

         for(int var11 = 0; var11 < 16; ++var11) {
            double var12 = var3.getX() + (var3.getRandom().nextDouble() - 0.5D) * 16.0D;
            double var14 = Mth.clamp(var3.getY() + (double)(var3.getRandom().nextInt(16) - 8), (double)var2.getMinBuildHeight(), (double)(var2.getMinBuildHeight() + ((ServerLevel)var2).getLogicalHeight() - 1));
            double var16 = var3.getZ() + (var3.getRandom().nextDouble() - 0.5D) * 16.0D;
            if (var3.isPassenger()) {
               var3.stopRiding();
            }

            if (var3.randomTeleport(var12, var14, var16, true)) {
               SoundEvent var18 = var3 instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
               var2.playSound((Player)null, var5, var7, var9, var18, SoundSource.PLAYERS, 1.0F, 1.0F);
               var3.playSound(var18, 1.0F, 1.0F);
               break;
            }
         }

         if (var3 instanceof Player) {
            ((Player)var3).getCooldowns().addCooldown(this, 20);
         }
      }

      return var4;
   }
}
