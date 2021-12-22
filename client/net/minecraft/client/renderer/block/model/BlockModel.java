package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
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
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
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
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.Material;
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
   private static final char REFERENCE_CHAR = '#';
   public static final String PARTICLE_TEXTURE_REFERENCE = "particle";
   private final List<BlockElement> elements;
   @Nullable
   private final BlockModel.GuiLight guiLight;
   private final boolean hasAmbientOcclusion;
   private final ItemTransforms transforms;
   private final List<ItemOverride> overrides;
   public String name = "";
   @VisibleForTesting
   protected final Map<String, Either<Material, String>> textureMap;
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

   public BlockModel(@Nullable ResourceLocation var1, List<BlockElement> var2, Map<String, Either<Material, String>> var3, boolean var4, @Nullable BlockModel.GuiLight var5, ItemTransforms var6, List<ItemOverride> var7) {
      super();
      this.elements = var2;
      this.hasAmbientOcclusion = var4;
      this.guiLight = var5;
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

   public BlockModel.GuiLight getGuiLight() {
      if (this.guiLight != null) {
         return this.guiLight;
      } else {
         return this.parent != null ? this.parent.getGuiLight() : BlockModel.GuiLight.SIDE;
      }
   }

   public boolean isResolved() {
      return this.parentLocation == null || this.parent != null && this.parent.isResolved();
   }

   public List<ItemOverride> getOverrides() {
      return this.overrides;
   }

   private ItemOverrides getItemOverrides(ModelBakery var1, BlockModel var2) {
      if (this.overrides.isEmpty()) {
         return ItemOverrides.EMPTY;
      } else {
         Objects.requireNonNull(var1);
         return new ItemOverrides(var1, var2, var1::getModel, this.overrides);
      }
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

   public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> var1, Set<Pair<String, String>> var2) {
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

      HashSet var11 = Sets.newHashSet(new Material[]{this.getMaterial("particle")});
      Iterator var6 = this.getElements().iterator();

      while(var6.hasNext()) {
         BlockElement var7 = (BlockElement)var6.next();

         Material var10;
         for(Iterator var8 = var7.faces.values().iterator(); var8.hasNext(); var11.add(var10)) {
            BlockElementFace var9 = (BlockElementFace)var8.next();
            var10 = this.getMaterial(var9.texture);
            if (Objects.equals(var10.texture(), MissingTextureAtlasSprite.getLocation())) {
               var2.add(Pair.of(var9.texture, this.name));
            }
         }
      }

      this.overrides.forEach((var4x) -> {
         UnbakedModel var5 = (UnbakedModel)var1.apply(var4x.getModel());
         if (!Objects.equals(var5, this)) {
            var11.addAll(var5.getMaterials(var1, var2));
         }
      });
      if (this.getRootModel() == ModelBakery.GENERATION_MARKER) {
         ItemModelGenerator.LAYERS.forEach((var2x) -> {
            var11.add(this.getMaterial(var2x));
         });
      }

      return var11;
   }

   public BakedModel bake(ModelBakery var1, Function<Material, TextureAtlasSprite> var2, ModelState var3, ResourceLocation var4) {
      return this.bake(var1, this, var2, var3, var4, true);
   }

   public BakedModel bake(ModelBakery var1, BlockModel var2, Function<Material, TextureAtlasSprite> var3, ModelState var4, ResourceLocation var5, boolean var6) {
      TextureAtlasSprite var7 = (TextureAtlasSprite)var3.apply(this.getMaterial("particle"));
      if (this.getRootModel() == ModelBakery.BLOCK_ENTITY_MARKER) {
         return new BuiltInModel(this.getTransforms(), this.getItemOverrides(var1, var2), var7, this.getGuiLight().lightLikeBlock());
      } else {
         SimpleBakedModel.Builder var8 = (new SimpleBakedModel.Builder(this, this.getItemOverrides(var1, var2), var6)).particle(var7);
         Iterator var9 = this.getElements().iterator();

         while(var9.hasNext()) {
            BlockElement var10 = (BlockElement)var9.next();
            Iterator var11 = var10.faces.keySet().iterator();

            while(var11.hasNext()) {
               Direction var12 = (Direction)var11.next();
               BlockElementFace var13 = (BlockElementFace)var10.faces.get(var12);
               TextureAtlasSprite var14 = (TextureAtlasSprite)var3.apply(this.getMaterial(var13.texture));
               if (var13.cullForDirection == null) {
                  var8.addUnculledFace(bakeFace(var10, var13, var14, var12, var4, var5));
               } else {
                  var8.addCulledFace(Direction.rotate(var4.getRotation().getMatrix(), var13.cullForDirection), bakeFace(var10, var13, var14, var12, var4, var5));
               }
            }
         }

         return var8.build();
      }
   }

   private static BakedQuad bakeFace(BlockElement var0, BlockElementFace var1, TextureAtlasSprite var2, Direction var3, ModelState var4, ResourceLocation var5) {
      return FACE_BAKERY.bakeQuad(var0.from, var0.field_315, var1, var2, var3, var4, var0.rotation, var0.shade, var5);
   }

   public boolean hasTexture(String var1) {
      return !MissingTextureAtlasSprite.getLocation().equals(this.getMaterial(var1).texture());
   }

   public Material getMaterial(String var1) {
      if (isTextureReference(var1)) {
         var1 = var1.substring(1);
      }

      ArrayList var2 = Lists.newArrayList();

      while(true) {
         Either var3 = this.findTextureEntry(var1);
         Optional var4 = var3.left();
         if (var4.isPresent()) {
            return (Material)var4.get();
         }

         var1 = (String)var3.right().get();
         if (var2.contains(var1)) {
            LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", Joiner.on("->").join(var2), var1, this.name);
            return new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
         }

         var2.add(var1);
      }
   }

   private Either<Material, String> findTextureEntry(String var1) {
      for(BlockModel var2 = this; var2 != null; var2 = var2.parent) {
         Either var3 = (Either)var2.textureMap.get(var1);
         if (var3 != null) {
            return var3;
         }
      }

      return Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation()));
   }

   static boolean isTextureReference(String var0) {
      return var0.charAt(0) == '#';
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

   public static enum GuiLight {
      FRONT("front"),
      SIDE("side");

      private final String name;

      private GuiLight(String var3) {
         this.name = var3;
      }

      public static BlockModel.GuiLight getByName(String var0) {
         BlockModel.GuiLight[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            BlockModel.GuiLight var4 = var1[var3];
            if (var4.name.equals(var0)) {
               return var4;
            }
         }

         throw new IllegalArgumentException("Invalid gui light: " + var0);
      }

      public boolean lightLikeBlock() {
         return this == SIDE;
      }

      // $FF: synthetic method
      private static BlockModel.GuiLight[] $values() {
         return new BlockModel.GuiLight[]{FRONT, SIDE};
      }
   }

   public static class Deserializer implements JsonDeserializer<BlockModel> {
      private static final boolean DEFAULT_AMBIENT_OCCLUSION = true;

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

         List var13 = this.getOverrides(var3, var4);
         BlockModel.GuiLight var11 = null;
         if (var4.has("gui_light")) {
            var11 = BlockModel.GuiLight.getByName(GsonHelper.getAsString(var4, "gui_light"));
         }

         ResourceLocation var12 = var6.isEmpty() ? null : new ResourceLocation(var6);
         return new BlockModel(var12, var5, var7, var8, var11, var9, var13);
      }

      protected List<ItemOverride> getOverrides(JsonDeserializationContext var1, JsonObject var2) {
         ArrayList var3 = Lists.newArrayList();
         if (var2.has("overrides")) {
            JsonArray var4 = GsonHelper.getAsJsonArray(var2, "overrides");
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               JsonElement var6 = (JsonElement)var5.next();
               var3.add((ItemOverride)var1.deserialize(var6, ItemOverride.class));
            }
         }

         return var3;
      }

      private Map<String, Either<Material, String>> getTextureMap(JsonObject var1) {
         ResourceLocation var2 = TextureAtlas.LOCATION_BLOCKS;
         HashMap var3 = Maps.newHashMap();
         if (var1.has("textures")) {
            JsonObject var4 = GsonHelper.getAsJsonObject(var1, "textures");
            Iterator var5 = var4.entrySet().iterator();

            while(var5.hasNext()) {
               Entry var6 = (Entry)var5.next();
               var3.put((String)var6.getKey(), parseTextureLocationOrReference(var2, ((JsonElement)var6.getValue()).getAsString()));
            }
         }

         return var3;
      }

      private static Either<Material, String> parseTextureLocationOrReference(ResourceLocation var0, String var1) {
         if (BlockModel.isTextureReference(var1)) {
            return Either.right(var1.substring(1));
         } else {
            ResourceLocation var2 = ResourceLocation.tryParse(var1);
            if (var2 == null) {
               throw new JsonParseException(var1 + " is not valid resource location");
            } else {
               return Either.left(new Material(var0, var2));
            }
         }
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
               var3.add((BlockElement)var1.deserialize(var5, BlockElement.class));
            }
         }

         return var3;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static class LoopException extends RuntimeException {
      public LoopException(String var1) {
         super(var1);
      }
   }
}
