package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootItemBlockStatePropertyCondition implements LootItemCondition {
   private final Block block;
   private final Map<Property<?>, Object> properties;
   private final Predicate<BlockState> composedPredicate;

   private LootItemBlockStatePropertyCondition(Block var1, Map<Property<?>, Object> var2) {
      super();
      this.block = var1;
      this.properties = ImmutableMap.copyOf(var2);
      this.composedPredicate = bakePredicate(var1, var2);
   }

   private static Predicate<BlockState> bakePredicate(Block var0, Map<Property<?>, Object> var1) {
      int var2 = var1.size();
      if (var2 == 0) {
         return (var1x) -> {
            return var1x.getBlock() == var0;
         };
      } else if (var2 == 1) {
         Entry var8 = (Entry)var1.entrySet().iterator().next();
         Property var9 = (Property)var8.getKey();
         Object var10 = var8.getValue();
         return (var3x) -> {
            return var3x.getBlock() == var0 && var10.equals(var3x.getValue(var9));
         };
      } else {
         Predicate var3 = (var1x) -> {
            return var1x.getBlock() == var0;
         };

         Property var6;
         Object var7;
         for(Iterator var4 = var1.entrySet().iterator(); var4.hasNext(); var3 = var3.and((var2x) -> {
            return var7.equals(var2x.getValue(var6));
         })) {
            Entry var5 = (Entry)var4.next();
            var6 = (Property)var5.getKey();
            var7 = var5.getValue();
         }

         return var3;
      }
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_STATE);
   }

   public boolean test(LootContext var1) {
      BlockState var2 = (BlockState)var1.getParamOrNull(LootContextParams.BLOCK_STATE);
      return var2 != null && this.composedPredicate.test(var2);
   }

   public static LootItemBlockStatePropertyCondition.Builder hasBlockStateProperties(Block var0) {
      return new LootItemBlockStatePropertyCondition.Builder(var0);
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   // $FF: synthetic method
   LootItemBlockStatePropertyCondition(Block var1, Map var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemCondition.Serializer<LootItemBlockStatePropertyCondition> {
      private static <T extends Comparable<T>> String valueToString(Property<T> var0, Object var1) {
         return var0.getName((Comparable)var1);
      }

      protected Serializer() {
         super(new ResourceLocation("block_state_property"), LootItemBlockStatePropertyCondition.class);
      }

      public void serialize(JsonObject var1, LootItemBlockStatePropertyCondition var2, JsonSerializationContext var3) {
         var1.addProperty("block", Registry.BLOCK.getKey(var2.block).toString());
         JsonObject var4 = new JsonObject();
         var2.properties.forEach((var1x, var2x) -> {
            var4.addProperty(var1x.getName(), valueToString(var1x, var2x));
         });
         var1.add("properties", var4);
      }

      public LootItemBlockStatePropertyCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(var1, "block"));
         Block var4 = (Block)Registry.BLOCK.getOptional(var3).orElseThrow(() -> {
            return new IllegalArgumentException("Can't find block " + var3);
         });
         StateDefinition var5 = var4.getStateDefinition();
         HashMap var6 = Maps.newHashMap();
         if (var1.has("properties")) {
            JsonObject var7 = GsonHelper.getAsJsonObject(var1, "properties");
            var7.entrySet().forEach((var3x) -> {
               String var4x = (String)var3x.getKey();
               Property var5x = var5.getProperty(var4x);
               if (var5x == null) {
                  throw new IllegalArgumentException("Block " + Registry.BLOCK.getKey(var4) + " does not have property '" + var4x + "'");
               } else {
                  String var6x = GsonHelper.convertToString((JsonElement)var3x.getValue(), "value");
                  Object var7 = var5x.getValue(var6x).orElseThrow(() -> {
                     return new IllegalArgumentException("Block " + Registry.BLOCK.getKey(var4) + " property '" + var4x + "' does not have value '" + var6x + "'");
                  });
                  var6.put(var5x, var7);
               }
            });
         }

         return new LootItemBlockStatePropertyCondition(var4, var6);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }

   public static class Builder implements LootItemCondition.Builder {
      private final Block block;
      private final Set<Property<?>> allowedProperties;
      private final Map<Property<?>, Object> properties = Maps.newHashMap();

      public Builder(Block var1) {
         super();
         this.block = var1;
         this.allowedProperties = Sets.newIdentityHashSet();
         this.allowedProperties.addAll(var1.getStateDefinition().getProperties());
      }

      public <T extends Comparable<T>> LootItemBlockStatePropertyCondition.Builder withProperty(Property<T> var1, T var2) {
         if (!this.allowedProperties.contains(var1)) {
            throw new IllegalArgumentException("Block " + Registry.BLOCK.getKey(this.block) + " does not have property '" + var1 + "'");
         } else if (!var1.getPossibleValues().contains(var2)) {
            throw new IllegalArgumentException("Block " + Registry.BLOCK.getKey(this.block) + " property '" + var1 + "' does not have value '" + var2 + "'");
         } else {
            this.properties.put(var1, var2);
            return this;
         }
      }

      public LootItemCondition build() {
         return new LootItemBlockStatePropertyCondition(this.block, this.properties);
      }
   }
}
