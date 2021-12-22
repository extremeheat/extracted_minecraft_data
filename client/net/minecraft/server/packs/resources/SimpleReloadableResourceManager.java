package net.minecraft.server.packs.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class SimpleReloadableResourceManager implements ReloadableResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, FallbackResourceManager> namespacedPacks = Maps.newHashMap();
   private final List<PreparableReloadListener> listeners = Lists.newArrayList();
   private final Set<String> namespaces = Sets.newLinkedHashSet();
   private final List<PackResources> packs = Lists.newArrayList();
   private final PackType type;

   public SimpleReloadableResourceManager(PackType var1) {
      super();
      this.type = var1;
   }

   public void add(PackResources var1) {
      this.packs.add(var1);

      FallbackResourceManager var4;
      for(Iterator var2 = var1.getNamespaces(this.type).iterator(); var2.hasNext(); var4.add(var1)) {
         String var3 = (String)var2.next();
         this.namespaces.add(var3);
         var4 = (FallbackResourceManager)this.namespacedPacks.get(var3);
         if (var4 == null) {
            var4 = new FallbackResourceManager(this.type, var3);
            this.namespacedPacks.put(var3, var4);
         }
      }

   }

   public Set<String> getNamespaces() {
      return this.namespaces;
   }

   public Resource getResource(ResourceLocation var1) throws IOException {
      ResourceManager var2 = (ResourceManager)this.namespacedPacks.get(var1.getNamespace());
      if (var2 != null) {
         return var2.getResource(var1);
      } else {
         throw new FileNotFoundException(var1.toString());
      }
   }

   public boolean hasResource(ResourceLocation var1) {
      ResourceManager var2 = (ResourceManager)this.namespacedPacks.get(var1.getNamespace());
      return var2 != null ? var2.hasResource(var1) : false;
   }

   public List<Resource> getResources(ResourceLocation var1) throws IOException {
      ResourceManager var2 = (ResourceManager)this.namespacedPacks.get(var1.getNamespace());
      if (var2 != null) {
         return var2.getResources(var1);
      } else {
         throw new FileNotFoundException(var1.toString());
      }
   }

   public Collection<ResourceLocation> listResources(String var1, Predicate<String> var2) {
      HashSet var3 = Sets.newHashSet();
      Iterator var4 = this.namespacedPacks.values().iterator();

      while(var4.hasNext()) {
         FallbackResourceManager var5 = (FallbackResourceManager)var4.next();
         var3.addAll(var5.listResources(var1, var2));
      }

      ArrayList var6 = Lists.newArrayList(var3);
      Collections.sort(var6);
      return var6;
   }

   private void clear() {
      this.namespacedPacks.clear();
      this.namespaces.clear();
      this.packs.forEach(PackResources::close);
      this.packs.clear();
   }

   public void close() {
      this.clear();
   }

   public void registerReloadListener(PreparableReloadListener var1) {
      this.listeners.add(var1);
   }

   public ReloadInstance createReload(Executor var1, Executor var2, CompletableFuture<Unit> var3, List<PackResources> var4) {
      LOGGER.info("Reloading ResourceManager: {}", new Supplier[]{() -> {
         return var4.stream().map(PackResources::getName).collect(Collectors.joining(", "));
      }});
      this.clear();
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         PackResources var6 = (PackResources)var5.next();

         try {
            this.add(var6);
         } catch (Exception var8) {
            LOGGER.error("Failed to add resource pack {}", var6.getName(), var8);
            return new SimpleReloadableResourceManager.FailingReloadInstance(new SimpleReloadableResourceManager.ResourcePackLoadingFailure(var6, var8));
         }
      }

      return (ReloadInstance)(LOGGER.isDebugEnabled() ? new ProfiledReloadInstance(this, Lists.newArrayList(this.listeners), var1, var2, var3) : SimpleReloadInstance.method_96(this, Lists.newArrayList(this.listeners), var1, var2, var3));
   }

   public Stream<PackResources> listPacks() {
      return this.packs.stream();
   }

   static class FailingReloadInstance implements ReloadInstance {
      private final SimpleReloadableResourceManager.ResourcePackLoadingFailure exception;
      private final CompletableFuture<Unit> failedFuture;

      public FailingReloadInstance(SimpleReloadableResourceManager.ResourcePackLoadingFailure var1) {
         super();
         this.exception = var1;
         this.failedFuture = new CompletableFuture();
         this.failedFuture.completeExceptionally(var1);
      }

      public CompletableFuture<Unit> done() {
         return this.failedFuture;
      }

      public float getActualProgress() {
         return 0.0F;
      }

      public boolean isDone() {
         return true;
      }

      public void checkExceptions() {
         throw this.exception;
      }
   }

   public static class ResourcePackLoadingFailure extends RuntimeException {
      private final PackResources pack;

      public ResourcePackLoadingFailure(PackResources var1, Throwable var2) {
         super(var1.getName(), var2);
         this.pack = var1;
      }

      public PackResources getPack() {
         return this.pack;
      }
   }
}
