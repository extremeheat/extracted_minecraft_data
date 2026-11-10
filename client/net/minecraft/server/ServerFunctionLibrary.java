package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParseBox;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.arguments.ValidationWrapper;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.CommandParser;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlagSet;
import org.slf4j.Logger;

public class ServerFunctionLibrary implements PreparableReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ResourceKey<Registry<CommandFunction<CommandSourceStack>>> TYPE_KEY = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("function"));
   private static final FileToIdConverter LISTER;
   private volatile Map<Identifier, CommandFunction<CommandSourceStack>> functions = ImmutableMap.of();
   private final TagLoader<CommandFunction<CommandSourceStack>> tagsLoader;
   private volatile Map<Identifier, List<CommandFunction<CommandSourceStack>>> tags;
   private final CommandParser.Provider<CommandSourceStack> parserProvider;

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

   public ServerFunctionLibrary(final PermissionSet functionCompilationPermissions, final HolderLookup.Provider registries, final FeatureFlagSet enabledFeatures, final ParseBox commandParseBox) {
      super();
      this.tagsLoader = new TagLoader<CommandFunction<CommandSourceStack>>((id, var2) -> this.getFunction(id), Registries.tagsDirPath(TYPE_KEY));
      this.tags = Map.of();
      HolderLookup.Provider filteredRegistries = registries.filterFeatures(enabledFeatures);
      this.parserProvider = (id) -> {
         final ValidationWrapper.Provider.Collecting collectingProvider = new ValidationWrapper.Provider.Collecting();
         final ParseContext compilationContext = new ParseContext.Simple(functionCompilationPermissions, filteredRegistries, enabledFeatures, collectingProvider);
         return new CommandParser<CommandSourceStack>() {
            {
               Objects.requireNonNull(ServerFunctionLibrary.this);
            }

            public ContextChain<CommandSourceStack> parse(final StringReader input) throws CommandSyntaxException {
               return commandParseBox.parse(input, compilationContext).unpack();
            }

            public void close() {
               ProblemReporter.Collector problems = new ProblemReporter.Collector();
               collectingProvider.validateAll(problems);
               if (problems.hasAnyProblems()) {
                  ServerFunctionLibrary.LOGGER.warn("Found validation issues in '{}': {}", id, problems.getTreeReport());
                  if (problems.hasErrors()) {
                     String var10002 = String.valueOf(id);
                     throw new IllegalStateException("Failed to validate function '" + var10002 + "': " + problems.getReport());
                  }
               }

            }
         };
      };
   }

   public CompletableFuture<Void> reload(final PreparableReloadListener.SharedState currentReload, final Executor taskExecutor, final PreparableReloadListener.PreparationBarrier preparationBarrier, final Executor reloadExecutor) {
      ResourceManager manager = currentReload.resourceManager();
      CompletableFuture<Map<Identifier, List<TagLoader.EntryWithSource>>> tags = CompletableFuture.supplyAsync(() -> this.tagsLoader.load(manager), taskExecutor);
      CompletableFuture<Map<Identifier, CompletableFuture<CommandFunction<CommandSourceStack>>>> functions = CompletableFuture.supplyAsync(() -> LISTER.listMatchingResources(manager), taskExecutor).thenCompose((functionsToLoad) -> {
         Map<Identifier, CompletableFuture<CommandFunction<CommandSourceStack>>> result = new HashMap(functionsToLoad.size());

         for(Map.Entry<Identifier, Resource> entry : functionsToLoad.entrySet()) {
            Identifier resourceId = (Identifier)entry.getKey();
            Identifier id = LISTER.fileToId(resourceId);
            result.put(id, CompletableFuture.supplyAsync(() -> {
               List<String> lines = readLines((Resource)entry.getValue());
               return CommandFunction.fromLines(id, this.parserProvider, lines);
            }, taskExecutor));
         }

         CompletableFuture<?>[] futuresToCollect = (CompletableFuture[])result.values().toArray(new CompletableFuture[0]);
         return CompletableFuture.allOf(futuresToCollect).handle((ignore, var2) -> result);
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
