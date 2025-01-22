package net.minecraft.world.item;

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

   public Component getName(ItemStack var1) {
      PotionContents var2 = (PotionContents)var1.get(DataComponents.POTION_CONTENTS);
      return var2 != null ? var2.getName(this.descriptionId + ".effect.") : super.getName(var1);
   }
}
