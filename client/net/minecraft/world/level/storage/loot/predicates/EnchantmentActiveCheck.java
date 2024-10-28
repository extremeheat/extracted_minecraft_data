package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record EnchantmentActiveCheck(boolean active) implements LootItemCondition {
   public static final MapCodec<EnchantmentActiveCheck> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.BOOL.fieldOf("active").forGetter(EnchantmentActiveCheck::active)).apply(var0, EnchantmentActiveCheck::new);
   });

   public EnchantmentActiveCheck(boolean var1) {
      super();
      this.active = var1;
   }

   public boolean test(LootContext var1) {
      return (Boolean)var1.getParameter(LootContextParams.ENCHANTMENT_ACTIVE) == this.active;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.ENCHANTMENT_ACTIVE_CHECK;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.ENCHANTMENT_ACTIVE);
   }

   public static LootItemCondition.Builder enchantmentActiveCheck() {
      return () -> {
         return new EnchantmentActiveCheck(true);
      };
   }

   public static LootItemCondition.Builder enchantmentInactiveCheck() {
      return () -> {
         return new EnchantmentActiveCheck(false);
      };
   }

   public boolean active() {
      return this.active;
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }
}
