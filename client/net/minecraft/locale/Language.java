package net.minecraft.locale;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
      DeprecatedTranslationsInfo deprecatedInfo = DeprecatedTranslationsInfo.loadFromDefaultResource();
      Map<String, String> loadedData = new HashMap();
      Objects.requireNonNull(loadedData);
      BiConsumer<String, String> output = loadedData::put;
      parseTranslations(output, "/assets/minecraft/lang/en_us.json");
      deprecatedInfo.applyToMap(loadedData);
      final Map<String, String> storage = Map.copyOf(loadedData);
      return new Language() {
         public String getOrDefault(final String elementId, final String defaultValue) {
            return (String)storage.getOrDefault(elementId, defaultValue);
         }

         public boolean has(final String elementId) {
            return storage.containsKey(elementId);
         }

         public boolean isDefaultRightToLeft() {
            return false;
         }

         public FormattedCharSequence getVisualOrder(final FormattedText logicalOrderText) {
            return (output) -> logicalOrderText.visit((style, contents) -> StringDecomposer.iterateFormatted(contents, style, output) ? Optional.empty() : FormattedText.STOP_ITERATION, Style.EMPTY).isPresent();
         }
      };
   }

   private static void parseTranslations(final BiConsumer<String, String> output, final String path) {
      try {
         InputStream stream = Language.class.getResourceAsStream(path);

         try {
            loadFromJson(stream, output);
         } catch (Throwable var6) {
            if (stream != null) {
               try {
                  stream.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (stream != null) {
            stream.close();
         }
      } catch (JsonParseException | IOException e) {
         LOGGER.error("Couldn't read strings from {}", path, e);
      }

   }

   public static void loadFromJson(final InputStream stream, final BiConsumer<String, String> output) {
      JsonObject entries = (JsonObject)GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);

      for(Map.Entry<String, JsonElement> entry : entries.entrySet()) {
         String text = UNSUPPORTED_FORMAT_PATTERN.matcher(GsonHelper.convertToString((JsonElement)entry.getValue(), (String)entry.getKey())).replaceAll("%$1s");
         output.accept((String)entry.getKey(), text);
      }

   }

   public static Language getInstance() {
      return instance;
   }

   public static void inject(final Language language) {
      instance = language;
   }

   public String getOrDefault(final String elementId) {
      return this.getOrDefault(elementId, elementId);
   }

   public abstract String getOrDefault(final String elementId, final String defaultValue);

   public abstract boolean has(final String elementId);

   public abstract boolean isDefaultRightToLeft();

   public abstract FormattedCharSequence getVisualOrder(final FormattedText logicalOrderText);

   public List<FormattedCharSequence> getVisualOrder(final List<FormattedText> lines) {
      return (List)lines.stream().map(this::getVisualOrder).collect(ImmutableList.toImmutableList());
   }
}
