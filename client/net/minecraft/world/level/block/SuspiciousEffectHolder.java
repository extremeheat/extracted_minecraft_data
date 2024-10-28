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
      return (List)BuiltInRegistries.ITEM.stream().map(SuspiciousEffectHolder::tryGet).filter(Objects::nonNull).collect(Collectors.toList());
   }

   @Nullable
   static SuspiciousEffectHolder tryGet(ItemLike var0) {
      Item var3 = var0.asItem();
      if (var3 instanceof BlockItem var1) {
         Block var6 = var1.getBlock();
         if (var6 instanceof SuspiciousEffectHolder var5) {
            return var5;
         }
      }

      Item var2 = var0.asItem();
      if (var2 instanceof SuspiciousEffectHolder var4) {
         return var4;
      } else {
         return null;
      }
   }
}
