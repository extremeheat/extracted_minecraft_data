package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerFunctionLibrary implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ResourceKey<Registry<CommandFunction<CommandSourceStack>>> TYPE_KEY = ResourceKey.createRegistryKey(
      ResourceLocation.withDefaultNamespace("function")
   );
   private static final FileToIdConverter LISTER = new FileToIdConverter(Registries.elementsDirPath(TYPE_KEY), ".mcfunction");
   private volatile Map<ResourceLocation, CommandFunction<CommandSourceStack>> functions = ImmutableMap.of();
   private final TagLoader<CommandFunction<CommandSourceStack>> tagsLoader = new TagLoader<>(
      (var1x, var2x) -> this.getFunction(var1x), Registries.tagsDirPath(TYPE_KEY)
   );
   private volatile Map<ResourceLocation, List<CommandFunction<CommandSourceStack>>> tags = Map.of();
   private final int functionCompilationLevel;
   private final CommandDispatcher<CommandSourceStack> dispatcher;

   public Optional<CommandFunction<CommandSourceStack>> getFunction(ResourceLocation var1) {
      return Optional.ofNullable(this.functions.get(var1));
   }

   public Map<ResourceLocation, CommandFunction<CommandSourceStack>> getFunctions() {
      return this.functions;
   }

   public List<CommandFunction<CommandSourceStack>> getTag(ResourceLocation var1) {
      return this.tags.getOrDefault(var1, List.of());
   }

   public Iterable<ResourceLocation> getAvailableTags() {
      return this.tags.keySet();
   }

   public ServerFunctionLibrary(int var1, CommandDispatcher<CommandSourceStack> var2) {
      super();
      this.functionCompilationLevel = var1;
      this.dispatcher = var2;
   }

   @Override
   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4) {
      CompletableFuture var5 = CompletableFuture.supplyAsync(() -> this.tagsLoader.load(var2), var3);
      CompletableFuture var6 = CompletableFuture.<Map<ResourceLocation, Resource>>supplyAsync(() -> LISTER.listMatchingResources(var2), var3)
         .thenCompose(
            var2x -> {
               HashMap var3x = Maps.newHashMap();
               CommandSourceStack var4x = new CommandSourceStack(
                  CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, null, this.functionCompilationLevel, "", CommonComponents.EMPTY, null, null
               );

               for (Entry var6x : var2x.entrySet()) {
                  ResourceLocation var7 = (ResourceLocation)var6x.getKey();
                  ResourceLocation var8 = LISTER.fileToId(var7);
                  var3x.put(var8, CompletableFuture.supplyAsync(() -> {
                     List var4xx = readLines((Resource)var6x.getValue());
                     return CommandFunction.fromLines(var8, this.dispatcher, var4x, var4xx);
                  }, var3));
               }

               CompletableFuture[] var9 = var3x.values().toArray(new CompletableFuture[0]);
               return CompletableFuture.allOf(var9).handle((var1xx, var2xx) -> var3x);
            }
         );
      return var5.thenCombine(var6, Pair::of).thenCompose(var1::wait).thenAcceptAsync(var1x -> {
         Map var2x = (Map)var1x.getSecond();
         Builder var3x = ImmutableMap.builder();
         var2x.forEach((var1xx, var2xx) -> var2xx.handle((var2xxx, var3xx) -> {
               if (var3xx != null) {
                  LOGGER.error("Failed to load function {}", var1xx, var3xx);
               } else {
                  var3x.put(var1xx, var2xxx);
               }

               return null;
            }).join());
         this.functions = var3x.build();
         this.tags = this.tagsLoader.build((Map<ResourceLocation, List<TagLoader.EntryWithSource>>)var1x.getFirst());
      }, var4);
   }

   private static List<String> readLines(Resource var0) {
      try {
         List var2;
         try (BufferedReader var1 = var0.openAsReader()) {
            var2 = var1.lines().toList();
         }

         return var2;
      } catch (IOException var6) {
         throw new CompletionException(var6);
      }
   }
}
