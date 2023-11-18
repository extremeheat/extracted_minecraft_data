package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest.ClientInfo;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest.RealmInfo;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest.ThirdPartyServerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public record ReportEnvironment(String a, @Nullable ReportEnvironment.Server b) {
   private final String clientVersion;
   @Nullable
   private final ReportEnvironment.Server server;

   public ReportEnvironment(String var1, @Nullable ReportEnvironment.Server var2) {
      super();
      this.clientVersion = var1;
      this.server = var2;
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
      ReportEnvironment.Server var2 = this.server;
      return var2 instanceof ReportEnvironment.Server.ThirdParty var1 ? new ThirdPartyServerInfo(var1.ip) : null;
   }

   @Nullable
   public RealmInfo realmInfo() {
      ReportEnvironment.Server var2 = this.server;
      return var2 instanceof ReportEnvironment.Server.Realm var1 ? new RealmInfo(String.valueOf(var1.realmId()), var1.slotId()) : null;
   }

   private static String getClientVersion() {
      StringBuilder var0 = new StringBuilder();
      var0.append("1.20.1");
      if (Minecraft.checkModStatus().shouldReportAsModified()) {
         var0.append(" (modded)");
      }

      return var0.toString();
   }

   public interface Server {
      public static record Realm(long a, int b) implements ReportEnvironment.Server {
         private final long realmId;
         private final int slotId;

         public Realm(RealmsServer var1) {
            this(var1.id, var1.activeSlot);
         }

         public Realm(long var1, int var3) {
            super();
            this.realmId = var1;
            this.slotId = var3;
         }
      }

      public static record ThirdParty(String a) implements ReportEnvironment.Server {
         final String ip;

         public ThirdParty(String var1) {
            super();
            this.ip = var1;
         }
      }
   }
}
