package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.slf4j.Logger;

public class BlockModelDefinition {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(BlockModelDefinition.class, new BlockModelDefinition.Deserializer())
      .registerTypeAdapter(Variant.class, new Variant.Deserializer())
      .registerTypeAdapter(MultiVariant.class, new MultiVariant.Deserializer())
      .registerTypeAdapter(MultiPart.Definition.class, new MultiPart.Deserializer())
      .registerTypeAdapter(Selector.class, new Selector.Deserializer())
      .create();
   private final Map<String, MultiVariant> variants;
   @Nullable
   private final MultiPart.Definition multiPart;

   public static BlockModelDefinition fromStream(Reader var0) {
      return GsonHelper.fromJson(GSON, var0, BlockModelDefinition.class);
   }

   public static BlockModelDefinition fromJsonElement(JsonElement var0) {
      return (BlockModelDefinition)GSON.fromJson(var0, BlockModelDefinition.class);
   }

   public BlockModelDefinition(Map<String, MultiVariant> var1, @Nullable MultiPart.Definition var2) {
      super();
      this.multiPart = var2;
      this.variants = var1;
   }

   @VisibleForTesting
   public MultiVariant getVariant(String var1) {
      MultiVariant var2 = this.variants.get(var1);
      if (var2 == null) {
         throw new BlockModelDefinition.MissingVariantException();
      } else {
         return var2;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof BlockModelDefinition var2) ? false : this.variants.equals(var2.variants) && Objects.equals(this.multiPart, var2.multiPart);
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.variants.hashCode() + (this.multiPart != null ? this.multiPart.hashCode() : 0);
   }

   @VisibleForTesting
   public Set<MultiVariant> getMultiVariants() {
      HashSet var1 = Sets.newHashSet(this.variants.values());
      if (this.multiPart != null) {
         var1.addAll(this.multiPart.getMultiVariants());
      }

      return var1;
   }

   @Nullable
   public MultiPart.Definition getMultiPart() {
      return this.multiPart;
   }

   public Map<BlockState, UnbakedBlockStateModel> instantiate(StateDefinition<Block, BlockState> var1, String var2) {
      IdentityHashMap var3 = new IdentityHashMap();
      ImmutableList var4 = var1.getPossibleStates();
      MultiPart var5;
      if (this.multiPart != null) {
         var5 = this.multiPart.instantiate(var1);
         var4.forEach(var2x -> var3.put(var2x, var5));
      } else {
         var5 = null;
      }

      this.variants.forEach((var6, var7) -> {
         try {
            var4.stream().filter(VariantSelector.predicate(var1, var6)).forEach(var4xx -> {
               UnbakedModel var5xx = var3.put(var4xx, var7);
               if (var5xx != null && var5xx != var5) {
                  String var6x = this.variants.entrySet().stream().filter(var1xxx -> var1xxx.getValue() == var5xx).findFirst().get().getKey();
                  throw new RuntimeException("Overlapping definition with: " + var6x);
               }
            });
         } catch (Exception var9) {
            LOGGER.warn("Exception loading blockstate definition: '{}' for variant: '{}': {}", new Object[]{var2, var6, var9.getMessage()});
         }
      });
      return var3;
   }

   public static class Deserializer implements JsonDeserializer<BlockModelDefinition> {
      public Deserializer() {
         super();
      }

      public BlockModelDefinition deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         Map var5 = this.getVariants(var3, var4);
         MultiPart.Definition var6 = this.getMultiPart(var3, var4);
         if (!var5.isEmpty() || var6 != null && !var6.getMultiVariants().isEmpty()) {
            return new BlockModelDefinition(var5, var6);
         } else {
            throw new JsonParseException("Neither 'variants' nor 'multipart' found");
         }
      }

      protected Map<String, MultiVariant> getVariants(JsonDeserializationContext var1, JsonObject var2) {
         HashMap var3 = Maps.newHashMap();
         if (var2.has("variants")) {
            JsonObject var4 = GsonHelper.getAsJsonObject(var2, "variants");

            for (Entry var6 : var4.entrySet()) {
               var3.put((String)var6.getKey(), (MultiVariant)var1.deserialize((JsonElement)var6.getValue(), MultiVariant.class));
            }
         }

         return var3;
      }

      @Nullable
      protected MultiPart.Definition getMultiPart(JsonDeserializationContext var1, JsonObject var2) {
         if (!var2.has("multipart")) {
            return null;
         } else {
            JsonArray var3 = GsonHelper.getAsJsonArray(var2, "multipart");
            return (MultiPart.Definition)var1.deserialize(var3, MultiPart.Definition.class);
         }
      }
   }

   protected static class MissingVariantException extends RuntimeException {
      protected MissingVariantException() {
         super();
      }
   }
}
