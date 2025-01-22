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
import net.minecraft.world.item.component.BundleContents;

public record BundlePredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> items) implements SingleComponentItemPredicate<BundleContents> {
   public static final Codec<BundlePredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(CollectionPredicate.codec(ItemPredicate.CODEC).optionalFieldOf("items").forGetter(BundlePredicate::items)).apply(var0, BundlePredicate::new));

   public BundlePredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> var1) {
      super();
      this.items = var1;
   }

   public DataComponentType<BundleContents> componentType() {
      return DataComponents.BUNDLE_CONTENTS;
   }

   public boolean matches(BundleContents var1) {
      return !this.items.isPresent() || ((CollectionPredicate)this.items.get()).test(var1.items());
   }
}
