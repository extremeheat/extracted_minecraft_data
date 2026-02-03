package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.Locale;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.Nullable;

public record ReportEnvironment(String clientVersion, @Nullable Server server) {
   public ReportEnvironment {
      super();
   }

   public static ReportEnvironment local() {
      return create((Server)null);
   }

   public static ReportEnvironment thirdParty(final String ip) {
      return create(new Server.ThirdParty(ip));
   }

   public static ReportEnvironment realm(final RealmsServer realm) {
      return create(new Server.Realm(realm));
   }

   public static ReportEnvironment create(final @Nullable Server server) {
      return new ReportEnvironment(getClientVersion(), server);
   }

   public AbuseReportRequest.ClientInfo clientInfo() {
      return new AbuseReportRequest.ClientInfo(this.clientVersion, Locale.getDefault().toLanguageTag());
   }

   public AbuseReportRequest.@Nullable ThirdPartyServerInfo thirdPartyServerInfo() {
      Server var2 = this.server;
      if (var2 instanceof Server.ThirdParty thirdParty) {
         return new AbuseReportRequest.ThirdPartyServerInfo(thirdParty.ip);
      } else {
         return null;
      }
   }

   public AbuseReportRequest.@Nullable RealmInfo realmInfo() {
      Server var2 = this.server;
      if (var2 instanceof Server.Realm realm) {
         return new AbuseReportRequest.RealmInfo(String.valueOf(realm.realmId()), realm.slotId());
      } else {
         return null;
      }
   }

   private static String getClientVersion() {
      StringBuilder version = new StringBuilder();
      version.append(SharedConstants.getCurrentVersion().id());
      if (Minecraft.checkModStatus().shouldReportAsModified()) {
         version.append(" (modded)");
      }

      return version.toString();
   }

   public interface Server {
      public static record ThirdParty(String ip) implements Server {
         public ThirdParty {
            super();
         }
      }

      public static record Realm(long realmId, int slotId) implements Server {
         public Realm(final RealmsServer realm) {
            this(realm.id, realm.activeSlot);
         }

         public Realm {
            super();
         }
      }
   }
}
