package io.netty.handler.codec.dns;

import io.netty.channel.AddressedEnvelope;
import java.net.InetSocketAddress;

public class DatagramDnsResponse extends DefaultDnsResponse implements AddressedEnvelope<DatagramDnsResponse, InetSocketAddress> {
   private final InetSocketAddress sender;
   private final InetSocketAddress recipient;

   public DatagramDnsResponse(InetSocketAddress var1, InetSocketAddress var2, int var3) {
      this(var1, var2, var3, DnsOpCode.QUERY, DnsResponseCode.NOERROR);
   }

   public DatagramDnsResponse(InetSocketAddress var1, InetSocketAddress var2, int var3, DnsOpCode var4) {
      this(var1, var2, var3, var4, DnsResponseCode.NOERROR);
   }

   public DatagramDnsResponse(InetSocketAddress var1, InetSocketAddress var2, int var3, DnsOpCode var4, DnsResponseCode var5) {
      super(var3, var4, var5);
      if (var2 == null && var1 == null) {
         throw new NullPointerException("recipient and sender");
      } else {
         this.sender = var1;
         this.recipient = var2;
      }
   }

   public DatagramDnsResponse content() {
      return this;
   }

   public InetSocketAddress sender() {
      return this.sender;
   }

   public InetSocketAddress recipient() {
      return this.recipient;
   }

   public DatagramDnsResponse setAuthoritativeAnswer(boolean var1) {
      return (DatagramDnsResponse)super.setAuthoritativeAnswer(var1);
   }

   public DatagramDnsResponse setTruncated(boolean var1) {
      return (DatagramDnsResponse)super.setTruncated(var1);
   }

   public DatagramDnsResponse setRecursionAvailable(boolean var1) {
      return (DatagramDnsResponse)super.setRecursionAvailable(var1);
   }

   public DatagramDnsResponse setCode(DnsResponseCode var1) {
      return (DatagramDnsResponse)super.setCode(var1);
   }

   public DatagramDnsResponse setId(int var1) {
      return (DatagramDnsResponse)super.setId(var1);
   }

   public DatagramDnsResponse setOpCode(DnsOpCode var1) {
      return (DatagramDnsResponse)super.setOpCode(var1);
   }

   public DatagramDnsResponse setRecursionDesired(boolean var1) {
      return (DatagramDnsResponse)super.setRecursionDesired(var1);
   }

   public DatagramDnsResponse setZ(int var1) {
      return (DatagramDnsResponse)super.setZ(var1);
   }

   public DatagramDnsResponse setRecord(DnsSection var1, DnsRecord var2) {
      return (DatagramDnsResponse)super.setRecord(var1, var2);
   }

   public DatagramDnsResponse addRecord(DnsSection var1, DnsRecord var2) {
      return (DatagramDnsResponse)super.addRecord(var1, var2);
   }

   public DatagramDnsResponse addRecord(DnsSection var1, int var2, DnsRecord var3) {
      return (DatagramDnsResponse)super.addRecord(var1, var2, var3);
   }

   public DatagramDnsResponse clear(DnsSection var1) {
      return (DatagramDnsResponse)super.clear(var1);
   }

   public DatagramDnsResponse clear() {
      return (DatagramDnsResponse)super.clear();
   }

   public DatagramDnsResponse touch() {
      return (DatagramDnsResponse)super.touch();
   }

   public DatagramDnsResponse touch(Object var1) {
      return (DatagramDnsResponse)super.touch(var1);
   }

   public DatagramDnsResponse retain() {
      return (DatagramDnsResponse)super.retain();
   }

   public DatagramDnsResponse retain(int var1) {
      return (DatagramDnsResponse)super.retain(var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!super.equals(var1)) {
         return false;
      } else if (!(var1 instanceof AddressedEnvelope)) {
         return false;
      } else {
         AddressedEnvelope var2 = (AddressedEnvelope)var1;
         if (this.sender() == null) {
            if (var2.sender() != null) {
               return false;
            }
         } else if (!this.sender().equals(var2.sender())) {
            return false;
         }

         if (this.recipient() == null) {
            if (var2.recipient() != null) {
               return false;
            }
         } else if (!this.recipient().equals(var2.recipient())) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      if (this.sender() != null) {
         var1 = var1 * 31 + this.sender().hashCode();
      }

      if (this.recipient() != null) {
         var1 = var1 * 31 + this.recipient().hashCode();
      }

      return var1;
   }
}
