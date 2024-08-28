package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SpyglassItem extends Item {
   public static final int USE_DURATION = 1200;
   public static final float ZOOM_FOV_MODIFIER = 0.1F;

   public SpyglassItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      return 1200;
   }

   @Override
   public ItemUseAnimation getUseAnimation(ItemStack var1) {
      return ItemUseAnimation.SPYGLASS;
   }

   @Override
   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      var2.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
      var2.awardStat(Stats.ITEM_USED.get(this));
      return ItemUtils.startUsingInstantly(var1, var2, var3);
   }

   @Override
   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      this.stopUsing(var3);
      return var1;
   }

   @Override
   public boolean releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
      this.stopUsing(var3);
      return true;
   }

   private void stopUsing(LivingEntity var1) {
      var1.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
   }
}
