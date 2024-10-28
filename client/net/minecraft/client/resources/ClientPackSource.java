package net.minecraft.client.resources;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.level.validation.DirectoryValidator;

public class ClientPackSource extends BuiltInPackSource {
   private static final PackMetadataSection VERSION_METADATA_SECTION;
   private static final BuiltInMetadata BUILT_IN_METADATA;
   public static final String HIGH_CONTRAST_PACK = "high_contrast";
   private static final Map<String, Component> SPECIAL_PACK_NAMES;
   private static final PackLocationInfo VANILLA_PACK_INFO;
   private static final PackSelectionConfig VANILLA_SELECTION_CONFIG;
   private static final PackSelectionConfig BUILT_IN_SELECTION_CONFIG;
   private static final ResourceLocation PACKS_DIR;
   @Nullable
   private final Path externalAssetDir;

   public ClientPackSource(Path var1, DirectoryValidator var2) {
      super(PackType.CLIENT_RESOURCES, createVanillaPackSource(var1), PACKS_DIR, var2);
      this.externalAssetDir = this.findExplodedAssetPacks(var1);
   }

   private static PackLocationInfo createBuiltInPackLocation(String var0, Component var1) {
      return new PackLocationInfo(var0, var1, PackSource.BUILT_IN, Optional.of(KnownPack.vanilla(var0)));
   }

   @Nullable
   private Path findExplodedAssetPacks(Path var1) {
      if (SharedConstants.IS_RUNNING_IN_IDE && var1.getFileSystem() == FileSystems.getDefault()) {
         Path var2 = var1.getParent().resolve("resourcepacks");
         if (Files.isDirectory(var2, new LinkOption[0])) {
            return var2;
         }
      }

      return null;
   }

   private static VanillaPackResources createVanillaPackSource(Path var0) {
      VanillaPackResourcesBuilder var1 = (new VanillaPackResourcesBuilder()).setMetadata(BUILT_IN_METADATA).exposeNamespace("minecraft", "realms");
      return var1.applyDevelopmentConfig().pushJarResources().pushAssetPath(PackType.CLIENT_RESOURCES, var0).build(VANILLA_PACK_INFO);
   }

   protected Component getPackTitle(String var1) {
      Component var2 = (Component)SPECIAL_PACK_NAMES.get(var1);
      return (Component)(var2 != null ? var2 : Component.literal(var1));
   }

   @Nullable
   protected Pack createVanillaPack(PackResources var1) {
      return Pack.readMetaAndCreate(VANILLA_PACK_INFO, fixedResources(var1), PackType.CLIENT_RESOURCES, VANILLA_SELECTION_CONFIG);
   }

   @Nullable
   protected Pack createBuiltinPack(String var1, Pack.ResourcesSupplier var2, Component var3) {
      return Pack.readMetaAndCreate(createBuiltInPackLocation(var1, var3), var2, PackType.CLIENT_RESOURCES, BUILT_IN_SELECTION_CONFIG);
   }

   protected void populatePackList(BiConsumer<String, Function<String, Pack>> var1) {
      super.populatePackList(var1);
      if (this.externalAssetDir != null) {
         this.discoverPacksInPath(this.externalAssetDir, var1);
      }

   }

   static {
      VERSION_METADATA_SECTION = new PackMetadataSection(Component.translatable("resourcePack.vanilla.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES), Optional.empty());
      BUILT_IN_METADATA = BuiltInMetadata.of(PackMetadataSection.TYPE, VERSION_METADATA_SECTION);
      SPECIAL_PACK_NAMES = Map.of("programmer_art", Component.translatable("resourcePack.programmer_art.name"), "high_contrast", Component.translatable("resourcePack.high_contrast.name"));
      VANILLA_PACK_INFO = new PackLocationInfo("vanilla", Component.translatable("resourcePack.vanilla.name"), PackSource.BUILT_IN, Optional.of(CORE_PACK_INFO));
      VANILLA_SELECTION_CONFIG = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);
      BUILT_IN_SELECTION_CONFIG = new PackSelectionConfig(false, Pack.Position.TOP, false);
      PACKS_DIR = ResourceLocation.withDefaultNamespace("resourcepacks");
   }
}
