package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
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
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBlock implements IUnbakedModel {
   private static final Logger field_178313_f = LogManager.getLogger();
   private static final ItemModelGenerator field_209571_g = new ItemModelGenerator();
   private static final FaceBakery field_209572_h = new FaceBakery();
   @VisibleForTesting
   static final Gson field_178319_a = (new GsonBuilder()).registerTypeAdapter(ModelBlock.class, new ModelBlock.Deserializer()).registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer()).registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer()).registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer()).create();
   private final List<BlockPart> field_178314_g;
   private final boolean field_178321_h;
   private final boolean field_178322_i;
   private final ItemCameraTransforms field_178320_j;
   private final List<ItemOverride> field_187968_k;
   public String field_178317_b = "";
   @VisibleForTesting
   protected final Map<String, String> field_178318_c;
   @VisibleForTesting
   ModelBlock field_178315_d;
   @VisibleForTesting
   ResourceLocation field_178316_e;

   public static ModelBlock func_178307_a(Reader var0) {
      return (ModelBlock)JsonUtils.func_193839_a(field_178319_a, var0, ModelBlock.class);
   }

   public static ModelBlock func_178294_a(String var0) {
      return func_178307_a(new StringReader(var0));
   }

   public ModelBlock(@Nullable ResourceLocation var1, List<BlockPart> var2, Map<String, String> var3, boolean var4, boolean var5, ItemCameraTransforms var6, List<ItemOverride> var7) {
      super();
      this.field_178314_g = var2;
      this.field_178322_i = var4;
      this.field_178321_h = var5;
      this.field_178318_c = var3;
      this.field_178316_e = var1;
      this.field_178320_j = var6;
      this.field_187968_k = var7;
   }

   public List<BlockPart> func_178298_a() {
      return this.field_178314_g.isEmpty() && this.func_178295_k() ? this.field_178315_d.func_178298_a() : this.field_178314_g;
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

   private void func_209566_a(Function<ResourceLocation, IUnbakedModel> var1) {
      if (this.field_178316_e != null) {
         IUnbakedModel var2 = (IUnbakedModel)var1.apply(this.field_178316_e);
         if (var2 != null) {
            if (!(var2 instanceof ModelBlock)) {
               throw new IllegalStateException("BlockModel parent has to be a block model.");
            }

            this.field_178315_d = (ModelBlock)var2;
         }
      }

   }

   public List<ItemOverride> func_187966_f() {
      return this.field_187968_k;
   }

   private ItemOverrideList func_209568_a(ModelBlock var1, Function<ResourceLocation, IUnbakedModel> var2, Function<ResourceLocation, TextureAtlasSprite> var3) {
      return this.field_187968_k.isEmpty() ? ItemOverrideList.field_188022_a : new ItemOverrideList(var1, var2, var3, this.field_187968_k);
   }

   public Collection<ResourceLocation> func_187965_e() {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = this.field_187968_k.iterator();

      while(var2.hasNext()) {
         ItemOverride var3 = (ItemOverride)var2.next();
         var1.add(var3.func_188026_a());
      }

      if (this.field_178316_e != null) {
         var1.add(this.field_178316_e);
      }

      return var1;
   }

   public Collection<ResourceLocation> func_209559_a(Function<ResourceLocation, IUnbakedModel> var1, Set<String> var2) {
      if (!this.func_178303_d()) {
         LinkedHashSet var3 = Sets.newLinkedHashSet();
         ModelBlock var4 = this;

         do {
            var3.add(var4);
            var4.func_209566_a(var1);
            if (var3.contains(var4.field_178315_d)) {
               field_178313_f.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", var4.field_178317_b, var3.stream().map((var0) -> {
                  return var0.field_178317_b;
               }).collect(Collectors.joining(" -> ")), var4.field_178315_d.field_178317_b);
               var4.field_178316_e = ModelBakery.field_177604_a;
               var4.func_209566_a(var1);
            }

            var4 = var4.field_178315_d;
         } while(!var4.func_178303_d());
      }

      HashSet var9 = Sets.newHashSet(new ResourceLocation[]{new ResourceLocation(this.func_178308_c("particle"))});
      Iterator var10 = this.func_178298_a().iterator();

      while(var10.hasNext()) {
         BlockPart var5 = (BlockPart)var10.next();

         String var8;
         for(Iterator var6 = var5.field_178240_c.values().iterator(); var6.hasNext(); var9.add(new ResourceLocation(var8))) {
            BlockPartFace var7 = (BlockPartFace)var6.next();
            var8 = this.func_178308_c(var7.field_178242_d);
            if (Objects.equals(var8, MissingTextureSprite.func_195677_a().func_195668_m().toString())) {
               var2.add(String.format("%s in %s", var7.field_178242_d, this.field_178317_b));
            }
         }
      }

      this.field_187968_k.forEach((var4x) -> {
         IUnbakedModel var5 = (IUnbakedModel)var1.apply(var4x.func_188026_a());
         if (!Objects.equals(var5, this)) {
            var9.addAll(var5.func_209559_a(var1, var2));
         }
      });
      if (this.func_178310_f() == ModelBakery.field_177606_o) {
         ItemModelGenerator.field_178398_a.forEach((var2x) -> {
            var9.add(new ResourceLocation(this.func_178308_c(var2x)));
         });
      }

      return var9;
   }

   public IBakedModel func_209558_a(Function<ResourceLocation, IUnbakedModel> var1, Function<ResourceLocation, TextureAtlasSprite> var2, ModelRotation var3, boolean var4) {
      return this.func_209565_a(this, var1, var2, var3, var4);
   }

   private IBakedModel func_209565_a(ModelBlock var1, Function<ResourceLocation, IUnbakedModel> var2, Function<ResourceLocation, TextureAtlasSprite> var3, ModelRotation var4, boolean var5) {
      ModelBlock var6 = this.func_178310_f();
      if (var6 == ModelBakery.field_177606_o) {
         return field_209571_g.func_209579_a(var3, this).func_209565_a(var1, var2, var3, var4, var5);
      } else if (var6 == ModelBakery.field_177616_r) {
         return new BuiltInModel(this.func_181682_g(), this.func_209568_a(var1, var2, var3));
      } else {
         TextureAtlasSprite var7 = (TextureAtlasSprite)var3.apply(new ResourceLocation(this.func_178308_c("particle")));
         SimpleBakedModel.Builder var8 = (new SimpleBakedModel.Builder(this, this.func_209568_a(var1, var2, var3))).func_177646_a(var7);
         Iterator var9 = this.func_178298_a().iterator();

         while(var9.hasNext()) {
            BlockPart var10 = (BlockPart)var9.next();
            Iterator var11 = var10.field_178240_c.keySet().iterator();

            while(var11.hasNext()) {
               EnumFacing var12 = (EnumFacing)var11.next();
               BlockPartFace var13 = (BlockPartFace)var10.field_178240_c.get(var12);
               TextureAtlasSprite var14 = (TextureAtlasSprite)var3.apply(new ResourceLocation(this.func_178308_c(var13.field_178242_d)));
               if (var13.field_178244_b == null) {
                  var8.func_177648_a(func_209567_a(var10, var13, var14, var12, var4, var5));
               } else {
                  var8.func_177650_a(var4.func_177523_a(var13.field_178244_b), func_209567_a(var10, var13, var14, var12, var4, var5));
               }
            }
         }

         return var8.func_177645_b();
      }
   }

   private static BakedQuad func_209567_a(BlockPart var0, BlockPartFace var1, TextureAtlasSprite var2, EnumFacing var3, ModelRotation var4, boolean var5) {
      return field_209572_h.func_199332_a(var0.field_178241_a, var0.field_178239_b, var1, var2, var3, var4, var0.field_178237_d, var5, var0.field_178238_e);
   }

   public boolean func_178300_b(String var1) {
      return !MissingTextureSprite.func_195677_a().func_195668_m().toString().equals(this.func_178308_c(var1));
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
            field_178313_f.warn("Unable to resolve texture due to upward reference: {} in {}", var1, this.field_178317_b);
            return MissingTextureSprite.func_195677_a().func_195668_m().toString();
         } else {
            String var3 = (String)this.field_178318_c.get(var1.substring(1));
            if (var3 == null && this.func_178295_k()) {
               var3 = this.field_178315_d.func_178302_a(var1, var2);
            }

            var2.field_178323_b = this;
            if (var3 != null && this.func_178304_d(var3)) {
               var3 = var2.field_178324_a.func_178302_a(var3, var2);
            }

            return var3 != null && !this.func_178304_d(var3) ? var3 : MissingTextureSprite.func_195677_a().func_195668_m().toString();
         }
      } else {
         return var1;
      }
   }

   private boolean func_178304_d(String var1) {
      return var1.charAt(0) == '#';
   }

   public ModelBlock func_178310_f() {
      return this.func_178295_k() ? this.field_178315_d.func_178310_f() : this;
   }

   public ItemCameraTransforms func_181682_g() {
      ItemTransformVec3f var1 = this.func_181681_a(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
      ItemTransformVec3f var2 = this.func_181681_a(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
      ItemTransformVec3f var3 = this.func_181681_a(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
      ItemTransformVec3f var4 = this.func_181681_a(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
      ItemTransformVec3f var5 = this.func_181681_a(ItemCameraTransforms.TransformType.HEAD);
      ItemTransformVec3f var6 = this.func_181681_a(ItemCameraTransforms.TransformType.GUI);
      ItemTransformVec3f var7 = this.func_181681_a(ItemCameraTransforms.TransformType.GROUND);
      ItemTransformVec3f var8 = this.func_181681_a(ItemCameraTransforms.TransformType.FIXED);
      return new ItemCameraTransforms(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private ItemTransformVec3f func_181681_a(ItemCameraTransforms.TransformType var1) {
      return this.field_178315_d != null && !this.field_178320_j.func_181687_c(var1) ? this.field_178315_d.func_181681_a(var1) : this.field_178320_j.func_181688_b(var1);
   }

   public static class Deserializer implements JsonDeserializer<ModelBlock> {
      public Deserializer() {
         super();
      }

      public ModelBlock deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         List var5 = this.func_178325_a(var3, var4);
         String var6 = this.func_178326_c(var4);
         Map var7 = this.func_178329_b(var4);
         boolean var8 = this.func_178328_a(var4);
         ItemCameraTransforms var9 = ItemCameraTransforms.field_178357_a;
         if (var4.has("display")) {
            JsonObject var10 = JsonUtils.func_152754_s(var4, "display");
            var9 = (ItemCameraTransforms)var3.deserialize(var10, ItemCameraTransforms.class);
         }

         List var12 = this.func_187964_a(var3, var4);
         ResourceLocation var11 = var6.isEmpty() ? null : new ResourceLocation(var6);
         return new ModelBlock(var11, var5, var7, var8, true, var9, var12);
      }

      protected List<ItemOverride> func_187964_a(JsonDeserializationContext var1, JsonObject var2) {
         ArrayList var3 = Lists.newArrayList();
         if (var2.has("overrides")) {
            JsonArray var4 = JsonUtils.func_151214_t(var2, "overrides");
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               JsonElement var6 = (JsonElement)var5.next();
               var3.add(var1.deserialize(var6, ItemOverride.class));
            }
         }

         return var3;
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
               var3.add(var1.deserialize(var5, BlockPart.class));
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
