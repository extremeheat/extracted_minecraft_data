package io.netty.handler.codec.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;

final class DnsMessageUtil {
   static StringBuilder appendQuery(StringBuilder var0, DnsQuery var1) {
      appendQueryHeader(var0, var1);
      appendAllRecords(var0, var1);
      return var0;
   }

   static StringBuilder appendResponse(StringBuilder var0, DnsResponse var1) {
      appendResponseHeader(var0, var1);
      appendAllRecords(var0, var1);
      return var0;
   }

   static StringBuilder appendRecordClass(StringBuilder var0, int var1) {
      String var2;
      switch(var1 &= 65535) {
      case 1:
         var2 = "IN";
         break;
      case 2:
         var2 = "CSNET";
         break;
      case 3:
         var2 = "CHAOS";
         break;
      case 4:
         var2 = "HESIOD";
         break;
      case 254:
         var2 = "NONE";
         break;
      case 255:
         var2 = "ANY";
         break;
      default:
         var2 = null;
      }

      if (var2 != null) {
         var0.append(var2);
      } else {
         var0.append("UNKNOWN(").append(var1).append(')');
      }

      return var0;
   }

   private static void appendQueryHeader(StringBuilder var0, DnsQuery var1) {
      var0.append(StringUtil.simpleClassName((Object)var1)).append('(');
      appendAddresses(var0, var1).append(var1.id()).append(", ").append(var1.opCode());
      if (var1.isRecursionDesired()) {
         var0.append(", RD");
      }

      if (var1.z() != 0) {
         var0.append(", Z: ").append(var1.z());
      }

      var0.append(')');
   }

   private static void appendResponseHeader(StringBuilder var0, DnsResponse var1) {
      var0.append(StringUtil.simpleClassName((Object)var1)).append('(');
      appendAddresses(var0, var1).append(var1.id()).append(", ").append(var1.opCode()).append(", ").append(var1.code()).append(',');
      boolean var2 = true;
      if (var1.isRecursionDesired()) {
         var2 = false;
         var0.append(" RD");
      }

      if (var1.isAuthoritativeAnswer()) {
         var2 = false;
         var0.append(" AA");
      }

      if (var1.isTruncated()) {
         var2 = false;
         var0.append(" TC");
      }

      if (var1.isRecursionAvailable()) {
         var2 = false;
         var0.append(" RA");
      }

      if (var1.z() != 0) {
         if (!var2) {
            var0.append(',');
         }

         var0.append(" Z: ").append(var1.z());
      }

      if (var2) {
         var0.setCharAt(var0.length() - 1, ')');
      } else {
         var0.append(')');
      }

   }

   private static StringBuilder appendAddresses(StringBuilder var0, DnsMessage var1) {
      if (!(var1 instanceof AddressedEnvelope)) {
         return var0;
      } else {
         AddressedEnvelope var2 = (AddressedEnvelope)var1;
         SocketAddress var3 = var2.sender();
         if (var3 != null) {
            var0.append("from: ").append(var3).append(", ");
         }

         var3 = var2.recipient();
         if (var3 != null) {
            var0.append("to: ").append(var3).append(", ");
         }

         return var0;
      }
   }

   private static void appendAllRecords(StringBuilder var0, DnsMessage var1) {
      appendRecords(var0, var1, DnsSection.QUESTION);
      appendRecords(var0, var1, DnsSection.ANSWER);
      appendRecords(var0, var1, DnsSection.AUTHORITY);
      appendRecords(var0, var1, DnsSection.ADDITIONAL);
   }

   private static void appendRecords(StringBuilder var0, DnsMessage var1, DnsSection var2) {
      int var3 = var1.count(var2);

      for(int var4 = 0; var4 < var3; ++var4) {
         var0.append(StringUtil.NEWLINE).append('\t').append(var1.recordAt(var2, var4));
      }

   }

   private DnsMessageUtil() {
      super();
   }
}
