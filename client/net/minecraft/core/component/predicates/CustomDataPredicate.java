package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.core.component.DataComponentGetter;

public record CustomDataPredicate(NbtPredicate value) implements DataComponentPredicate {
   public static final Codec<CustomDataPredicate> CODEC;

   public CustomDataPredicate(NbtPredicate var1) {
      super();
      this.value = var1;
   }

   public boolean matches(DataComponentGetter var1) {
      return this.value.matches(var1);
   }

   public static CustomDataPredicate customData(NbtPredicate var0) {
      return new CustomDataPredicate(var0);
   }

   static {
      CODEC = NbtPredicate.CODEC.xmap(CustomDataPredicate::new, CustomDataPredicate::value);
   }
}
