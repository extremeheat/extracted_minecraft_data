package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootItemKilledByPlayerCondition implements LootItemCondition {
   private static final LootItemKilledByPlayerCondition INSTANCE = new LootItemKilledByPlayerCondition();
   public static final MapCodec<LootItemKilledByPlayerCondition> CODEC;

   private LootItemKilledByPlayerCondition() {
      super();
   }

   public LootItemConditionType getType() {
      return LootItemConditions.KILLED_BY_PLAYER;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.LAST_DAMAGE_PLAYER);
   }

   public boolean test(LootContext var1) {
      return var1.hasParameter(LootContextParams.LAST_DAMAGE_PLAYER);
   }

   public static LootItemCondition.Builder killedByPlayer() {
      return () -> {
         return INSTANCE;
      };
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }

   static {
      CODEC = MapCodec.unit(INSTANCE);
   }
}
