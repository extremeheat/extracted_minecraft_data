package net.minecraft.world.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      if (var4.isCreative()) {
         ArrayList var5 = new ArrayList();
         SuspiciousStewEffects var6 = (SuspiciousStewEffects)var1.getOrDefault(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY);
         Iterator var7 = var6.effects().iterator();

         while(var7.hasNext()) {
            SuspiciousStewEffects.Entry var8 = (SuspiciousStewEffects.Entry)var7.next();
            var5.add(var8.createEffectInstance());
         }

         Objects.requireNonNull(var3);
         PotionContents.addPotionTooltip(var5, var3::add, 1.0F, var2.tickRate());
      }

   }

   public ItemStack finishUsingItem(ItemStack var1, Level var2, LivingEntity var3) {
      SuspiciousStewEffects var4 = (SuspiciousStewEffects)var1.getOrDefault(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY);
      Iterator var5 = var4.effects().iterator();

      while(var5.hasNext()) {
         SuspiciousStewEffects.Entry var6 = (SuspiciousStewEffects.Entry)var5.next();
         var3.addEffect(var6.createEffectInstance());
      }

      return super.finishUsingItem(var1, var2, var3);
   }
}
