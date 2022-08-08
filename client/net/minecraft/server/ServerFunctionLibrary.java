package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerFunctionLibrary implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String FILE_EXTENSION = ".mcfunction";
   private static final int PATH_PREFIX_LENGTH = "functions/".length();
   private static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
   private volatile Map<ResourceLocation, CommandFunction> functions = ImmutableMap.of();
   private final TagLoader<CommandFunction> tagsLoader = new TagLoader(this::getFunction, "tags/functions");
   private volatile Map<ResourceLocation, Collection<CommandFunction>> tags = Map.of();
   private final int functionCompilationLevel;
   private final CommandDispatcher<CommandSourceStack> dispatcher;

   public Optional<CommandFunction> getFunction(ResourceLocation var1) {
      return Optional.ofNullable((CommandFunction)this.functions.get(var1));
   }

   public Map<ResourceLocation, CommandFunction> getFunctions() {
      return this.functions;
   }

   public Collection<CommandFunction> getTag(ResourceLocation var1) {
      return (Collection)this.tags.getOrDefault(var1, List.of());
   }

   public Iterable<ResourceLocation> getAvailableTags() {
      return this.tags.keySet();
   }

   public ServerFunctionLibrary(int var1, CommandDispatcher<CommandSourceStack> var2) {
      super();
      this.functionCompilationLevel = var1;
      this.dispatcher = var2;
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      CompletableFuture var7 = CompletableFuture.supplyAsync(() -> {
         return this.tagsLoader.load(var2);
      }, var5);
      CompletableFuture var8 = CompletableFuture.supplyAsync(() -> {
         return var2.listResources("functions", (var0) -> {
            return var0.getPath().endsWith(".mcfunction");
         });
      }, var5).thenCompose((var2x) -> {
         HashMap var3 = Maps.newHashMap();
         CommandSourceStack var4 = new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, (ServerLevel)null, this.functionCompilationLevel, "", CommonComponents.EMPTY, (MinecraftServer)null, (Entity)null);
         Iterator var5x = var2x.entrySet().iterator();

         while(var5x.hasNext()) {
            Map.Entry var6 = (Map.Entry)var5x.next();
            ResourceLocation var7 = (ResourceLocation)var6.getKey();
            String var8 = var7.getPath();
            ResourceLocation var9 = new ResourceLocation(var7.getNamespace(), var8.substring(PATH_PREFIX_LENGTH, var8.length() - PATH_SUFFIX_LENGTH));
            var3.put(var9, CompletableFuture.supplyAsync(() -> {
               List var4x = readLines((Resource)var6.getValue());
               return CommandFunction.fromLines(var9, this.dispatcher, var4, var4x);
            }, var5));
         }

         CompletableFuture[] var10 = (CompletableFuture[])var3.values().toArray(new CompletableFuture[0]);
         return CompletableFuture.allOf(var10).handle((var1, var2) -> {
            return var3;
         });
      });
      CompletableFuture var10000 = var7.thenCombine(var8, Pair::of);
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var1x) -> {
         Map var2 = (Map)var1x.getSecond();
         ImmutableMap.Builder var3 = ImmutableMap.builder();
         var2.forEach((var1, var2x) -> {
            var2x.handle((var2, var3x) -> {
               if (var3x != null) {
                  LOGGER.error("Failed to load function {}", var1, var3x);
               } else {
                  var3.put(var1, var2);
               }

               return null;
            }).join();
         });
         this.functions = var3.build();
         this.tags = this.tagsLoader.build((Map)var1x.getFirst());
      }, var6);
   }

   private static List<String> readLines(Resource var0) {
      try {
         BufferedReader var1 = var0.openAsReader();

         List var2;
         try {
            var2 = var1.lines().toList();
         } catch (Throwable var5) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (var1 != null) {
            var1.close();
         }

         return var2;
      } catch (IOException var6) {
         throw new CompletionException(var6);
      }
   }
}
