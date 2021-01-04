package net.minecraft.world.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class FishingRodItem extends Item {
   public FishingRodItem(Item.Properties var1) {
      super(var1);
      this.addProperty(new ResourceLocation("cast"), (var0, var1x, var2) -> {
         if (var2 == null) {
            return 0.0F;
         } else {
            boolean var3 = var2.getMainHandItem() == var0;
            boolean var4 = var2.getOffhandItem() == var0;
            if (var2.getMainHandItem().getItem() instanceof FishingRodItem) {
               var4 = false;
            }

            return (var3 || var4) && var2 instanceof Player && ((Player)var2).fishing != null ? 1.0F : 0.0F;
         }
      });
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      int var5;
      if (var2.fishing != null) {
         if (!var1.isClientSide) {
            var5 = var2.fishing.retrieve(var4);
            var4.hurtAndBreak(var5, var2, (var1x) -> {
               var1x.broadcastBreakEvent(var3);
            });
         }

         var2.swing(var3);
         var1.playSound((Player)null, var2.x, var2.y, var2.z, SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      } else {
         var1.playSound((Player)null, var2.x, var2.y, var2.z, SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         if (!var1.isClientSide) {
            var5 = EnchantmentHelper.getFishingSpeedBonus(var4);
            int var6 = EnchantmentHelper.getFishingLuckBonus(var4);
            var1.addFreshEntity(new FishingHook(var2, var1, var6, var5));
         }

         var2.swing(var3);
         var2.awardStat(Stats.ITEM_USED.get(this));
      }

      return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
   }

   public int getEnchantmentValue() {
      return 1;
   }
}
