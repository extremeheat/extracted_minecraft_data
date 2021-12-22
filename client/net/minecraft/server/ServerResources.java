package net.minecraft.server;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.tags.TagContainer;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Unit;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
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
   private final ItemModifierManager itemModifierManager;
   private final ServerAdvancementManager advancements;
   private final ServerFunctionLibrary functionLibrary;

   public ServerResources(RegistryAccess var1, Commands.CommandSelection var2, int var3) {
      super();
      this.resources = new SimpleReloadableResourceManager(PackType.SERVER_DATA);
      this.recipes = new RecipeManager();
      this.predicateManager = new PredicateManager();
      this.lootTables = new LootTables(this.predicateManager);
      this.itemModifierManager = new ItemModifierManager(this.predicateManager, this.lootTables);
      this.advancements = new ServerAdvancementManager(this.predicateManager);
      this.tagManager = new TagManager(var1);
      this.commands = new Commands(var2);
      this.functionLibrary = new ServerFunctionLibrary(var3, this.commands.getDispatcher());
      this.resources.registerReloadListener(this.tagManager);
      this.resources.registerReloadListener(this.predicateManager);
      this.resources.registerReloadListener(this.recipes);
      this.resources.registerReloadListener(this.lootTables);
      this.resources.registerReloadListener(this.itemModifierManager);
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

   public ItemModifierManager getItemModifierManager() {
      return this.itemModifierManager;
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

   public static CompletableFuture<ServerResources> loadResources(List<PackResources> var0, RegistryAccess var1, Commands.CommandSelection var2, int var3, Executor var4, Executor var5) {
      ServerResources var6 = new ServerResources(var1, var2, var3);
      CompletableFuture var7 = var6.resources.reload(var4, var5, var0, DATA_RELOAD_INITIAL_TASK);
      return var7.whenComplete((var1x, var2x) -> {
         if (var2x != null) {
            var6.close();
         }

      }).thenApply((var1x) -> {
         return var6;
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
