package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Unit;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.PredicateManager;
import org.slf4j.Logger;

public class ReloadableServerResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
   private final CommandBuildContext commandBuildContext;
   private final Commands commands;
   private final RecipeManager recipes = new RecipeManager();
   private final TagManager tagManager;
   private final PredicateManager predicateManager = new PredicateManager();
   private final LootTables lootTables = new LootTables(this.predicateManager);
   private final ItemModifierManager itemModifierManager = new ItemModifierManager(this.predicateManager, this.lootTables);
   private final ServerAdvancementManager advancements = new ServerAdvancementManager(this.predicateManager);
   private final ServerFunctionLibrary functionLibrary;

   public ReloadableServerResources(RegistryAccess.Frozen var1, Commands.CommandSelection var2, int var3) {
      super();
      this.tagManager = new TagManager(var1);
      this.commandBuildContext = new CommandBuildContext(var1);
      this.commands = new Commands(var2, this.commandBuildContext);
      this.commandBuildContext.missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy.CREATE_NEW);
      this.functionLibrary = new ServerFunctionLibrary(var3, this.commands.getDispatcher());
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

   public RecipeManager getRecipeManager() {
      return this.recipes;
   }

   public Commands getCommands() {
      return this.commands;
   }

   public ServerAdvancementManager getAdvancements() {
      return this.advancements;
   }

   public List<PreparableReloadListener> listeners() {
      return List.of(this.tagManager, this.predicateManager, this.recipes, this.lootTables, this.itemModifierManager, this.functionLibrary, this.advancements);
   }

   public static CompletableFuture<ReloadableServerResources> loadResources(
      ResourceManager var0, RegistryAccess.Frozen var1, Commands.CommandSelection var2, int var3, Executor var4, Executor var5
   ) {
      ReloadableServerResources var6 = new ReloadableServerResources(var1, var2, var3);
      return SimpleReloadInstance.create(var0, var6.listeners(), var4, var5, DATA_RELOAD_INITIAL_TASK, LOGGER.isDebugEnabled())
         .done()
         .whenComplete((var1x, var2x) -> var6.commandBuildContext.missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy.FAIL))
         .thenApply(var1x -> var6);
   }

   public void updateRegistryTags(RegistryAccess var1) {
      this.tagManager.getResult().forEach(var1x -> updateRegistryTags(var1, var1x));
      Blocks.rebuildCache();
   }

   private static <T> void updateRegistryTags(RegistryAccess var0, TagManager.LoadResult<T> var1) {
      ResourceKey var2 = var1.key();
      Map var3 = var1.tags()
         .entrySet()
         .stream()
         .collect(
            Collectors.toUnmodifiableMap(var1x -> TagKey.create(var2, (ResourceLocation)var1x.getKey()), var0x -> List.copyOf((Collection)var0x.getValue()))
         );
      var0.registryOrThrow(var2).bindTags(var3);
   }
}
