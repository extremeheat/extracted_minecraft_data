package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FormattedCharSequence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientLanguage extends Language {
   private static final Logger LOGGER = LogManager.getLogger();
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
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         LanguageInfo var5 = (LanguageInfo)var4.next();
         var3 |= var5.isBidirectional();
         String var6 = String.format("lang/%s.json", var5.getCode());
         Iterator var7 = var0.getNamespaces().iterator();

         while(var7.hasNext()) {
            String var8 = (String)var7.next();

            try {
               ResourceLocation var9 = new ResourceLocation(var8, var6);
               appendFrom(var0.getResources(var9), var2);
            } catch (FileNotFoundException var10) {
            } catch (Exception var11) {
               LOGGER.warn("Skipped language file: {}:{} ({})", var8, var6, var11.toString());
            }
         }
      }

      return new ClientLanguage(ImmutableMap.copyOf(var2), var3);
   }

   private static void appendFrom(List<Resource> var0, Map<String, String> var1) {
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Resource var3 = (Resource)var2.next();

         try {
            InputStream var4 = var3.getInputStream();

            try {
               Objects.requireNonNull(var1);
               Language.loadFromJson(var4, var1::put);
            } catch (Throwable var8) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (var4 != null) {
               var4.close();
            }
         } catch (IOException var9) {
            LOGGER.warn("Failed to load translations from {}", var3, var9);
         }
      }

   }

   public String getOrDefault(String var1) {
      return (String)this.storage.getOrDefault(var1, var1);
   }

   public boolean has(String var1) {
      return this.storage.containsKey(var1);
   }

   public boolean isDefaultRightToLeft() {
      return this.defaultRightToLeft;
   }

   public FormattedCharSequence getVisualOrder(FormattedText var1) {
      return FormattedBidiReorder.reorder(var1, this.defaultRightToLeft);
   }
}
