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
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public class DatapackStructureReport implements DataProvider {
   private final PackOutput output;
   private static final Entry PSEUDO_REGISTRY = new Entry(true, false, true);
   private static final Entry STABLE_DYNAMIC_REGISTRY = new Entry(true, true, true);
   private static final Entry UNSTABLE_DYNAMIC_REGISTRY = new Entry(true, true, false);
   private static final Entry BUILT_IN_REGISTRY = new Entry(false, true, true);
   private static final Map<ResourceKey<? extends Registry<?>>, Entry> MANUAL_ENTRIES;
   private static final Map<String, CustomPackEntry> NON_REGISTRY_ENTRIES;
   static final Codec<ResourceKey<? extends Registry<?>>> REGISTRY_KEY_CODEC;

   public DatapackStructureReport(PackOutput var1) {
      super();
      this.output = var1;
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      Report var2 = new Report(this.listRegistries(), NON_REGISTRY_ENTRIES);
      Path var3 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("datapack.json");
      return DataProvider.saveStable(var1, (JsonElement)DatapackStructureReport.Report.CODEC.encodeStart(JsonOps.INSTANCE, var2).getOrThrow(), var3);
   }

   public String getName() {
      return "Datapack Structure";
   }

   private void putIfNotPresent(Map<ResourceKey<? extends Registry<?>>, Entry> var1, ResourceKey<? extends Registry<?>> var2, Entry var3) {
      Entry var4 = (Entry)var1.putIfAbsent(var2, var3);
      if (var4 != null) {
         throw new IllegalStateException("Duplicate entry for key " + String.valueOf(var2.location()));
      }
   }

   private Map<ResourceKey<? extends Registry<?>>, Entry> listRegistries() {
      HashMap var1 = new HashMap();
      BuiltInRegistries.REGISTRY.forEach((var2) -> this.putIfNotPresent(var1, var2.key(), BUILT_IN_REGISTRY));
      RegistryDataLoader.WORLDGEN_REGISTRIES.forEach((var2) -> this.putIfNotPresent(var1, var2.key(), UNSTABLE_DYNAMIC_REGISTRY));
      RegistryDataLoader.DIMENSION_REGISTRIES.forEach((var2) -> this.putIfNotPresent(var1, var2.key(), UNSTABLE_DYNAMIC_REGISTRY));
      MANUAL_ENTRIES.forEach((var2, var3) -> this.putIfNotPresent(var1, var2, var3));
      return var1;
   }

   static {
      MANUAL_ENTRIES = Map.of(Registries.RECIPE, PSEUDO_REGISTRY, Registries.ADVANCEMENT, PSEUDO_REGISTRY, Registries.LOOT_TABLE, STABLE_DYNAMIC_REGISTRY, Registries.ITEM_MODIFIER, STABLE_DYNAMIC_REGISTRY, Registries.PREDICATE, STABLE_DYNAMIC_REGISTRY);
      NON_REGISTRY_ENTRIES = Map.of("structure", new CustomPackEntry(DatapackStructureReport.Format.STRUCTURE, new Entry(true, false, true)), "function", new CustomPackEntry(DatapackStructureReport.Format.MCFUNCTION, new Entry(true, true, true)));
      REGISTRY_KEY_CODEC = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
   }

   static record Report(Map<ResourceKey<? extends Registry<?>>, Entry> registries, Map<String, CustomPackEntry> others) {
      public static final Codec<Report> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.unboundedMap(DatapackStructureReport.REGISTRY_KEY_CODEC, DatapackStructureReport.Entry.CODEC).fieldOf("registries").forGetter(Report::registries), Codec.unboundedMap(Codec.STRING, DatapackStructureReport.CustomPackEntry.CODEC).fieldOf("others").forGetter(Report::others)).apply(var0, Report::new));

      Report(Map<ResourceKey<? extends Registry<?>>, Entry> var1, Map<String, CustomPackEntry> var2) {
         super();
         this.registries = var1;
         this.others = var2;
      }
   }

   static record Entry(boolean elements, boolean tags, boolean stable) {
      public static final MapCodec<Entry> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.BOOL.fieldOf("elements").forGetter(Entry::elements), Codec.BOOL.fieldOf("tags").forGetter(Entry::tags), Codec.BOOL.fieldOf("stable").forGetter(Entry::stable)).apply(var0, Entry::new));
      public static final Codec<Entry> CODEC;

      Entry(boolean var1, boolean var2, boolean var3) {
         super();
         this.elements = var1;
         this.tags = var2;
         this.stable = var3;
      }

      static {
         CODEC = MAP_CODEC.codec();
      }
   }

   static enum Format implements StringRepresentable {
      STRUCTURE("structure"),
      MCFUNCTION("mcfunction");

      public static final Codec<Format> CODEC = StringRepresentable.<Format>fromEnum(Format::values);
      private final String name;

      private Format(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Format[] $values() {
         return new Format[]{STRUCTURE, MCFUNCTION};
      }
   }

   static record CustomPackEntry(Format format, Entry entry) {
      public static final Codec<CustomPackEntry> CODEC = RecordCodecBuilder.create((var0) -> var0.group(DatapackStructureReport.Format.CODEC.fieldOf("format").forGetter(CustomPackEntry::format), DatapackStructureReport.Entry.MAP_CODEC.forGetter(CustomPackEntry::entry)).apply(var0, CustomPackEntry::new));

      CustomPackEntry(Format var1, Entry var2) {
         super();
         this.format = var1;
         this.entry = var2;
      }
   }
}
