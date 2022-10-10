package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.ITextComponent;

public class SPacketTitle implements Packet<INetHandlerPlayClient> {
   private SPacketTitle.Type field_179812_a;
   private ITextComponent field_179810_b;
   private int field_179811_c;
   private int field_179808_d;
   private int field_179809_e;

   public SPacketTitle() {
      super();
   }

   public SPacketTitle(SPacketTitle.Type var1, ITextComponent var2) {
      this(var1, var2, -1, -1, -1);
   }

   public SPacketTitle(int var1, int var2, int var3) {
      this(SPacketTitle.Type.TIMES, (ITextComponent)null, var1, var2, var3);
   }

   public SPacketTitle(SPacketTitle.Type var1, @Nullable ITextComponent var2, int var3, int var4, int var5) {
      super();
      this.field_179812_a = var1;
      this.field_179810_b = var2;
      this.field_179811_c = var3;
      this.field_179808_d = var4;
      this.field_179809_e = var5;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179812_a = (SPacketTitle.Type)var1.func_179257_a(SPacketTitle.Type.class);
      if (this.field_179812_a == SPacketTitle.Type.TITLE || this.field_179812_a == SPacketTitle.Type.SUBTITLE || this.field_179812_a == SPacketTitle.Type.ACTIONBAR) {
         this.field_179810_b = var1.func_179258_d();
      }

      if (this.field_179812_a == SPacketTitle.Type.TIMES) {
         this.field_179811_c = var1.readInt();
         this.field_179808_d = var1.readInt();
         this.field_179809_e = var1.readInt();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_179812_a);
      if (this.field_179812_a == SPacketTitle.Type.TITLE || this.field_179812_a == SPacketTitle.Type.SUBTITLE || this.field_179812_a == SPacketTitle.Type.ACTIONBAR) {
         var1.func_179256_a(this.field_179810_b);
      }

      if (this.field_179812_a == SPacketTitle.Type.TIMES) {
         var1.writeInt(this.field_179811_c);
         var1.writeInt(this.field_179808_d);
         var1.writeInt(this.field_179809_e);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_175099_a(this);
   }

   public SPacketTitle.Type func_179807_a() {
      return this.field_179812_a;
   }

   public ITextComponent func_179805_b() {
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
      ACTIONBAR,
      TIMES,
      CLEAR,
      RESET;

      private Type() {
      }
   }
}
