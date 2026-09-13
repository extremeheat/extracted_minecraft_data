package net.minecraft.world.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class MapData extends WorldSavedData {
   public int field_76201_a;
   public int field_76199_b;
   public byte field_76200_c;
   public byte field_76197_d;
   public byte[] field_76198_e = new byte[16384];
   public List field_76196_g = new ArrayList();
   private Map field_76202_j = new HashMap();
   public Map field_76203_h = new LinkedHashMap();

   public MapData(String var1) {
      super(var1);
   }

   @Override
   public void func_76184_a(NBTTagCompound var1) {
      this.field_76200_c = var1.func_74771_c("dimension");
      this.field_76201_a = var1.func_74762_e("xCenter");
      this.field_76199_b = var1.func_74762_e("zCenter");
      this.field_76197_d = var1.func_74771_c("scale");
      if (this.field_76197_d < 0) {
         this.field_76197_d = 0;
      }

      if (this.field_76197_d > 4) {
         this.field_76197_d = 4;
      }

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

   @Override
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
         MapData$MapInfo var3 = new MapData$MapInfo(this, var1);
         this.field_76202_j.put(var1, var3);
         this.field_76196_g.add(var3);
      }

      if (!var1.field_71071_by.func_70431_c(var2)) {
         this.field_76203_h.remove(var1.func_70005_c_());
      }

      for(int var5 = 0; var5 < this.field_76196_g.size(); ++var5) {
         MapData$MapInfo var4 = (MapData$MapInfo)this.field_76196_g.get(var5);
         if (!var4.field_76211_a.field_70128_L && (var4.field_76211_a.field_71071_by.func_70431_c(var2) || var2.func_82839_y())) {
            if (!var2.func_82839_y() && var4.field_76211_a.field_71093_bK == this.field_76200_c) {
               this.func_82567_a(
                  0,
                  var4.field_76211_a.field_70170_p,
                  var4.field_76211_a.func_70005_c_(),
                  var4.field_76211_a.field_70165_t,
                  var4.field_76211_a.field_70161_v,
                  (double)var4.field_76211_a.field_70177_z
               );
            }
         } else {
            this.field_76202_j.remove(var4.field_76211_a);
            this.field_76196_g.remove(var4);
         }
      }

      if (var2.func_82839_y()) {
         this.func_82567_a(
            1,
            var1.field_70170_p,
            "frame-" + var2.func_82836_z().func_145782_y(),
            (double)var2.func_82836_z().field_146063_b,
            (double)var2.func_82836_z().field_146062_d,
            (double)(var2.func_82836_z().field_82332_a * 90)
         );
      }
   }

   private void func_82567_a(int var1, World var2, String var3, double var4, double var6, double var8) {
      int var10 = 1 << this.field_76197_d;
      float var11 = (float)(var4 - (double)this.field_76201_a) / (float)var10;
      float var12 = (float)(var6 - (double)this.field_76199_b) / (float)var10;
      byte var13 = (byte)((int)((double)(var11 * 2.0F) + 0.5));
      byte var14 = (byte)((int)((double)(var12 * 2.0F) + 0.5));
      byte var16 = 63;
      byte var15;
      if (var11 >= (float)(-var16) && var12 >= (float)(-var16) && var11 <= (float)var16 && var12 <= (float)var16) {
         var8 += var8 < 0.0 ? -8.0 : 8.0;
         var15 = (byte)((int)(var8 * 16.0 / 360.0));
         if (this.field_76200_c < 0) {
            int var17 = (int)(var2.func_72912_H().func_76073_f() / 10L);
            var15 = (byte)(var17 * var17 * 34187121 + var17 * 121 >> 15 & 15);
         }
      } else {
         if (!(Math.abs(var11) < 320.0F) || !(Math.abs(var12) < 320.0F)) {
            this.field_76203_h.remove(var3);
            return;
         }

         var1 = 6;
         var15 = 0;
         if (var11 <= (float)(-var16)) {
            var13 = (byte)((int)((double)(var16 * 2) + 2.5));
         }

         if (var12 <= (float)(-var16)) {
            var14 = (byte)((int)((double)(var16 * 2) + 2.5));
         }

         if (var11 >= (float)var16) {
            var13 = (byte)(var16 * 2 + 1);
         }

         if (var12 >= (float)var16) {
            var14 = (byte)(var16 * 2 + 1);
         }
      }

      this.field_76203_h.put(var3, new MapData$MapCoord(this, (byte)var1, var13, var14, var15));
   }

   public byte[] func_76193_a(ItemStack var1, World var2, EntityPlayer var3) {
      MapData$MapInfo var4 = (MapData$MapInfo)this.field_76202_j.get(var3);
      return var4 == null ? null : var4.func_76204_a(var1);
   }

   public void func_76194_a(int var1, int var2, int var3) {
      super.func_76185_a();

      for(int var4 = 0; var4 < this.field_76196_g.size(); ++var4) {
         MapData$MapInfo var5 = (MapData$MapInfo)this.field_76196_g.get(var4);
         if (var5.field_76209_b[var1] < 0 || var5.field_76209_b[var1] > var2) {
            var5.field_76209_b[var1] = var2;
         }

         if (var5.field_76210_c[var1] < 0 || var5.field_76210_c[var1] < var3) {
            var5.field_76210_c[var1] = var3;
         }
      }
   }

   public void func_76192_a(byte[] var1) {
      if (var1[0] == 0) {
         int var2 = var1[1] & 255;
         int var3 = var1[2] & 255;

         for(int var4 = 0; var4 < var1.length - 3; ++var4) {
            this.field_76198_e[(var4 + var3) * 128 + var2] = var1[var4 + 3];
         }

         this.func_76185_a();
      } else if (var1[0] == 1) {
         this.field_76203_h.clear();

         for(int var7 = 0; var7 < (var1.length - 1) / 3; ++var7) {
            byte var8 = (byte)(var1[var7 * 3 + 1] >> 4);
            byte var9 = var1[var7 * 3 + 2];
            byte var5 = var1[var7 * 3 + 3];
            byte var6 = (byte)(var1[var7 * 3 + 1] & 15);
            this.field_76203_h.put("icon-" + var7, new MapData$MapCoord(this, var8, var9, var5, var6));
         }
      } else if (var1[0] == 2) {
         this.field_76197_d = var1[1];
      }
   }

   public MapData$MapInfo func_82568_a(EntityPlayer var1) {
      MapData$MapInfo var2 = (MapData$MapInfo)this.field_76202_j.get(var1);
      if (var2 == null) {
         var2 = new MapData$MapInfo(this, var1);
         this.field_76202_j.put(var1, var2);
         this.field_76196_g.add(var2);
      }

      return var2;
   }
}
