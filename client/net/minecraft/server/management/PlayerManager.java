package net.minecraft.server.management;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerManager {
   private static final Logger field_152627_a = LogManager.getLogger();
   private final WorldServer field_72701_a;
   private final List field_72699_b = new ArrayList();
   private final LongHashMap field_72700_c = new LongHashMap();
   private final List field_72697_d = new ArrayList();
   private final List field_111193_e = new ArrayList();
   private int field_72698_e;
   private long field_111192_g;
   private final int[][] field_72696_f = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

   public PlayerManager(WorldServer var1) {
      super();
      this.field_72701_a = var1;
      this.func_152622_a(var1.func_73046_m().func_71203_ab().func_72395_o());
   }

   public WorldServer func_72688_a() {
      return this.field_72701_a;
   }

   public void func_72693_b() {
      long var1 = this.field_72701_a.func_82737_E();
      if (var1 - this.field_111192_g > 8000L) {
         this.field_111192_g = var1;

         for(int var3 = 0; var3 < this.field_111193_e.size(); ++var3) {
            PlayerManager$PlayerInstance var4 = (PlayerManager$PlayerInstance)this.field_111193_e.get(var3);
            var4.func_73254_a();
            var4.func_111194_a();
         }
      } else {
         for(int var5 = 0; var5 < this.field_72697_d.size(); ++var5) {
            PlayerManager$PlayerInstance var7 = (PlayerManager$PlayerInstance)this.field_72697_d.get(var5);
            var7.func_73254_a();
         }
      }

      this.field_72697_d.clear();
      if (this.field_72699_b.isEmpty()) {
         WorldProvider var6 = this.field_72701_a.field_73011_w;
         if (!var6.func_76567_e()) {
            this.field_72701_a.field_73059_b.func_73240_a();
         }
      }
   }

   public boolean func_152621_a(int var1, int var2) {
      long var3 = (long)var1 + 2147483647L | (long)var2 + 2147483647L << 32;
      return this.field_72700_c.func_76164_a(var3) != null;
   }

   private PlayerManager$PlayerInstance func_72690_a(int var1, int var2, boolean var3) {
      long var4 = (long)var1 + 2147483647L | (long)var2 + 2147483647L << 32;
      PlayerManager$PlayerInstance var6 = (PlayerManager$PlayerInstance)this.field_72700_c.func_76164_a(var4);
      if (var6 == null && var3) {
         var6 = new PlayerManager$PlayerInstance(this, var1, var2);
         this.field_72700_c.func_76163_a(var4, var6);
         this.field_111193_e.add(var6);
      }

      return var6;
   }

   public void func_151250_a(int var1, int var2, int var3) {
      int var4 = var1 >> 4;
      int var5 = var3 >> 4;
      PlayerManager$PlayerInstance var6 = this.func_72690_a(var4, var5, false);
      if (var6 != null) {
         var6.func_151253_a(var1 & 15, var2, var3 & 15);
      }
   }

   public void func_72683_a(EntityPlayerMP var1) {
      int var2 = (int)var1.field_70165_t >> 4;
      int var3 = (int)var1.field_70161_v >> 4;
      var1.field_71131_d = var1.field_70165_t;
      var1.field_71132_e = var1.field_70161_v;

      for(int var4 = var2 - this.field_72698_e; var4 <= var2 + this.field_72698_e; ++var4) {
         for(int var5 = var3 - this.field_72698_e; var5 <= var3 + this.field_72698_e; ++var5) {
            this.func_72690_a(var4, var5, true).func_73255_a(var1);
         }
      }

      this.field_72699_b.add(var1);
      this.func_72691_b(var1);
   }

   public void func_72691_b(EntityPlayerMP var1) {
      ArrayList var2 = new ArrayList(var1.field_71129_f);
      int var3 = 0;
      int var4 = this.field_72698_e;
      int var5 = (int)var1.field_70165_t >> 4;
      int var6 = (int)var1.field_70161_v >> 4;
      int var7 = 0;
      int var8 = 0;
      ChunkCoordIntPair var9 = PlayerManager$PlayerInstance.access$500(this.func_72690_a(var5, var6, true));
      var1.field_71129_f.clear();
      if (var2.contains(var9)) {
         var1.field_71129_f.add(var9);
      }

      for(int var10 = 1; var10 <= var4 * 2; ++var10) {
         for(int var11 = 0; var11 < 2; ++var11) {
            int[] var12 = this.field_72696_f[var3++ % 4];

            for(int var13 = 0; var13 < var10; ++var13) {
               var7 += var12[0];
               var8 += var12[1];
               var9 = PlayerManager$PlayerInstance.access$500(this.func_72690_a(var5 + var7, var6 + var8, true));
               if (var2.contains(var9)) {
                  var1.field_71129_f.add(var9);
               }
            }
         }
      }

      var3 %= 4;

      for(int var17 = 0; var17 < var4 * 2; ++var17) {
         var7 += this.field_72696_f[var3][0];
         var8 += this.field_72696_f[var3][1];
         var9 = PlayerManager$PlayerInstance.access$500(this.func_72690_a(var5 + var7, var6 + var8, true));
         if (var2.contains(var9)) {
            var1.field_71129_f.add(var9);
         }
      }
   }

   public void func_72695_c(EntityPlayerMP var1) {
      int var2 = (int)var1.field_71131_d >> 4;
      int var3 = (int)var1.field_71132_e >> 4;

      for(int var4 = var2 - this.field_72698_e; var4 <= var2 + this.field_72698_e; ++var4) {
         for(int var5 = var3 - this.field_72698_e; var5 <= var3 + this.field_72698_e; ++var5) {
            PlayerManager$PlayerInstance var6 = this.func_72690_a(var4, var5, false);
            if (var6 != null) {
               var6.func_73252_b(var1);
            }
         }
      }

      this.field_72699_b.remove(var1);
   }

   private boolean func_72684_a(int var1, int var2, int var3, int var4, int var5) {
      int var6 = var1 - var3;
      int var7 = var2 - var4;
      if (var6 < -var5 || var6 > var5) {
         return false;
      } else {
         return var7 >= -var5 && var7 <= var5;
      }
   }

   public void func_72685_d(EntityPlayerMP var1) {
      int var2 = (int)var1.field_70165_t >> 4;
      int var3 = (int)var1.field_70161_v >> 4;
      double var4 = var1.field_71131_d - var1.field_70165_t;
      double var6 = var1.field_71132_e - var1.field_70161_v;
      double var8 = var4 * var4 + var6 * var6;
      if (!(var8 < 64.0)) {
         int var10 = (int)var1.field_71131_d >> 4;
         int var11 = (int)var1.field_71132_e >> 4;
         int var12 = this.field_72698_e;
         int var13 = var2 - var10;
         int var14 = var3 - var11;
         if (var13 != 0 || var14 != 0) {
            for(int var15 = var2 - var12; var15 <= var2 + var12; ++var15) {
               for(int var16 = var3 - var12; var16 <= var3 + var12; ++var16) {
                  if (!this.func_72684_a(var15, var16, var10, var11, var12)) {
                     this.func_72690_a(var15, var16, true).func_73255_a(var1);
                  }

                  if (!this.func_72684_a(var15 - var13, var16 - var14, var2, var3, var12)) {
                     PlayerManager$PlayerInstance var17 = this.func_72690_a(var15 - var13, var16 - var14, false);
                     if (var17 != null) {
                        var17.func_73252_b(var1);
                     }
                  }
               }
            }

            this.func_72691_b(var1);
            var1.field_71131_d = var1.field_70165_t;
            var1.field_71132_e = var1.field_70161_v;
         }
      }
   }

   public boolean func_72694_a(EntityPlayerMP var1, int var2, int var3) {
      PlayerManager$PlayerInstance var4 = this.func_72690_a(var2, var3, false);
      return var4 != null
         && PlayerManager$PlayerInstance.access$600(var4).contains(var1)
         && !var1.field_71129_f.contains(PlayerManager$PlayerInstance.access$500(var4));
   }

   public void func_152622_a(int var1) {
      var1 = MathHelper.func_76125_a(var1, 3, 20);
      if (var1 != this.field_72698_e) {
         int var2 = var1 - this.field_72698_e;

         for(EntityPlayerMP var4 : this.field_72699_b) {
            int var5 = (int)var4.field_70165_t >> 4;
            int var6 = (int)var4.field_70161_v >> 4;
            if (var2 > 0) {
               for(int var11 = var5 - var1; var11 <= var5 + var1; ++var11) {
                  for(int var12 = var6 - var1; var12 <= var6 + var1; ++var12) {
                     PlayerManager$PlayerInstance var9 = this.func_72690_a(var11, var12, true);
                     if (!PlayerManager$PlayerInstance.access$600(var9).contains(var4)) {
                        var9.func_73255_a(var4);
                     }
                  }
               }
            } else {
               for(int var7 = var5 - this.field_72698_e; var7 <= var5 + this.field_72698_e; ++var7) {
                  for(int var8 = var6 - this.field_72698_e; var8 <= var6 + this.field_72698_e; ++var8) {
                     if (!this.func_72684_a(var7, var8, var5, var6, var1)) {
                        this.func_72690_a(var7, var8, true).func_73252_b(var4);
                     }
                  }
               }
            }
         }

         this.field_72698_e = var1;
      }
   }

   public static int func_72686_a(int var0) {
      return var0 * 16 - 16;
   }
}
