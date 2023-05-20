package net.minecraft.locale;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringDecomposer;
import org.slf4j.Logger;

public abstract class Language {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new Gson();
   private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
   public static final String DEFAULT = "en_us";
   private static volatile Language instance = loadDefault();

   public Language() {
      super();
   }

   private static Language loadDefault() {
      Builder var0 = ImmutableMap.builder();
      BiConsumer var1 = var0::put;
      String var2 = "/assets/minecraft/lang/en_us.json";

      try (InputStream var3 = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json")) {
         loadFromJson(var3, var1);
      } catch (JsonParseException | IOException var8) {
         LOGGER.error("Couldn't read strings from {}", "/assets/minecraft/lang/en_us.json", var8);
      }

      final ImmutableMap var9 = var0.build();
      return new Language() {
         @Override
         public String getOrDefault(String var1, String var2) {
            return var9.getOrDefault(var1, var2);
         }

         @Override
         public boolean has(String var1) {
            return var9.containsKey(var1);
         }

         @Override
         public boolean isDefaultRightToLeft() {
            return false;
         }

         @Override
         public FormattedCharSequence getVisualOrder(FormattedText var1) {
            return var1x -> var1.visit(
                     (var1xx, var2) -> StringDecomposer.iterateFormatted(var2, var1xx, var1x) ? Optional.empty() : FormattedText.STOP_ITERATION, Style.EMPTY
                  )
                  .isPresent();
         }
      };
   }

   public static void loadFromJson(InputStream var0, BiConsumer<String, String> var1) {
      JsonObject var2 = (JsonObject)GSON.fromJson(new InputStreamReader(var0, StandardCharsets.UTF_8), JsonObject.class);

      for(Entry var4 : var2.entrySet()) {
         String var5 = UNSUPPORTED_FORMAT_PATTERN.matcher(GsonHelper.convertToString((JsonElement)var4.getValue(), (String)var4.getKey())).replaceAll("%$1s");
         var1.accept((String)var4.getKey(), var5);
      }
   }

   public static Language getInstance() {
      return instance;
   }

   public static void inject(Language var0) {
      instance = var0;
   }

   public String getOrDefault(String var1) {
      return this.getOrDefault(var1, var1);
   }

   public abstract String getOrDefault(String var1, String var2);

   public abstract boolean has(String var1);

   public abstract boolean isDefaultRightToLeft();

   public abstract FormattedCharSequence getVisualOrder(FormattedText var1);

   public List<FormattedCharSequence> getVisualOrder(List<FormattedText> var1) {
      return var1.stream().map(this::getVisualOrder).collect(ImmutableList.toImmutableList());
   }
}
