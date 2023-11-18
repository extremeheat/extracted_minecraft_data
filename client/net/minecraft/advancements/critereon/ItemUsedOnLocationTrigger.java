package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.BlockPos;
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
   public ItemUsedOnLocationTrigger() {
      super();
   }

   public ItemUsedOnLocationTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = ContextAwarePredicate.fromElement("location", var3, var1.get("location"), LootContextParamSets.ADVANCEMENT_LOCATION);
      if (var4.isEmpty()) {
         throw new JsonParseException("Failed to parse 'location' field");
      } else {
         return new ItemUsedOnLocationTrigger.TriggerInstance(var2, (Optional<ContextAwarePredicate>)var4.get());
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
      LootContext var7 = new LootContext.Builder(var6).create(Optional.empty());
      this.trigger(var1, var1x -> var1x.matches(var7));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<ContextAwarePredicate> location;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ContextAwarePredicate> var2) {
         super(var1);
         this.location = var2;
      }

      public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placedBlock(Block var0) {
         ContextAwarePredicate var1 = ContextAwarePredicate.create(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).build());
         return CriteriaTriggers.PLACED_BLOCK.createCriterion(new ItemUsedOnLocationTrigger.TriggerInstance(Optional.empty(), Optional.of(var1)));
      }

      public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placedBlock(LootItemCondition.Builder... var0) {
         ContextAwarePredicate var1 = ContextAwarePredicate.create(
            Arrays.stream(var0).map(LootItemCondition.Builder::build).toArray(var0x -> new LootItemCondition[var0x])
         );
         return CriteriaTriggers.PLACED_BLOCK.createCriterion(new ItemUsedOnLocationTrigger.TriggerInstance(Optional.empty(), Optional.of(var1)));
      }

      private static ItemUsedOnLocationTrigger.TriggerInstance itemUsedOnLocation(LocationPredicate.Builder var0, ItemPredicate.Builder var1) {
         ContextAwarePredicate var2 = ContextAwarePredicate.create(LocationCheck.checkLocation(var0).build(), MatchTool.toolMatches(var1).build());
         return new ItemUsedOnLocationTrigger.TriggerInstance(Optional.empty(), Optional.of(var2));
      }

      public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> itemUsedOnBlock(LocationPredicate.Builder var0, ItemPredicate.Builder var1) {
         return CriteriaTriggers.ITEM_USED_ON_BLOCK.createCriterion(itemUsedOnLocation(var0, var1));
      }

      public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> allayDropItemOnBlock(LocationPredicate.Builder var0, ItemPredicate.Builder var1) {
         return CriteriaTriggers.ALLAY_DROP_ITEM_ON_BLOCK.createCriterion(itemUsedOnLocation(var0, var1));
      }

      public boolean matches(LootContext var1) {
         return this.location.isEmpty() || this.location.get().matches(var1);
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.location.ifPresent(var1x -> var1.add("location", var1x.toJson()));
         return var1;
      }
   }
}
