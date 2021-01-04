package net.minecraft.server.rcon.thread;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.Util;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.rcon.NetworkDataOutputStream;
import net.minecraft.server.rcon.PktUtils;

public class QueryThreadGs4 extends GenericThread {
   private long lastChallengeCheck;
   private final int port;
   private final int serverPort;
   private final int maxPlayers;
   private final String serverName;
   private final String worldName;
   private DatagramSocket socket;
   private final byte[] buffer = new byte[1460];
   private DatagramPacket request;
   private final Map<SocketAddress, String> idents;
   private String hostIp;
   private String serverIp;
   private final Map<SocketAddress, QueryThreadGs4.RequestChallenge> validChallenges;
   private final long lastChallengeClean;
   private final NetworkDataOutputStream rulesResponse;
   private long lastRulesResponse;

   public QueryThreadGs4(ServerInterface var1) {
      super(var1, "Query Listener");
      this.port = var1.getProperties().queryPort;
      this.serverIp = var1.getServerIp();
      this.serverPort = var1.getServerPort();
      this.serverName = var1.getServerName();
      this.maxPlayers = var1.getMaxPlayers();
      this.worldName = var1.getLevelIdName();
      this.lastRulesResponse = 0L;
      this.hostIp = "0.0.0.0";
      if (!this.serverIp.isEmpty() && !this.hostIp.equals(this.serverIp)) {
         this.hostIp = this.serverIp;
      } else {
         this.serverIp = "0.0.0.0";

         try {
            InetAddress var2 = InetAddress.getLocalHost();
            this.hostIp = var2.getHostAddress();
         } catch (UnknownHostException var3) {
            this.warn("Unable to determine local host IP, please set server-ip in server.properties: " + var3.getMessage());
         }
      }

      this.idents = Maps.newHashMap();
      this.rulesResponse = new NetworkDataOutputStream(1460);
      this.validChallenges = Maps.newHashMap();
      this.lastChallengeClean = (new Date()).getTime();
   }

   private void sendTo(byte[] var1, DatagramPacket var2) throws IOException {
      this.socket.send(new DatagramPacket(var1, var1.length, var2.getSocketAddress()));
   }

   private boolean processPacket(DatagramPacket var1) throws IOException {
      byte[] var2 = var1.getData();
      int var3 = var1.getLength();
      SocketAddress var4 = var1.getSocketAddress();
      this.debug("Packet len " + var3 + " [" + var4 + "]");
      if (3 <= var3 && -2 == var2[0] && -3 == var2[1]) {
         this.debug("Packet '" + PktUtils.toHexString(var2[2]) + "' [" + var4 + "]");
         switch(var2[2]) {
         case 0:
            if (!this.validChallenge(var1)) {
               this.debug("Invalid challenge [" + var4 + "]");
               return false;
            } else if (15 == var3) {
               this.sendTo(this.buildRuleResponse(var1), var1);
               this.debug("Rules [" + var4 + "]");
            } else {
               NetworkDataOutputStream var5 = new NetworkDataOutputStream(1460);
               var5.write(0);
               var5.writeBytes(this.getIdentBytes(var1.getSocketAddress()));
               var5.writeString(this.serverName);
               var5.writeString("SMP");
               var5.writeString(this.worldName);
               var5.writeString(Integer.toString(this.currentPlayerCount()));
               var5.writeString(Integer.toString(this.maxPlayers));
               var5.writeShort((short)this.serverPort);
               var5.writeString(this.hostIp);
               this.sendTo(var5.toByteArray(), var1);
               this.debug("Status [" + var4 + "]");
            }
         default:
            return true;
         case 9:
            this.sendChallenge(var1);
            this.debug("Challenge [" + var4 + "]");
            return true;
         }
      } else {
         this.debug("Invalid packet [" + var4 + "]");
         return false;
      }
   }

   private byte[] buildRuleResponse(DatagramPacket var1) throws IOException {
      long var2 = Util.getMillis();
      if (var2 < this.lastRulesResponse + 5000L) {
         byte[] var9 = this.rulesResponse.toByteArray();
         byte[] var10 = this.getIdentBytes(var1.getSocketAddress());
         var9[1] = var10[0];
         var9[2] = var10[1];
         var9[3] = var10[2];
         var9[4] = var10[3];
         return var9;
      } else {
         this.lastRulesResponse = var2;
         this.rulesResponse.reset();
         this.rulesResponse.write(0);
         this.rulesResponse.writeBytes(this.getIdentBytes(var1.getSocketAddress()));
         this.rulesResponse.writeString("splitnum");
         this.rulesResponse.write(128);
         this.rulesResponse.write(0);
         this.rulesResponse.writeString("hostname");
         this.rulesResponse.writeString(this.serverName);
         this.rulesResponse.writeString("gametype");
         this.rulesResponse.writeString("SMP");
         this.rulesResponse.writeString("game_id");
         this.rulesResponse.writeString("MINECRAFT");
         this.rulesResponse.writeString("version");
         this.rulesResponse.writeString(this.serverInterface.getServerVersion());
         this.rulesResponse.writeString("plugins");
         this.rulesResponse.writeString(this.serverInterface.getPluginNames());
         this.rulesResponse.writeString("map");
         this.rulesResponse.writeString(this.worldName);
         this.rulesResponse.writeString("numplayers");
         this.rulesResponse.writeString("" + this.currentPlayerCount());
         this.rulesResponse.writeString("maxplayers");
         this.rulesResponse.writeString("" + this.maxPlayers);
         this.rulesResponse.writeString("hostport");
         this.rulesResponse.writeString("" + this.serverPort);
         this.rulesResponse.writeString("hostip");
         this.rulesResponse.writeString(this.hostIp);
         this.rulesResponse.write(0);
         this.rulesResponse.write(1);
         this.rulesResponse.writeString("player_");
         this.rulesResponse.write(0);
         String[] var4 = this.serverInterface.getPlayerNames();
         String[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            this.rulesResponse.writeString(var8);
         }

         this.rulesResponse.write(0);
         return this.rulesResponse.toByteArray();
      }
   }

   private byte[] getIdentBytes(SocketAddress var1) {
      return ((QueryThreadGs4.RequestChallenge)this.validChallenges.get(var1)).getIdentBytes();
   }

   private Boolean validChallenge(DatagramPacket var1) {
      SocketAddress var2 = var1.getSocketAddress();
      if (!this.validChallenges.containsKey(var2)) {
         return false;
      } else {
         byte[] var3 = var1.getData();
         return ((QueryThreadGs4.RequestChallenge)this.validChallenges.get(var2)).getChallenge() != PktUtils.intFromNetworkByteArray(var3, 7, var1.getLength()) ? false : true;
      }
   }

   private void sendChallenge(DatagramPacket var1) throws IOException {
      QueryThreadGs4.RequestChallenge var2 = new QueryThreadGs4.RequestChallenge(var1);
      this.validChallenges.put(var1.getSocketAddress(), var2);
      this.sendTo(var2.getChallengeBytes(), var1);
   }

   private void pruneChallenges() {
      if (this.running) {
         long var1 = Util.getMillis();
         if (var1 >= this.lastChallengeCheck + 30000L) {
            this.lastChallengeCheck = var1;
            Iterator var3 = this.validChallenges.entrySet().iterator();

            while(var3.hasNext()) {
               Entry var4 = (Entry)var3.next();
               if (((QueryThreadGs4.RequestChallenge)var4.getValue()).before(var1)) {
                  var3.remove();
               }
            }

         }
      }
   }

   public void run() {
      this.info("Query running on " + this.serverIp + ":" + this.port);
      this.lastChallengeCheck = Util.getMillis();
      this.request = new DatagramPacket(this.buffer, this.buffer.length);

      try {
         while(this.running) {
            try {
               this.socket.receive(this.request);
               this.pruneChallenges();
               this.processPacket(this.request);
            } catch (SocketTimeoutException var7) {
               this.pruneChallenges();
            } catch (PortUnreachableException var8) {
            } catch (IOException var9) {
               this.recoverSocketError(var9);
            }
         }
      } finally {
         this.closeSockets();
      }

   }

   public void start() {
      if (!this.running) {
         if (0 < this.port && 65535 >= this.port) {
            if (this.initSocket()) {
               super.start();
            }

         } else {
            this.warn("Invalid query port " + this.port + " found in server.properties (queries disabled)");
         }
      }
   }

   private void recoverSocketError(Exception var1) {
      if (this.running) {
         this.warn("Unexpected exception, buggy JRE? (" + var1 + ")");
         if (!this.initSocket()) {
            this.error("Failed to recover from buggy JRE, shutting down!");
            this.running = false;
         }

      }
   }

   private boolean initSocket() {
      try {
         this.socket = new DatagramSocket(this.port, InetAddress.getByName(this.serverIp));
         this.registerSocket(this.socket);
         this.socket.setSoTimeout(500);
         return true;
      } catch (SocketException var2) {
         this.warn("Unable to initialise query system on " + this.serverIp + ":" + this.port + " (Socket): " + var2.getMessage());
      } catch (UnknownHostException var3) {
         this.warn("Unable to initialise query system on " + this.serverIp + ":" + this.port + " (Unknown Host): " + var3.getMessage());
      } catch (Exception var4) {
         this.warn("Unable to initialise query system on " + this.serverIp + ":" + this.port + " (E): " + var4.getMessage());
      }

      return false;
   }

   class RequestChallenge {
      private final long time = (new Date()).getTime();
      private final int challenge;
      private final byte[] identBytes;
      private final byte[] challengeBytes;
      private final String ident;

      public RequestChallenge(DatagramPacket var2) {
         super();
         byte[] var3 = var2.getData();
         this.identBytes = new byte[4];
         this.identBytes[0] = var3[3];
         this.identBytes[1] = var3[4];
         this.identBytes[2] = var3[5];
         this.identBytes[3] = var3[6];
         this.ident = new String(this.identBytes, StandardCharsets.UTF_8);
         this.challenge = (new Random()).nextInt(16777216);
         this.challengeBytes = String.format("\t%s%d\u0000", this.ident, this.challenge).getBytes(StandardCharsets.UTF_8);
      }

      public Boolean before(long var1) {
         return this.time < var1;
      }

      public int getChallenge() {
         return this.challenge;
      }

      public byte[] getChallengeBytes() {
         return this.challengeBytes;
      }

      public byte[] getIdentBytes() {
         return this.identBytes;
      }
   }
}
