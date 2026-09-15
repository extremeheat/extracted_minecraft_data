package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record EnchantmentActiveCheck(boolean active) implements LootItemCondition {
   public static final MapCodec<EnchantmentActiveCheck> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.BOOL.fieldOf("active").forGetter(EnchantmentActiveCheck::active)).apply(i, EnchantmentActiveCheck::new));

   public EnchantmentActiveCheck {
      super();
   }

   public boolean test(final LootContext lootContext) {
      Boolean value = (Boolean)lootContext.getOptional(LootContextParams.ENCHANTMENT_ACTIVE);
      return value != null && value == this.active;
   }

   public MapCodec<EnchantmentActiveCheck> codec() {
      return MAP_CODEC;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.ENCHANTMENT_ACTIVE);
   }

   public static LootItemCondition.Builder enchantmentActiveCheck() {
      return () -> new EnchantmentActiveCheck(true);
   }

   public static LootItemCondition.Builder enchantmentInactiveCheck() {
      return () -> new EnchantmentActiveCheck(false);
   }
}
