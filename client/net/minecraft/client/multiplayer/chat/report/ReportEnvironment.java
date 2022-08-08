package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.realmsclient.dto.RealmsServer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public record ReportEnvironment(String a, @Nullable Server b) {
   private final String clientVersion;
   @Nullable
   private final Server server;

   public ReportEnvironment(String var1, @Nullable Server var2) {
      super();
      this.clientVersion = var1;
      this.server = var2;
   }

   public static ReportEnvironment local() {
      return create((Server)null);
   }

   public static ReportEnvironment thirdParty(String var0) {
      return create(new Server.ThirdParty(var0));
   }

   public static ReportEnvironment realm(RealmsServer var0) {
      return create(new Server.Realm(var0));
   }

   public static ReportEnvironment create(@Nullable Server var0) {
      return new ReportEnvironment(getClientVersion(), var0);
   }

   public AbuseReportRequest.ClientInfo clientInfo() {
      return new AbuseReportRequest.ClientInfo(this.clientVersion);
   }

   @Nullable
   public AbuseReportRequest.ThirdPartyServerInfo thirdPartyServerInfo() {
      Server var2 = this.server;
      if (var2 instanceof Server.ThirdParty var1) {
         return new AbuseReportRequest.ThirdPartyServerInfo(var1.ip);
      } else {
         return null;
      }
   }

   @Nullable
   public AbuseReportRequest.RealmInfo realmInfo() {
      Server var2 = this.server;
      if (var2 instanceof Server.Realm var1) {
         return new AbuseReportRequest.RealmInfo(String.valueOf(var1.realmId()), var1.slotId());
      } else {
         return null;
      }
   }

   private static String getClientVersion() {
      StringBuilder var0 = new StringBuilder();
      var0.append("1.19.1");
      if (Minecraft.checkModStatus().shouldReportAsModified()) {
         var0.append(" (modded)");
      }

      return var0.toString();
   }

   public String clientVersion() {
      return this.clientVersion;
   }

   @Nullable
   public Server server() {
      return this.server;
   }

   public interface Server {
      public static record Realm(long a, int b) implements Server {
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

         public long realmId() {
            return this.realmId;
         }

         public int slotId() {
            return this.slotId;
         }
      }

      public static record ThirdParty(String a) implements Server {
         final String ip;

         public ThirdParty(String var1) {
            super();
            this.ip = var1;
         }

         public String ip() {
            return this.ip;
         }
      }
   }
}
