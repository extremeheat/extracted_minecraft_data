package net.minecraft.client.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

public class DefaultClientPackResources extends VanillaPackResources {
   private final AssetIndex assetIndex;

   public DefaultClientPackResources(PackMetadataSection var1, AssetIndex var2) {
      super(var1, "minecraft", "realms");
      this.assetIndex = var2;
   }

   @Nullable
   protected InputStream getResourceAsStream(PackType var1, ResourceLocation var2) {
      if (var1 == PackType.CLIENT_RESOURCES) {
         File var3 = this.assetIndex.getFile(var2);
         if (var3 != null && var3.exists()) {
            try {
               return new FileInputStream(var3);
            } catch (FileNotFoundException var5) {
            }
         }
      }

      return super.getResourceAsStream(var1, var2);
   }

   public boolean hasResource(PackType var1, ResourceLocation var2) {
      if (var1 == PackType.CLIENT_RESOURCES) {
         File var3 = this.assetIndex.getFile(var2);
         if (var3 != null && var3.exists()) {
            return true;
         }
      }

      return super.hasResource(var1, var2);
   }

   @Nullable
   protected InputStream getResourceAsStream(String var1) {
      File var2 = this.assetIndex.getRootFile(var1);
      if (var2 != null && var2.exists()) {
         try {
            return new FileInputStream(var2);
         } catch (FileNotFoundException var4) {
         }
      }

      return super.getResourceAsStream(var1);
   }

   public Collection<ResourceLocation> getResources(PackType var1, String var2, String var3, int var4, Predicate<String> var5) {
      Collection var6 = super.getResources(var1, var2, var3, var4, var5);
      var6.addAll(this.assetIndex.getFiles(var3, var2, var4, var5));
      return var6;
   }
}
