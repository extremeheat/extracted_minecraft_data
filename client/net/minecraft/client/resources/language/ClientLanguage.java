package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FormattedCharSequence;
import org.slf4j.Logger;

public class ClientLanguage extends Language {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<String, String> storage;
   private final boolean defaultRightToLeft;

   private ClientLanguage(Map<String, String> var1, boolean var2) {
      super();
      this.storage = var1;
      this.defaultRightToLeft = var2;
   }

   public static ClientLanguage loadFrom(ResourceManager var0, List<LanguageInfo> var1) {
      HashMap var2 = Maps.newHashMap();
      boolean var3 = false;

      for(LanguageInfo var5 : var1) {
         var3 |= var5.isBidirectional();
         String var6 = var5.getCode();
         String var7 = String.format("lang/%s.json", var6);

         for(String var9 : var0.getNamespaces()) {
            try {
               ResourceLocation var10 = new ResourceLocation(var9, var7);
               appendFrom(var6, var0.getResourceStack(var10), var2);
            } catch (Exception var11) {
               LOGGER.warn("Skipped language file: {}:{} ({})", new Object[]{var9, var7, var11.toString()});
            }
         }
      }

      return new ClientLanguage(ImmutableMap.copyOf(var2), var3);
   }

   private static void appendFrom(String var0, List<Resource> var1, Map<String, String> var2) {
      for(Resource var4 : var1) {
         try (InputStream var5 = var4.open()) {
            Language.loadFromJson(var5, var2::put);
         } catch (IOException var10) {
            LOGGER.warn("Failed to load translations for {} from pack {}", new Object[]{var0, var4.sourcePackId(), var10});
         }
      }
   }

   @Override
   public String getOrDefault(String var1) {
      return this.storage.getOrDefault(var1, var1);
   }

   @Override
   public boolean has(String var1) {
      return this.storage.containsKey(var1);
   }

   @Override
   public boolean isDefaultRightToLeft() {
      return this.defaultRightToLeft;
   }

   @Override
   public FormattedCharSequence getVisualOrder(FormattedText var1) {
      return FormattedBidiReorder.reorder(var1, this.defaultRightToLeft);
   }
}
