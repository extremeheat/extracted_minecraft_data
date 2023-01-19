package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerFunctionLibrary implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String FILE_EXTENSION = ".mcfunction";
   private static final int PATH_PREFIX_LENGTH = "functions/".length();
   private static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
   private volatile Map<ResourceLocation, CommandFunction> functions = ImmutableMap.of();
   private final TagLoader<CommandFunction> tagsLoader = new TagLoader<>(this::getFunction, "tags/functions");
   private volatile Map<ResourceLocation, Collection<CommandFunction>> tags = Map.of();
   private final int functionCompilationLevel;
   private final CommandDispatcher<CommandSourceStack> dispatcher;

   public Optional<CommandFunction> getFunction(ResourceLocation var1) {
      return Optional.ofNullable(this.functions.get(var1));
   }

   public Map<ResourceLocation, CommandFunction> getFunctions() {
      return this.functions;
   }

   public Collection<CommandFunction> getTag(ResourceLocation var1) {
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
   public CompletableFuture<Void> reload(
      PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6
   ) {
      CompletableFuture var7 = CompletableFuture.supplyAsync(() -> this.tagsLoader.load(var2), var5);
      CompletableFuture var8 = CompletableFuture.<Map<ResourceLocation, Resource>>supplyAsync(
            () -> var2.listResources("functions", var0x -> var0x.getPath().endsWith(".mcfunction")), var5
         )
         .thenCompose(
            var2x -> {
               HashMap var3x = Maps.newHashMap();
               CommandSourceStack var4x = new CommandSourceStack(
                  CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, null, this.functionCompilationLevel, "", CommonComponents.EMPTY, null, null
               );
      
               for(Entry var6x : var2x.entrySet()) {
                  ResourceLocation var7x = (ResourceLocation)var6x.getKey();
                  String var8x = var7x.getPath();
                  ResourceLocation var9 = new ResourceLocation(var7x.getNamespace(), var8x.substring(PATH_PREFIX_LENGTH, var8x.length() - PATH_SUFFIX_LENGTH));
                  var3x.put(var9, CompletableFuture.supplyAsync(() -> {
                     List var4xx = readLines((Resource)var6x.getValue());
                     return CommandFunction.fromLines(var9, this.dispatcher, var4x, var4xx);
                  }, var5));
               }
      
               CompletableFuture[] var10 = var3x.values().toArray(new CompletableFuture[0]);
               return CompletableFuture.allOf(var10).handle((var1xx, var2xx) -> var3x);
            }
         );
      return var7.thenCombine(var8, Pair::of).thenCompose(var1::wait).thenAcceptAsync(var1x -> {
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
      }, var6);
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