package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
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
   private static final DatapackStructureReport.Entry PSEUDO_REGISTRY = new DatapackStructureReport.Entry(true, false, true);
   private static final DatapackStructureReport.Entry STABLE_DYNAMIC_REGISTRY = new DatapackStructureReport.Entry(true, true, true);
   private static final DatapackStructureReport.Entry UNSTABLE_DYNAMIC_REGISTRY = new DatapackStructureReport.Entry(true, true, false);
   private static final DatapackStructureReport.Entry BUILT_IN_REGISTRY = new DatapackStructureReport.Entry(false, true, true);
   private static final Map<ResourceKey<? extends Registry<?>>, DatapackStructureReport.Entry> MANUAL_ENTRIES = Map.of(
      Registries.RECIPE,
      PSEUDO_REGISTRY,
      Registries.ADVANCEMENT,
      PSEUDO_REGISTRY,
      Registries.LOOT_TABLE,
      STABLE_DYNAMIC_REGISTRY,
      Registries.ITEM_MODIFIER,
      STABLE_DYNAMIC_REGISTRY,
      Registries.PREDICATE,
      STABLE_DYNAMIC_REGISTRY
   );
   private static final Map<String, DatapackStructureReport.CustomPackEntry> NON_REGISTRY_ENTRIES = Map.of(
      "structure",
      new DatapackStructureReport.CustomPackEntry(DatapackStructureReport.Format.STRUCTURE, new DatapackStructureReport.Entry(true, false, true)),
      "function",
      new DatapackStructureReport.CustomPackEntry(DatapackStructureReport.Format.MCFUNCTION, new DatapackStructureReport.Entry(true, true, true))
   );
   static final Codec<ResourceKey<? extends Registry<?>>> REGISTRY_KEY_CODEC = ResourceLocation.CODEC
      .xmap(ResourceKey::createRegistryKey, ResourceKey::location);

   public DatapackStructureReport(PackOutput var1) {
      super();
      this.output = var1;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      DatapackStructureReport.Report var2 = new DatapackStructureReport.Report(this.listRegistries(), NON_REGISTRY_ENTRIES);
      Path var3 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("datapack.json");
      return DataProvider.saveStable(var1, (JsonElement)DatapackStructureReport.Report.CODEC.encodeStart(JsonOps.INSTANCE, var2).getOrThrow(), var3);
   }

   @Override
   public String getName() {
      return "Datapack Structure";
   }

   private void putIfNotPresent(
      Map<ResourceKey<? extends Registry<?>>, DatapackStructureReport.Entry> var1, ResourceKey<? extends Registry<?>> var2, DatapackStructureReport.Entry var3
   ) {
      DatapackStructureReport.Entry var4 = var1.putIfAbsent(var2, var3);
      if (var4 != null) {
         throw new IllegalStateException("Duplicate entry for key " + var2.location());
      }
   }

   private Map<ResourceKey<? extends Registry<?>>, DatapackStructureReport.Entry> listRegistries() {
      HashMap var1 = new HashMap();
      BuiltInRegistries.REGISTRY.forEach(var2 -> this.putIfNotPresent(var1, var2.key(), BUILT_IN_REGISTRY));
      RegistryDataLoader.WORLDGEN_REGISTRIES.forEach(var2 -> this.putIfNotPresent(var1, var2.key(), UNSTABLE_DYNAMIC_REGISTRY));
      RegistryDataLoader.DIMENSION_REGISTRIES.forEach(var2 -> this.putIfNotPresent(var1, var2.key(), UNSTABLE_DYNAMIC_REGISTRY));
      MANUAL_ENTRIES.forEach((var2, var3) -> this.putIfNotPresent(var1, (ResourceKey<? extends Registry<?>>)var2, var3));
      return var1;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   static enum Format implements StringRepresentable {
      STRUCTURE("structure"),
      MCFUNCTION("mcfunction");

      public static final Codec<DatapackStructureReport.Format> CODEC = StringRepresentable.fromEnum(DatapackStructureReport.Format::values);
      private final String name;

      private Format(final String nullxx) {
         this.name = nullxx;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
