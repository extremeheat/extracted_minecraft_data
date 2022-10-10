package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class CPacketUpdateSign implements Packet<INetHandlerPlayServer> {
   private BlockPos field_179723_a;
   private String[] field_149590_d;

   public CPacketUpdateSign() {
      super();
   }

   public CPacketUpdateSign(BlockPos var1, ITextComponent var2, ITextComponent var3, ITextComponent var4, ITextComponent var5) {
      super();
      this.field_179723_a = var1;
      this.field_149590_d = new String[]{var2.getString(), var3.getString(), var4.getString(), var5.getString()};
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179723_a = var1.func_179259_c();
      this.field_149590_d = new String[4];

      for(int var2 = 0; var2 < 4; ++var2) {
         this.field_149590_d[var2] = var1.func_150789_c(384);
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179255_a(this.field_179723_a);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.func_180714_a(this.field_149590_d[var2]);
      }

   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147343_a(this);
   }

   public BlockPos func_179722_a() {
      return this.field_179723_a;
   }

   public String[] func_187017_b() {
      return this.field_149590_d;
   }
}
