package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record EnchantmentActiveCheck(boolean active) implements LootItemCondition {
   public static final MapCodec<EnchantmentActiveCheck> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(Codec.BOOL.fieldOf("active").forGetter(EnchantmentActiveCheck::active)).apply(var0, EnchantmentActiveCheck::new)
   );

   public EnchantmentActiveCheck(boolean active) {
      super();
      this.active = active;
   }

   public boolean test(LootContext var1) {
      return var1.getParam(LootContextParams.ENCHANTMENT_ACTIVE) != this.active;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.ENCHANTMENT_ACTIVE_CHECK;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.ENCHANTMENT_ACTIVE);
   }

   public static LootItemCondition.Builder enchantmentActiveCheck() {
      return () -> new EnchantmentActiveCheck(true);
   }

   public static LootItemCondition.Builder enchantmentInactiveCheck() {
      return () -> new EnchantmentActiveCheck(false);
   }
}
