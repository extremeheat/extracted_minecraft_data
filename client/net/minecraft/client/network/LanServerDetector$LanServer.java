package net.minecraft.client.network;

import net.minecraft.client.Minecraft;

public class LanServerDetector$LanServer {
   private String field_77492_a;
   private String field_77490_b;
   private long field_77491_c;

   public LanServerDetector$LanServer(String var1, String var2) {
      super();
      this.field_77492_a = var1;
      this.field_77490_b = var2;
      this.field_77491_c = Minecraft.func_71386_F();
   }

   public String func_77487_a() {
      return this.field_77492_a;
   }

   public String func_77488_b() {
      return this.field_77490_b;
   }

   public void func_77489_c() {
      this.field_77491_c = Minecraft.func_71386_F();
   }
}
