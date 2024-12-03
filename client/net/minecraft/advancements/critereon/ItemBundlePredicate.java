package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

public record ItemBundlePredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> items) implements SingleComponentItemPredicate<BundleContents> {
   public static final Codec<ItemBundlePredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(CollectionPredicate.codec(ItemPredicate.CODEC).optionalFieldOf("items").forGetter(ItemBundlePredicate::items)).apply(var0, ItemBundlePredicate::new));

   public ItemBundlePredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> var1) {
      super();
      this.items = var1;
   }

   public DataComponentType<BundleContents> componentType() {
      return DataComponents.BUNDLE_CONTENTS;
   }

   public boolean matches(ItemStack var1, BundleContents var2) {
      return !this.items.isPresent() || ((CollectionPredicate)this.items.get()).test(var2.items());
   }
}
