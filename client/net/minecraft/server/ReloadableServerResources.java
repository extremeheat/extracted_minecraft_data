package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Unit;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.storage.loot.LootDataManager;
import org.slf4j.Logger;

public class ReloadableServerResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final CompletableFuture<Unit> DATA_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture(Unit.INSTANCE);
   private final ReloadableServerResources.ConfigurableRegistryLookup registryLookup;
   private final Commands commands;
   private final RecipeManager recipes;
   private final TagManager tagManager;
   private final LootDataManager lootData;
   private final ServerAdvancementManager advancements;
   private final ServerFunctionLibrary functionLibrary;

   public ReloadableServerResources(RegistryAccess.Frozen var1, FeatureFlagSet var2, Commands.CommandSelection var3, int var4) {
      super();
      this.registryLookup = new ReloadableServerResources.ConfigurableRegistryLookup(var1);
      this.registryLookup.missingTagAccessPolicy(ReloadableServerResources.MissingTagAccessPolicy.CREATE_NEW);
      this.recipes = new RecipeManager(this.registryLookup);
      this.tagManager = new TagManager(var1);
      this.commands = new Commands(var3, CommandBuildContext.simple(this.registryLookup, var2));
      this.lootData = new LootDataManager(this.registryLookup);
      this.advancements = new ServerAdvancementManager(this.registryLookup, this.lootData);
      this.functionLibrary = new ServerFunctionLibrary(var4, this.commands.getDispatcher());
   }

   public ServerFunctionLibrary getFunctionLibrary() {
      return this.functionLibrary;
   }

   public LootDataManager getLootData() {
      return this.lootData;
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
      return List.of(this.tagManager, this.lootData, this.recipes, this.functionLibrary, this.advancements);
   }

   public static CompletableFuture<ReloadableServerResources> loadResources(
      ResourceManager var0, RegistryAccess.Frozen var1, FeatureFlagSet var2, Commands.CommandSelection var3, int var4, Executor var5, Executor var6
   ) {
      ReloadableServerResources var7 = new ReloadableServerResources(var1, var2, var3, var4);
      return SimpleReloadInstance.create(var0, var7.listeners(), var5, var6, DATA_RELOAD_INITIAL_TASK, LOGGER.isDebugEnabled())
         .done()
         .whenComplete((var1x, var2x) -> var7.registryLookup.missingTagAccessPolicy(ReloadableServerResources.MissingTagAccessPolicy.FAIL))
         .thenApply(var1x -> var7);
   }

   public void updateRegistryTags(RegistryAccess var1) {
      this.tagManager.getResult().forEach(var1x -> updateRegistryTags(var1, var1x));
      AbstractFurnaceBlockEntity.invalidateCache();
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

   static class ConfigurableRegistryLookup implements HolderLookup.Provider {
      private final RegistryAccess registryAccess;
      ReloadableServerResources.MissingTagAccessPolicy missingTagAccessPolicy = ReloadableServerResources.MissingTagAccessPolicy.FAIL;

      ConfigurableRegistryLookup(RegistryAccess var1) {
         super();
         this.registryAccess = var1;
      }

      public void missingTagAccessPolicy(ReloadableServerResources.MissingTagAccessPolicy var1) {
         this.missingTagAccessPolicy = var1;
      }

      @Override
      public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
         return this.registryAccess.listRegistries();
      }

      @Override
      public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
         return this.registryAccess.registry(var1).map(var1x -> this.createDispatchedLookup(var1x.asLookup(), var1x.asTagAddingLookup()));
      }

      private <T> HolderLookup.RegistryLookup<T> createDispatchedLookup(final HolderLookup.RegistryLookup<T> var1, final HolderLookup.RegistryLookup<T> var2) {
         return new HolderLookup.RegistryLookup.Delegate<T>() {
            @Override
            public HolderLookup.RegistryLookup<T> parent() {
               return switch(ConfigurableRegistryLookup.this.missingTagAccessPolicy) {
                  case FAIL -> var1;
                  case CREATE_NEW -> var2;
               };
            }
         };
      }
   }

   static enum MissingTagAccessPolicy {
      CREATE_NEW,
      FAIL;

      private MissingTagAccessPolicy() {
      }
   }
}
