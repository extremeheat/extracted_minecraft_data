package net.minecraft.client.resources;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

public class ClientPackSource extends BuiltInPackSource {
   private static final PackMetadataSection VERSION_METADATA_SECTION = new PackMetadataSection(
      Component.translatable("resourcePack.vanilla.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)
   );
   private static final BuiltInMetadata BUILT_IN_METADATA = BuiltInMetadata.of(PackMetadataSection.TYPE, VERSION_METADATA_SECTION);
   private static final Component VANILLA_NAME = Component.translatable("resourcePack.vanilla.name");
   public static final String HIGH_CONTRAST_PACK = "high_contrast";
   private static final Map<String, Component> SPECIAL_PACK_NAMES = Map.of(
      "programmer_art", Component.translatable("resourcePack.programmer_art.name"), "high_contrast", Component.translatable("resourcePack.high_contrast.name")
   );
   private static final ResourceLocation PACKS_DIR = new ResourceLocation("minecraft", "resourcepacks");
   @Nullable
   private final Path externalAssetDir;

   public ClientPackSource(Path var1) {
      super(PackType.CLIENT_RESOURCES, createVanillaPackSource(var1), PACKS_DIR);
      this.externalAssetDir = this.findExplodedAssetPacks(var1);
   }

   @Nullable
   private Path findExplodedAssetPacks(Path var1) {
      if (SharedConstants.IS_RUNNING_IN_IDE && var1.getFileSystem() == FileSystems.getDefault()) {
         Path var2 = var1.getParent().resolve("resourcepacks");
         if (Files.isDirectory(var2)) {
            return var2;
         }
      }

      return null;
   }

   private static VanillaPackResources createVanillaPackSource(Path var0) {
      VanillaPackResourcesBuilder var1 = new VanillaPackResourcesBuilder().setMetadata(BUILT_IN_METADATA).exposeNamespace("minecraft", "realms");
      return var1.applyDevelopmentConfig().pushJarResources().pushAssetPath(PackType.CLIENT_RESOURCES, var0).build();
   }

   @Override
   protected Component getPackTitle(String var1) {
      Component var2 = SPECIAL_PACK_NAMES.get(var1);
      return (Component)(var2 != null ? var2 : Component.literal(var1));
   }

   @Nullable
   @Override
   protected Pack createVanillaPack(PackResources var1) {
      return Pack.readMetaAndCreate("vanilla", VANILLA_NAME, true, var1x -> var1, PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, PackSource.BUILT_IN);
   }

   @Nullable
   @Override
   protected Pack createBuiltinPack(String var1, Pack.ResourcesSupplier var2, Component var3) {
      return Pack.readMetaAndCreate(var1, var3, false, var2, PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
   }

   @Override
   protected void populatePackList(BiConsumer<String, Function<String, Pack>> var1) {
      super.populatePackList(var1);
      if (this.externalAssetDir != null) {
         this.discoverPacksInPath(this.externalAssetDir, var1);
      }
   }
}
