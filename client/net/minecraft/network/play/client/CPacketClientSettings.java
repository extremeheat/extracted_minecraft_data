package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHandSide;

public class CPacketClientSettings implements Packet<INetHandlerPlayServer> {
   private String field_149530_a;
   private int field_149528_b;
   private EntityPlayer.EnumChatVisibility field_149529_c;
   private boolean field_149526_d;
   private int field_179711_e;
   private EnumHandSide field_186992_f;

   public CPacketClientSettings() {
      super();
   }

   public CPacketClientSettings(String var1, int var2, EntityPlayer.EnumChatVisibility var3, boolean var4, int var5, EnumHandSide var6) {
      super();
      this.field_149530_a = var1;
      this.field_149528_b = var2;
      this.field_149529_c = var3;
      this.field_149526_d = var4;
      this.field_179711_e = var5;
      this.field_186992_f = var6;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149530_a = var1.func_150789_c(16);
      this.field_149528_b = var1.readByte();
      this.field_149529_c = (EntityPlayer.EnumChatVisibility)var1.func_179257_a(EntityPlayer.EnumChatVisibility.class);
      this.field_149526_d = var1.readBoolean();
      this.field_179711_e = var1.readUnsignedByte();
      this.field_186992_f = (EnumHandSide)var1.func_179257_a(EnumHandSide.class);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_180714_a(this.field_149530_a);
      var1.writeByte(this.field_149528_b);
      var1.func_179249_a(this.field_149529_c);
      var1.writeBoolean(this.field_149526_d);
      var1.writeByte(this.field_179711_e);
      var1.func_179249_a(this.field_186992_f);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147352_a(this);
   }

   public String func_149524_c() {
      return this.field_149530_a;
   }

   public EntityPlayer.EnumChatVisibility func_149523_e() {
      return this.field_149529_c;
   }

   public boolean func_149520_f() {
      return this.field_149526_d;
   }

   public int func_149521_d() {
      return this.field_179711_e;
   }

   public EnumHandSide func_186991_f() {
      return this.field_186992_f;
   }
}
