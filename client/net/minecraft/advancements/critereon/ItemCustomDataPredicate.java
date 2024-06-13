package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

public record ItemCustomDataPredicate(NbtPredicate value) implements ItemSubPredicate {
   public static final Codec<ItemCustomDataPredicate> CODEC = NbtPredicate.CODEC.xmap(ItemCustomDataPredicate::new, ItemCustomDataPredicate::value);

   public ItemCustomDataPredicate(NbtPredicate value) {
      super();
      this.value = value;
   }

   @Override
   public boolean matches(ItemStack var1) {
      return this.value.matches(var1);
   }

   public static ItemCustomDataPredicate customData(NbtPredicate var0) {
      return new ItemCustomDataPredicate(var0);
   }
}
