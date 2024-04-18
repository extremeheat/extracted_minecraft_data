package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest.ClientInfo;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest.RealmInfo;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest.ThirdPartyServerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public record ReportEnvironment(String clientVersion, @Nullable ReportEnvironment.Server server) {
   public ReportEnvironment(String clientVersion, @Nullable ReportEnvironment.Server server) {
      super();
      this.clientVersion = clientVersion;
      this.server = server;
   }

   public static ReportEnvironment local() {
      return create(null);
   }

   public static ReportEnvironment thirdParty(String var0) {
      return create(new ReportEnvironment.Server.ThirdParty(var0));
   }

   public static ReportEnvironment realm(RealmsServer var0) {
      return create(new ReportEnvironment.Server.Realm(var0));
   }

   public static ReportEnvironment create(@Nullable ReportEnvironment.Server var0) {
      return new ReportEnvironment(getClientVersion(), var0);
   }

   public ClientInfo clientInfo() {
      return new ClientInfo(this.clientVersion, Locale.getDefault().toLanguageTag());
   }

   @Nullable
   public ThirdPartyServerInfo thirdPartyServerInfo() {
      return this.server instanceof ReportEnvironment.Server.ThirdParty var1 ? new ThirdPartyServerInfo(var1.ip) : null;
   }

   @Nullable
   public RealmInfo realmInfo() {
      return this.server instanceof ReportEnvironment.Server.Realm var1 ? new RealmInfo(String.valueOf(var1.realmId()), var1.slotId()) : null;
   }

   private static String getClientVersion() {
      StringBuilder var0 = new StringBuilder();
      var0.append("1.20.5-rc1");
      if (Minecraft.checkModStatus().shouldReportAsModified()) {
         var0.append(" (modded)");
      }

      return var0.toString();
   }

   public interface Server {
      public static record Realm(long realmId, int slotId) implements ReportEnvironment.Server {
         public Realm(RealmsServer var1) {
            this(var1.id, var1.activeSlot);
         }

         public Realm(long realmId, int slotId) {
            super();
            this.realmId = realmId;
            this.slotId = slotId;
         }
      }

      public static record ThirdParty(String ip) implements ReportEnvironment.Server {

         public ThirdParty(String ip) {
            super();
            this.ip = ip;
         }
      }
   }
}
