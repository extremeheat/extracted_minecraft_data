package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class TippedArrowItem extends ArrowItem {
   public TippedArrowItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public ItemStack getDefaultInstance() {
      ItemStack var1 = super.getDefaultInstance();
      var1.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.POISON));
      return var1;
   }

   @Override
   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      PotionContents var5 = var1.get(DataComponents.POTION_CONTENTS);
      if (var5 != null) {
         var5.addPotionTooltip(var3::add, 0.125F, var2 == null ? 20.0F : var2.tickRateManager().tickrate());
      }
   }

   @Override
   public String getDescriptionId(ItemStack var1) {
      return Potion.getName(
         ((PotionContents)var1.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).potion(), this.getDescriptionId() + ".effect."
      );
   }
}
