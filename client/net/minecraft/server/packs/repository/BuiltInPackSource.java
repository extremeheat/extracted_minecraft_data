package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public abstract class BuiltInPackSource implements RepositorySource {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String VANILLA_ID = "vanilla";
   public static final KnownPack CORE_PACK_INFO = KnownPack.vanilla("core");
   private final PackType packType;
   private final VanillaPackResources vanillaPack;
   private final ResourceLocation packDir;
   private final DirectoryValidator validator;

   public BuiltInPackSource(PackType var1, VanillaPackResources var2, ResourceLocation var3, DirectoryValidator var4) {
      super();
      this.packType = var1;
      this.vanillaPack = var2;
      this.packDir = var3;
      this.validator = var4;
   }

   public void loadPacks(Consumer<Pack> var1) {
      Pack var2 = this.createVanillaPack(this.vanillaPack);
      if (var2 != null) {
         var1.accept(var2);
      }

      this.listBundledPacks(var1);
   }

   @Nullable
   protected abstract Pack createVanillaPack(PackResources var1);

   protected abstract Component getPackTitle(String var1);

   public VanillaPackResources getVanillaPack() {
      return this.vanillaPack;
   }

   private void listBundledPacks(Consumer<Pack> var1) {
      HashMap var2 = new HashMap();
      Objects.requireNonNull(var2);
      this.populatePackList(var2::put);
      var2.forEach((var1x, var2x) -> {
         Pack var3 = (Pack)var2x.apply(var1x);
         if (var3 != null) {
            var1.accept(var3);
         }

      });
   }

   protected void populatePackList(BiConsumer<String, Function<String, Pack>> var1) {
      this.vanillaPack.listRawPaths(this.packType, this.packDir, (var2) -> {
         this.discoverPacksInPath(var2, var1);
      });
   }

   protected void discoverPacksInPath(@Nullable Path var1, BiConsumer<String, Function<String, Pack>> var2) {
      if (var1 != null && Files.isDirectory(var1, new LinkOption[0])) {
         try {
            FolderRepositorySource.discoverPacks(var1, this.validator, (var2x, var3) -> {
               var2.accept(pathToId(var2x), (var2xx) -> {
                  return this.createBuiltinPack(var2xx, var3, this.getPackTitle(var2xx));
               });
            });
         } catch (IOException var4) {
            LOGGER.warn("Failed to discover packs in {}", var1, var4);
         }
      }

   }

   private static String pathToId(Path var0) {
      return StringUtils.removeEnd(var0.getFileName().toString(), ".zip");
   }

   @Nullable
   protected abstract Pack createBuiltinPack(String var1, Pack.ResourcesSupplier var2, Component var3);

   protected static Pack.ResourcesSupplier fixedResources(final PackResources var0) {
      return new Pack.ResourcesSupplier() {
         public PackResources openPrimary(PackLocationInfo var1) {
            return var0;
         }

         public PackResources openFull(PackLocationInfo var1, Pack.Metadata var2) {
            return var0;
         }
      };
   }
}
