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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.Pack;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements ReloadableResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map namespacedPacks = Maps.newHashMap();
   private final List listeners = Lists.newArrayList();
   private final List recentlyRegistered = Lists.newArrayList();
   private final Set namespaces = Sets.newLinkedHashSet();
   private final PackType type;
   private final Thread mainThread;

   public SimpleReloadableResourceManager(PackType var1, Thread var2) {
      this.type = var1;
      this.mainThread = var2;
   }

   public void add(Pack var1) {
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

   public Set getNamespaces() {
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

   public List getResources(ResourceLocation var1) throws IOException {
      ResourceManager var2 = (ResourceManager)this.namespacedPacks.get(var1.getNamespace());
      if (var2 != null) {
         return var2.getResources(var1);
      } else {
         throw new FileNotFoundException(var1.toString());
      }
   }

   public Collection listResources(String var1, Predicate var2) {
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
   }

   public CompletableFuture reload(Executor var1, Executor var2, List var3, CompletableFuture var4) {
      ReloadInstance var5 = this.createFullReload(var1, var2, var4, var3);
      return var5.done();
   }

   public void registerReloadListener(PreparableReloadListener var1) {
      this.listeners.add(var1);
      this.recentlyRegistered.add(var1);
   }

   protected ReloadInstance createReload(Executor var1, Executor var2, List var3, CompletableFuture var4) {
      Object var5;
      if (LOGGER.isDebugEnabled()) {
         var5 = new ProfiledReloadInstance(this, Lists.newArrayList(var3), var1, var2, var4);
      } else {
         var5 = SimpleReloadInstance.of(this, Lists.newArrayList(var3), var1, var2, var4);
      }

      this.recentlyRegistered.clear();
      return (ReloadInstance)var5;
   }

   public ReloadInstance createFullReload(Executor var1, Executor var2, CompletableFuture var3, List var4) {
      this.clear();
      LOGGER.info("Reloading ResourceManager: {}", var4.stream().map(Pack::getName).collect(Collectors.joining(", ")));
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         Pack var6 = (Pack)var5.next();

         try {
            this.add(var6);
         } catch (Exception var8) {
            LOGGER.error("Failed to add resource pack {}", var6.getName(), var8);
            return new SimpleReloadableResourceManager.FailingReloadInstance(new SimpleReloadableResourceManager.ResourcePackLoadingFailure(var6, var8));
         }
      }

      return this.createReload(var1, var2, this.listeners, var3);
   }

   static class FailingReloadInstance implements ReloadInstance {
      private final SimpleReloadableResourceManager.ResourcePackLoadingFailure exception;
      private final CompletableFuture failedFuture;

      public FailingReloadInstance(SimpleReloadableResourceManager.ResourcePackLoadingFailure var1) {
         this.exception = var1;
         this.failedFuture = new CompletableFuture();
         this.failedFuture.completeExceptionally(var1);
      }

      public CompletableFuture done() {
         return this.failedFuture;
      }

      public float getActualProgress() {
         return 0.0F;
      }

      public boolean isApplying() {
         return false;
      }

      public boolean isDone() {
         return true;
      }

      public void checkExceptions() {
         throw this.exception;
      }
   }

   public static class ResourcePackLoadingFailure extends RuntimeException {
      private final Pack pack;

      public ResourcePackLoadingFailure(Pack var1, Throwable var2) {
         super(var1.getName(), var2);
         this.pack = var1;
      }

      public Pack getPack() {
         return this.pack;
      }
   }
}
