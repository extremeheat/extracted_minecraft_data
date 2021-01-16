package net.minecraft.server;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.Commands;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.tags.TagContainer;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Unit;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.PredicateManager;

public class ServerResources implements AutoCloseable {
   private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK;
   private final ReloadableResourceManager resources;
   private final Commands commands;
   private final RecipeManager recipes;
   private final TagManager tagManager;
   private final PredicateManager predicateManager;
   private final LootTables lootTables;
   private final ServerAdvancementManager advancements;
   private final ServerFunctionLibrary functionLibrary;

   public ServerResources(Commands.CommandSelection var1, int var2) {
      super();
      this.resources = new SimpleReloadableResourceManager(PackType.SERVER_DATA);
      this.recipes = new RecipeManager();
      this.tagManager = new TagManager();
      this.predicateManager = new PredicateManager();
      this.lootTables = new LootTables(this.predicateManager);
      this.advancements = new ServerAdvancementManager(this.predicateManager);
      this.commands = new Commands(var1);
      this.functionLibrary = new ServerFunctionLibrary(var2, this.commands.getDispatcher());
      this.resources.registerReloadListener(this.tagManager);
      this.resources.registerReloadListener(this.predicateManager);
      this.resources.registerReloadListener(this.recipes);
      this.resources.registerReloadListener(this.lootTables);
      this.resources.registerReloadListener(this.functionLibrary);
      this.resources.registerReloadListener(this.advancements);
   }

   public ServerFunctionLibrary getFunctionLibrary() {
      return this.functionLibrary;
   }

   public PredicateManager getPredicateManager() {
      return this.predicateManager;
   }

   public LootTables getLootTables() {
      return this.lootTables;
   }

   public TagContainer getTags() {
      return this.tagManager.getTags();
   }

   public RecipeManager getRecipeManager() {
      return this.recipes;
   }

   public Commands getCommands() {
      return this.commands;
   }

   public ServerAdvancementManager getAdvancements() {
      return this.advancements;
   }

   public ResourceManager getResourceManager() {
      return this.resources;
   }

   public static CompletableFuture<ServerResources> loadResources(List<PackResources> var0, Commands.CommandSelection var1, int var2, Executor var3, Executor var4) {
      ServerResources var5 = new ServerResources(var1, var2);
      CompletableFuture var6 = var5.resources.reload(var3, var4, var0, DATA_RELOAD_INITIAL_TASK);
      return var6.whenComplete((var1x, var2x) -> {
         if (var2x != null) {
            var5.close();
         }

      }).thenApply((var1x) -> {
         return var5;
      });
   }

   public void updateGlobals() {
      this.tagManager.getTags().bindToGlobal();
   }

   public void close() {
      this.resources.close();
   }

   static {
      DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
   }
}
