package net.minecraft.tags;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagManager implements PreparableReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RegistryAccess registryAccess;
   private TagContainer tags;

   public TagManager(RegistryAccess var1) {
      super();
      this.tags = TagContainer.EMPTY;
      this.registryAccess = var1;
   }

   public TagContainer getTags() {
      return this.tags;
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      ArrayList var7 = Lists.newArrayList();
      StaticTags.visitHelpers((var4x) -> {
         TagManager.LoaderInfo var5x = this.createLoader(var2, var5, var4x);
         if (var5x != null) {
            var7.add(var5x);
         }

      });
      CompletableFuture var10000 = CompletableFuture.allOf((CompletableFuture[])var7.stream().map((var0) -> {
         return var0.pendingLoad;
      }).toArray((var0) -> {
         return new CompletableFuture[var0];
      }));
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var2x) -> {
         TagContainer.Builder var3 = new TagContainer.Builder();
         var7.forEach((var1) -> {
            var1.addToBuilder(var3);
         });
         TagContainer var4 = var3.build();
         Multimap var5 = StaticTags.getAllMissingTags(var4);
         if (!var5.isEmpty()) {
            Stream var10002 = var5.entries().stream().map((var0) -> {
               Object var10000 = var0.getKey();
               return var10000 + ":" + var0.getValue();
            }).sorted();
            throw new IllegalStateException("Missing required tags: " + (String)var10002.collect(Collectors.joining(",")));
         } else {
            SerializationTags.bind(var4);
            this.tags = var4;
         }
      }, var6);
   }

   @Nullable
   private <T> TagManager.LoaderInfo<T> createLoader(ResourceManager var1, Executor var2, StaticTagHelper<T> var3) {
      Optional var4 = this.registryAccess.registry(var3.getKey());
      if (var4.isPresent()) {
         Registry var5 = (Registry)var4.get();
         Objects.requireNonNull(var5);
         TagLoader var6 = new TagLoader(var5::getOptional, var3.getDirectory());
         CompletableFuture var7 = CompletableFuture.supplyAsync(() -> {
            return var6.loadAndBuild(var1);
         }, var2);
         return new TagManager.LoaderInfo(var3, var7);
      } else {
         LOGGER.warn("Can't find registry for {}", var3.getKey());
         return null;
      }
   }

   static class LoaderInfo<T> {
      private final StaticTagHelper<T> helper;
      final CompletableFuture<? extends TagCollection<T>> pendingLoad;

      LoaderInfo(StaticTagHelper<T> var1, CompletableFuture<? extends TagCollection<T>> var2) {
         super();
         this.helper = var1;
         this.pendingLoad = var2;
      }

      public void addToBuilder(TagContainer.Builder var1) {
         var1.add(this.helper.getKey(), (TagCollection)this.pendingLoad.join());
      }
   }
}
