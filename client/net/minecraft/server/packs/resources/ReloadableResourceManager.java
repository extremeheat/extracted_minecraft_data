package net.minecraft.server.packs.resources;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Unit;
import org.slf4j.Logger;

public class ReloadableResourceManager implements ResourceManager, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private CloseableResourceManager resources;
   private final List<PreparableReloadListener> listeners = Lists.newArrayList();
   private final PackType type;

   public ReloadableResourceManager(PackType var1) {
      super();
      this.type = var1;
      this.resources = new MultiPackResourceManager(var1, List.of());
   }

   public void close() {
      this.resources.close();
   }

   public void registerReloadListener(PreparableReloadListener var1) {
      this.listeners.add(var1);
   }

   public ReloadInstance createReload(Executor var1, Executor var2, CompletableFuture<Unit> var3, List<PackResources> var4) {
      LOGGER.info("Reloading ResourceManager: {}", LogUtils.defer(() -> {
         return var4.stream().map(PackResources::packId).collect(Collectors.joining(", "));
      }));
      this.resources.close();
      this.resources = new MultiPackResourceManager(this.type, var4);
      return SimpleReloadInstance.create(this.resources, this.listeners, var1, var2, var3, LOGGER.isDebugEnabled());
   }

   public Optional<Resource> getResource(ResourceLocation var1) {
      return this.resources.getResource(var1);
   }

   public Set<String> getNamespaces() {
      return this.resources.getNamespaces();
   }

   public List<Resource> getResourceStack(ResourceLocation var1) {
      return this.resources.getResourceStack(var1);
   }

   public Map<ResourceLocation, Resource> listResources(String var1, Predicate<ResourceLocation> var2) {
      return this.resources.listResources(var1, var2);
   }

   public Map<ResourceLocation, List<Resource>> listResourceStacks(String var1, Predicate<ResourceLocation> var2) {
      return this.resources.listResourceStacks(var1, var2);
   }

   public Stream<PackResources> listPacks() {
      return this.resources.listPacks();
   }
}
