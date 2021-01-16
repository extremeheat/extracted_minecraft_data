package io.netty.handler.codec.socksx.v5;

public class Socks5AddressType implements Comparable<Socks5AddressType> {
   public static final Socks5AddressType IPv4 = new Socks5AddressType(1, "IPv4");
   public static final Socks5AddressType DOMAIN = new Socks5AddressType(3, "DOMAIN");
   public static final Socks5AddressType IPv6 = new Socks5AddressType(4, "IPv6");
   private final byte byteValue;
   private final String name;
   private String text;

   public static Socks5AddressType valueOf(byte var0) {
      switch(var0) {
      case 1:
         return IPv4;
      case 2:
      default:
         return new Socks5AddressType(var0);
      case 3:
         return DOMAIN;
      case 4:
         return IPv6;
      }
   }

   public Socks5AddressType(int var1) {
      this(var1, "UNKNOWN");
   }

   public Socks5AddressType(int var1, String var2) {
      super();
      if (var2 == null) {
         throw new NullPointerException("name");
      } else {
         this.byteValue = (byte)var1;
         this.name = var2;
      }
   }

   public byte byteValue() {
      return this.byteValue;
   }

   public int hashCode() {
      return this.byteValue;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Socks5AddressType)) {
         return false;
      } else {
         return this.byteValue == ((Socks5AddressType)var1).byteValue;
      }
   }

   public int compareTo(Socks5AddressType var1) {
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
