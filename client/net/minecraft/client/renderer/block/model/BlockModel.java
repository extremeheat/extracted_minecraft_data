package net.minecraft.client.renderer.block.model;

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
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockModel implements UnbakedModel {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final FaceBakery FACE_BAKERY = new FaceBakery();
   @VisibleForTesting
   static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(BlockModel.class, new BlockModel.Deserializer()).registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer()).registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer()).registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer()).registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer()).create();
   private final List<BlockElement> elements;
   private final boolean isGui3d;
   private final boolean hasAmbientOcclusion;
   private final ItemTransforms transforms;
   private final List<ItemOverride> overrides;
   public String name = "";
   @VisibleForTesting
   protected final Map<String, String> textureMap;
   @Nullable
   protected BlockModel parent;
   @Nullable
   protected ResourceLocation parentLocation;

   public static BlockModel fromStream(Reader var0) {
      return (BlockModel)GsonHelper.fromJson(GSON, var0, BlockModel.class);
   }

   public static BlockModel fromString(String var0) {
      return fromStream(new StringReader(var0));
   }

   public BlockModel(@Nullable ResourceLocation var1, List<BlockElement> var2, Map<String, String> var3, boolean var4, boolean var5, ItemTransforms var6, List<ItemOverride> var7) {
      super();
      this.elements = var2;
      this.hasAmbientOcclusion = var4;
      this.isGui3d = var5;
      this.textureMap = var3;
      this.parentLocation = var1;
      this.transforms = var6;
      this.overrides = var7;
   }

   public List<BlockElement> getElements() {
      return this.elements.isEmpty() && this.parent != null ? this.parent.getElements() : this.elements;
   }

   public boolean hasAmbientOcclusion() {
      return this.parent != null ? this.parent.hasAmbientOcclusion() : this.hasAmbientOcclusion;
   }

   public boolean isGui3d() {
      return this.isGui3d;
   }

   public List<ItemOverride> getOverrides() {
      return this.overrides;
   }

   private ItemOverrides getItemOverrides(ModelBakery var1, BlockModel var2) {
      return this.overrides.isEmpty() ? ItemOverrides.EMPTY : new ItemOverrides(var1, var2, var1::getModel, this.overrides);
   }

   public Collection<ResourceLocation> getDependencies() {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = this.overrides.iterator();

      while(var2.hasNext()) {
         ItemOverride var3 = (ItemOverride)var2.next();
         var1.add(var3.getModel());
      }

      if (this.parentLocation != null) {
         var1.add(this.parentLocation);
      }

      return var1;
   }

   public Collection<ResourceLocation> getTextures(Function<ResourceLocation, UnbakedModel> var1, Set<String> var2) {
      LinkedHashSet var3 = Sets.newLinkedHashSet();

      for(BlockModel var4 = this; var4.parentLocation != null && var4.parent == null; var4 = var4.parent) {
         var3.add(var4);
         UnbakedModel var5 = (UnbakedModel)var1.apply(var4.parentLocation);
         if (var5 == null) {
            LOGGER.warn("No parent '{}' while loading model '{}'", this.parentLocation, var4);
         }

         if (var3.contains(var5)) {
            LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", var4, var3.stream().map(Object::toString).collect(Collectors.joining(" -> ")), this.parentLocation);
            var5 = null;
         }

         if (var5 == null) {
            var4.parentLocation = ModelBakery.MISSING_MODEL_LOCATION;
            var5 = (UnbakedModel)var1.apply(var4.parentLocation);
         }

         if (!(var5 instanceof BlockModel)) {
            throw new IllegalStateException("BlockModel parent has to be a block model.");
         }

         var4.parent = (BlockModel)var5;
      }

      HashSet var11 = Sets.newHashSet(new ResourceLocation[]{new ResourceLocation(this.getTexture("particle"))});
      Iterator var6 = this.getElements().iterator();

      while(var6.hasNext()) {
         BlockElement var7 = (BlockElement)var6.next();

         String var10;
         for(Iterator var8 = var7.faces.values().iterator(); var8.hasNext(); var11.add(new ResourceLocation(var10))) {
            BlockElementFace var9 = (BlockElementFace)var8.next();
            var10 = this.getTexture(var9.texture);
            if (Objects.equals(var10, MissingTextureAtlasSprite.getLocation().toString())) {
               var2.add(String.format("%s in %s", var9.texture, this.name));
            }
         }
      }

      this.overrides.forEach((var4x) -> {
         UnbakedModel var5 = (UnbakedModel)var1.apply(var4x.getModel());
         if (!Objects.equals(var5, this)) {
            var11.addAll(var5.getTextures(var1, var2));
         }
      });
      if (this.getRootModel() == ModelBakery.GENERATION_MARKER) {
         ItemModelGenerator.LAYERS.forEach((var2x) -> {
            var11.add(new ResourceLocation(this.getTexture(var2x)));
         });
      }

      return var11;
   }

   public BakedModel bake(ModelBakery var1, Function<ResourceLocation, TextureAtlasSprite> var2, ModelState var3) {
      return this.bake(var1, this, var2, var3);
   }

   public BakedModel bake(ModelBakery var1, BlockModel var2, Function<ResourceLocation, TextureAtlasSprite> var3, ModelState var4) {
      TextureAtlasSprite var5 = (TextureAtlasSprite)var3.apply(new ResourceLocation(this.getTexture("particle")));
      if (this.getRootModel() == ModelBakery.BLOCK_ENTITY_MARKER) {
         return new BuiltInModel(this.getTransforms(), this.getItemOverrides(var1, var2), var5);
      } else {
         SimpleBakedModel.Builder var6 = (new SimpleBakedModel.Builder(this, this.getItemOverrides(var1, var2))).particle(var5);
         Iterator var7 = this.getElements().iterator();

         while(var7.hasNext()) {
            BlockElement var8 = (BlockElement)var7.next();
            Iterator var9 = var8.faces.keySet().iterator();

            while(var9.hasNext()) {
               Direction var10 = (Direction)var9.next();
               BlockElementFace var11 = (BlockElementFace)var8.faces.get(var10);
               TextureAtlasSprite var12 = (TextureAtlasSprite)var3.apply(new ResourceLocation(this.getTexture(var11.texture)));
               if (var11.cullForDirection == null) {
                  var6.addUnculledFace(bakeFace(var8, var11, var12, var10, var4));
               } else {
                  var6.addCulledFace(var4.getRotation().rotate(var11.cullForDirection), bakeFace(var8, var11, var12, var10, var4));
               }
            }
         }

         return var6.build();
      }
   }

   private static BakedQuad bakeFace(BlockElement var0, BlockElementFace var1, TextureAtlasSprite var2, Direction var3, ModelState var4) {
      return FACE_BAKERY.bakeQuad(var0.from, var0.to, var1, var2, var3, var4, var0.rotation, var0.shade);
   }

   public boolean hasTexture(String var1) {
      return !MissingTextureAtlasSprite.getLocation().toString().equals(this.getTexture(var1));
   }

   public String getTexture(String var1) {
      if (!this.isTextureReference(var1)) {
         var1 = '#' + var1;
      }

      return this.getTexture(var1, new BlockModel.Bookkeep(this));
   }

   private String getTexture(String var1, BlockModel.Bookkeep var2) {
      if (this.isTextureReference(var1)) {
         if (this == var2.maxDepth) {
            LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", var1, this.name);
            return MissingTextureAtlasSprite.getLocation().toString();
         } else {
            String var3 = (String)this.textureMap.get(var1.substring(1));
            if (var3 == null && this.parent != null) {
               var3 = this.parent.getTexture(var1, var2);
            }

            var2.maxDepth = this;
            if (var3 != null && this.isTextureReference(var3)) {
               var3 = var2.root.getTexture(var3, var2);
            }

            return var3 != null && !this.isTextureReference(var3) ? var3 : MissingTextureAtlasSprite.getLocation().toString();
         }
      } else {
         return var1;
      }
   }

   private boolean isTextureReference(String var1) {
      return var1.charAt(0) == '#';
   }

   public BlockModel getRootModel() {
      return this.parent == null ? this : this.parent.getRootModel();
   }

   public ItemTransforms getTransforms() {
      ItemTransform var1 = this.getTransform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
      ItemTransform var2 = this.getTransform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
      ItemTransform var3 = this.getTransform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
      ItemTransform var4 = this.getTransform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
      ItemTransform var5 = this.getTransform(ItemTransforms.TransformType.HEAD);
      ItemTransform var6 = this.getTransform(ItemTransforms.TransformType.GUI);
      ItemTransform var7 = this.getTransform(ItemTransforms.TransformType.GROUND);
      ItemTransform var8 = this.getTransform(ItemTransforms.TransformType.FIXED);
      return new ItemTransforms(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private ItemTransform getTransform(ItemTransforms.TransformType var1) {
      return this.parent != null && !this.transforms.hasTransform(var1) ? this.parent.getTransform(var1) : this.transforms.getTransform(var1);
   }

   public String toString() {
      return this.name;
   }

   public static class Deserializer implements JsonDeserializer<BlockModel> {
      public Deserializer() {
         super();
      }

      public BlockModel deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         List var5 = this.getElements(var3, var4);
         String var6 = this.getParentName(var4);
         Map var7 = this.getTextureMap(var4);
         boolean var8 = this.getAmbientOcclusion(var4);
         ItemTransforms var9 = ItemTransforms.NO_TRANSFORMS;
         if (var4.has("display")) {
            JsonObject var10 = GsonHelper.getAsJsonObject(var4, "display");
            var9 = (ItemTransforms)var3.deserialize(var10, ItemTransforms.class);
         }

         List var12 = this.getOverrides(var3, var4);
         ResourceLocation var11 = var6.isEmpty() ? null : new ResourceLocation(var6);
         return new BlockModel(var11, var5, var7, var8, true, var9, var12);
      }

      protected List<ItemOverride> getOverrides(JsonDeserializationContext var1, JsonObject var2) {
         ArrayList var3 = Lists.newArrayList();
         if (var2.has("overrides")) {
            JsonArray var4 = GsonHelper.getAsJsonArray(var2, "overrides");
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               JsonElement var6 = (JsonElement)var5.next();
               var3.add(var1.deserialize(var6, ItemOverride.class));
            }
         }

         return var3;
      }

      private Map<String, String> getTextureMap(JsonObject var1) {
         HashMap var2 = Maps.newHashMap();
         if (var1.has("textures")) {
            JsonObject var3 = GsonHelper.getAsJsonObject(var1, "textures");
            Iterator var4 = var3.entrySet().iterator();

            while(var4.hasNext()) {
               Entry var5 = (Entry)var4.next();
               var2.put(var5.getKey(), ((JsonElement)var5.getValue()).getAsString());
            }
         }

         return var2;
      }

      private String getParentName(JsonObject var1) {
         return GsonHelper.getAsString(var1, "parent", "");
      }

      protected boolean getAmbientOcclusion(JsonObject var1) {
         return GsonHelper.getAsBoolean(var1, "ambientocclusion", true);
      }

      protected List<BlockElement> getElements(JsonDeserializationContext var1, JsonObject var2) {
         ArrayList var3 = Lists.newArrayList();
         if (var2.has("elements")) {
            Iterator var4 = GsonHelper.getAsJsonArray(var2, "elements").iterator();

            while(var4.hasNext()) {
               JsonElement var5 = (JsonElement)var4.next();
               var3.add(var1.deserialize(var5, BlockElement.class));
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
      public final BlockModel root;
      public BlockModel maxDepth;

      private Bookkeep(BlockModel var1) {
         super();
         this.root = var1;
      }

      // $FF: synthetic method
      Bookkeep(BlockModel var1, Object var2) {
         this(var1);
      }
   }
}
