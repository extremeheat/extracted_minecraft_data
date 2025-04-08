package net.minecraft.client.gui.screens.worldselection;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;

public record WorldCreationContext(WorldOptions options, Registry<LevelStem> datapackDimensions, WorldDimensions selectedDimensions, LayeredRegistryAccess<RegistryLayer> worldgenRegistries, ReloadableServerResources dataPackResources, WorldDataConfiguration dataConfiguration, InitialWorldCreationOptions initialWorldCreationOptions) {
   public WorldCreationContext(WorldGenSettings var1, LayeredRegistryAccess<RegistryLayer> var2, ReloadableServerResources var3, WorldDataConfiguration var4) {
      this(var1.options(), var1.dimensions(), var2, var3, var4, new InitialWorldCreationOptions(WorldCreationUiState.SelectedGameMode.SURVIVAL, Set.of(), (ResourceKey)null));
   }

   public WorldCreationContext(WorldOptions var1, WorldDimensions var2, LayeredRegistryAccess<RegistryLayer> var3, ReloadableServerResources var4, WorldDataConfiguration var5, InitialWorldCreationOptions var6) {
      this(var1, var3.getLayer(RegistryLayer.DIMENSIONS).lookupOrThrow(Registries.LEVEL_STEM), var2, var3.replaceFrom(RegistryLayer.DIMENSIONS), var4, var5, var6);
   }

   public WorldCreationContext(WorldOptions var1, Registry<LevelStem> var2, WorldDimensions var3, LayeredRegistryAccess<RegistryLayer> var4, ReloadableServerResources var5, WorldDataConfiguration var6, InitialWorldCreationOptions var7) {
      super();
      this.options = var1;
      this.datapackDimensions = var2;
      this.selectedDimensions = var3;
      this.worldgenRegistries = var4;
      this.dataPackResources = var5;
      this.dataConfiguration = var6;
      this.initialWorldCreationOptions = var7;
   }

   public WorldCreationContext withSettings(WorldOptions var1, WorldDimensions var2) {
      return new WorldCreationContext(var1, this.datapackDimensions, var2, this.worldgenRegistries, this.dataPackResources, this.dataConfiguration, this.initialWorldCreationOptions);
   }

   public WorldCreationContext withOptions(OptionsModifier var1) {
      return new WorldCreationContext((WorldOptions)var1.apply(this.options), this.datapackDimensions, this.selectedDimensions, this.worldgenRegistries, this.dataPackResources, this.dataConfiguration, this.initialWorldCreationOptions);
   }

   public WorldCreationContext withDimensions(DimensionsUpdater var1) {
      return new WorldCreationContext(this.options, this.datapackDimensions, (WorldDimensions)var1.apply(this.worldgenLoadContext(), this.selectedDimensions), this.worldgenRegistries, this.dataPackResources, this.dataConfiguration, this.initialWorldCreationOptions);
   }

   public RegistryAccess.Frozen worldgenLoadContext() {
      return this.worldgenRegistries.compositeAccess();
   }

   public void validate() {
      for(LevelStem var2 : this.datapackDimensions()) {
         var2.generator().validate();
      }

   }

   @FunctionalInterface
   public interface DimensionsUpdater extends BiFunction<RegistryAccess.Frozen, WorldDimensions, WorldDimensions> {
   }

   public interface OptionsModifier extends UnaryOperator<WorldOptions> {
   }
}
