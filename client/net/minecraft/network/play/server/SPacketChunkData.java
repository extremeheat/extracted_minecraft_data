package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

public class SPacketChunkData implements Packet<INetHandlerPlayClient> {
   private int field_149284_a;
   private int field_149282_b;
   private int field_186948_c;
   private byte[] field_186949_d;
   private List<NBTTagCompound> field_189557_e;
   private boolean field_149279_g;

   public SPacketChunkData() {
      super();
   }

   public SPacketChunkData(Chunk var1, int var2) {
      super();
      this.field_149284_a = var1.field_76635_g;
      this.field_149282_b = var1.field_76647_h;
      this.field_149279_g = var2 == 65535;
      boolean var3 = var1.func_177412_p().field_73011_w.func_191066_m();
      this.field_186949_d = new byte[this.func_189556_a(var1, var3, var2)];
      this.field_186948_c = this.func_189555_a(new PacketBuffer(this.func_186945_f()), var1, var3, var2);
      this.field_189557_e = Lists.newArrayList();
      Iterator var4 = var1.func_177434_r().entrySet().iterator();

      while(true) {
         TileEntity var7;
         int var8;
         do {
            if (!var4.hasNext()) {
               return;
            }

            Entry var5 = (Entry)var4.next();
            BlockPos var6 = (BlockPos)var5.getKey();
            var7 = (TileEntity)var5.getValue();
            var8 = var6.func_177956_o() >> 4;
         } while(!this.func_149274_i() && (var2 & 1 << var8) == 0);

         NBTTagCompound var9 = var7.func_189517_E_();
         this.field_189557_e.add(var9);
      }
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149284_a = var1.readInt();
      this.field_149282_b = var1.readInt();
      this.field_149279_g = var1.readBoolean();
      this.field_186948_c = var1.func_150792_a();
      int var2 = var1.func_150792_a();
      if (var2 > 2097152) {
         throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
      } else {
         this.field_186949_d = new byte[var2];
         var1.readBytes(this.field_186949_d);
         int var3 = var1.func_150792_a();
         this.field_189557_e = Lists.newArrayList();

         for(int var4 = 0; var4 < var3; ++var4) {
            this.field_189557_e.add(var1.func_150793_b());
         }

      }
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeInt(this.field_149284_a);
      var1.writeInt(this.field_149282_b);
      var1.writeBoolean(this.field_149279_g);
      var1.func_150787_b(this.field_186948_c);
      var1.func_150787_b(this.field_186949_d.length);
      var1.writeBytes(this.field_186949_d);
      var1.func_150787_b(this.field_189557_e.size());
      Iterator var2 = this.field_189557_e.iterator();

      while(var2.hasNext()) {
         NBTTagCompound var3 = (NBTTagCompound)var2.next();
         var1.func_150786_a(var3);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147263_a(this);
   }

   public PacketBuffer func_186946_a() {
      return new PacketBuffer(Unpooled.wrappedBuffer(this.field_186949_d));
   }

   private ByteBuf func_186945_f() {
      ByteBuf var1 = Unpooled.wrappedBuffer(this.field_186949_d);
      var1.writerIndex(0);
      return var1;
   }

   public int func_189555_a(PacketBuffer var1, Chunk var2, boolean var3, int var4) {
      int var5 = 0;
      ChunkSection[] var6 = var2.func_76587_i();
      int var7 = 0;

      int var8;
      for(var8 = var6.length; var7 < var8; ++var7) {
         ChunkSection var9 = var6[var7];
         if (var9 != Chunk.field_186036_a && (!this.func_149274_i() || !var9.func_76663_a()) && (var4 & 1 << var7) != 0) {
            var5 |= 1 << var7;
            var9.func_186049_g().func_186009_b(var1);
            var1.writeBytes(var9.func_76661_k().func_177481_a());
            if (var3) {
               var1.writeBytes(var9.func_76671_l().func_177481_a());
            }
         }
      }

      if (this.func_149274_i()) {
         Biome[] var10 = var2.func_201590_e();

         for(var8 = 0; var8 < var10.length; ++var8) {
            var1.writeInt(IRegistry.field_212624_m.func_148757_b(var10[var8]));
         }
      }

      return var5;
   }

   protected int func_189556_a(Chunk var1, boolean var2, int var3) {
      int var4 = 0;
      ChunkSection[] var5 = var1.func_76587_i();
      int var6 = 0;

      for(int var7 = var5.length; var6 < var7; ++var6) {
         ChunkSection var8 = var5[var6];
         if (var8 != Chunk.field_186036_a && (!this.func_149274_i() || !var8.func_76663_a()) && (var3 & 1 << var6) != 0) {
            var4 += var8.func_186049_g().func_186018_a();
            var4 += var8.func_76661_k().func_177481_a().length;
            if (var2) {
               var4 += var8.func_76671_l().func_177481_a().length;
            }
         }
      }

      if (this.func_149274_i()) {
         var4 += var1.func_201590_e().length * 4;
      }

      return var4;
   }

   public int func_149273_e() {
      return this.field_149284_a;
   }

   public int func_149271_f() {
      return this.field_149282_b;
   }

   public int func_149276_g() {
      return this.field_186948_c;
   }

   public boolean func_149274_i() {
      return this.field_149279_g;
   }

   public List<NBTTagCompound> func_189554_f() {
      return this.field_189557_e;
   }
}
