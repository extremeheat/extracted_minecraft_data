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
import net.minecraft.client.resources.model.UnbakedGeometry;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;

public record BlockModel(@Nullable UnbakedGeometry geometry, UnbakedModel.@Nullable GuiLight guiLight, @Nullable Boolean ambientOcclusion, @Nullable ItemTransforms transforms, TextureSlots.Data textureSlots, @Nullable Identifier parent) implements UnbakedModel {
   @VisibleForTesting
   static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(BlockModel.class, new Deserializer()).registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer()).registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer()).registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer()).registerTypeAdapter(ItemTransforms.class, new ItemTransforms.Deserializer()).create();

   public BlockModel(@Nullable UnbakedGeometry var1, UnbakedModel.@Nullable GuiLight var2, @Nullable Boolean var3, @Nullable ItemTransforms var4, TextureSlots.Data var5, @Nullable Identifier var6) {
      super();
      this.geometry = var1;
      this.guiLight = var2;
      this.ambientOcclusion = var3;
      this.transforms = var4;
      this.textureSlots = var5;
      this.parent = var6;
   }

   public static BlockModel fromStream(Reader var0) {
      return (BlockModel)GsonHelper.fromJson(GSON, var0, BlockModel.class);
   }

   public static class Deserializer implements JsonDeserializer<BlockModel> {
      public Deserializer() {
         super();
      }

      public BlockModel deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         UnbakedGeometry var5 = this.getElements(var3, var4);
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

         Identifier var11 = var6.isEmpty() ? null : Identifier.parse(var6);
         return new BlockModel(var5, var12, var8, var9, var7, var11);
      }

      private TextureSlots.Data getTextureMap(JsonObject var1) {
         if (var1.has("textures")) {
            JsonObject var2 = GsonHelper.getAsJsonObject(var1, "textures");
            return TextureSlots.parseTextureMap(var2);
         } else {
            return TextureSlots.Data.EMPTY;
         }
      }

      private String getParentName(JsonObject var1) {
         return GsonHelper.getAsString(var1, "parent", "");
      }

      protected @Nullable Boolean getAmbientOcclusion(JsonObject var1) {
         return var1.has("ambientocclusion") ? GsonHelper.getAsBoolean(var1, "ambientocclusion") : null;
      }

      protected @Nullable UnbakedGeometry getElements(JsonDeserializationContext var1, JsonObject var2) {
         if (!var2.has("elements")) {
            return null;
         } else {
            ArrayList var3 = new ArrayList();

            for(JsonElement var5 : GsonHelper.getAsJsonArray(var2, "elements")) {
               var3.add((BlockElement)var1.deserialize(var5, BlockElement.class));
            }

            return new SimpleUnbakedGeometry(var3);
         }
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement var1, final Type var2, final JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
