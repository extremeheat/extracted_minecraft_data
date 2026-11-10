package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootItemKilledByPlayerCondition implements LootItemCondition.ContextUser {
   private static final LootItemKilledByPlayerCondition INSTANCE = new LootItemKilledByPlayerCondition();
   public static final MapCodec<LootItemKilledByPlayerCondition> MAP_CODEC;

   private LootItemKilledByPlayerCondition() {
      super();
   }

   public MapCodec<LootItemKilledByPlayerCondition> codec() {
      return MAP_CODEC;
   }

   public Set<ContextKey<?>> requiredContextParams() {
      return Set.of(LootContextParams.LAST_DAMAGE_PLAYER);
   }

   public void validate(final ValidationContext context) {
   }

   public boolean test(final LootContext context) {
      return context.hasParameter(LootContextParams.LAST_DAMAGE_PLAYER);
   }

   public static LootItemCondition.Builder killedByPlayer() {
      return () -> INSTANCE;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
