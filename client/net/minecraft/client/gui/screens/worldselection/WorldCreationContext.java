package net.minecraft.client.gui.screens.worldselection;

import com.mojang.serialization.Lifecycle;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.level.levelgen.WorldGenSettings;

public record WorldCreationContext(WorldGenSettings a, Lifecycle b, RegistryAccess.Frozen c, ReloadableServerResources d) {
   private final WorldGenSettings worldGenSettings;
   private final Lifecycle worldSettingsStability;
   private final RegistryAccess.Frozen registryAccess;
   private final ReloadableServerResources dataPackResources;

   public WorldCreationContext(WorldGenSettings var1, Lifecycle var2, RegistryAccess.Frozen var3, ReloadableServerResources var4) {
      super();
      this.worldGenSettings = var1;
      this.worldSettingsStability = var2;
      this.registryAccess = var3;
      this.dataPackResources = var4;
   }

   public WorldCreationContext withSettings(WorldGenSettings var1) {
      return new WorldCreationContext(var1, this.worldSettingsStability, this.registryAccess, this.dataPackResources);
   }

   public WorldCreationContext withSettings(WorldCreationContext.SimpleUpdater var1) {
      WorldGenSettings var2 = var1.apply(this.worldGenSettings);
      return this.withSettings(var2);
   }

   public WorldCreationContext withSettings(WorldCreationContext.Updater var1) {
      WorldGenSettings var2 = var1.apply(this.registryAccess, this.worldGenSettings);
      return this.withSettings(var2);
   }

   @FunctionalInterface
   public interface SimpleUpdater extends UnaryOperator<WorldGenSettings> {
   }

   @FunctionalInterface
   public interface Updater extends BiFunction<RegistryAccess.Frozen, WorldGenSettings, WorldGenSettings> {
   }
}
