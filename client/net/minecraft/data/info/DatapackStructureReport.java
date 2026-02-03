package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;

public class DatapackStructureReport implements DataProvider {
   private final PackOutput output;
   private static final Entry PSEUDO_REGISTRY = new Entry(true, false, true);
   private static final Entry STABLE_DYNAMIC_REGISTRY = new Entry(true, true, true);
   private static final Entry UNSTABLE_DYNAMIC_REGISTRY = new Entry(true, true, false);
   private static final Entry BUILT_IN_REGISTRY = new Entry(false, true, true);
   private static final Map<ResourceKey<? extends Registry<?>>, Entry> MANUAL_ENTRIES;
   private static final Map<String, CustomPackEntry> NON_REGISTRY_ENTRIES;
   private static final Codec<ResourceKey<? extends Registry<?>>> REGISTRY_KEY_CODEC;

   public DatapackStructureReport(final PackOutput output) {
      super();
      this.output = output;
   }

   public CompletableFuture<?> run(final CachedOutput cache) {
      Report report = new Report(this.listRegistries(), NON_REGISTRY_ENTRIES);
      Path path = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("datapack.json");
      return DataProvider.saveStable(cache, (JsonElement)DatapackStructureReport.Report.CODEC.encodeStart(JsonOps.INSTANCE, report).getOrThrow(), path);
   }

   public String getName() {
      return "Datapack Structure";
   }

   private void putIfNotPresent(final Map<ResourceKey<? extends Registry<?>>, Entry> output, final ResourceKey<? extends Registry<?>> key, final Entry entry) {
      Entry previous = (Entry)output.putIfAbsent(key, entry);
      if (previous != null) {
         throw new IllegalStateException("Duplicate entry for key " + String.valueOf(key.identifier()));
      }
   }

   private Map<ResourceKey<? extends Registry<?>>, Entry> listRegistries() {
      Map<ResourceKey<? extends Registry<?>>, Entry> result = new HashMap();
      BuiltInRegistries.REGISTRY.forEach((entry) -> this.putIfNotPresent(result, entry.key(), BUILT_IN_REGISTRY));
      RegistryDataLoader.WORLDGEN_REGISTRIES.forEach((entry) -> this.putIfNotPresent(result, entry.key(), UNSTABLE_DYNAMIC_REGISTRY));
      RegistryDataLoader.DIMENSION_REGISTRIES.forEach((entry) -> this.putIfNotPresent(result, entry.key(), UNSTABLE_DYNAMIC_REGISTRY));
      MANUAL_ENTRIES.forEach((key, entry) -> this.putIfNotPresent(result, key, entry));
      return result;
   }

   static {
      MANUAL_ENTRIES = Map.of(Registries.RECIPE, PSEUDO_REGISTRY, Registries.ADVANCEMENT, PSEUDO_REGISTRY, Registries.LOOT_TABLE, STABLE_DYNAMIC_REGISTRY, Registries.ITEM_MODIFIER, STABLE_DYNAMIC_REGISTRY, Registries.PREDICATE, STABLE_DYNAMIC_REGISTRY);
      NON_REGISTRY_ENTRIES = Map.of("structure", new CustomPackEntry(DatapackStructureReport.Format.STRUCTURE, new Entry(true, false, true)), "function", new CustomPackEntry(DatapackStructureReport.Format.MCFUNCTION, new Entry(true, true, true)));
      REGISTRY_KEY_CODEC = Identifier.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::identifier);
   }

   private static record Report(Map<ResourceKey<? extends Registry<?>>, Entry> registries, Map<String, CustomPackEntry> others) {
      public static final Codec<Report> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.unboundedMap(DatapackStructureReport.REGISTRY_KEY_CODEC, DatapackStructureReport.Entry.CODEC).fieldOf("registries").forGetter(Report::registries), Codec.unboundedMap(Codec.STRING, DatapackStructureReport.CustomPackEntry.CODEC).fieldOf("others").forGetter(Report::others)).apply(i, Report::new));

      private Report {
         super();
      }
   }

   private static record Entry(boolean elements, boolean tags, boolean stable) {
      public static final MapCodec<Entry> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.BOOL.fieldOf("elements").forGetter(Entry::elements), Codec.BOOL.fieldOf("tags").forGetter(Entry::tags), Codec.BOOL.fieldOf("stable").forGetter(Entry::stable)).apply(i, Entry::new));
      public static final Codec<Entry> CODEC;

      private Entry {
         super();
      }

      static {
         CODEC = MAP_CODEC.codec();
      }
   }

   private static enum Format implements StringRepresentable {
      STRUCTURE("structure"),
      MCFUNCTION("mcfunction");

      public static final Codec<Format> CODEC = StringRepresentable.<Format>fromEnum(Format::values);
      private final String name;

      private Format(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Format[] $values() {
         return new Format[]{STRUCTURE, MCFUNCTION};
      }
   }

   private static record CustomPackEntry(Format format, Entry entry) {
      public static final Codec<CustomPackEntry> CODEC = RecordCodecBuilder.create((i) -> i.group(DatapackStructureReport.Format.CODEC.fieldOf("format").forGetter(CustomPackEntry::format), DatapackStructureReport.Entry.MAP_CODEC.forGetter(CustomPackEntry::entry)).apply(i, CustomPackEntry::new));

      private CustomPackEntry {
         super();
      }
   }
}
