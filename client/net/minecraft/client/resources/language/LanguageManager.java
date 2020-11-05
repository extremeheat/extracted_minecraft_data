package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Stream;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageManager implements ResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final LanguageInfo DEFAULT_LANGUAGE = new LanguageInfo("en_us", "US", "English", false);
   private Map<String, LanguageInfo> languages;
   private String currentCode;
   private LanguageInfo currentLanguage;

   public LanguageManager(String var1) {
      super();
      this.languages = ImmutableMap.of("en_us", DEFAULT_LANGUAGE);
      this.currentLanguage = DEFAULT_LANGUAGE;
      this.currentCode = var1;
   }

   private static Map<String, LanguageInfo> extractLanguages(Stream<PackResources> var0) {
      HashMap var1 = Maps.newHashMap();
      var0.forEach((var1x) -> {
         try {
            LanguageMetadataSection var2 = (LanguageMetadataSection)var1x.getMetadataSection(LanguageMetadataSection.SERIALIZER);
            if (var2 != null) {
               Iterator var3 = var2.getLanguages().iterator();

               while(var3.hasNext()) {
                  LanguageInfo var4 = (LanguageInfo)var3.next();
                  var1.putIfAbsent(var4.getCode(), var4);
               }
            }
         } catch (IOException | RuntimeException var5) {
            LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", var1x.getName(), var5);
         }

      });
      return ImmutableMap.copyOf(var1);
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.languages = extractLanguages(var1.listPacks());
      LanguageInfo var2 = (LanguageInfo)this.languages.getOrDefault("en_us", DEFAULT_LANGUAGE);
      this.currentLanguage = (LanguageInfo)this.languages.getOrDefault(this.currentCode, var2);
      ArrayList var3 = Lists.newArrayList(new LanguageInfo[]{var2});
      if (this.currentLanguage != var2) {
         var3.add(this.currentLanguage);
      }

      ClientLanguage var4 = ClientLanguage.loadFrom(var1, var3);
      I18n.setLanguage(var4);
      Language.inject(var4);
   }

   public void setSelected(LanguageInfo var1) {
      this.currentCode = var1.getCode();
      this.currentLanguage = var1;
   }

   public LanguageInfo getSelected() {
      return this.currentLanguage;
   }

   public SortedSet<LanguageInfo> getLanguages() {
      return Sets.newTreeSet(this.languages.values());
   }

   public LanguageInfo getLanguage(String var1) {
      return (LanguageInfo)this.languages.get(var1);
   }
}
