package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.slf4j.Logger;

public class LanguageManager implements ResourceManagerReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final LanguageInfo DEFAULT_LANGUAGE = new LanguageInfo("US", "English", false);
   private Map<String, LanguageInfo> languages;
   private String currentCode;
   private final Consumer<ClientLanguage> reloadCallback;

   public LanguageManager(String var1, Consumer<ClientLanguage> var2) {
      super();
      this.languages = ImmutableMap.of("en_us", DEFAULT_LANGUAGE);
      this.currentCode = var1;
      this.reloadCallback = var2;
   }

   private static Map<String, LanguageInfo> extractLanguages(Stream<PackResources> var0) {
      HashMap var1 = Maps.newHashMap();
      var0.forEach((var1x) -> {
         try {
            LanguageMetadataSection var2 = (LanguageMetadataSection)var1x.getMetadataSection(LanguageMetadataSection.TYPE);
            if (var2 != null) {
               Map var10000 = var2.languages();
               Objects.requireNonNull(var1);
               var10000.forEach(var1::putIfAbsent);
            }
         } catch (IOException | RuntimeException var3) {
            LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", var1x.packId(), var3);
         }

      });
      return ImmutableMap.copyOf(var1);
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.languages = extractLanguages(var1.listPacks());
      ArrayList var2 = new ArrayList(2);
      boolean var3 = DEFAULT_LANGUAGE.bidirectional();
      var2.add("en_us");
      if (!this.currentCode.equals("en_us")) {
         LanguageInfo var4 = (LanguageInfo)this.languages.get(this.currentCode);
         if (var4 != null) {
            var2.add(this.currentCode);
            var3 = var4.bidirectional();
         }
      }

      ClientLanguage var5 = ClientLanguage.loadFrom(var1, var2, var3);
      I18n.setLanguage(var5);
      Language.inject(var5);
      this.reloadCallback.accept(var5);
   }

   public void setSelected(String var1) {
      this.currentCode = var1;
   }

   public String getSelected() {
      return this.currentCode;
   }

   public SortedMap<String, LanguageInfo> getLanguages() {
      return new TreeMap(this.languages);
   }

   @Nullable
   public LanguageInfo getLanguage(String var1) {
      return (LanguageInfo)this.languages.get(var1);
   }
}
