package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec4b;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class MapData extends WorldSavedData {
   public int field_76201_a;
   public int field_76199_b;
   public byte field_76200_c;
   public byte field_76197_d;
   public byte[] field_76198_e = new byte[16384];
   public List<MapData.MapInfo> field_76196_g = Lists.newArrayList();
   private Map<EntityPlayer, MapData.MapInfo> field_76202_j = Maps.newHashMap();
   public Map<String, Vec4b> field_76203_h = Maps.newLinkedHashMap();

   public MapData(String var1) {
      super(var1);
   }

   public void func_176054_a(double var1, double var3, int var5) {
      int var6 = 128 * (1 << var5);
      int var7 = MathHelper.func_76128_c((var1 + 64.0D) / (double)var6);
      int var8 = MathHelper.func_76128_c((var3 + 64.0D) / (double)var6);
      this.field_76201_a = var7 * var6 + var6 / 2 - 64;
      this.field_76199_b = var8 * var6 + var6 / 2 - 64;
   }

   public void func_76184_a(NBTTagCompound var1) {
      this.field_76200_c = var1.func_74771_c("dimension");
      this.field_76201_a = var1.func_74762_e("xCenter");
      this.field_76199_b = var1.func_74762_e("zCenter");
      this.field_76197_d = var1.func_74771_c("scale");
      this.field_76197_d = (byte)MathHelper.func_76125_a(this.field_76197_d, 0, 4);
      short var2 = var1.func_74765_d("width");
      short var3 = var1.func_74765_d("height");
      if (var2 == 128 && var3 == 128) {
         this.field_76198_e = var1.func_74770_j("colors");
      } else {
         byte[] var4 = var1.func_74770_j("colors");
         this.field_76198_e = new byte[16384];
         int var5 = (128 - var2) / 2;
         int var6 = (128 - var3) / 2;

         for(int var7 = 0; var7 < var3; ++var7) {
            int var8 = var7 + var6;
            if (var8 >= 0 || var8 < 128) {
               for(int var9 = 0; var9 < var2; ++var9) {
                  int var10 = var9 + var5;
                  if (var10 >= 0 || var10 < 128) {
                     this.field_76198_e[var10 + var8 * 128] = var4[var9 + var7 * var2];
                  }
               }
            }
         }
      }

   }

   public void func_76187_b(NBTTagCompound var1) {
      var1.func_74774_a("dimension", this.field_76200_c);
      var1.func_74768_a("xCenter", this.field_76201_a);
      var1.func_74768_a("zCenter", this.field_76199_b);
      var1.func_74774_a("scale", this.field_76197_d);
      var1.func_74777_a("width", (short)128);
      var1.func_74777_a("height", (short)128);
      var1.func_74773_a("colors", this.field_76198_e);
   }

   public void func_76191_a(EntityPlayer var1, ItemStack var2) {
      if (!this.field_76202_j.containsKey(var1)) {
         MapData.MapInfo var3 = new MapData.MapInfo(var1);
         this.field_76202_j.put(var1, var3);
         this.field_76196_g.add(var3);
      }

      if (!var1.field_71071_by.func_70431_c(var2)) {
         this.field_76203_h.remove(var1.func_70005_c_());
      }

      for(int var6 = 0; var6 < this.field_76196_g.size(); ++var6) {
         MapData.MapInfo var4 = (MapData.MapInfo)this.field_76196_g.get(var6);
         if (var4.field_76211_a.field_70128_L || !var4.field_76211_a.field_71071_by.func_70431_c(var2) && !var2.func_82839_y()) {
            this.field_76202_j.remove(var4.field_76211_a);
            this.field_76196_g.remove(var4);
         } else if (!var2.func_82839_y() && var4.field_76211_a.field_71093_bK == this.field_76200_c) {
            this.func_82567_a(0, var4.field_76211_a.field_70170_p, var4.field_76211_a.func_70005_c_(), var4.field_76211_a.field_70165_t, var4.field_76211_a.field_70161_v, (double)var4.field_76211_a.field_70177_z);
         }
      }

      if (var2.func_82839_y()) {
         EntityItemFrame var7 = var2.func_82836_z();
         BlockPos var8 = var7.func_174857_n();
         this.func_82567_a(1, var1.field_70170_p, "frame-" + var7.func_145782_y(), (double)var8.func_177958_n(), (double)var8.func_177952_p(), (double)(var7.field_174860_b.func_176736_b() * 90));
      }

      if (var2.func_77942_o() && var2.func_77978_p().func_150297_b("Decorations", 9)) {
         NBTTagList var9 = var2.func_77978_p().func_150295_c("Decorations", 10);

         for(int var10 = 0; var10 < var9.func_74745_c(); ++var10) {
            NBTTagCompound var5 = var9.func_150305_b(var10);
            if (!this.field_76203_h.containsKey(var5.func_74779_i("id"))) {
               this.func_82567_a(var5.func_74771_c("type"), var1.field_70170_p, var5.func_74779_i("id"), var5.func_74769_h("x"), var5.func_74769_h("z"), var5.func_74769_h("rot"));
            }
         }
      }

   }

   private void func_82567_a(int var1, World var2, String var3, double var4, double var6, double var8) {
      int var10 = 1 << this.field_76197_d;
      float var11 = (float)(var4 - (double)this.field_76201_a) / (float)var10;
      float var12 = (float)(var6 - (double)this.field_76199_b) / (float)var10;
      byte var13 = (byte)((int)((double)(var11 * 2.0F) + 0.5D));
      byte var14 = (byte)((int)((double)(var12 * 2.0F) + 0.5D));
      byte var16 = 63;
      byte var15;
      if (var11 >= (float)(-var16) && var12 >= (float)(-var16) && var11 <= (float)var16 && var12 <= (float)var16) {
         var8 += var8 < 0.0D ? -8.0D : 8.0D;
         var15 = (byte)((int)(var8 * 16.0D / 360.0D));
         if (this.field_76200_c < 0) {
            int var17 = (int)(var2.func_72912_H().func_76073_f() / 10L);
            var15 = (byte)(var17 * var17 * 34187121 + var17 * 121 >> 15 & 15);
         }
      } else {
         if (Math.abs(var11) >= 320.0F || Math.abs(var12) >= 320.0F) {
            this.field_76203_h.remove(var3);
            return;
         }

         var1 = 6;
         var15 = 0;
         if (var11 <= (float)(-var16)) {
            var13 = (byte)((int)((double)(var16 * 2) + 2.5D));
         }

         if (var12 <= (float)(-var16)) {
            var14 = (byte)((int)((double)(var16 * 2) + 2.5D));
         }

         if (var11 >= (float)var16) {
            var13 = (byte)(var16 * 2 + 1);
         }

         if (var12 >= (float)var16) {
            var14 = (byte)(var16 * 2 + 1);
         }
      }

      this.field_76203_h.put(var3, new Vec4b((byte)var1, var13, var14, var15));
   }

   public Packet func_176052_a(ItemStack var1, World var2, EntityPlayer var3) {
      MapData.MapInfo var4 = (MapData.MapInfo)this.field_76202_j.get(var3);
      return var4 == null ? null : var4.func_176101_a(var1);
   }

   public void func_176053_a(int var1, int var2) {
      super.func_76185_a();
      Iterator var3 = this.field_76196_g.iterator();

      while(var3.hasNext()) {
         MapData.MapInfo var4 = (MapData.MapInfo)var3.next();
         var4.func_176102_a(var1, var2);
      }

   }

   public MapData.MapInfo func_82568_a(EntityPlayer var1) {
      MapData.MapInfo var2 = (MapData.MapInfo)this.field_76202_j.get(var1);
      if (var2 == null) {
         var2 = new MapData.MapInfo(var1);
         this.field_76202_j.put(var1, var2);
         this.field_76196_g.add(var2);
      }

      return var2;
   }

   public class MapInfo {
      public final EntityPlayer field_76211_a;
      private boolean field_176105_d = true;
      private int field_176106_e = 0;
      private int field_176103_f = 0;
      private int field_176104_g = 127;
      private int field_176108_h = 127;
      private int field_176109_i;
      public int field_82569_d;

      public MapInfo(EntityPlayer var2) {
         super();
         this.field_76211_a = var2;
      }

      public Packet func_176101_a(ItemStack var1) {
         if (this.field_176105_d) {
            this.field_176105_d = false;
            return new S34PacketMaps(var1.func_77960_j(), MapData.this.field_76197_d, MapData.this.field_76203_h.values(), MapData.this.field_76198_e, this.field_176106_e, this.field_176103_f, this.field_176104_g + 1 - this.field_176106_e, this.field_176108_h + 1 - this.field_176103_f);
         } else {
            return this.field_176109_i++ % 5 == 0 ? new S34PacketMaps(var1.func_77960_j(), MapData.this.field_76197_d, MapData.this.field_76203_h.values(), MapData.this.field_76198_e, 0, 0, 0, 0) : null;
         }
      }

      public void func_176102_a(int var1, int var2) {
         if (this.field_176105_d) {
            this.field_176106_e = Math.min(this.field_176106_e, var1);
            this.field_176103_f = Math.min(this.field_176103_f, var2);
            this.field_176104_g = Math.max(this.field_176104_g, var1);
            this.field_176108_h = Math.max(this.field_176108_h, var2);
         } else {
            this.field_176105_d = true;
            this.field_176106_e = var1;
            this.field_176103_f = var2;
            this.field_176104_g = var1;
            this.field_176108_h = var2;
         }

      }
   }
}
