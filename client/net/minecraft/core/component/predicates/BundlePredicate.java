package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.criterion.CollectionPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.component.BundleContents;

public record BundlePredicate(Optional<CollectionPredicate<ItemInstance, ItemPredicate>> items) implements SingleComponentItemPredicate<BundleContents> {
   public static final Codec<BundlePredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(CollectionPredicate.codec(ItemPredicate.CODEC).optionalFieldOf("items").forGetter(BundlePredicate::items)).apply(i, BundlePredicate::new));

   public BundlePredicate {
      super();
   }

   public DataComponentType<BundleContents> componentType() {
      return DataComponents.BUNDLE_CONTENTS;
   }

   public boolean matches(final BundleContents value) {
      return !this.items.isPresent() || ((CollectionPredicate)this.items.get()).test(value.items());
   }
}
