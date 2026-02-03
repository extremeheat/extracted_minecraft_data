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
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;

public record BlockModel(@Nullable UnbakedGeometry geometry, UnbakedModel.@Nullable GuiLight guiLight, @Nullable Boolean ambientOcclusion, @Nullable ItemTransforms transforms, TextureSlots.Data textureSlots, @Nullable Identifier parent) implements UnbakedModel {
   @VisibleForTesting
   static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(BlockModel.class, new Deserializer()).registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer()).registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer()).registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer()).registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer()).create();

   public BlockModel {
      super();
   }

   public static BlockModel fromStream(final Reader reader) {
      return (BlockModel)GsonHelper.fromJson(GSON, reader, BlockModel.class);
   }

   public static class Deserializer implements JsonDeserializer<BlockModel> {
      public Deserializer() {
         super();
      }

      public BlockModel deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         UnbakedGeometry elements = this.getElements(context, object);
         String parentName = this.getParentName(object);
         TextureSlots.Data textureMap = this.getTextureMap(object);
         Boolean hasAmbientOcclusion = this.getAmbientOcclusion(object);
         ItemTransforms transforms = null;
         if (object.has("display")) {
            JsonObject display = GsonHelper.getAsJsonObject(object, "display");
            transforms = (ItemTransforms)context.deserialize(display, ItemTransforms.class);
         }

         UnbakedModel.GuiLight guiLight = null;
         if (object.has("gui_light")) {
            guiLight = UnbakedModel.GuiLight.getByName(GsonHelper.getAsString(object, "gui_light"));
         }

         Identifier parentLocation = parentName.isEmpty() ? null : Identifier.parse(parentName);
         return new BlockModel(elements, guiLight, hasAmbientOcclusion, transforms, textureMap, parentLocation);
      }

      private TextureSlots.Data getTextureMap(final JsonObject object) {
         if (object.has("textures")) {
            JsonObject texturesObject = GsonHelper.getAsJsonObject(object, "textures");
            return TextureSlots.parseTextureMap(texturesObject);
         } else {
            return TextureSlots.Data.EMPTY;
         }
      }

      private String getParentName(final JsonObject object) {
         return GsonHelper.getAsString(object, "parent", "");
      }

      protected @Nullable Boolean getAmbientOcclusion(final JsonObject object) {
         return object.has("ambientocclusion") ? GsonHelper.getAsBoolean(object, "ambientocclusion") : null;
      }

      protected @Nullable UnbakedGeometry getElements(final JsonDeserializationContext context, final JsonObject object) {
         if (!object.has("elements")) {
            return null;
         } else {
            List<BlockElement> elements = new ArrayList();

            for(JsonElement element : GsonHelper.getAsJsonArray(object, "elements")) {
               elements.add((BlockElement)context.deserialize(element, BlockElement.class));
            }

            return new SimpleUnbakedGeometry(elements);
         }
      }
   }
}
