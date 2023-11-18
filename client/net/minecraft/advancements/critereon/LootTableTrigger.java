package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class LootTableTrigger extends SimpleCriterionTrigger<LootTableTrigger.TriggerInstance> {
   public LootTableTrigger() {
      super();
   }

   protected LootTableTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "loot_table"));
      return new LootTableTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, ResourceLocation var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ResourceLocation lootTable;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, ResourceLocation var2) {
         super(var1);
         this.lootTable = var2;
      }

      public static Criterion<LootTableTrigger.TriggerInstance> lootTableUsed(ResourceLocation var0) {
         return CriteriaTriggers.GENERATE_LOOT.createCriterion(new LootTableTrigger.TriggerInstance(Optional.empty(), var0));
      }

      public boolean matches(ResourceLocation var1) {
         return this.lootTable.equals(var1);
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         var1.addProperty("loot_table", this.lootTable.toString());
         return var1;
      }
   }
}
