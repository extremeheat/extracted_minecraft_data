package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class CPacketUpdateStructureBlock implements Packet<INetHandlerPlayServer> {
   private BlockPos field_210391_a;
   private TileEntityStructure.UpdateCommand field_210392_b;
   private StructureMode field_210393_c;
   private String field_210394_d;
   private BlockPos field_210395_e;
   private BlockPos field_210396_f;
   private Mirror field_210397_g;
   private Rotation field_210398_h;
   private String field_210399_i;
   private boolean field_210400_j;
   private boolean field_210401_k;
   private boolean field_210402_l;
   private float field_210403_m;
   private long field_210404_n;

   public CPacketUpdateStructureBlock() {
      super();
   }

   public CPacketUpdateStructureBlock(BlockPos var1, TileEntityStructure.UpdateCommand var2, StructureMode var3, String var4, BlockPos var5, BlockPos var6, Mirror var7, Rotation var8, String var9, boolean var10, boolean var11, boolean var12, float var13, long var14) {
      super();
      this.field_210391_a = var1;
      this.field_210392_b = var2;
      this.field_210393_c = var3;
      this.field_210394_d = var4;
      this.field_210395_e = var5;
      this.field_210396_f = var6;
      this.field_210397_g = var7;
      this.field_210398_h = var8;
      this.field_210399_i = var9;
      this.field_210400_j = var10;
      this.field_210401_k = var11;
      this.field_210402_l = var12;
      this.field_210403_m = var13;
      this.field_210404_n = var14;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_210391_a = var1.func_179259_c();
      this.field_210392_b = (TileEntityStructure.UpdateCommand)var1.func_179257_a(TileEntityStructure.UpdateCommand.class);
      this.field_210393_c = (StructureMode)var1.func_179257_a(StructureMode.class);
      this.field_210394_d = var1.func_150789_c(32767);
      this.field_210395_e = new BlockPos(MathHelper.func_76125_a(var1.readByte(), -32, 32), MathHelper.func_76125_a(var1.readByte(), -32, 32), MathHelper.func_76125_a(var1.readByte(), -32, 32));
      this.field_210396_f = new BlockPos(MathHelper.func_76125_a(var1.readByte(), 0, 32), MathHelper.func_76125_a(var1.readByte(), 0, 32), MathHelper.func_76125_a(var1.readByte(), 0, 32));
      this.field_210397_g = (Mirror)var1.func_179257_a(Mirror.class);
      this.field_210398_h = (Rotation)var1.func_179257_a(Rotation.class);
      this.field_210399_i = var1.func_150789_c(12);
      this.field_210403_m = MathHelper.func_76131_a(var1.readFloat(), 0.0F, 1.0F);
      this.field_210404_n = var1.func_179260_f();
      byte var2 = var1.readByte();
      this.field_210400_j = (var2 & 1) != 0;
      this.field_210401_k = (var2 & 2) != 0;
      this.field_210402_l = (var2 & 4) != 0;
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179255_a(this.field_210391_a);
      var1.func_179249_a(this.field_210392_b);
      var1.func_179249_a(this.field_210393_c);
      var1.func_180714_a(this.field_210394_d);
      var1.writeByte(this.field_210395_e.func_177958_n());
      var1.writeByte(this.field_210395_e.func_177956_o());
      var1.writeByte(this.field_210395_e.func_177952_p());
      var1.writeByte(this.field_210396_f.func_177958_n());
      var1.writeByte(this.field_210396_f.func_177956_o());
      var1.writeByte(this.field_210396_f.func_177952_p());
      var1.func_179249_a(this.field_210397_g);
      var1.func_179249_a(this.field_210398_h);
      var1.func_180714_a(this.field_210399_i);
      var1.writeFloat(this.field_210403_m);
      var1.func_179254_b(this.field_210404_n);
      int var2 = 0;
      if (this.field_210400_j) {
         var2 |= 1;
      }

      if (this.field_210401_k) {
         var2 |= 2;
      }

      if (this.field_210402_l) {
         var2 |= 4;
      }

      var1.writeByte(var2);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_210157_a(this);
   }

   public BlockPos func_210380_a() {
      return this.field_210391_a;
   }

   public TileEntityStructure.UpdateCommand func_210384_b() {
      return this.field_210392_b;
   }

   public StructureMode func_210378_c() {
      return this.field_210393_c;
   }

   public String func_210377_d() {
      return this.field_210394_d;
   }

   public BlockPos func_210383_e() {
      return this.field_210395_e;
   }

   public BlockPos func_210385_f() {
      return this.field_210396_f;
   }

   public Mirror func_210386_g() {
      return this.field_210397_g;
   }

   public Rotation func_210379_h() {
      return this.field_210398_h;
   }

   public String func_210388_i() {
      return this.field_210399_i;
   }

   public boolean func_210389_j() {
      return this.field_210400_j;
   }

   public boolean func_210390_k() {
      return this.field_210401_k;
   }

   public boolean func_210387_l() {
      return this.field_210402_l;
   }

   public float func_210382_m() {
      return this.field_210403_m;
   }

   public long func_210381_n() {
      return this.field_210404_n;
   }
}
