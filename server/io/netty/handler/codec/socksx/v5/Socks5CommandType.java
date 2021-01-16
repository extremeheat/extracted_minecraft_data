package io.netty.handler.codec.socksx.v5;

public class Socks5CommandType implements Comparable<Socks5CommandType> {
   public static final Socks5CommandType CONNECT = new Socks5CommandType(1, "CONNECT");
   public static final Socks5CommandType BIND = new Socks5CommandType(2, "BIND");
   public static final Socks5CommandType UDP_ASSOCIATE = new Socks5CommandType(3, "UDP_ASSOCIATE");
   private final byte byteValue;
   private final String name;
   private String text;

   public static Socks5CommandType valueOf(byte var0) {
      switch(var0) {
      case 1:
         return CONNECT;
      case 2:
         return BIND;
      case 3:
         return UDP_ASSOCIATE;
      default:
         return new Socks5CommandType(var0);
      }
   }

   public Socks5CommandType(int var1) {
      this(var1, "UNKNOWN");
   }

   public Socks5CommandType(int var1, String var2) {
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
      if (!(var1 instanceof Socks5CommandType)) {
         return false;
      } else {
         return this.byteValue == ((Socks5CommandType)var1).byteValue;
      }
   }

   public int compareTo(Socks5CommandType var1) {
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
