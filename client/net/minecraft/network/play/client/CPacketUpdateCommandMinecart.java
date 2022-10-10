package net.minecraft.network.play.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.world.World;

public class CPacketUpdateCommandMinecart implements Packet<INetHandlerPlayServer> {
   private int field_210374_a;
   private String field_210375_b;
   private boolean field_210376_c;

   public CPacketUpdateCommandMinecart() {
      super();
   }

   public CPacketUpdateCommandMinecart(int var1, String var2, boolean var3) {
      super();
      this.field_210374_a = var1;
      this.field_210375_b = var2;
      this.field_210376_c = var3;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_210374_a = var1.func_150792_a();
      this.field_210375_b = var1.func_150789_c(32767);
      this.field_210376_c = var1.readBoolean();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_210374_a);
      var1.func_180714_a(this.field_210375_b);
      var1.writeBoolean(this.field_210376_c);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_210158_a(this);
   }

   @Nullable
   public CommandBlockBaseLogic func_210371_a(World var1) {
      Entity var2 = var1.func_73045_a(this.field_210374_a);
      return var2 instanceof EntityMinecartCommandBlock ? ((EntityMinecartCommandBlock)var2).func_145822_e() : null;
   }

   public String func_210372_a() {
      return this.field_210375_b;
   }

   public boolean func_210373_b() {
      return this.field_210376_c;
   }
}
