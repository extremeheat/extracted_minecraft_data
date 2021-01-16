package io.netty.handler.codec.dns;

import io.netty.util.internal.ObjectUtil;

public class DnsResponseCode implements Comparable<DnsResponseCode> {
   public static final DnsResponseCode NOERROR = new DnsResponseCode(0, "NoError");
   public static final DnsResponseCode FORMERR = new DnsResponseCode(1, "FormErr");
   public static final DnsResponseCode SERVFAIL = new DnsResponseCode(2, "ServFail");
   public static final DnsResponseCode NXDOMAIN = new DnsResponseCode(3, "NXDomain");
   public static final DnsResponseCode NOTIMP = new DnsResponseCode(4, "NotImp");
   public static final DnsResponseCode REFUSED = new DnsResponseCode(5, "Refused");
   public static final DnsResponseCode YXDOMAIN = new DnsResponseCode(6, "YXDomain");
   public static final DnsResponseCode YXRRSET = new DnsResponseCode(7, "YXRRSet");
   public static final DnsResponseCode NXRRSET = new DnsResponseCode(8, "NXRRSet");
   public static final DnsResponseCode NOTAUTH = new DnsResponseCode(9, "NotAuth");
   public static final DnsResponseCode NOTZONE = new DnsResponseCode(10, "NotZone");
   public static final DnsResponseCode BADVERS_OR_BADSIG = new DnsResponseCode(16, "BADVERS_OR_BADSIG");
   public static final DnsResponseCode BADKEY = new DnsResponseCode(17, "BADKEY");
   public static final DnsResponseCode BADTIME = new DnsResponseCode(18, "BADTIME");
   public static final DnsResponseCode BADMODE = new DnsResponseCode(19, "BADMODE");
   public static final DnsResponseCode BADNAME = new DnsResponseCode(20, "BADNAME");
   public static final DnsResponseCode BADALG = new DnsResponseCode(21, "BADALG");
   private final int code;
   private final String name;
   private String text;

   public static DnsResponseCode valueOf(int var0) {
      switch(var0) {
      case 0:
         return NOERROR;
      case 1:
         return FORMERR;
      case 2:
         return SERVFAIL;
      case 3:
         return NXDOMAIN;
      case 4:
         return NOTIMP;
      case 5:
         return REFUSED;
      case 6:
         return YXDOMAIN;
      case 7:
         return YXRRSET;
      case 8:
         return NXRRSET;
      case 9:
         return NOTAUTH;
      case 10:
         return NOTZONE;
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      default:
         return new DnsResponseCode(var0);
      case 16:
         return BADVERS_OR_BADSIG;
      case 17:
         return BADKEY;
      case 18:
         return BADTIME;
      case 19:
         return BADMODE;
      case 20:
         return BADNAME;
      case 21:
         return BADALG;
      }
   }

   private DnsResponseCode(int var1) {
      this(var1, "UNKNOWN");
   }

   public DnsResponseCode(int var1, String var2) {
      super();
      if (var1 >= 0 && var1 <= 65535) {
         this.code = var1;
         this.name = (String)ObjectUtil.checkNotNull(var2, "name");
      } else {
         throw new IllegalArgumentException("code: " + var1 + " (expected: 0 ~ 65535)");
      }
   }

   public int intValue() {
      return this.code;
   }

   public int compareTo(DnsResponseCode var1) {
      return this.intValue() - var1.intValue();
   }

   public int hashCode() {
      return this.intValue();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DnsResponseCode)) {
         return false;
      } else {
         return this.intValue() == ((DnsResponseCode)var1).intValue();
      }
   }

   public String toString() {
      String var1 = this.text;
      if (var1 == null) {
         this.text = var1 = this.name + '(' + this.intValue() + ')';
      }

      return var1;
   }
}
