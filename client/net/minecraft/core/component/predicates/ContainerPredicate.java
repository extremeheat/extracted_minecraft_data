package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.CollectionPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public record ContainerPredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> items) implements SingleComponentItemPredicate<ItemContainerContents> {
   public static final Codec<ContainerPredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(CollectionPredicate.codec(ItemPredicate.CODEC).optionalFieldOf("items").forGetter(ContainerPredicate::items)).apply(var0, ContainerPredicate::new));

   public ContainerPredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> var1) {
      super();
      this.items = var1;
   }

   public DataComponentType<ItemContainerContents> componentType() {
      return DataComponents.CONTAINER;
   }

   public boolean matches(ItemContainerContents var1) {
      return !this.items.isPresent() || ((CollectionPredicate)this.items.get()).test(var1.nonEmptyItems());
   }
}
