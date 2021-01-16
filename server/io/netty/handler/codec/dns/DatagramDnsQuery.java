package io.netty.handler.codec.dns;

import io.netty.channel.AddressedEnvelope;
import java.net.InetSocketAddress;

public class DatagramDnsQuery extends DefaultDnsQuery implements AddressedEnvelope<DatagramDnsQuery, InetSocketAddress> {
   private final InetSocketAddress sender;
   private final InetSocketAddress recipient;

   public DatagramDnsQuery(InetSocketAddress var1, InetSocketAddress var2, int var3) {
      this(var1, var2, var3, DnsOpCode.QUERY);
   }

   public DatagramDnsQuery(InetSocketAddress var1, InetSocketAddress var2, int var3, DnsOpCode var4) {
      super(var3, var4);
      if (var2 == null && var1 == null) {
         throw new NullPointerException("recipient and sender");
      } else {
         this.sender = var1;
         this.recipient = var2;
      }
   }

   public DatagramDnsQuery content() {
      return this;
   }

   public InetSocketAddress sender() {
      return this.sender;
   }

   public InetSocketAddress recipient() {
      return this.recipient;
   }

   public DatagramDnsQuery setId(int var1) {
      return (DatagramDnsQuery)super.setId(var1);
   }

   public DatagramDnsQuery setOpCode(DnsOpCode var1) {
      return (DatagramDnsQuery)super.setOpCode(var1);
   }

   public DatagramDnsQuery setRecursionDesired(boolean var1) {
      return (DatagramDnsQuery)super.setRecursionDesired(var1);
   }

   public DatagramDnsQuery setZ(int var1) {
      return (DatagramDnsQuery)super.setZ(var1);
   }

   public DatagramDnsQuery setRecord(DnsSection var1, DnsRecord var2) {
      return (DatagramDnsQuery)super.setRecord(var1, var2);
   }

   public DatagramDnsQuery addRecord(DnsSection var1, DnsRecord var2) {
      return (DatagramDnsQuery)super.addRecord(var1, var2);
   }

   public DatagramDnsQuery addRecord(DnsSection var1, int var2, DnsRecord var3) {
      return (DatagramDnsQuery)super.addRecord(var1, var2, var3);
   }

   public DatagramDnsQuery clear(DnsSection var1) {
      return (DatagramDnsQuery)super.clear(var1);
   }

   public DatagramDnsQuery clear() {
      return (DatagramDnsQuery)super.clear();
   }

   public DatagramDnsQuery touch() {
      return (DatagramDnsQuery)super.touch();
   }

   public DatagramDnsQuery touch(Object var1) {
      return (DatagramDnsQuery)super.touch(var1);
   }

   public DatagramDnsQuery retain() {
      return (DatagramDnsQuery)super.retain();
   }

   public DatagramDnsQuery retain(int var1) {
      return (DatagramDnsQuery)super.retain(var1);
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
