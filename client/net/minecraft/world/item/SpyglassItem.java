package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SpyglassItem extends Item {
   public static final int USE_DURATION = 1200;
   public static final float ZOOM_FOV_MODIFIER = 0.1F;

   public SpyglassItem(Item.Properties var1) {
      super(var1);
   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      return 1200;
   }

   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.SPYGLASS;
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      var2.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
      var2.awardStat(Stats.ITEM_USED.get(this));
      return ItemUtils.startUsingInstantly(var1, var2, var3);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      this.stopUsing(var3);
      return var1;
   }

   public void releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
      this.stopUsing(var3);
   }

   private void stopUsing(LivingEntity var1) {
      var1.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
   }
}
