package net.minecraft.server.management;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;

public class PlayerChunkMap {
   private static final Predicate<EntityPlayerMP> field_187308_a = (var0) -> {
      return var0 != null && !var0.func_175149_v();
   };
   private static final Predicate<EntityPlayerMP> field_187309_b = (var0) -> {
      return var0 != null && (!var0.func_175149_v() || var0.func_71121_q().func_82736_K().func_82766_b("spectatorsGenerateChunks"));
   };
   private final WorldServer field_72701_a;
   private final List<EntityPlayerMP> field_72699_b = Lists.newArrayList();
   private final Long2ObjectMap<PlayerChunkMapEntry> field_72700_c = new Long2ObjectOpenHashMap(4096);
   private final Set<PlayerChunkMapEntry> field_72697_d = Sets.newHashSet();
   private final List<PlayerChunkMapEntry> field_187310_g = Lists.newLinkedList();
   private final List<PlayerChunkMapEntry> field_187311_h = Lists.newLinkedList();
   private final List<PlayerChunkMapEntry> field_111193_e = Lists.newArrayList();
   private int field_72698_e;
   private long field_111192_g;
   private boolean field_187312_l = true;
   private boolean field_187313_m = true;

   public PlayerChunkMap(WorldServer var1) {
      super();
      this.field_72701_a = var1;
      this.func_152622_a(var1.func_73046_m().func_184103_al().func_72395_o());
   }

   public WorldServer func_72688_a() {
      return this.field_72701_a;
   }

   public Iterator<Chunk> func_187300_b() {
      final Iterator var1 = this.field_111193_e.iterator();
      return new AbstractIterator<Chunk>() {
         protected Chunk computeNext() {
            while(true) {
               if (var1.hasNext()) {
                  PlayerChunkMapEntry var1x = (PlayerChunkMapEntry)var1.next();
                  Chunk var2 = var1x.func_187266_f();
                  if (var2 == null) {
                     continue;
                  }

                  if (!var2.func_186035_j()) {
                     return var2;
                  }

                  if (!var1x.func_187271_a(128.0D, PlayerChunkMap.field_187308_a)) {
                     continue;
                  }

                  return var2;
               }

               return (Chunk)this.endOfData();
            }
         }

         // $FF: synthetic method
         protected Object computeNext() {
            return this.computeNext();
         }
      };
   }

   public void func_72693_b() {
      long var1 = this.field_72701_a.func_82737_E();
      int var3;
      PlayerChunkMapEntry var4;
      if (var1 - this.field_111192_g > 8000L) {
         this.field_111192_g = var1;

         for(var3 = 0; var3 < this.field_111193_e.size(); ++var3) {
            var4 = (PlayerChunkMapEntry)this.field_111193_e.get(var3);
            var4.func_187280_d();
            var4.func_187279_c();
         }
      }

      if (!this.field_72697_d.isEmpty()) {
         Iterator var9 = this.field_72697_d.iterator();

         while(var9.hasNext()) {
            var4 = (PlayerChunkMapEntry)var9.next();
            var4.func_187280_d();
         }

         this.field_72697_d.clear();
      }

      if (this.field_187312_l && var1 % 4L == 0L) {
         this.field_187312_l = false;
         Collections.sort(this.field_187311_h, (var0, var1x) -> {
            return ComparisonChain.start().compare(var0.func_187270_g(), var1x.func_187270_g()).result();
         });
      }

      if (this.field_187313_m && var1 % 4L == 2L) {
         this.field_187313_m = false;
         Collections.sort(this.field_187310_g, (var0, var1x) -> {
            return ComparisonChain.start().compare(var0.func_187270_g(), var1x.func_187270_g()).result();
         });
      }

      if (!this.field_187311_h.isEmpty()) {
         long var10 = Util.func_211178_c() + 50000000L;
         int var5 = 49;
         Iterator var6 = this.field_187311_h.iterator();

         while(var6.hasNext()) {
            PlayerChunkMapEntry var7 = (PlayerChunkMapEntry)var6.next();
            if (var7.func_187266_f() == null) {
               boolean var8 = var7.func_187269_a(field_187309_b);
               if (var7.func_187268_a(var8)) {
                  var6.remove();
                  if (var7.func_187272_b()) {
                     this.field_187310_g.remove(var7);
                  }

                  --var5;
                  if (var5 < 0 || Util.func_211178_c() > var10) {
                     break;
                  }
               }
            }
         }
      }

      if (!this.field_187310_g.isEmpty()) {
         var3 = 81;
         Iterator var11 = this.field_187310_g.iterator();

         while(var11.hasNext()) {
            PlayerChunkMapEntry var13 = (PlayerChunkMapEntry)var11.next();
            if (var13.func_187272_b()) {
               var11.remove();
               --var3;
               if (var3 < 0) {
                  break;
               }
            }
         }
      }

      if (this.field_72699_b.isEmpty()) {
         Dimension var12 = this.field_72701_a.field_73011_w;
         if (!var12.func_76567_e()) {
            this.field_72701_a.func_72863_F().func_73240_a();
         }
      }

   }

   public boolean func_152621_a(int var1, int var2) {
      long var3 = func_187307_d(var1, var2);
      return this.field_72700_c.get(var3) != null;
   }

   @Nullable
   public PlayerChunkMapEntry func_187301_b(int var1, int var2) {
      return (PlayerChunkMapEntry)this.field_72700_c.get(func_187307_d(var1, var2));
   }

   private PlayerChunkMapEntry func_187302_c(int var1, int var2) {
      long var3 = func_187307_d(var1, var2);
      PlayerChunkMapEntry var5 = (PlayerChunkMapEntry)this.field_72700_c.get(var3);
      if (var5 == null) {
         var5 = new PlayerChunkMapEntry(this, var1, var2);
         this.field_72700_c.put(var3, var5);
         this.field_111193_e.add(var5);
         if (var5.func_187266_f() == null) {
            this.field_187311_h.add(var5);
         }

         if (!var5.func_187272_b()) {
            this.field_187310_g.add(var5);
         }
      }

      return var5;
   }

   public void func_180244_a(BlockPos var1) {
      int var2 = var1.func_177958_n() >> 4;
      int var3 = var1.func_177952_p() >> 4;
      PlayerChunkMapEntry var4 = this.func_187301_b(var2, var3);
      if (var4 != null) {
         var4.func_187265_a(var1.func_177958_n() & 15, var1.func_177956_o(), var1.func_177952_p() & 15);
      }

   }

   public void func_72683_a(EntityPlayerMP var1) {
      int var2 = (int)var1.field_70165_t >> 4;
      int var3 = (int)var1.field_70161_v >> 4;
      var1.field_71131_d = var1.field_70165_t;
      var1.field_71132_e = var1.field_70161_v;

      for(int var4 = var2 - this.field_72698_e; var4 <= var2 + this.field_72698_e; ++var4) {
         for(int var5 = var3 - this.field_72698_e; var5 <= var3 + this.field_72698_e; ++var5) {
            this.func_187302_c(var4, var5).func_187276_a(var1);
         }
      }

      this.field_72699_b.add(var1);
      this.func_187306_e();
   }

   public void func_72695_c(EntityPlayerMP var1) {
      int var2 = (int)var1.field_71131_d >> 4;
      int var3 = (int)var1.field_71132_e >> 4;

      for(int var4 = var2 - this.field_72698_e; var4 <= var2 + this.field_72698_e; ++var4) {
         for(int var5 = var3 - this.field_72698_e; var5 <= var3 + this.field_72698_e; ++var5) {
            PlayerChunkMapEntry var6 = this.func_187301_b(var4, var5);
            if (var6 != null) {
               var6.func_187277_b(var1);
            }
         }
      }

      this.field_72699_b.remove(var1);
      this.func_187306_e();
   }

   private boolean func_72684_a(int var1, int var2, int var3, int var4, int var5) {
      int var6 = var1 - var3;
      int var7 = var2 - var4;
      if (var6 >= -var5 && var6 <= var5) {
         return var7 >= -var5 && var7 <= var5;
      } else {
         return false;
      }
   }

   public void func_72685_d(EntityPlayerMP var1) {
      int var2 = (int)var1.field_70165_t >> 4;
      int var3 = (int)var1.field_70161_v >> 4;
      double var4 = var1.field_71131_d - var1.field_70165_t;
      double var6 = var1.field_71132_e - var1.field_70161_v;
      double var8 = var4 * var4 + var6 * var6;
      if (var8 >= 64.0D) {
         int var10 = (int)var1.field_71131_d >> 4;
         int var11 = (int)var1.field_71132_e >> 4;
         int var12 = this.field_72698_e;
         int var13 = var2 - var10;
         int var14 = var3 - var11;
         if (var13 != 0 || var14 != 0) {
            for(int var15 = var2 - var12; var15 <= var2 + var12; ++var15) {
               for(int var16 = var3 - var12; var16 <= var3 + var12; ++var16) {
                  if (!this.func_72684_a(var15, var16, var10, var11, var12)) {
                     this.func_187302_c(var15, var16).func_187276_a(var1);
                  }

                  if (!this.func_72684_a(var15 - var13, var16 - var14, var2, var3, var12)) {
                     PlayerChunkMapEntry var17 = this.func_187301_b(var15 - var13, var16 - var14);
                     if (var17 != null) {
                        var17.func_187277_b(var1);
                     }
                  }
               }
            }

            var1.field_71131_d = var1.field_70165_t;
            var1.field_71132_e = var1.field_70161_v;
            this.func_187306_e();
         }
      }
   }

   public boolean func_72694_a(EntityPlayerMP var1, int var2, int var3) {
      PlayerChunkMapEntry var4 = this.func_187301_b(var2, var3);
      return var4 != null && var4.func_187275_d(var1) && var4.func_187274_e();
   }

   public void func_152622_a(int var1) {
      var1 = MathHelper.func_76125_a(var1, 3, 32);
      if (var1 != this.field_72698_e) {
         int var2 = var1 - this.field_72698_e;
         ArrayList var3 = Lists.newArrayList(this.field_72699_b);
         Iterator var4 = var3.iterator();

         while(true) {
            while(var4.hasNext()) {
               EntityPlayerMP var5 = (EntityPlayerMP)var4.next();
               int var6 = (int)var5.field_70165_t >> 4;
               int var7 = (int)var5.field_70161_v >> 4;
               int var8;
               int var9;
               if (var2 > 0) {
                  for(var8 = var6 - var1; var8 <= var6 + var1; ++var8) {
                     for(var9 = var7 - var1; var9 <= var7 + var1; ++var9) {
                        PlayerChunkMapEntry var10 = this.func_187302_c(var8, var9);
                        if (!var10.func_187275_d(var5)) {
                           var10.func_187276_a(var5);
                        }
                     }
                  }
               } else {
                  for(var8 = var6 - this.field_72698_e; var8 <= var6 + this.field_72698_e; ++var8) {
                     for(var9 = var7 - this.field_72698_e; var9 <= var7 + this.field_72698_e; ++var9) {
                        if (!this.func_72684_a(var8, var9, var6, var7, var1)) {
                           this.func_187302_c(var8, var9).func_187277_b(var5);
                        }
                     }
                  }
               }
            }

            this.field_72698_e = var1;
            this.func_187306_e();
            return;
         }
      }
   }

   private void func_187306_e() {
      this.field_187312_l = true;
      this.field_187313_m = true;
   }

   public static int func_72686_a(int var0) {
      return var0 * 16 - 16;
   }

   private static long func_187307_d(int var0, int var1) {
      return (long)var0 + 2147483647L | (long)var1 + 2147483647L << 32;
   }

   public void func_187304_a(PlayerChunkMapEntry var1) {
      this.field_72697_d.add(var1);
   }

   public void func_187305_b(PlayerChunkMapEntry var1) {
      ChunkPos var2 = var1.func_187264_a();
      long var3 = func_187307_d(var2.field_77276_a, var2.field_77275_b);
      var1.func_187279_c();
      this.field_72700_c.remove(var3);
      this.field_111193_e.remove(var1);
      this.field_72697_d.remove(var1);
      this.field_187310_g.remove(var1);
      this.field_187311_h.remove(var1);
      Chunk var5 = var1.func_187266_f();
      if (var5 != null) {
         this.func_72688_a().func_72863_F().func_189549_a(var5);
      }

   }
}
