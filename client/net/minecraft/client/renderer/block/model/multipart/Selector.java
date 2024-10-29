package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class Selector {
   private final Condition condition;
   private final MultiVariant variant;

   public Selector(Condition var1, MultiVariant var2) {
      super();
      this.condition = var1;
      this.variant = var2;
   }

   public MultiVariant getVariant() {
      return this.variant;
   }

   public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> var1) {
      return this.condition.getPredicate(var1);
   }

   public static class Deserializer implements JsonDeserializer<Selector> {
      public Deserializer() {
         super();
      }

      public Selector deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         return new Selector(this.getSelector(var4), (MultiVariant)var3.deserialize(var4.get("apply"), MultiVariant.class));
      }

      private Condition getSelector(JsonObject var1) {
         return var1.has("when") ? getCondition(GsonHelper.getAsJsonObject(var1, "when")) : Condition.TRUE;
      }

      @VisibleForTesting
      static Condition getCondition(JsonObject var0) {
         Set var1 = var0.entrySet();
         if (var1.isEmpty()) {
            throw new JsonParseException("No elements found in selector");
         } else if (var1.size() == 1) {
            List var2;
            if (var0.has("OR")) {
               var2 = (List)Streams.stream(GsonHelper.getAsJsonArray(var0, "OR")).map((var0x) -> {
                  return getCondition(var0x.getAsJsonObject());
               }).collect(Collectors.toList());
               return new OrCondition(var2);
            } else if (var0.has("AND")) {
               var2 = (List)Streams.stream(GsonHelper.getAsJsonArray(var0, "AND")).map((var0x) -> {
                  return getCondition(var0x.getAsJsonObject());
               }).collect(Collectors.toList());
               return new AndCondition(var2);
            } else {
               return getKeyValueCondition((Map.Entry)var1.iterator().next());
            }
         } else {
            return new AndCondition((Iterable)var1.stream().map(Deserializer::getKeyValueCondition).collect(Collectors.toList()));
         }
      }

      private static Condition getKeyValueCondition(Map.Entry<String, JsonElement> var0) {
         return new KeyValueCondition((String)var0.getKey(), ((JsonElement)var0.getValue()).getAsString());
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement var1, final Type var2, final JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
