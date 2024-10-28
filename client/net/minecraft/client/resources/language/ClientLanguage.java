package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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

   public static ClientLanguage loadFrom(ResourceManager var0, List<String> var1, boolean var2) {
      HashMap var3 = Maps.newHashMap();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         String var6 = String.format(Locale.ROOT, "lang/%s.json", var5);
         Iterator var7 = var0.getNamespaces().iterator();

         while(var7.hasNext()) {
            String var8 = (String)var7.next();

            try {
               ResourceLocation var9 = ResourceLocation.fromNamespaceAndPath(var8, var6);
               appendFrom(var5, var0.getResourceStack(var9), var3);
            } catch (Exception var10) {
               LOGGER.warn("Skipped language file: {}:{} ({})", new Object[]{var8, var6, var10.toString()});
            }
         }
      }

      return new ClientLanguage(ImmutableMap.copyOf(var3), var2);
   }

   private static void appendFrom(String var0, List<Resource> var1, Map<String, String> var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Resource var4 = (Resource)var3.next();

         try {
            InputStream var5 = var4.open();

            try {
               Objects.requireNonNull(var2);
               Language.loadFromJson(var5, var2::put);
            } catch (Throwable var9) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (var5 != null) {
               var5.close();
            }
         } catch (IOException var10) {
            LOGGER.warn("Failed to load translations for {} from pack {}", new Object[]{var0, var4.sourcePackId(), var10});
         }
      }

   }

   public String getOrDefault(String var1, String var2) {
      return (String)this.storage.getOrDefault(var1, var2);
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
