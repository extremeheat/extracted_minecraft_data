package net.minecraft.tags;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class TagManager implements PreparableReloadListener {
   private final RegistryAccess registryAccess;
   private List<TagManager.LoadResult<?>> results = List.of();

   public TagManager(RegistryAccess var1) {
      super();
      this.registryAccess = var1;
   }

   public List<TagManager.LoadResult<?>> getResult() {
      return this.results;
   }

   @Override
   public CompletableFuture<Void> reload(
      PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6
   ) {
      List var7 = this.registryAccess.registries().map(var3x -> this.createLoader(var2, var5, (RegistryAccess.RegistryEntry<?>)var3x)).toList();
      return CompletableFuture.allOf(var7.toArray(CompletableFuture[]::new))
         .thenCompose(var1::wait)
         .thenAcceptAsync(var2x -> this.results = var7.stream().map(CompletableFuture::join).collect(Collectors.toUnmodifiableList()), var6);
   }

   private <T> CompletableFuture<TagManager.LoadResult<T>> createLoader(ResourceManager var1, Executor var2, RegistryAccess.RegistryEntry<T> var3) {
      ResourceKey var4 = var3.key();
      Registry var5 = var3.value();
      TagLoader var6 = new TagLoader<>(var5::getHolder, Registries.tagsDirPath(var4));
      return CompletableFuture.supplyAsync(() -> new TagManager.LoadResult<>(var4, var6.loadAndBuild(var1)), var2);
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
