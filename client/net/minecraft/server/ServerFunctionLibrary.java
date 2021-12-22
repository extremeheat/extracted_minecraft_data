package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerFunctionLibrary implements PreparableReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final String FILE_EXTENSION = ".mcfunction";
   private static final int PATH_PREFIX_LENGTH = "functions/".length();
   private static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
   private volatile Map<ResourceLocation, CommandFunction> functions = ImmutableMap.of();
   private final TagLoader<CommandFunction> tagsLoader = new TagLoader(this::getFunction, "tags/functions");
   private volatile TagCollection<CommandFunction> tags = TagCollection.empty();
   private final int functionCompilationLevel;
   private final CommandDispatcher<CommandSourceStack> dispatcher;

   public Optional<CommandFunction> getFunction(ResourceLocation var1) {
      return Optional.ofNullable((CommandFunction)this.functions.get(var1));
   }

   public Map<ResourceLocation, CommandFunction> getFunctions() {
      return this.functions;
   }

   public TagCollection<CommandFunction> getTags() {
      return this.tags;
   }

   public Tag<CommandFunction> getTag(ResourceLocation var1) {
      return this.tags.getTagOrEmpty(var1);
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
            return var0.endsWith(".mcfunction");
         });
      }, var5).thenCompose((var3x) -> {
         HashMap var4 = Maps.newHashMap();
         CommandSourceStack var5x = new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, (ServerLevel)null, this.functionCompilationLevel, "", TextComponent.EMPTY, (MinecraftServer)null, (Entity)null);
         Iterator var6 = var3x.iterator();

         while(var6.hasNext()) {
            ResourceLocation var7 = (ResourceLocation)var6.next();
            String var8 = var7.getPath();
            ResourceLocation var9 = new ResourceLocation(var7.getNamespace(), var8.substring(PATH_PREFIX_LENGTH, var8.length() - PATH_SUFFIX_LENGTH));
            var4.put(var9, CompletableFuture.supplyAsync(() -> {
               List var5 = readLines(var2, var7);
               return CommandFunction.fromLines(var9, this.dispatcher, var5x, var5);
            }, var5));
         }

         CompletableFuture[] var10 = (CompletableFuture[])var4.values().toArray(new CompletableFuture[0]);
         return CompletableFuture.allOf(var10).handle((var1, var2x) -> {
            return var4;
         });
      });
      CompletableFuture var10000 = var7.thenCombine(var8, Pair::of);
      Objects.requireNonNull(var1);
      return var10000.thenCompose(var1::wait).thenAcceptAsync((var1x) -> {
         Map var2 = (Map)var1x.getSecond();
         Builder var3 = ImmutableMap.builder();
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

   private static List<String> readLines(ResourceManager var0, ResourceLocation var1) {
      try {
         Resource var2 = var0.getResource(var1);

         List var3;
         try {
            var3 = IOUtils.readLines(var2.getInputStream(), StandardCharsets.UTF_8);
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }

         return var3;
      } catch (IOException var7) {
         throw new CompletionException(var7);
      }
   }
}
