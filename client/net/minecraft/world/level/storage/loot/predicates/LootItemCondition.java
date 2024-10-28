package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface LootItemCondition extends LootContextUser, Predicate<LootContext> {
   Codec<LootItemCondition> TYPED_CODEC = BuiltInRegistries.LOOT_CONDITION_TYPE.byNameCodec().dispatch("condition", LootItemCondition::getType, LootItemConditionType::codec);
   Codec<LootItemCondition> DIRECT_CODEC = Codec.lazyInitialized(() -> {
      return Codec.withAlternative(TYPED_CODEC, AllOfCondition.INLINE_CODEC);
   });
   Codec<Holder<LootItemCondition>> CODEC = RegistryFileCodec.create(Registries.PREDICATE, DIRECT_CODEC);

   LootItemConditionType getType();

   @FunctionalInterface
   public interface Builder {
      LootItemCondition build();

      default Builder invert() {
         return InvertedLootItemCondition.invert(this);
      }

      default AnyOfCondition.Builder or(Builder var1) {
         return AnyOfCondition.anyOf(this, var1);
      }

      default AllOfCondition.Builder and(Builder var1) {
         return AllOfCondition.allOf(this, var1);
      }
   }
}
