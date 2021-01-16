package io.netty.handler.codec.socksx.v5;

public class Socks5PasswordAuthStatus implements Comparable<Socks5PasswordAuthStatus> {
   public static final Socks5PasswordAuthStatus SUCCESS = new Socks5PasswordAuthStatus(0, "SUCCESS");
   public static final Socks5PasswordAuthStatus FAILURE = new Socks5PasswordAuthStatus(255, "FAILURE");
   private final byte byteValue;
   private final String name;
   private String text;

   public static Socks5PasswordAuthStatus valueOf(byte var0) {
      switch(var0) {
      case -1:
         return FAILURE;
      case 0:
         return SUCCESS;
      default:
         return new Socks5PasswordAuthStatus(var0);
      }
   }

   public Socks5PasswordAuthStatus(int var1) {
      this(var1, "UNKNOWN");
   }

   public Socks5PasswordAuthStatus(int var1, String var2) {
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

   public boolean isSuccess() {
      return this.byteValue == 0;
   }

   public int hashCode() {
      return this.byteValue;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Socks5PasswordAuthStatus)) {
         return false;
      } else {
         return this.byteValue == ((Socks5PasswordAuthStatus)var1).byteValue;
      }
   }

   public int compareTo(Socks5PasswordAuthStatus var1) {
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
