package net.minecraft;

import java.util.Date;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.world.level.storage.DataVersion;

public interface WorldVersion {
   DataVersion dataVersion();

   String id();

   String name();

   int protocolVersion();

   PackFormat packVersion(PackType packType);

   Date buildTime();

   boolean stable();

   public static record Simple(String id, String name, DataVersion dataVersion, int protocolVersion, PackFormat resourcePackVersion, PackFormat datapackVersion, Date buildTime, boolean stable) implements WorldVersion {
      public Simple {
         super();
      }

      public PackFormat packVersion(final PackType packType) {
         PackFormat var10000;
         switch (packType) {
            case CLIENT_RESOURCES -> var10000 = this.resourcePackVersion;
            case SERVER_DATA -> var10000 = this.datapackVersion;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }
}
