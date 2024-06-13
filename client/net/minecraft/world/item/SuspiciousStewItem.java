package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.Level;

public class SuspiciousStewItem extends Item {
   public static final int DEFAULT_DURATION = 160;

   public SuspiciousStewItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      if (var4.isCreative()) {
         ArrayList var5 = new ArrayList();
         SuspiciousStewEffects var6 = var1.getOrDefault(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY);

         for (SuspiciousStewEffects.Entry var8 : var6.effects()) {
            var5.add(var8.createEffectInstance());
         }

         PotionContents.addPotionTooltip(var5, var3::add, 1.0F, var2.tickRate());
      }
   }

   @Override
   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      SuspiciousStewEffects var4 = var1.getOrDefault(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY);

      for (SuspiciousStewEffects.Entry var6 : var4.effects()) {
         var3.addEffect(var6.createEffectInstance());
      }

      return super.finishUsingItem(var1, var2, var3);
   }
}
