package com.mojang.realmsclient.client;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.dto.RegionPingResult;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;

public class Ping {
   public Ping() {
      super();
   }

   public static List<RegionPingResult> ping(final Region... regions) {
      for(Region region : regions) {
         ping(region.endpoint);
      }

      List<RegionPingResult> results = Lists.newArrayList();

      for(Region region : regions) {
         results.add(new RegionPingResult(region.name, ping(region.endpoint)));
      }

      results.sort(Comparator.comparingInt(RegionPingResult::ping));
      return results;
   }

   private static int ping(final String host) {
      int timeout = 700;
      long sum = 0L;
      Socket socket = null;

      for(int i = 0; i < 5; ++i) {
         try {
            SocketAddress sockAddr = new InetSocketAddress(host, 80);
            socket = new Socket();
            long t1 = now();
            socket.connect(sockAddr, 700);
            sum += now() - t1;
         } catch (Exception var12) {
            sum += 700L;
         } finally {
            IOUtils.closeQuietly(socket);
         }
      }

      return (int)((double)sum / 5.0);
   }

   private static long now() {
      return Util.getMillis();
   }

   public static List<RegionPingResult> pingAllRegions() {
      return ping(Ping.Region.values());
   }

   private static enum Region {
      US_EAST_1("us-east-1", "ec2.us-east-1.amazonaws.com"),
      US_WEST_2("us-west-2", "ec2.us-west-2.amazonaws.com"),
      US_WEST_1("us-west-1", "ec2.us-west-1.amazonaws.com"),
      EU_WEST_1("eu-west-1", "ec2.eu-west-1.amazonaws.com"),
      AP_SOUTHEAST_1("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com"),
      AP_SOUTHEAST_2("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com"),
      AP_NORTHEAST_1("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com"),
      SA_EAST_1("sa-east-1", "ec2.sa-east-1.amazonaws.com");

      private final String name;
      private final String endpoint;

      private Region(final String name, final String endpoint) {
         this.name = name;
         this.endpoint = endpoint;
      }

      // $FF: synthetic method
      private static Region[] $values() {
         return new Region[]{US_EAST_1, US_WEST_2, US_WEST_1, EU_WEST_1, AP_SOUTHEAST_1, AP_SOUTHEAST_2, AP_NORTHEAST_1, SA_EAST_1};
      }
   }
}
