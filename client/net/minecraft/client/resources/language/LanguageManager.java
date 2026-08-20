package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class LanguageManager implements ResourceManagerReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final LanguageInfo DEFAULT_LANGUAGE = new LanguageInfo("US", "English", false);
   private final Minecraft minecraft;
   private Map<String, LanguageInfo> languages;
   private String currentCode;
   private final Consumer<ClientLanguage> reloadCallback;

   public LanguageManager(final Minecraft minecraft, final String languageCode, final Consumer<ClientLanguage> reloadCallback) {
      super();
      this.languages = ImmutableMap.of("en_us", DEFAULT_LANGUAGE);
      this.minecraft = minecraft;
      this.currentCode = languageCode;
      this.reloadCallback = reloadCallback;
   }

   private static Map<String, LanguageInfo> extractLanguages(final Stream<PackResources> resourcePacks) {
      Map<String, LanguageInfo> result = Maps.newHashMap();
      resourcePacks.forEach((resourcePack) -> {
         try {
            LanguageMetadataSection languageMetadataSection = (LanguageMetadataSection)resourcePack.getMetadataSection(LanguageMetadataSection.TYPE);
            if (languageMetadataSection != null) {
               Map var10000 = languageMetadataSection.languages();
               Objects.requireNonNull(result);
               var10000.forEach(result::putIfAbsent);
            }
         } catch (Exception e) {
            LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", resourcePack.packId(), e);
         }

      });
      return ImmutableMap.copyOf(result);
   }

   public void onResourceManagerReload(final ResourceManager resourceManager) {
      this.languages = extractLanguages(resourceManager.listPacks());
      List<String> languageStack = new ArrayList(2);
      boolean defaultRightToLeft = DEFAULT_LANGUAGE.bidirectional();
      languageStack.add("en_us");
      if (!this.currentCode.equals("en_us")) {
         LanguageInfo currentLanguage = (LanguageInfo)this.languages.get(this.currentCode);
         if (currentLanguage != null) {
            languageStack.add(this.currentCode);
            defaultRightToLeft = currentLanguage.bidirectional();
         }
      }

      try {
         ClientLanguage locale = ClientLanguage.loadFrom(resourceManager, languageStack, defaultRightToLeft);
         Language.inject(locale);
         this.reloadCallback.accept(locale);
      } catch (EmptyTranslationsException ex) {
         this.minecraft.gui.setScreen(new ErrorScreen(Component.translatable("options.language.load_translations_failed"), Component.translatable("options.language.empty_or_missing_translation", ex.getLanguageCode())));
         LOGGER.warn("Skipped unable to find any translations for {}", ex.getLanguageCode());
      } catch (Exception ex) {
         LOGGER.warn("Unable to load languages: {} ({})", languageStack, ex.toString());
      }

   }

   public void setSelected(final String code) {
      this.currentCode = code;
   }

   public String getSelected() {
      return this.currentCode;
   }

   public SortedMap<String, LanguageInfo> getLanguages() {
      return new TreeMap(this.languages);
   }

   public @Nullable LanguageInfo getLanguage(final String code) {
      return (LanguageInfo)this.languages.get(code);
   }
}
