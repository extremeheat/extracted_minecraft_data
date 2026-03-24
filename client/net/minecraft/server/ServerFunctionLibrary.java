package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.tags.TagLoader;
import org.slf4j.Logger;

public class ServerFunctionLibrary implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ResourceKey<Registry<CommandFunction<CommandSourceStack>>> TYPE_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("function"));
   private static final FileToIdConverter LISTER;
   private volatile Map<Identifier, CommandFunction<CommandSourceStack>> functions = ImmutableMap.of();
   private final TagLoader<CommandFunction<CommandSourceStack>> tagsLoader;
   private volatile Map<Identifier, List<CommandFunction<CommandSourceStack>>> tags;
   private final PermissionSet functionCompilationPermissions;
   private final CommandDispatcher<CommandSourceStack> dispatcher;

   public Optional<CommandFunction<CommandSourceStack>> getFunction(final Identifier id) {
      return Optional.ofNullable((CommandFunction)this.functions.get(id));
   }

   public Map<Identifier, CommandFunction<CommandSourceStack>> getFunctions() {
      return this.functions;
   }

   public List<CommandFunction<CommandSourceStack>> getTag(final Identifier tag) {
      return (List)this.tags.getOrDefault(tag, List.of());
   }

   public Iterable<Identifier> getAvailableTags() {
      return this.tags.keySet();
   }

   public ServerFunctionLibrary(final PermissionSet functionCompilationPermissions, final CommandDispatcher<CommandSourceStack> dispatcher) {
      super();
      this.tagsLoader = new TagLoader<CommandFunction<CommandSourceStack>>((id, required) -> this.getFunction(id), Registries.tagsDirPath(TYPE_KEY));
      this.tags = Map.of();
      this.functionCompilationPermissions = functionCompilationPermissions;
      this.dispatcher = dispatcher;
   }

   public CompletableFuture<Void> reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      ResourceManager manager = currentReload.resourceManager();
      CompletableFuture<Map<Identifier, List<TagLoader.EntryWithSource>>> tags = CompletableFuture.supplyAsync(() -> this.tagsLoader.load(manager), taskExecutor);
      CompletableFuture<Map<Identifier, CompletableFuture<CommandFunction<CommandSourceStack>>>> functions = CompletableFuture.supplyAsync(() -> LISTER.listMatchingResources(manager), taskExecutor).thenCompose((functionsToLoad) -> {
         Map<Identifier, CompletableFuture<CommandFunction<CommandSourceStack>>> result = Maps.newHashMap();
         CommandSourceStack compilationContext = Commands.createCompilationContext(this.functionCompilationPermissions);

         for(Map.Entry<Identifier, Resource> entry : functionsToLoad.entrySet()) {
            Identifier resourceId = (Identifier)entry.getKey();
            Identifier id = LISTER.fileToId(resourceId);
            result.put(id, CompletableFuture.supplyAsync(() -> {
               List<String> lines = readLines((Resource)entry.getValue());
               return CommandFunction.fromLines(id, this.dispatcher, compilationContext, lines);
            }, taskExecutor));
         }

         CompletableFuture<?>[] futuresToCollect = (CompletableFuture[])result.values().toArray(new CompletableFuture[0]);
         return CompletableFuture.allOf(futuresToCollect).handle((ignore, throwable) -> result);
      });
      CompletableFuture var10000 = tags.thenCombine(functions, Pair::of);
      Objects.requireNonNull(preparationBarrier);
      return var10000.thenCompose(preparationBarrier::wait).thenAcceptAsync((data) -> {
         Map<Identifier, CompletableFuture<CommandFunction<CommandSourceStack>>> functionFutures = (Map)data.getSecond();
         ImmutableMap.Builder<Identifier, CommandFunction<CommandSourceStack>> newFunctions = ImmutableMap.builder();
         functionFutures.forEach((id, functionFuture) -> functionFuture.handle((function, throwable) -> {
               if (throwable != null) {
                  LOGGER.error("Failed to load function {}", id, throwable);
               } else {
                  newFunctions.put(id, function);
               }

               return null;
            }).join());
         this.functions = newFunctions.build();
         this.tags = this.tagsLoader.build((Map)data.getFirst());
      }, reloadExecutor);
   }

   private static List<String> readLines(final Resource resource) {
      try {
         BufferedReader reader = resource.openAsReader();

         List var2;
         try {
            var2 = reader.lines().toList();
         } catch (Throwable var5) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (reader != null) {
            reader.close();
         }

         return var2;
      } catch (IOException ex) {
         throw new CompletionException(ex);
      }
   }

   static {
      LISTER = new FileToIdConverter(Registries.elementsDirPath(TYPE_KEY), ".mcfunction");
   }
}
