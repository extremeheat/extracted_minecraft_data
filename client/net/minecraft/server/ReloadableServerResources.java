package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;

public class ReloadableServerResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
   private final ReloadableServerRegistries.Holder fullRegistryHolder;
   private final Commands commands;
   private final RecipeManager recipes;
   private final ServerAdvancementManager advancements;
   private final ServerFunctionLibrary functionLibrary;
   private final List<Registry.PendingTags<?>> postponedTags;

   private ReloadableServerResources(
      LayeredRegistryAccess<RegistryLayer> var1,
      HolderLookup.Provider var2,
      FeatureFlagSet var3,
      Commands.CommandSelection var4,
      List<Registry.PendingTags<?>> var5,
      int var6
   ) {
      super();
      this.fullRegistryHolder = new ReloadableServerRegistries.Holder(var1.compositeAccess());
      this.postponedTags = var5;
      this.recipes = new RecipeManager(var2);
      this.commands = new Commands(var4, CommandBuildContext.simple(var2, var3));
      this.advancements = new ServerAdvancementManager(var2);
      this.functionLibrary = new ServerFunctionLibrary(var6, this.commands.getDispatcher());
   }

   public ServerFunctionLibrary getFunctionLibrary() {
      return this.functionLibrary;
   }

   public ReloadableServerRegistries.Holder fullRegistries() {
      return this.fullRegistryHolder;
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
      return List.of(this.recipes, this.functionLibrary, this.advancements);
   }

   public static CompletableFuture<ReloadableServerResources> loadResources(
      ResourceManager var0,
      LayeredRegistryAccess<RegistryLayer> var1,
      List<Registry.PendingTags<?>> var2,
      FeatureFlagSet var3,
      Commands.CommandSelection var4,
      int var5,
      Executor var6,
      Executor var7
   ) {
      return ReloadableServerRegistries.reload(var1, var2, var0, var6)
         .thenCompose(
            var7x -> {
               ReloadableServerResources var8 = new ReloadableServerResources(var7x.layers(), var7x.lookupWithUpdatedTags(), var3, var4, var2, var5);
               return SimpleReloadInstance.create(var0, var8.listeners(), var6, var7, DATA_RELOAD_INITIAL_TASK, LOGGER.isDebugEnabled())
                  .done()
                  .thenApply(var1xx -> var8);
            }
         );
   }

   public void updateStaticRegistryTags() {
      this.postponedTags.forEach(Registry.PendingTags::apply);
   }
}
