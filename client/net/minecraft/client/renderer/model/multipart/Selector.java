package net.minecraft.client.renderer.model.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JsonUtils;

public class Selector {
   private final ICondition field_188167_a;
   private final VariantList field_188168_b;

   public Selector(ICondition var1, VariantList var2) {
      super();
      if (var1 == null) {
         throw new IllegalArgumentException("Missing condition for selector");
      } else if (var2 == null) {
         throw new IllegalArgumentException("Missing variant for selector");
      } else {
         this.field_188167_a = var1;
         this.field_188168_b = var2;
      }
   }

   public VariantList func_188165_a() {
      return this.field_188168_b;
   }

   public Predicate<IBlockState> func_188166_a(StateContainer<Block, IBlockState> var1) {
      return this.field_188167_a.getPredicate(var1);
   }

   public boolean equals(Object var1) {
      return this == var1;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   public static class Deserializer implements JsonDeserializer<Selector> {
      public Deserializer() {
         super();
      }

      public Selector deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         return new Selector(this.func_188159_b(var4), (VariantList)var3.deserialize(var4.get("apply"), VariantList.class));
      }

      private ICondition func_188159_b(JsonObject var1) {
         return var1.has("when") ? func_188158_a(JsonUtils.func_152754_s(var1, "when")) : ICondition.TRUE;
      }

      @VisibleForTesting
      static ICondition func_188158_a(JsonObject var0) {
         Set var1 = var0.entrySet();
         if (var1.isEmpty()) {
            throw new JsonParseException("No elements found in selector");
         } else if (var1.size() == 1) {
            List var2;
            if (var0.has("OR")) {
               var2 = (List)Streams.stream(JsonUtils.func_151214_t(var0, "OR")).map((var0x) -> {
                  return func_188158_a(var0x.getAsJsonObject());
               }).collect(Collectors.toList());
               return new OrCondition(var2);
            } else if (var0.has("AND")) {
               var2 = (List)Streams.stream(JsonUtils.func_151214_t(var0, "AND")).map((var0x) -> {
                  return func_188158_a(var0x.getAsJsonObject());
               }).collect(Collectors.toList());
               return new AndCondition(var2);
            } else {
               return func_188161_b((Entry)var1.iterator().next());
            }
         } else {
            return new AndCondition((Iterable)var1.stream().map((var0x) -> {
               return func_188161_b(var0x);
            }).collect(Collectors.toList()));
         }
      }

      private static ICondition func_188161_b(Entry<String, JsonElement> var0) {
         return new PropertyValueCondition((String)var0.getKey(), ((JsonElement)var0.getValue()).getAsString());
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
