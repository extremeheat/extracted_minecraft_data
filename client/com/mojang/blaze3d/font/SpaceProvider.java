package com.mojang.blaze3d.font;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMaps;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.util.GsonHelper;

public class SpaceProvider implements GlyphProvider {
   private final Int2ObjectMap<GlyphInfo.SpaceGlyphInfo> glyphs;

   public SpaceProvider(Int2FloatMap var1) {
      super();
      this.glyphs = new Int2ObjectOpenHashMap(var1.size());
      Int2FloatMaps.fastForEach(var1, (var1x) -> {
         float var2 = var1x.getFloatValue();
         this.glyphs.put(var1x.getIntKey(), () -> {
            return var2;
         });
      });
   }

   @Nullable
   public GlyphInfo getGlyph(int var1) {
      return (GlyphInfo)this.glyphs.get(var1);
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.keySet());
   }

   public static GlyphProviderBuilder builderFromJson(JsonObject var0) {
      Int2FloatOpenHashMap var1 = new Int2FloatOpenHashMap();
      JsonObject var2 = GsonHelper.getAsJsonObject(var0, "advances");
      Iterator var3 = var2.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         int[] var5 = ((String)var4.getKey()).codePoints().toArray();
         if (var5.length != 1) {
            throw new JsonParseException("Expected single codepoint, got " + Arrays.toString(var5));
         }

         float var6 = GsonHelper.convertToFloat((JsonElement)var4.getValue(), "advance");
         var1.put(var5[0], var6);
      }

      return (var1x) -> {
         return new SpaceProvider(var1);
      };
   }
}
