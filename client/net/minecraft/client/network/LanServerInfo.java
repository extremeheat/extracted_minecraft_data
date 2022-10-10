package net.minecraft.client.network;

import net.minecraft.util.Util;

public class LanServerInfo {
   private final String field_77492_a;
   private final String field_77490_b;
   private long field_77491_c;

   public LanServerInfo(String var1, String var2) {
      super();
      this.field_77492_a = var1;
      this.field_77490_b = var2;
      this.field_77491_c = Util.func_211177_b();
   }

   public String func_77487_a() {
      return this.field_77492_a;
   }

   public String func_77488_b() {
      return this.field_77490_b;
   }

   public void func_77489_c() {
      this.field_77491_c = Util.func_211177_b();
   }
}
