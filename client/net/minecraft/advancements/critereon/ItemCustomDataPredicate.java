package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

public record ItemCustomDataPredicate(NbtPredicate value) implements ItemSubPredicate {
   public static final Codec<ItemCustomDataPredicate> CODEC;

   public ItemCustomDataPredicate(NbtPredicate var1) {
      super();
      this.value = var1;
   }

   public boolean matches(ItemStack var1) {
      return this.value.matches(var1);
   }

   public static ItemCustomDataPredicate customData(NbtPredicate var0) {
      return new ItemCustomDataPredicate(var0);
   }

   public NbtPredicate value() {
      return this.value;
   }

   static {
      CODEC = NbtPredicate.CODEC.xmap(ItemCustomDataPredicate::new, ItemCustomDataPredicate::value);
   }
}
