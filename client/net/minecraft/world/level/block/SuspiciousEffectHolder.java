package net.minecraft.world.level.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.ItemLike;

public interface SuspiciousEffectHolder {
   SuspiciousStewEffects getSuspiciousEffects();

   static List<SuspiciousEffectHolder> getAllEffectHolders() {
      return BuiltInRegistries.ITEM.stream().map(SuspiciousEffectHolder::tryGet).filter(Objects::nonNull).collect(Collectors.toList());
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Nullable
   static SuspiciousEffectHolder tryGet(ItemLike var0) {
      Item var3 = var0.asItem();
      if (var3 instanceof BlockItem var1) {
         Block var4 = var1.getBlock();
         if (var4 instanceof SuspiciousEffectHolder) {
            return (SuspiciousEffectHolder)var4;
         }
      }

      Item var2 = var0.asItem();
      return var2 instanceof SuspiciousEffectHolder ? (SuspiciousEffectHolder)var2 : null;
   }
}
