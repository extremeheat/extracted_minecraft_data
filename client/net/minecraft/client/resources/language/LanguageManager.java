package net.minecraft.client.resources.language;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.server.packs.Pack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageManager implements ResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   protected static final Locale LOCALE = new Locale();
   private String currentCode;
   private final Map<String, Language> languages = Maps.newHashMap();

   public LanguageManager(String var1) {
      super();
      this.currentCode = var1;
      I18n.setLocale(LOCALE);
   }

   public void reload(List<Pack> var1) {
      this.languages.clear();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Pack var3 = (Pack)var2.next();

         try {
            LanguageMetadataSection var4 = (LanguageMetadataSection)var3.getMetadataSection(LanguageMetadataSection.SERIALIZER);
            if (var4 != null) {
               Iterator var5 = var4.getLanguages().iterator();

               while(var5.hasNext()) {
                  Language var6 = (Language)var5.next();
                  if (!this.languages.containsKey(var6.getCode())) {
                     this.languages.put(var6.getCode(), var6);
                  }
               }
            }
         } catch (IOException | RuntimeException var7) {
            LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", var3.getName(), var7);
         }
      }

   }

   public void onResourceManagerReload(ResourceManager var1) {
      ArrayList var2 = Lists.newArrayList(new String[]{"en_us"});
      if (!"en_us".equals(this.currentCode)) {
         var2.add(this.currentCode);
      }

      LOCALE.loadFrom(var1, var2);
      net.minecraft.locale.Language.forceData(LOCALE.storage);
   }

   public boolean isBidirectional() {
      return this.getSelected() != null && this.getSelected().isBidirectional();
   }

   public void setSelected(Language var1) {
      this.currentCode = var1.getCode();
   }

   public Language getSelected() {
      String var1 = this.languages.containsKey(this.currentCode) ? this.currentCode : "en_us";
      return (Language)this.languages.get(var1);
   }

   public SortedSet<Language> getLanguages() {
      return Sets.newTreeSet(this.languages.values());
   }

   public Language getLanguage(String var1) {
      return (Language)this.languages.get(var1);
   }
}
