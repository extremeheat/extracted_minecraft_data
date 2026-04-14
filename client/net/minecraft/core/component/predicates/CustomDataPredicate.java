package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.predicates.NbtPredicate;
import net.minecraft.core.component.DataComponentGetter;

public record CustomDataPredicate(NbtPredicate value) implements DataComponentPredicate {
   public static final Codec<CustomDataPredicate> CODEC;

   public CustomDataPredicate {
      super();
   }

   public boolean matches(final DataComponentGetter components) {
      return this.value.matches(components);
   }

   public static CustomDataPredicate customData(final NbtPredicate value) {
      return new CustomDataPredicate(value);
   }

   static {
      CODEC = NbtPredicate.CODEC.xmap(CustomDataPredicate::new, CustomDataPredicate::value);
   }
}
