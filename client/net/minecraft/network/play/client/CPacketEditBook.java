package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.EnumHand;

public class CPacketEditBook implements Packet<INetHandlerPlayServer> {
   private ItemStack field_210347_a;
   private boolean field_210348_b;
   private EnumHand field_212645_c;

   public CPacketEditBook() {
      super();
   }

   public CPacketEditBook(ItemStack var1, boolean var2, EnumHand var3) {
      super();
      this.field_210347_a = var1.func_77946_l();
      this.field_210348_b = var2;
      this.field_212645_c = var3;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_210347_a = var1.func_150791_c();
      this.field_210348_b = var1.readBoolean();
      this.field_212645_c = (EnumHand)var1.func_179257_a(EnumHand.class);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150788_a(this.field_210347_a);
      var1.writeBoolean(this.field_210348_b);
      var1.func_179249_a(this.field_212645_c);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_210156_a(this);
   }

   public ItemStack func_210346_a() {
      return this.field_210347_a;
   }

   public boolean func_210345_b() {
      return this.field_210348_b;
   }

   public EnumHand func_212644_d() {
      return this.field_212645_c;
   }
}
