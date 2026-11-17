package net.minecraft.client.resources.language;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.locale.DeprecatedTranslationsInfo;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FormattedCharSequence;
import org.slf4j.Logger;

public class ClientLanguage extends Language {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Set<String> NON_REPLACEABLE_PREFIXES = Set.of("menu.custom_options", "menu.custom_screen_info");
   private final Map<String, String> storage;
   private final boolean defaultRightToLeft;

   private ClientLanguage(final Map<String, String> storage, final boolean defaultRightToLeft) {
      super();
      this.storage = storage;
      this.defaultRightToLeft = defaultRightToLeft;
   }

   public static ClientLanguage loadFrom(final ResourceManager resourceManager, final List<String> languageStack, final boolean defaultRightToLeft) {
      Map<String, String> translations = new HashMap();

      for(String languageCode : languageStack) {
         String path = String.format(Locale.ROOT, "lang/%s.json", languageCode);
         boolean hasAnyTranslations = false;

         for(String namespace : resourceManager.getNamespaces()) {
            Identifier location = Identifier.fromNamespaceAndPath(namespace, path);
            List<Resource> resourceStack = resourceManager.getResourceStack(location);
            if (!resourceStack.isEmpty()) {
               appendFrom(languageCode, resourceStack, translations);
               hasAnyTranslations = true;
            }
         }

         if (!hasAnyTranslations) {
            throw new EmptyTranslationsException(languageCode);
         }
      }

      DeprecatedTranslationsInfo.loadFromDefaultResource().applyToMap(translations);
      return new ClientLanguage(Map.copyOf(translations), defaultRightToLeft);
   }

   private static void appendFrom(final String languageCode, final List<Resource> resources, final Map<String, String> translations) {
      for(Resource resource : resources) {
         try {
            InputStream inputStream = resource.open();

            try {
               boolean isBuiltIn = (Boolean)resource.knownPackInfo().map(KnownPack::isVanilla).orElse(false);
               Language.loadFromJson(inputStream, (key, value) -> {
                  if (!isBuiltIn) {
                     Stream var10000 = NON_REPLACEABLE_PREFIXES.stream();
                     Objects.requireNonNull(key);
                     if (!var10000.noneMatch(key::startsWith)) {
                        LOGGER.warn("Rejected language key {} from pack {} - this key cannot be replaced", key, resource.sourcePackId());
                        return;
                     }
                  }

                  translations.put(key, value);
               });
            } catch (Throwable var9) {
               if (inputStream != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (inputStream != null) {
               inputStream.close();
            }
         } catch (IOException e) {
            LOGGER.warn("Failed to load translations for {} from pack {}", new Object[]{languageCode, resource.sourcePackId(), e});
         }
      }

   }

   public String getOrDefault(final String key, final String defaultValue) {
      return (String)this.storage.getOrDefault(key, defaultValue);
   }

   public boolean has(final String key) {
      return this.storage.containsKey(key);
   }

   public boolean isDefaultRightToLeft() {
      return this.defaultRightToLeft;
   }

   public FormattedCharSequence getVisualOrder(final FormattedText logicalOrderText) {
      return FormattedBidiReorder.reorder(logicalOrderText, this.defaultRightToLeft);
   }
}
