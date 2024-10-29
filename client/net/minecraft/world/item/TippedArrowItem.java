package net.minecraft.world.item;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

public class TippedArrowItem extends ArrowItem {
   public TippedArrowItem(Item.Properties var1) {
      super(var1);
   }

   public ItemStack getDefaultInstance() {
      ItemStack var1 = super.getDefaultInstance();
      var1.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.POISON));
      return var1;
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      PotionContents var5 = (PotionContents)var1.get(DataComponents.POTION_CONTENTS);
      if (var5 != null) {
         Objects.requireNonNull(var3);
         var5.addPotionTooltip(var3::add, 0.125F, var2.tickRate());
      }
   }

   public Component getName(ItemStack var1) {
      PotionContents var2 = (PotionContents)var1.get(DataComponents.POTION_CONTENTS);
      return var2 != null ? var2.getName(this.descriptionId + ".effect.") : super.getName(var1);
   }
}
