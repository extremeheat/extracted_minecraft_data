package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S45PacketTitle implements Packet<INetHandlerPlayClient> {
   private S45PacketTitle.Type field_179812_a;
   private IChatComponent field_179810_b;
   private int field_179811_c;
   private int field_179808_d;
   private int field_179809_e;

   public S45PacketTitle() {
      super();
   }

   public S45PacketTitle(S45PacketTitle.Type var1, IChatComponent var2) {
      this(var1, var2, -1, -1, -1);
   }

   public S45PacketTitle(int var1, int var2, int var3) {
      this(S45PacketTitle.Type.TIMES, (IChatComponent)null, var1, var2, var3);
   }

   public S45PacketTitle(S45PacketTitle.Type var1, IChatComponent var2, int var3, int var4, int var5) {
      super();
      this.field_179812_a = var1;
      this.field_179810_b = var2;
      this.field_179811_c = var3;
      this.field_179808_d = var4;
      this.field_179809_e = var5;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179812_a = (S45PacketTitle.Type)var1.func_179257_a(S45PacketTitle.Type.class);
      if (this.field_179812_a == S45PacketTitle.Type.TITLE || this.field_179812_a == S45PacketTitle.Type.SUBTITLE) {
         this.field_179810_b = var1.func_179258_d();
      }

      if (this.field_179812_a == S45PacketTitle.Type.TIMES) {
         this.field_179811_c = var1.readInt();
         this.field_179808_d = var1.readInt();
         this.field_179809_e = var1.readInt();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_179812_a);
      if (this.field_179812_a == S45PacketTitle.Type.TITLE || this.field_179812_a == S45PacketTitle.Type.SUBTITLE) {
         var1.func_179256_a(this.field_179810_b);
      }

      if (this.field_179812_a == S45PacketTitle.Type.TIMES) {
         var1.writeInt(this.field_179811_c);
         var1.writeInt(this.field_179808_d);
         var1.writeInt(this.field_179809_e);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_175099_a(this);
   }

   public S45PacketTitle.Type func_179807_a() {
      return this.field_179812_a;
   }

   public IChatComponent func_179805_b() {
      return this.field_179810_b;
   }

   public int func_179806_c() {
      return this.field_179811_c;
   }

   public int func_179804_d() {
      return this.field_179808_d;
   }

   public int func_179803_e() {
      return this.field_179809_e;
   }

   public static enum Type {
      TITLE,
      SUBTITLE,
      TIMES,
      CLEAR,
      RESET;

      private Type() {
      }

      public static S45PacketTitle.Type func_179969_a(String var0) {
         S45PacketTitle.Type[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            S45PacketTitle.Type var4 = var1[var3];
            if (var4.name().equalsIgnoreCase(var0)) {
               return var4;
            }
         }

         return TITLE;
      }

      public static String[] func_179971_a() {
         String[] var0 = new String[values().length];
         int var1 = 0;
         S45PacketTitle.Type[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            S45PacketTitle.Type var5 = var2[var4];
            var0[var1++] = var5.name().toLowerCase();
         }

         return var0;
      }
   }
}
