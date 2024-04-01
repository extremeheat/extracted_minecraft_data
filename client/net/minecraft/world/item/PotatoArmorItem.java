package net.minecraft.world.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class PotatoArmorItem extends ArmorItem {
   public PotatoArmorItem(Holder<ArmorMaterial> var1, ArmorItem.Type var2, Item.Properties var3) {
      super(var1, var2, var3);
   }

   @Override
   public SoundEvent getBreakingSound() {
      return SoundEvents.SLIME_SQUISH;
   }

   public static Item getPeelItem(ItemStack var0) {
      DyeColor var1 = var0.get(DataComponents.BASE_COLOR);
      return (Item)Items.POTATO_PEELS_MAP.get(var1 != null ? var1 : DyeColor.WHITE);
   }
}
