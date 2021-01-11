package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBlock {
   private static final Logger field_178313_f = LogManager.getLogger();
   static final Gson field_178319_a = (new GsonBuilder()).registerTypeAdapter(ModelBlock.class, new ModelBlock.Deserializer()).registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer()).registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer()).create();
   private final List<BlockPart> field_178314_g;
   private final boolean field_178321_h;
   private final boolean field_178322_i;
   private ItemCameraTransforms field_178320_j;
   public String field_178317_b;
   protected final Map<String, String> field_178318_c;
   protected ModelBlock field_178315_d;
   protected ResourceLocation field_178316_e;

   public static ModelBlock func_178307_a(Reader var0) {
      return (ModelBlock)field_178319_a.fromJson(var0, ModelBlock.class);
   }

   public static ModelBlock func_178294_a(String var0) {
      return func_178307_a(new StringReader(var0));
   }

   protected ModelBlock(List<BlockPart> var1, Map<String, String> var2, boolean var3, boolean var4, ItemCameraTransforms var5) {
      this((ResourceLocation)null, var1, var2, var3, var4, var5);
   }

   protected ModelBlock(ResourceLocation var1, Map<String, String> var2, boolean var3, boolean var4, ItemCameraTransforms var5) {
      this(var1, Collections.emptyList(), var2, var3, var4, var5);
   }

   private ModelBlock(ResourceLocation var1, List<BlockPart> var2, Map<String, String> var3, boolean var4, boolean var5, ItemCameraTransforms var6) {
      super();
      this.field_178317_b = "";
      this.field_178314_g = var2;
      this.field_178322_i = var4;
      this.field_178321_h = var5;
      this.field_178318_c = var3;
      this.field_178316_e = var1;
      this.field_178320_j = var6;
   }

   public List<BlockPart> func_178298_a() {
      return this.func_178295_k() ? this.field_178315_d.func_178298_a() : this.field_178314_g;
   }

   private boolean func_178295_k() {
      return this.field_178315_d != null;
   }

   public boolean func_178309_b() {
      return this.func_178295_k() ? this.field_178315_d.func_178309_b() : this.field_178322_i;
   }

   public boolean func_178311_c() {
      return this.field_178321_h;
   }

   public boolean func_178303_d() {
      return this.field_178316_e == null || this.field_178315_d != null && this.field_178315_d.func_178303_d();
   }

   public void func_178299_a(Map<ResourceLocation, ModelBlock> var1) {
      if (this.field_178316_e != null) {
         this.field_178315_d = (ModelBlock)var1.get(this.field_178316_e);
      }

   }

   public boolean func_178300_b(String var1) {
      return !"missingno".equals(this.func_178308_c(var1));
   }

   public String func_178308_c(String var1) {
      if (!this.func_178304_d(var1)) {
         var1 = '#' + var1;
      }

      return this.func_178302_a(var1, new ModelBlock.Bookkeep(this));
   }

   private String func_178302_a(String var1, ModelBlock.Bookkeep var2) {
      if (this.func_178304_d(var1)) {
         if (this == var2.field_178323_b) {
            field_178313_f.warn("Unable to resolve texture due to upward reference: " + var1 + " in " + this.field_178317_b);
            return "missingno";
         } else {
            String var3 = (String)this.field_178318_c.get(var1.substring(1));
            if (var3 == null && this.func_178295_k()) {
               var3 = this.field_178315_d.func_178302_a(var1, var2);
            }

            var2.field_178323_b = this;
            if (var3 != null && this.func_178304_d(var3)) {
               var3 = var2.field_178324_a.func_178302_a(var3, var2);
            }

            return var3 != null && !this.func_178304_d(var3) ? var3 : "missingno";
         }
      } else {
         return var1;
      }
   }

   private boolean func_178304_d(String var1) {
      return var1.charAt(0) == '#';
   }

   public ResourceLocation func_178305_e() {
      return this.field_178316_e;
   }

   public ModelBlock func_178310_f() {
      return this.func_178295_k() ? this.field_178315_d.func_178310_f() : this;
   }

   public ItemCameraTransforms func_181682_g() {
      ItemTransformVec3f var1 = this.func_181681_a(ItemCameraTransforms.TransformType.THIRD_PERSON);
      ItemTransformVec3f var2 = this.func_181681_a(ItemCameraTransforms.TransformType.FIRST_PERSON);
      ItemTransformVec3f var3 = this.func_181681_a(ItemCameraTransforms.TransformType.HEAD);
      ItemTransformVec3f var4 = this.func_181681_a(ItemCameraTransforms.TransformType.GUI);
      ItemTransformVec3f var5 = this.func_181681_a(ItemCameraTransforms.TransformType.GROUND);
      ItemTransformVec3f var6 = this.func_181681_a(ItemCameraTransforms.TransformType.FIXED);
      return new ItemCameraTransforms(var1, var2, var3, var4, var5, var6);
   }

   private ItemTransformVec3f func_181681_a(ItemCameraTransforms.TransformType var1) {
      return this.field_178315_d != null && !this.field_178320_j.func_181687_c(var1) ? this.field_178315_d.func_181681_a(var1) : this.field_178320_j.func_181688_b(var1);
   }

   public static void func_178312_b(Map<ResourceLocation, ModelBlock> var0) {
      Iterator var1 = var0.values().iterator();

      while(var1.hasNext()) {
         ModelBlock var2 = (ModelBlock)var1.next();

         try {
            ModelBlock var3 = var2.field_178315_d;

            for(ModelBlock var4 = var3.field_178315_d; var3 != var4; var4 = var4.field_178315_d.field_178315_d) {
               var3 = var3.field_178315_d;
            }

            throw new ModelBlock.LoopException();
         } catch (NullPointerException var5) {
         }
      }

   }

   public static class LoopException extends RuntimeException {
      public LoopException() {
         super();
      }
   }

   public static class Deserializer implements JsonDeserializer<ModelBlock> {
      public Deserializer() {
         super();
      }

      public ModelBlock deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         List var5 = this.func_178325_a(var3, var4);
         String var6 = this.func_178326_c(var4);
         boolean var7 = StringUtils.isEmpty(var6);
         boolean var8 = var5.isEmpty();
         if (var8 && var7) {
            throw new JsonParseException("BlockModel requires either elements or parent, found neither");
         } else if (!var7 && !var8) {
            throw new JsonParseException("BlockModel requires either elements or parent, found both");
         } else {
            Map var9 = this.func_178329_b(var4);
            boolean var10 = this.func_178328_a(var4);
            ItemCameraTransforms var11 = ItemCameraTransforms.field_178357_a;
            if (var4.has("display")) {
               JsonObject var12 = JsonUtils.func_152754_s(var4, "display");
               var11 = (ItemCameraTransforms)var3.deserialize(var12, ItemCameraTransforms.class);
            }

            return var8 ? new ModelBlock(new ResourceLocation(var6), var9, var10, true, var11) : new ModelBlock(var5, var9, var10, true, var11);
         }
      }

      private Map<String, String> func_178329_b(JsonObject var1) {
         HashMap var2 = Maps.newHashMap();
         if (var1.has("textures")) {
            JsonObject var3 = var1.getAsJsonObject("textures");
            Iterator var4 = var3.entrySet().iterator();

            while(var4.hasNext()) {
               Entry var5 = (Entry)var4.next();
               var2.put(var5.getKey(), ((JsonElement)var5.getValue()).getAsString());
            }
         }

         return var2;
      }

      private String func_178326_c(JsonObject var1) {
         return JsonUtils.func_151219_a(var1, "parent", "");
      }

      protected boolean func_178328_a(JsonObject var1) {
         return JsonUtils.func_151209_a(var1, "ambientocclusion", true);
      }

      protected List<BlockPart> func_178325_a(JsonDeserializationContext var1, JsonObject var2) {
         ArrayList var3 = Lists.newArrayList();
         if (var2.has("elements")) {
            Iterator var4 = JsonUtils.func_151214_t(var2, "elements").iterator();

            while(var4.hasNext()) {
               JsonElement var5 = (JsonElement)var4.next();
               var3.add((BlockPart)var1.deserialize(var5, BlockPart.class));
            }
         }

         return var3;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }

   static final class Bookkeep {
      public final ModelBlock field_178324_a;
      public ModelBlock field_178323_b;

      private Bookkeep(ModelBlock var1) {
         super();
         this.field_178324_a = var1;
      }

      // $FF: synthetic method
      Bookkeep(ModelBlock var1, Object var2) {
         this(var1);
      }
   }
}
