package io.netty.handler.codec.dns;

import io.netty.util.internal.ObjectUtil;

public class DnsOpCode implements Comparable<DnsOpCode> {
   public static final DnsOpCode QUERY = new DnsOpCode(0, "QUERY");
   public static final DnsOpCode IQUERY = new DnsOpCode(1, "IQUERY");
   public static final DnsOpCode STATUS = new DnsOpCode(2, "STATUS");
   public static final DnsOpCode NOTIFY = new DnsOpCode(4, "NOTIFY");
   public static final DnsOpCode UPDATE = new DnsOpCode(5, "UPDATE");
   private final byte byteValue;
   private final String name;
   private String text;

   public static DnsOpCode valueOf(int var0) {
      switch(var0) {
      case 0:
         return QUERY;
      case 1:
         return IQUERY;
      case 2:
         return STATUS;
      case 3:
      default:
         return new DnsOpCode(var0);
      case 4:
         return NOTIFY;
      case 5:
         return UPDATE;
      }
   }

   private DnsOpCode(int var1) {
      this(var1, "UNKNOWN");
   }

   public DnsOpCode(int var1, String var2) {
      super();
      this.byteValue = (byte)var1;
      this.name = (String)ObjectUtil.checkNotNull(var2, "name");
   }

   public byte byteValue() {
      return this.byteValue;
   }

   public int hashCode() {
      return this.byteValue;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DnsOpCode)) {
         return false;
      } else {
         return this.byteValue == ((DnsOpCode)var1).byteValue;
      }
   }

   public int compareTo(DnsOpCode var1) {
      return this.byteValue - var1.byteValue;
   }

   public String toString() {
      String var1 = this.text;
      if (var1 == null) {
         this.text = var1 = this.name + '(' + (this.byteValue & 255) + ')';
      }

      return var1;
   }
}
