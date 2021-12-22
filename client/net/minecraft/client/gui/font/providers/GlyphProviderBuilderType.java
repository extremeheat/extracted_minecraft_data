package net.minecraft.client.gui.font.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.Util;

public enum GlyphProviderBuilderType {
   BITMAP("bitmap", BitmapProvider.Builder::fromJson),
   TTF("ttf", TrueTypeGlyphProviderBuilder::fromJson),
   LEGACY_UNICODE("legacy_unicode", LegacyUnicodeBitmapsProvider.Builder::fromJson);

   private static final Map<String, GlyphProviderBuilderType> BY_NAME = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      GlyphProviderBuilderType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GlyphProviderBuilderType var4 = var1[var3];
         var0.put(var4.name, var4);
      }

   });
   private final String name;
   private final Function<JsonObject, GlyphProviderBuilder> factory;

   private GlyphProviderBuilderType(String var3, Function<JsonObject, GlyphProviderBuilder> var4) {
      this.name = var3;
      this.factory = var4;
   }

   public static GlyphProviderBuilderType byName(String var0) {
      GlyphProviderBuilderType var1 = (GlyphProviderBuilderType)BY_NAME.get(var0);
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid type: " + var0);
      } else {
         return var1;
      }
   }

   public GlyphProviderBuilder create(JsonObject var1) {
      return (GlyphProviderBuilder)this.factory.apply(var1);
   }

   // $FF: synthetic method
   private static GlyphProviderBuilderType[] $values() {
      return new GlyphProviderBuilderType[]{BITMAP, TTF, LEGACY_UNICODE};
   }
}
