package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.state.StateDefinition;

public class BlockModelDefinition {
   private final Map variants = Maps.newLinkedHashMap();
   private MultiPart multiPart;

   public static BlockModelDefinition fromStream(BlockModelDefinition.Context var0, Reader var1) {
      return (BlockModelDefinition)GsonHelper.fromJson(var0.gson, var1, BlockModelDefinition.class);
   }

   public BlockModelDefinition(Map var1, MultiPart var2) {
      this.multiPart = var2;
      this.variants.putAll(var1);
   }

   public BlockModelDefinition(List var1) {
      BlockModelDefinition var2 = null;

      BlockModelDefinition var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); this.variants.putAll(var4.variants)) {
         var4 = (BlockModelDefinition)var3.next();
         if (var4.isMultiPart()) {
            this.variants.clear();
            var2 = var4;
         }
      }

      if (var2 != null) {
         this.multiPart = var2.multiPart;
      }

   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof BlockModelDefinition) {
            BlockModelDefinition var2 = (BlockModelDefinition)var1;
            if (this.variants.equals(var2.variants)) {
               return this.isMultiPart() ? this.multiPart.equals(var2.multiPart) : !var2.isMultiPart();
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return 31 * this.variants.hashCode() + (this.isMultiPart() ? this.multiPart.hashCode() : 0);
   }

   public Map getVariants() {
      return this.variants;
   }

   public boolean isMultiPart() {
      return this.multiPart != null;
   }

   public MultiPart getMultiPart() {
      return this.multiPart;
   }

   public static class Deserializer implements JsonDeserializer {
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

      protected Map getVariants(JsonDeserializationContext var1, JsonObject var2) {
         HashMap var3 = Maps.newHashMap();
         if (var2.has("variants")) {
            JsonObject var4 = GsonHelper.getAsJsonObject(var2, "variants");
            Iterator var5 = var4.entrySet().iterator();

            while(var5.hasNext()) {
               Entry var6 = (Entry)var5.next();
               var3.put(var6.getKey(), var1.deserialize((JsonElement)var6.getValue(), MultiVariant.class));
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

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static final class Context {
      protected final Gson gson = (new GsonBuilder()).registerTypeAdapter(BlockModelDefinition.class, new BlockModelDefinition.Deserializer()).registerTypeAdapter(Variant.class, new Variant.Deserializer()).registerTypeAdapter(MultiVariant.class, new MultiVariant.Deserializer()).registerTypeAdapter(MultiPart.class, new MultiPart.Deserializer(this)).registerTypeAdapter(Selector.class, new Selector.Deserializer()).create();
      private StateDefinition definition;

      public StateDefinition getDefinition() {
         return this.definition;
      }

      public void setDefinition(StateDefinition var1) {
         this.definition = var1;
      }
   }
}
