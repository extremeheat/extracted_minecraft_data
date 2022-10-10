package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.math.BlockPos;

public class CPacketUpdateCommandBlock implements Packet<INetHandlerPlayServer> {
   private BlockPos field_210365_a;
   private String field_210366_b;
   private boolean field_210367_c;
   private boolean field_210368_d;
   private boolean field_210369_e;
   private TileEntityCommandBlock.Mode field_210370_f;

   public CPacketUpdateCommandBlock() {
      super();
   }

   public CPacketUpdateCommandBlock(BlockPos var1, String var2, TileEntityCommandBlock.Mode var3, boolean var4, boolean var5, boolean var6) {
      super();
      this.field_210365_a = var1;
      this.field_210366_b = var2;
      this.field_210367_c = var4;
      this.field_210368_d = var5;
      this.field_210369_e = var6;
      this.field_210370_f = var3;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_210365_a = var1.func_179259_c();
      this.field_210366_b = var1.func_150789_c(32767);
      this.field_210370_f = (TileEntityCommandBlock.Mode)var1.func_179257_a(TileEntityCommandBlock.Mode.class);
      byte var2 = var1.readByte();
      this.field_210367_c = (var2 & 1) != 0;
      this.field_210368_d = (var2 & 2) != 0;
      this.field_210369_e = (var2 & 4) != 0;
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179255_a(this.field_210365_a);
      var1.func_180714_a(this.field_210366_b);
      var1.func_179249_a(this.field_210370_f);
      int var2 = 0;
      if (this.field_210367_c) {
         var2 |= 1;
      }

      if (this.field_210368_d) {
         var2 |= 2;
      }

      if (this.field_210369_e) {
         var2 |= 4;
      }

      var1.writeByte(var2);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_210153_a(this);
   }

   public BlockPos func_210361_a() {
      return this.field_210365_a;
   }

   public String func_210359_b() {
      return this.field_210366_b;
   }

   public boolean func_210363_c() {
      return this.field_210367_c;
   }

   public boolean func_210364_d() {
      return this.field_210368_d;
   }

   public boolean func_210362_e() {
      return this.field_210369_e;
   }

   public TileEntityCommandBlock.Mode func_210360_f() {
      return this.field_210370_f;
   }
}
