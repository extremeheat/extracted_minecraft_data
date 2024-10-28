package net.minecraft.server.packs.repository;

import com.google.common.annotations.VisibleForTesting;
import java.nio.file.Path;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.DirectoryValidator;

public class ServerPacksSource extends BuiltInPackSource {
   private static final PackMetadataSection VERSION_METADATA_SECTION;
   private static final FeatureFlagsMetadataSection FEATURE_FLAGS_METADATA_SECTION;
   private static final BuiltInMetadata BUILT_IN_METADATA;
   private static final PackLocationInfo VANILLA_PACK_INFO;
   private static final PackSelectionConfig VANILLA_SELECTION_CONFIG;
   private static final PackSelectionConfig FEATURE_SELECTION_CONFIG;
   private static final ResourceLocation PACKS_DIR;

   public ServerPacksSource(DirectoryValidator var1) {
      super(PackType.SERVER_DATA, createVanillaPackSource(), PACKS_DIR, var1);
   }

   private static PackLocationInfo createBuiltInPackLocation(String var0, Component var1) {
      return new PackLocationInfo(var0, var1, PackSource.FEATURE, Optional.of(KnownPack.vanilla(var0)));
   }

   @VisibleForTesting
   public static VanillaPackResources createVanillaPackSource() {
      return (new VanillaPackResourcesBuilder()).setMetadata(BUILT_IN_METADATA).exposeNamespace("minecraft").applyDevelopmentConfig().pushJarResources().build(VANILLA_PACK_INFO);
   }

   protected Component getPackTitle(String var1) {
      return Component.literal(var1);
   }

   @Nullable
   protected Pack createVanillaPack(PackResources var1) {
      return Pack.readMetaAndCreate(VANILLA_PACK_INFO, fixedResources(var1), PackType.SERVER_DATA, VANILLA_SELECTION_CONFIG);
   }

   @Nullable
   protected Pack createBuiltinPack(String var1, Pack.ResourcesSupplier var2, Component var3) {
      return Pack.readMetaAndCreate(createBuiltInPackLocation(var1, var3), var2, PackType.SERVER_DATA, FEATURE_SELECTION_CONFIG);
   }

   public static PackRepository createPackRepository(Path var0, DirectoryValidator var1) {
      return new PackRepository(new RepositorySource[]{new ServerPacksSource(var1), new FolderRepositorySource(var0, PackType.SERVER_DATA, PackSource.WORLD, var1)});
   }

   public static PackRepository createVanillaTrustedRepository() {
      return new PackRepository(new RepositorySource[]{new ServerPacksSource(new DirectoryValidator((var0) -> {
         return true;
      }))});
   }

   public static PackRepository createPackRepository(LevelStorageSource.LevelStorageAccess var0) {
      return createPackRepository(var0.getLevelPath(LevelResource.DATAPACK_DIR), var0.parent().getWorldDirValidator());
   }

   static {
      VERSION_METADATA_SECTION = new PackMetadataSection(Component.translatable("dataPack.vanilla.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA), Optional.empty());
      FEATURE_FLAGS_METADATA_SECTION = new FeatureFlagsMetadataSection(FeatureFlags.DEFAULT_FLAGS);
      BUILT_IN_METADATA = BuiltInMetadata.of(PackMetadataSection.TYPE, VERSION_METADATA_SECTION, FeatureFlagsMetadataSection.TYPE, FEATURE_FLAGS_METADATA_SECTION);
      VANILLA_PACK_INFO = new PackLocationInfo("vanilla", Component.translatable("dataPack.vanilla.name"), PackSource.BUILT_IN, Optional.of(CORE_PACK_INFO));
      VANILLA_SELECTION_CONFIG = new PackSelectionConfig(false, Pack.Position.BOTTOM, false);
      FEATURE_SELECTION_CONFIG = new PackSelectionConfig(false, Pack.Position.TOP, false);
      PACKS_DIR = ResourceLocation.withDefaultNamespace("datapacks");
   }
}
