package com.mojang.realmsclient.client;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.dto.RegionPingResult;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.Util;
import org.apache.commons.io.IOUtils;

public class Ping {
   public Ping() {
      super();
   }

   public static List<RegionPingResult> ping(Region... var0) {
      for(Region var4 : var0) {
         ping(var4.endpoint);
      }

      ArrayList var6 = Lists.newArrayList();

      for(Region var5 : var0) {
         var6.add(new RegionPingResult(var5.name, ping(var5.endpoint)));
      }

      var6.sort(Comparator.comparingInt(RegionPingResult::ping));
      return var6;
   }

   private static int ping(String var0) {
      boolean var1 = true;
      long var2 = 0L;
      Socket var4 = null;

      for(int var5 = 0; var5 < 5; ++var5) {
         try {
            InetSocketAddress var6 = new InetSocketAddress(var0, 80);
            var4 = new Socket();
            long var7 = now();
            var4.connect(var6, 700);
            var2 += now() - var7;
         } catch (Exception var12) {
            var2 += 700L;
         } finally {
            IOUtils.closeQuietly(var4);
         }
      }

      return (int)((double)var2 / 5.0);
   }

   private static long now() {
      return Util.getMillis();
   }

   public static List<RegionPingResult> pingAllRegions() {
      return ping(Ping.Region.values());
   }

   static enum Region {
      US_EAST_1("us-east-1", "ec2.us-east-1.amazonaws.com"),
      US_WEST_2("us-west-2", "ec2.us-west-2.amazonaws.com"),
      US_WEST_1("us-west-1", "ec2.us-west-1.amazonaws.com"),
      EU_WEST_1("eu-west-1", "ec2.eu-west-1.amazonaws.com"),
      AP_SOUTHEAST_1("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com"),
      AP_SOUTHEAST_2("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com"),
      AP_NORTHEAST_1("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com"),
      SA_EAST_1("sa-east-1", "ec2.sa-east-1.amazonaws.com");

      final String name;
      final String endpoint;

      private Region(final String var3, final String var4) {
         this.name = var3;
         this.endpoint = var4;
      }

      // $FF: synthetic method
      private static Region[] $values() {
         return new Region[]{US_EAST_1, US_WEST_2, US_WEST_1, EU_WEST_1, AP_SOUTHEAST_1, AP_SOUTHEAST_2, AP_NORTHEAST_1, SA_EAST_1};
      }
   }
}
