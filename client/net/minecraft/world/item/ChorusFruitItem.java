package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ChorusFruitItem extends Item {
   public ChorusFruitItem(Item.Properties var1) {
      super(var1);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      ItemStack var4 = super.finishUsingItem(var1, var2, var3);
      if (!var2.isClientSide) {
         double var5 = var3.x;
         double var7 = var3.y;
         double var9 = var3.z;

         for(int var11 = 0; var11 < 16; ++var11) {
            double var12 = var3.x + (var3.getRandom().nextDouble() - 0.5D) * 16.0D;
            double var14 = Mth.clamp(var3.y + (double)(var3.getRandom().nextInt(16) - 8), 0.0D, (double)(var2.getHeight() - 1));
            double var16 = var3.z + (var3.getRandom().nextDouble() - 0.5D) * 16.0D;
            if (var3.isPassenger()) {
               var3.stopRiding();
            }

            if (var3.randomTeleport(var12, var14, var16, true)) {
               var2.playSound((Player)null, var5, var7, var9, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
               var3.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
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
