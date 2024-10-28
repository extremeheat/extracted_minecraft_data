package com.mojang.blaze3d.platform;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.lang3.ArrayUtils;

public enum IconSet {
   RELEASE(new String[]{"icons"}),
   SNAPSHOT(new String[]{"icons", "snapshot"});

   private final String[] path;

   private IconSet(final String... var3) {
      this.path = var3;
   }

   public List<IoSupplier<InputStream>> getStandardIcons(PackResources var1) throws IOException {
      return List.of(this.getFile(var1, "icon_16x16.png"), this.getFile(var1, "icon_32x32.png"), this.getFile(var1, "icon_48x48.png"), this.getFile(var1, "icon_128x128.png"), this.getFile(var1, "icon_256x256.png"));
   }

   public IoSupplier<InputStream> getMacIcon(PackResources var1) throws IOException {
      return this.getFile(var1, "minecraft.icns");
   }

   private IoSupplier<InputStream> getFile(PackResources var1, String var2) throws IOException {
      String[] var3 = (String[])ArrayUtils.add(this.path, var2);
      IoSupplier var4 = var1.getRootResource(var3);
      if (var4 == null) {
         throw new FileNotFoundException(String.join("/", var3));
      } else {
         return var4;
      }
   }

   // $FF: synthetic method
   private static IconSet[] $values() {
      return new IconSet[]{RELEASE, SNAPSHOT};
   }
}
