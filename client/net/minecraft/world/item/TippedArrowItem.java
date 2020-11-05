package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class TippedArrowItem extends ArrowItem {
   public TippedArrowItem(Item.Properties var1) {
      super(var1);
   }

   public ItemStack getDefaultInstance() {
      return PotionUtils.setPotion(super.getDefaultInstance(), Potions.POISON);
   }

   public void fillItemCategory(CreativeModeTab var1, NonNullList<ItemStack> var2) {
      if (this.allowdedIn(var1)) {
         Iterator var3 = Registry.POTION.iterator();

         while(var3.hasNext()) {
            Potion var4 = (Potion)var3.next();
            if (!var4.getEffects().isEmpty()) {
               var2.add(PotionUtils.setPotion(new ItemStack(this), var4));
            }
         }
      }

   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      PotionUtils.addPotionTooltip(var1, var3, 0.125F);
   }

   public String getDescriptionId(ItemStack var1) {
      return PotionUtils.getPotion(var1).getName(this.getDescriptionId() + ".effect.");
   }
}
