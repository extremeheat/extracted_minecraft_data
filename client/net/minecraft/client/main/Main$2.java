package net.minecraft.client.main;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public final class Main$2 extends Authenticator {
   public Main$2(String var1, String var2) {
      super();
      this.field_152581_a = var1;
      this.field_152582_b = var2;
   }

   @Override
   protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(this.field_152581_a, this.field_152582_b.toCharArray());
   }
}
