package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;

public class SPacketPlaceGhostRecipe implements Packet<INetHandlerPlayClient> {
   private int field_194314_a;
   private ResourceLocation field_194315_b;

   public SPacketPlaceGhostRecipe() {
      super();
   }

   public SPacketPlaceGhostRecipe(int var1, IRecipe var2) {
      super();
      this.field_194314_a = var1;
      this.field_194315_b = var2.func_199560_c();
   }

   public ResourceLocation func_199615_a() {
      return this.field_194315_b;
   }

   public int func_194313_b() {
      return this.field_194314_a;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_194314_a = var1.readByte();
      this.field_194315_b = var1.func_192575_l();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_194314_a);
      var1.func_192572_a(this.field_194315_b);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_194307_a(this);
   }
}
