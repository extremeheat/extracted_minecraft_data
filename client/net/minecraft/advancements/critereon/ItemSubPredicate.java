package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public interface ItemSubPredicate {
   Codec<Map<ItemSubPredicate.Type<?>, ItemSubPredicate>> CODEC = Codec.dispatchedMap(
      BuiltInRegistries.ITEM_SUB_PREDICATE_TYPE.byNameCodec(), ItemSubPredicate.Type::codec
   );

   boolean matches(ItemStack var1);

   public static record Type<T extends ItemSubPredicate>(Codec<T> a) {
      private final Codec<T> codec;

      public Type(Codec<T> var1) {
         super();
         this.codec = var1;
      }
   }
}
