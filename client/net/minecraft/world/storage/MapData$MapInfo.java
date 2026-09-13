package net.minecraft.world.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class MapData$MapInfo {
   public final EntityPlayer field_76211_a;
   public int[] field_76209_b;
   public int[] field_76210_c;
   private int field_76208_e;
   private int field_76205_f;
   private byte[] field_76206_g;
   public int field_82569_d;
   private boolean field_82570_i;

   public MapData$MapInfo(MapData var1, EntityPlayer var2) {
      super();
      this.field_76207_d = var1;
      this.field_76209_b = new int[128];
      this.field_76210_c = new int[128];
      this.field_76211_a = var2;

      for(int var3 = 0; var3 < this.field_76209_b.length; ++var3) {
         this.field_76209_b[var3] = 0;
         this.field_76210_c[var3] = 127;
      }
   }

   public byte[] func_76204_a(ItemStack var1) {
      if (!this.field_82570_i) {
         byte[] var9 = new byte[]{2, this.field_76207_d.field_76197_d};
         this.field_82570_i = true;
         return var9;
      } else {
         if (--this.field_76205_f < 0) {
            this.field_76205_f = 4;
            byte[] var2 = new byte[this.field_76207_d.field_76203_h.size() * 3 + 1];
            var2[0] = 1;
            int var3 = 0;

            for(MapData$MapCoord var5 : this.field_76207_d.field_76203_h.values()) {
               var2[var3 * 3 + 1] = (byte)(var5.field_76216_a << 4 | var5.field_76212_d & 15);
               var2[var3 * 3 + 2] = var5.field_76214_b;
               var2[var3 * 3 + 3] = var5.field_76215_c;
               ++var3;
            }

            boolean var11 = !var1.func_82839_y();
            if (this.field_76206_g != null && this.field_76206_g.length == var2.length) {
               for(int var13 = 0; var13 < var2.length; ++var13) {
                  if (var2[var13] != this.field_76206_g[var13]) {
                     var11 = false;
                     break;
                  }
               }
            } else {
               var11 = false;
            }

            if (!var11) {
               this.field_76206_g = var2;
               return var2;
            }
         }

         for(int var8 = 0; var8 < 1; ++var8) {
            int var10 = this.field_76208_e++ * 11 % 128;
            if (this.field_76209_b[var10] >= 0) {
               int var12 = this.field_76210_c[var10] - this.field_76209_b[var10] + 1;
               int var14 = this.field_76209_b[var10];
               byte[] var6 = new byte[var12 + 3];
               var6[0] = 0;
               var6[1] = (byte)var10;
               var6[2] = (byte)var14;

               for(int var7 = 0; var7 < var6.length - 3; ++var7) {
                  var6[var7 + 3] = this.field_76207_d.field_76198_e[(var7 + var14) * 128 + var10];
               }

               this.field_76210_c[var10] = -1;
               this.field_76209_b[var10] = -1;
               return var6;
            }
         }

         return null;
      }
   }
}
