package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

public class ItemUsedOnLocationTrigger extends SimpleCriterionTrigger<ItemUsedOnLocationTrigger.TriggerInstance> {
   final ResourceLocation id;

   public ItemUsedOnLocationTrigger(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   public ItemUsedOnLocationTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      ContextAwarePredicate var4 = ContextAwarePredicate.fromElement("location", var3, var1.get("location"), LootContextParamSets.ADVANCEMENT_LOCATION);
      if (var4 == null) {
         throw new JsonParseException("Failed to parse 'location' field");
      } else {
         return new ItemUsedOnLocationTrigger.TriggerInstance(this.id, var2, var4);
      }
   }

   public void trigger(ServerPlayer var1, BlockPos var2, ItemStack var3) {
      ServerLevel var4 = var1.serverLevel();
      BlockState var5 = var4.getBlockState(var2);
      LootParams var6 = new LootParams.Builder(var4)
         .withParameter(LootContextParams.ORIGIN, var2.getCenter())
         .withParameter(LootContextParams.THIS_ENTITY, var1)
         .withParameter(LootContextParams.BLOCK_STATE, var5)
         .withParameter(LootContextParams.TOOL, var3)
         .create(LootContextParamSets.ADVANCEMENT_LOCATION);
      LootContext var7 = new LootContext.Builder(var6).create(null);
      this.trigger(var1, var1x -> var1x.matches(var7));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ContextAwarePredicate location;

      public TriggerInstance(ResourceLocation var1, ContextAwarePredicate var2, ContextAwarePredicate var3) {
         super(var1, var2);
         this.location = var3;
      }

      public static ItemUsedOnLocationTrigger.TriggerInstance placedBlock(Block var0) {
         ContextAwarePredicate var1 = ContextAwarePredicate.create(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).build());
         return new ItemUsedOnLocationTrigger.TriggerInstance(CriteriaTriggers.PLACED_BLOCK.id, ContextAwarePredicate.ANY, var1);
      }

      public static ItemUsedOnLocationTrigger.TriggerInstance placedBlock(LootItemCondition.Builder... var0) {
         ContextAwarePredicate var1 = ContextAwarePredicate.create(
            Arrays.stream(var0).map(LootItemCondition.Builder::build).toArray(var0x -> new LootItemCondition[var0x])
         );
         return new ItemUsedOnLocationTrigger.TriggerInstance(CriteriaTriggers.PLACED_BLOCK.id, ContextAwarePredicate.ANY, var1);
      }

      private static ItemUsedOnLocationTrigger.TriggerInstance itemUsedOnLocation(
         LocationPredicate.Builder var0, ItemPredicate.Builder var1, ResourceLocation var2
      ) {
         ContextAwarePredicate var3 = ContextAwarePredicate.create(LocationCheck.checkLocation(var0).build(), MatchTool.toolMatches(var1).build());
         return new ItemUsedOnLocationTrigger.TriggerInstance(var2, ContextAwarePredicate.ANY, var3);
      }

      public static ItemUsedOnLocationTrigger.TriggerInstance itemUsedOnBlock(LocationPredicate.Builder var0, ItemPredicate.Builder var1) {
         return itemUsedOnLocation(var0, var1, CriteriaTriggers.ITEM_USED_ON_BLOCK.id);
      }

      public static ItemUsedOnLocationTrigger.TriggerInstance allayDropItemOnBlock(LocationPredicate.Builder var0, ItemPredicate.Builder var1) {
         return itemUsedOnLocation(var0, var1, CriteriaTriggers.ALLAY_DROP_ITEM_ON_BLOCK.id);
      }

      public boolean matches(LootContext var1) {
         return this.location.matches(var1);
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("location", this.location.toJson(var1));
         return var2;
      }
   }
}
