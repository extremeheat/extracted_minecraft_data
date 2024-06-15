package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public record ItemContainerPredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> items)
   implements SingleComponentItemPredicate<ItemContainerContents> {
   public static final Codec<ItemContainerPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(CollectionPredicate.codec(ItemPredicate.CODEC).optionalFieldOf("items").forGetter(ItemContainerPredicate::items))
            .apply(var0, ItemContainerPredicate::new)
   );

   public ItemContainerPredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> items) {
      super();
      this.items = items;
   }

   @Override
   public DataComponentType<ItemContainerContents> componentType() {
      return DataComponents.CONTAINER;
   }

   public boolean matches(ItemStack var1, ItemContainerContents var2) {
      return !this.items.isPresent() || this.items.get().test(var2.nonEmptyItems());
   }
}
