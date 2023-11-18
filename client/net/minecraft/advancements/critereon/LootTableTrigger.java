package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class LootTableTrigger extends SimpleCriterionTrigger<LootTableTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("player_generates_container_loot");

   public LootTableTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   protected LootTableTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "loot_table"));
      return new LootTableTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, ResourceLocation var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ResourceLocation lootTable;

      public TriggerInstance(ContextAwarePredicate var1, ResourceLocation var2) {
         super(LootTableTrigger.ID, var1);
         this.lootTable = var2;
      }

      public static LootTableTrigger.TriggerInstance lootTableUsed(ResourceLocation var0) {
         return new LootTableTrigger.TriggerInstance(ContextAwarePredicate.ANY, var0);
      }

      public boolean matches(ResourceLocation var1) {
         return this.lootTable.equals(var1);
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.addProperty("loot_table", this.lootTable.toString());
         return var2;
      }
   }
}
