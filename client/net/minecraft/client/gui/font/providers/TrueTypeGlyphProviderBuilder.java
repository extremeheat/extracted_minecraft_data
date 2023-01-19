package net.minecraft.client.gui.font.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class TrueTypeGlyphProviderBuilder implements GlyphProviderBuilder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ResourceLocation location;
   private final float size;
   private final float oversample;
   private final float shiftX;
   private final float shiftY;
   private final String skip;

   public TrueTypeGlyphProviderBuilder(ResourceLocation var1, float var2, float var3, float var4, float var5, String var6) {
      super();
      this.location = var1;
      this.size = var2;
      this.oversample = var3;
      this.shiftX = var4;
      this.shiftY = var5;
      this.skip = var6;
   }

   public static GlyphProviderBuilder fromJson(JsonObject var0) {
      float var1 = 0.0F;
      float var2 = 0.0F;
      if (var0.has("shift")) {
         JsonArray var3 = var0.getAsJsonArray("shift");
         if (var3.size() != 2) {
            throw new JsonParseException("Expected 2 elements in 'shift', found " + var3.size());
         }

         var1 = GsonHelper.convertToFloat(var3.get(0), "shift[0]");
         var2 = GsonHelper.convertToFloat(var3.get(1), "shift[1]");
      }

      StringBuilder var7 = new StringBuilder();
      if (var0.has("skip")) {
         JsonElement var4 = var0.get("skip");
         if (var4.isJsonArray()) {
            JsonArray var5 = GsonHelper.convertToJsonArray(var4, "skip");

            for(int var6 = 0; var6 < var5.size(); ++var6) {
               var7.append(GsonHelper.convertToString(var5.get(var6), "skip[" + var6 + "]"));
            }
         } else {
            var7.append(GsonHelper.convertToString(var4, "skip"));
         }
      }

      return new TrueTypeGlyphProviderBuilder(
         new ResourceLocation(GsonHelper.getAsString(var0, "file")),
         GsonHelper.getAsFloat(var0, "size", 11.0F),
         GsonHelper.getAsFloat(var0, "oversample", 1.0F),
         var1,
         var2,
         var7.toString()
      );
   }

   @Nullable
   @Override
   public GlyphProvider create(ResourceManager var1) {
      STBTTFontinfo var2 = null;
      ByteBuffer var3 = null;

      try {
         TrueTypeGlyphProvider var5;
         try (InputStream var4 = var1.open(new ResourceLocation(this.location.getNamespace(), "font/" + this.location.getPath()))) {
            LOGGER.debug("Loading font {}", this.location);
            var2 = STBTTFontinfo.malloc();
            var3 = TextureUtil.readResource(var4);
            var3.flip();
            LOGGER.debug("Reading font {}", this.location);
            if (!STBTruetype.stbtt_InitFont(var2, var3)) {
               throw new IOException("Invalid ttf");
            }

            var5 = new TrueTypeGlyphProvider(var3, var2, this.size, this.oversample, this.shiftX, this.shiftY, this.skip);
         }

         return var5;
      } catch (Exception var9) {
         LOGGER.error("Couldn't load truetype font {}", this.location, var9);
         if (var2 != null) {
            var2.free();
         }

         MemoryUtil.memFree(var3);
         return null;
      }
   }
}
