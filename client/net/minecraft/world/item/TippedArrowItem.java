package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

public class TippedArrowItem extends ArrowItem {
   public TippedArrowItem(final Item.Properties properties) {
      super(properties);
   }

   public ItemStack getDefaultInstance() {
      ItemStack itemStack = super.getDefaultInstance();
      itemStack.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.POISON));
      return itemStack;
   }

   public Component getName(final ItemStack itemStack) {
      PotionContents potion = (PotionContents)itemStack.get(DataComponents.POTION_CONTENTS);
      return potion != null ? potion.getName(this.descriptionId + ".effect.") : super.getName(itemStack);
   }
}
