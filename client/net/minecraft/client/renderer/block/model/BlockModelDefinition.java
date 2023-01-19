package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
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
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class BlockModelDefinition {
   private final Map<String, MultiVariant> variants = Maps.newLinkedHashMap();
   private MultiPart multiPart;

   public static BlockModelDefinition fromStream(BlockModelDefinition.Context var0, Reader var1) {
      return GsonHelper.fromJson(var0.gson, var1, BlockModelDefinition.class);
   }

   public static BlockModelDefinition fromJsonElement(BlockModelDefinition.Context var0, JsonElement var1) {
      return (BlockModelDefinition)var0.gson.fromJson(var1, BlockModelDefinition.class);
   }

   public BlockModelDefinition(Map<String, MultiVariant> var1, MultiPart var2) {
      super();
      this.multiPart = var2;
      this.variants.putAll(var1);
   }

   public BlockModelDefinition(List<BlockModelDefinition> var1) {
      super();
      BlockModelDefinition var2 = null;

      for(BlockModelDefinition var4 : var1) {
         if (var4.isMultiPart()) {
            this.variants.clear();
            var2 = var4;
         }

         this.variants.putAll(var4.variants);
      }

      if (var2 != null) {
         this.multiPart = var2.multiPart;
      }
   }

   @VisibleForTesting
   public boolean hasVariant(String var1) {
      return this.variants.get(var1) != null;
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

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof BlockModelDefinition var2 && this.variants.equals(var2.variants)) {
            return this.isMultiPart() ? this.multiPart.equals(var2.multiPart) : !var2.isMultiPart();
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.variants.hashCode() + (this.isMultiPart() ? this.multiPart.hashCode() : 0);
   }

   public Map<String, MultiVariant> getVariants() {
      return this.variants;
   }

   @VisibleForTesting
   public Set<MultiVariant> getMultiVariants() {
      HashSet var1 = Sets.newHashSet(this.variants.values());
      if (this.isMultiPart()) {
         var1.addAll(this.multiPart.getMultiVariants());
      }

      return var1;
   }

   public boolean isMultiPart() {
      return this.multiPart != null;
   }

   public MultiPart getMultiPart() {
      return this.multiPart;
   }

   public static final class Context {
      protected final Gson gson = new GsonBuilder()
         .registerTypeAdapter(BlockModelDefinition.class, new BlockModelDefinition.Deserializer())
         .registerTypeAdapter(Variant.class, new Variant.Deserializer())
         .registerTypeAdapter(MultiVariant.class, new MultiVariant.Deserializer())
         .registerTypeAdapter(MultiPart.class, new MultiPart.Deserializer(this))
         .registerTypeAdapter(Selector.class, new Selector.Deserializer())
         .create();
      private StateDefinition<Block, BlockState> definition;

      public Context() {
         super();
      }

      public StateDefinition<Block, BlockState> getDefinition() {
         return this.definition;
      }

      public void setDefinition(StateDefinition<Block, BlockState> var1) {
         this.definition = var1;
      }
   }

   public static class Deserializer implements JsonDeserializer<BlockModelDefinition> {
      public Deserializer() {
         super();
      }

      public BlockModelDefinition deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         Map var5 = this.getVariants(var3, var4);
         MultiPart var6 = this.getMultiPart(var3, var4);
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

            for(Entry var6 : var4.entrySet()) {
               var3.put((String)var6.getKey(), (MultiVariant)var1.deserialize((JsonElement)var6.getValue(), MultiVariant.class));
            }
         }

         return var3;
      }

      @Nullable
      protected MultiPart getMultiPart(JsonDeserializationContext var1, JsonObject var2) {
         if (!var2.has("multipart")) {
            return null;
         } else {
            JsonArray var3 = GsonHelper.getAsJsonArray(var2, "multipart");
            return (MultiPart)var1.deserialize(var3, MultiPart.class);
         }
      }
   }

   protected class MissingVariantException extends RuntimeException {
      protected MissingVariantException() {
         super();
      }
   }
}
