package net.minecraft.world.item;

import java.util.List;
import java.util.Objects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

public class OminousBottleItem extends Item {
   private static final int DRINK_DURATION = 32;
   public static final int EFFECT_DURATION = 120000;
   public static final int MIN_AMPLIFIER = 0;
   public static final int MAX_AMPLIFIER = 4;

   public OminousBottleItem(Item.Properties var1) {
      super(var1);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      if (var3 instanceof ServerPlayer var4) {
         CriteriaTriggers.CONSUME_ITEM.trigger(var4, var1);
         var4.awardStat(Stats.ITEM_USED.get(this));
      }

      if (!var2.isClientSide) {
         var2.playSound((Player)null, (BlockPos)var3.blockPosition(), SoundEvents.OMINOUS_BOTTLE_DISPOSE, var3.getSoundSource(), 1.0F, 1.0F);
         Integer var5 = (Integer)var1.getOrDefault(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, 0);
         var3.removeEffect(MobEffects.BAD_OMEN);
         var3.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 120000, var5, false, false, true));
      }

      var1.consume(1, var3);
      return var1;
   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      return 32;
   }

   public UseAnim getUseAnimation(ItemStack var1) {
      return UseAnim.DRINK;
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      return ItemUtils.startUsingInstantly(var1, var2, var3);
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      Integer var5 = (Integer)var1.getOrDefault(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, 0);
      List var6 = List.of(new MobEffectInstance(MobEffects.BAD_OMEN, 120000, var5, false, false, true));
      Objects.requireNonNull(var3);
      PotionContents.addPotionTooltip(var6, var3::add, 1.0F, var2.tickRate());
   }
}
