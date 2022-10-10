package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
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
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.multipart.Multipart;
import net.minecraft.client.renderer.model.multipart.Selector;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JsonUtils;

public class ModelBlockDefinition {
   private final Map<String, VariantList> field_178332_b = Maps.newLinkedHashMap();
   private Multipart field_188005_c;

   public static ModelBlockDefinition func_209577_a(ModelBlockDefinition.ContainerHolder var0, Reader var1) {
      return (ModelBlockDefinition)JsonUtils.func_193839_a(var0.field_209575_a, var1, ModelBlockDefinition.class);
   }

   public ModelBlockDefinition(Map<String, VariantList> var1, Multipart var2) {
      super();
      this.field_188005_c = var2;
      this.field_178332_b.putAll(var1);
   }

   public ModelBlockDefinition(List<ModelBlockDefinition> var1) {
      super();
      ModelBlockDefinition var2 = null;

      ModelBlockDefinition var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); this.field_178332_b.putAll(var4.field_178332_b)) {
         var4 = (ModelBlockDefinition)var3.next();
         if (var4.func_188002_b()) {
            this.field_178332_b.clear();
            var2 = var4;
         }
      }

      if (var2 != null) {
         this.field_188005_c = var2.field_188005_c;
      }

   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof ModelBlockDefinition) {
            ModelBlockDefinition var2 = (ModelBlockDefinition)var1;
            if (this.field_178332_b.equals(var2.field_178332_b)) {
               return this.func_188002_b() ? this.field_188005_c.equals(var2.field_188005_c) : !var2.func_188002_b();
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return 31 * this.field_178332_b.hashCode() + (this.func_188002_b() ? this.field_188005_c.hashCode() : 0);
   }

   public Map<String, VariantList> func_209578_a() {
      return this.field_178332_b;
   }

   public boolean func_188002_b() {
      return this.field_188005_c != null;
   }

   public Multipart func_188001_c() {
      return this.field_188005_c;
   }

   public static class Deserializer implements JsonDeserializer<ModelBlockDefinition> {
      public Deserializer() {
         super();
      }

      public ModelBlockDefinition deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         Map var5 = this.func_187999_a(var3, var4);
         Multipart var6 = this.func_187998_b(var3, var4);
         if (!var5.isEmpty() || var6 != null && !var6.func_188137_b().isEmpty()) {
            return new ModelBlockDefinition(var5, var6);
         } else {
            throw new JsonParseException("Neither 'variants' nor 'multipart' found");
         }
      }

      protected Map<String, VariantList> func_187999_a(JsonDeserializationContext var1, JsonObject var2) {
         HashMap var3 = Maps.newHashMap();
         if (var2.has("variants")) {
            JsonObject var4 = JsonUtils.func_152754_s(var2, "variants");
            Iterator var5 = var4.entrySet().iterator();

            while(var5.hasNext()) {
               Entry var6 = (Entry)var5.next();
               var3.put(var6.getKey(), var1.deserialize((JsonElement)var6.getValue(), VariantList.class));
            }
         }

         return var3;
      }

      @Nullable
      protected Multipart func_187998_b(JsonDeserializationContext var1, JsonObject var2) {
         if (!var2.has("multipart")) {
            return null;
         } else {
            JsonArray var3 = JsonUtils.func_151214_t(var2, "multipart");
            return (Multipart)var1.deserialize(var3, Multipart.class);
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static final class ContainerHolder {
      @VisibleForTesting
      final Gson field_209575_a = (new GsonBuilder()).registerTypeAdapter(ModelBlockDefinition.class, new ModelBlockDefinition.Deserializer()).registerTypeAdapter(Variant.class, new Variant.Deserializer()).registerTypeAdapter(VariantList.class, new VariantList.Deserializer()).registerTypeAdapter(Multipart.class, new Multipart.Deserializer(this)).registerTypeAdapter(Selector.class, new Selector.Deserializer()).create();
      private StateContainer<Block, IBlockState> field_209576_b;

      public ContainerHolder() {
         super();
      }

      public StateContainer<Block, IBlockState> func_209574_a() {
         return this.field_209576_b;
      }

      public void func_209573_a(StateContainer<Block, IBlockState> var1) {
         this.field_209576_b = var1;
      }
   }
}
