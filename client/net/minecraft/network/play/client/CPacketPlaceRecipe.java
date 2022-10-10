package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;

public class CPacketPlaceRecipe implements Packet<INetHandlerPlayServer> {
   private int field_194320_a;
   private ResourceLocation field_194321_b;
   private boolean field_194322_c;

   public CPacketPlaceRecipe() {
      super();
   }

   public CPacketPlaceRecipe(int var1, IRecipe var2, boolean var3) {
      super();
      this.field_194320_a = var1;
      this.field_194321_b = var2.func_199560_c();
      this.field_194322_c = var3;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_194320_a = var1.readByte();
      this.field_194321_b = var1.func_192575_l();
      this.field_194322_c = var1.readBoolean();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_194320_a);
      var1.func_192572_a(this.field_194321_b);
      var1.writeBoolean(this.field_194322_c);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_194308_a(this);
   }

   public int func_194318_a() {
      return this.field_194320_a;
   }

   public ResourceLocation func_199618_b() {
      return this.field_194321_b;
   }

   public boolean func_194319_c() {
      return this.field_194322_c;
   }
}
