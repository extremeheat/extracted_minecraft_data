package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class BlockModel implements UnbakedModel {
   @VisibleForTesting
   static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(BlockModel.class, new Deserializer()).registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer()).registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer()).registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer()).create();
   private final List<BlockElement> elements;
   @Nullable
   private final UnbakedModel.GuiLight guiLight;
   @Nullable
   private final Boolean hasAmbientOcclusion;
   @Nullable
   private final ItemTransforms transforms;
   @VisibleForTesting
   private final TextureSlots.Data textureSlots;
   @Nullable
   private UnbakedModel parent;
   @Nullable
   private final ResourceLocation parentLocation;

   public static BlockModel fromStream(Reader var0) {
      return (BlockModel)GsonHelper.fromJson(GSON, var0, BlockModel.class);
   }

   public BlockModel(@Nullable ResourceLocation var1, List<BlockElement> var2, TextureSlots.Data var3, @Nullable Boolean var4, @Nullable UnbakedModel.GuiLight var5, @Nullable ItemTransforms var6) {
      super();
      this.elements = var2;
      this.hasAmbientOcclusion = var4;
      this.guiLight = var5;
      this.textureSlots = var3;
      this.parentLocation = var1;
      this.transforms = var6;
   }

   @Nullable
   public Boolean getAmbientOcclusion() {
      return this.hasAmbientOcclusion;
   }

   @Nullable
   public UnbakedModel.GuiLight getGuiLight() {
      return this.guiLight;
   }

   public void resolveDependencies(ResolvableModel.Resolver var1) {
      if (this.parentLocation != null) {
         this.parent = var1.resolve(this.parentLocation);
      }

   }

   @Nullable
   public UnbakedModel getParent() {
      return this.parent;
   }

   public TextureSlots.Data getTextureSlots() {
      return this.textureSlots;
   }

   @Nullable
   public ItemTransforms getTransforms() {
      return this.transforms;
   }

   public BakedModel bake(TextureSlots var1, ModelBaker var2, ModelState var3, boolean var4, boolean var5, ItemTransforms var6) {
      return this.elements.isEmpty() && this.parent != null ? this.parent.bake(var1, var2, var3, var4, var5, var6) : SimpleBakedModel.bakeElements(this.elements, var1, var2.sprites(), var3, var4, var5, true, var6);
   }

   @Nullable
   @VisibleForTesting
   List<BlockElement> getElements() {
      return this.elements;
   }

   @Nullable
   @VisibleForTesting
   ResourceLocation getParentLocation() {
      return this.parentLocation;
   }

   public static class Deserializer implements JsonDeserializer<BlockModel> {
      public Deserializer() {
         super();
      }

      public BlockModel deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         List var5 = this.getElements(var3, var4);
         String var6 = this.getParentName(var4);
         TextureSlots.Data var7 = this.getTextureMap(var4);
         Boolean var8 = this.getAmbientOcclusion(var4);
         ItemTransforms var9 = null;
         if (var4.has("display")) {
            JsonObject var10 = GsonHelper.getAsJsonObject(var4, "display");
            var9 = (ItemTransforms)var3.deserialize(var10, ItemTransforms.class);
         }

         UnbakedModel.GuiLight var12 = null;
         if (var4.has("gui_light")) {
            var12 = UnbakedModel.GuiLight.getByName(GsonHelper.getAsString(var4, "gui_light"));
         }

         ResourceLocation var11 = var6.isEmpty() ? null : ResourceLocation.parse(var6);
         return new BlockModel(var11, var5, var7, var8, var12, var9);
      }

      private TextureSlots.Data getTextureMap(JsonObject var1) {
         if (var1.has("textures")) {
            JsonObject var2 = GsonHelper.getAsJsonObject(var1, "textures");
            return TextureSlots.parseTextureMap(var2, TextureAtlas.LOCATION_BLOCKS);
         } else {
            return TextureSlots.Data.EMPTY;
         }
      }

      private String getParentName(JsonObject var1) {
         return GsonHelper.getAsString(var1, "parent", "");
      }

      @Nullable
      protected Boolean getAmbientOcclusion(JsonObject var1) {
         return var1.has("ambientocclusion") ? GsonHelper.getAsBoolean(var1, "ambientocclusion") : null;
      }

      protected List<BlockElement> getElements(JsonDeserializationContext var1, JsonObject var2) {
         if (!var2.has("elements")) {
            return List.of();
         } else {
            ArrayList var3 = new ArrayList();

            for(JsonElement var5 : GsonHelper.getAsJsonArray(var2, "elements")) {
               var3.add((BlockElement)var1.deserialize(var5, BlockElement.class));
            }

            return var3;
         }
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement var1, final Type var2, final JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
