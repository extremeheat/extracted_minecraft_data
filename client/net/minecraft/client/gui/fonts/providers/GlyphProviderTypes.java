package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.Util;

public enum GlyphProviderTypes {
   BITMAP("bitmap", TextureGlyphProvider.Factory::func_211633_a),
   TTF("ttf", TrueTypeGlyphProvider.Factory::func_211624_a),
   LEGACY_UNICODE("legacy_unicode", TextureGlyphProviderUnicode.Factory::func_211629_a);

   private static final Map<String, GlyphProviderTypes> field_211640_d = (Map)Util.func_200696_a(Maps.newHashMap(), (var0) -> {
      GlyphProviderTypes[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GlyphProviderTypes var4 = var1[var3];
         var0.put(var4.field_211641_e, var4);
      }

   });
   private final String field_211641_e;
   private final Function<JsonObject, IGlyphProviderFactory> field_211642_f;

   private GlyphProviderTypes(String var3, Function<JsonObject, IGlyphProviderFactory> var4) {
      this.field_211641_e = var3;
      this.field_211642_f = var4;
   }

   public static GlyphProviderTypes func_211638_a(String var0) {
      GlyphProviderTypes var1 = (GlyphProviderTypes)field_211640_d.get(var0);
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid type: " + var0);
      } else {
         return var1;
      }
   }

   public IGlyphProviderFactory func_211637_a(JsonObject var1) {
      return (IGlyphProviderFactory)this.field_211642_f.apply(var1);
   }
}
