package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class RegistryDataCollector {
   private static final Logger LOGGER = LogUtils.getLogger();
   private @Nullable ContentsCollector contentsCollector;
   private @Nullable TagCollector tagCollector;

   public RegistryDataCollector() {
      super();
   }

   public void appendContents(final ResourceKey<? extends Registry<?>> registry, final List<RegistrySynchronization.PackedRegistryEntry> elementData) {
      if (this.contentsCollector == null) {
         this.contentsCollector = new ContentsCollector();
      }

      this.contentsCollector.append(registry, elementData);
   }

   public void appendTags(final Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> data) {
      if (this.tagCollector == null) {
         this.tagCollector = new TagCollector();
      }

      TagCollector var10001 = this.tagCollector;
      Objects.requireNonNull(var10001);
      data.forEach(var10001::append);
   }

   private static <T> Registry.PendingTags<T> resolveRegistryTags(final RegistryAccess.Frozen context, final ResourceKey<? extends Registry<? extends T>> registryKey, final TagNetworkSerialization.NetworkPayload tags) {
      Registry<T> staticRegistry = context.lookupOrThrow(registryKey);
      return staticRegistry.prepareTagReload(tags.resolve(staticRegistry));
   }

   private RegistryAccess loadNewElementsAndTags(final ResourceProvider knownDataSource, final ContentsCollector contentsCollector, final boolean tagsForSynchronizedRegistriesOnly) {
      LayeredRegistryAccess<ClientRegistryLayer> base = ClientRegistryLayer.createRegistryAccess();
      RegistryAccess.Frozen loadingContext = base.getAccessForLoading(ClientRegistryLayer.REMOTE);
      Map<ResourceKey<? extends Registry<?>>, RegistryDataLoader.NetworkedRegistryData> entriesToLoad = new HashMap();
      contentsCollector.elements.forEach((registryKey, elements) -> entriesToLoad.put(registryKey, new RegistryDataLoader.NetworkedRegistryData(elements, TagNetworkSerialization.NetworkPayload.EMPTY)));
      List<Registry.PendingTags<?>> pendingStaticTags = new ArrayList();
      if (this.tagCollector != null) {
         this.tagCollector.forEach((registryKey, tags) -> {
            if (!tags.isEmpty()) {
               if (RegistrySynchronization.isNetworkable(registryKey)) {
                  entriesToLoad.compute(registryKey, (key, previousData) -> {
                     List<RegistrySynchronization.PackedRegistryEntry> elements = previousData != null ? previousData.elements() : List.of();
                     return new RegistryDataLoader.NetworkedRegistryData(elements, tags);
                  });
               } else if (!tagsForSynchronizedRegistriesOnly) {
                  pendingStaticTags.add(resolveRegistryTags(loadingContext, registryKey, tags));
               }

            }
         });
      }

      List<HolderLookup.RegistryLookup<?>> contextRegistriesWithTags = TagLoader.buildUpdatedLookups(loadingContext, pendingStaticTags);

      RegistryAccess.Frozen receivedRegistries;
      try {
         long start = Util.getMillis();
         receivedRegistries = (RegistryAccess.Frozen)RegistryDataLoader.load(entriesToLoad, knownDataSource, contextRegistriesWithTags, RegistryDataLoader.SYNCHRONIZED_REGISTRIES, Util.backgroundExecutor()).join();
         long end = Util.getMillis();
         LOGGER.debug("Loading network data took {} ms", end - start);
      } catch (Exception e) {
         CrashReport report = CrashReport.forThrowable(e, "Network Registry Load");
         addCrashDetails(report, entriesToLoad, pendingStaticTags);
         throw new ReportedException(report);
      }

      RegistryAccess registries = base.replaceFrom(ClientRegistryLayer.REMOTE, receivedRegistries).compositeAccess();
      pendingStaticTags.forEach(Registry.PendingTags::apply);
      return registries;
   }

   private static void addCrashDetails(final CrashReport report, final Map<ResourceKey<? extends Registry<?>>, RegistryDataLoader.NetworkedRegistryData> dynamicRegistries, final List<Registry.PendingTags<?>> staticRegistries) {
      CrashReportCategory details = report.addCategory("Received Elements and Tags");
      details.setDetail("Dynamic Registries", (CrashReportDetail)(() -> (String)dynamicRegistries.entrySet().stream().sorted(Comparator.comparing((entry) -> ((ResourceKey)entry.getKey()).identifier())).map((entry) -> String.format(Locale.ROOT, "\n\t\t%s: elements=%d tags=%d", ((ResourceKey)entry.getKey()).identifier(), ((RegistryDataLoader.NetworkedRegistryData)entry.getValue()).elements().size(), ((RegistryDataLoader.NetworkedRegistryData)entry.getValue()).tags().size())).collect(Collectors.joining())));
      details.setDetail("Static Registries", (CrashReportDetail)(() -> (String)staticRegistries.stream().sorted(Comparator.comparing((entry) -> entry.key().identifier())).map((entry) -> String.format(Locale.ROOT, "\n\t\t%s: tags=%d", entry.key().identifier(), entry.size())).collect(Collectors.joining())));
   }

   private static void loadOnlyTags(final TagCollector tagCollector, final RegistryAccess.Frozen originalRegistries, final boolean includeSharedRegistries) {
      tagCollector.forEach((registryKey, tags) -> {
         if (includeSharedRegistries || RegistrySynchronization.isNetworkable(registryKey)) {
            resolveRegistryTags(originalRegistries, registryKey, tags).apply();
         }

      });
   }

   private static void updateComponents(final RegistryAccess.Frozen frozenRegistries, final boolean includeSharedRegistries) {
      BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(frozenRegistries).forEach((pendingComponents) -> {
         if (includeSharedRegistries || RegistrySynchronization.isNetworkable(pendingComponents.key())) {
            pendingComponents.apply();
         }

      });
   }

   public RegistryAccess.Frozen collectGameRegistries(final ResourceProvider knownDataSource, final RegistryAccess.Frozen originalRegistries, final boolean tagsAndComponentsForSynchronizedRegistriesOnly) {
      RegistryAccess registries;
      if (this.contentsCollector != null) {
         registries = this.loadNewElementsAndTags(knownDataSource, this.contentsCollector, tagsAndComponentsForSynchronizedRegistriesOnly);
      } else {
         if (this.tagCollector != null) {
            loadOnlyTags(this.tagCollector, originalRegistries, !tagsAndComponentsForSynchronizedRegistriesOnly);
         }

         registries = originalRegistries;
      }

      RegistryAccess.Frozen frozenRegistries = registries.freeze();
      updateComponents(frozenRegistries, !tagsAndComponentsForSynchronizedRegistriesOnly);
      return frozenRegistries;
   }

   private static class ContentsCollector {
      private final Map<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> elements = new HashMap();

      private ContentsCollector() {
         super();
      }

      public void append(final ResourceKey<? extends Registry<?>> registry, final List<RegistrySynchronization.PackedRegistryEntry> elementData) {
         ((List)this.elements.computeIfAbsent(registry, (ignore) -> new ArrayList())).addAll(elementData);
      }
   }

   private static class TagCollector {
      private final Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> tags = new HashMap();

      private TagCollector() {
         super();
      }

      public void append(final ResourceKey<? extends Registry<?>> registry, final TagNetworkSerialization.NetworkPayload tagData) {
         this.tags.put(registry, tagData);
      }

      public void forEach(final BiConsumer<? super ResourceKey<? extends Registry<?>>, ? super TagNetworkSerialization.NetworkPayload> action) {
         this.tags.forEach(action);
      }
   }
}
