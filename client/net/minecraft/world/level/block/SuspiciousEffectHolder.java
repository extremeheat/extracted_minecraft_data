package net.minecraft.world.level.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

public interface SuspiciousEffectHolder {
   SuspiciousStewEffects getSuspiciousEffects();

   static List<SuspiciousEffectHolder> getAllEffectHolders() {
      return (List)BuiltInRegistries.ITEM.stream().map(SuspiciousEffectHolder::tryGet).filter(Objects::nonNull).collect(Collectors.toList());
   }

   static @Nullable SuspiciousEffectHolder tryGet(final ItemLike item) {
      Item var3 = item.asItem();
      if (var3 instanceof BlockItem blockItem) {
         Block var6 = blockItem.getBlock();
         if (var6 instanceof SuspiciousEffectHolder effectHolder) {
            return effectHolder;
         }
      }

      Item effectHolder = item.asItem();
      if (effectHolder instanceof SuspiciousEffectHolder effectHolder) {
         return effectHolder;
      } else {
         return null;
      }
   }
}
