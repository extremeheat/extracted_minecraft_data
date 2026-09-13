package net.minecraft.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public class BlockRailBase$Rail {
   private World field_150660_b;
   private int field_150661_c;
   private int field_150658_d;
   private int field_150659_e;
   private final boolean field_150656_f;
   private List field_150657_g;

   public BlockRailBase$Rail(BlockRailBase var1, World var2, int var3, int var4, int var5) {
      super();
      this.field_150662_a = var1;
      this.field_150657_g = new ArrayList();
      this.field_150660_b = var2;
      this.field_150661_c = var3;
      this.field_150658_d = var4;
      this.field_150659_e = var5;
      Block var6 = var2.func_147439_a(var3, var4, var5);
      int var7 = var2.func_72805_g(var3, var4, var5);
      if (((BlockRailBase)var6).field_150053_a) {
         this.field_150656_f = true;
         var7 &= -9;
      } else {
         this.field_150656_f = false;
      }

      this.func_150648_a(var7);
   }

   private void func_150648_a(int var1) {
      this.field_150657_g.clear();
      if (var1 == 0) {
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1));
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1));
      } else if (var1 == 1) {
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e));
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e));
      } else if (var1 == 2) {
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e));
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c + 1, this.field_150658_d + 1, this.field_150659_e));
      } else if (var1 == 3) {
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c - 1, this.field_150658_d + 1, this.field_150659_e));
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e));
      } else if (var1 == 4) {
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d + 1, this.field_150659_e - 1));
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1));
      } else if (var1 == 5) {
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1));
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d + 1, this.field_150659_e + 1));
      } else if (var1 == 6) {
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e));
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1));
      } else if (var1 == 7) {
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e));
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1));
      } else if (var1 == 8) {
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e));
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1));
      } else if (var1 == 9) {
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e));
         this.field_150657_g.add(new ChunkPosition(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1));
      }
   }

   private void func_150651_b() {
      for(int var1 = 0; var1 < this.field_150657_g.size(); ++var1) {
         BlockRailBase$Rail var2 = this.func_150654_a((ChunkPosition)this.field_150657_g.get(var1));
         if (var2 != null && var2.func_150653_a(this)) {
            this.field_150657_g.set(var1, new ChunkPosition(var2.field_150661_c, var2.field_150658_d, var2.field_150659_e));
         } else {
            this.field_150657_g.remove(var1--);
         }
      }
   }

   private boolean func_150646_a(int var1, int var2, int var3) {
      if (BlockRailBase.func_150049_b_(this.field_150660_b, var1, var2, var3)) {
         return true;
      } else if (BlockRailBase.func_150049_b_(this.field_150660_b, var1, var2 + 1, var3)) {
         return true;
      } else {
         return BlockRailBase.func_150049_b_(this.field_150660_b, var1, var2 - 1, var3);
      }
   }

   private BlockRailBase$Rail func_150654_a(ChunkPosition var1) {
      if (BlockRailBase.func_150049_b_(this.field_150660_b, var1.field_151329_a, var1.field_151327_b, var1.field_151328_c)) {
         return new BlockRailBase$Rail(this.field_150662_a, this.field_150660_b, var1.field_151329_a, var1.field_151327_b, var1.field_151328_c);
      } else if (BlockRailBase.func_150049_b_(this.field_150660_b, var1.field_151329_a, var1.field_151327_b + 1, var1.field_151328_c)) {
         return new BlockRailBase$Rail(this.field_150662_a, this.field_150660_b, var1.field_151329_a, var1.field_151327_b + 1, var1.field_151328_c);
      } else {
         return BlockRailBase.func_150049_b_(this.field_150660_b, var1.field_151329_a, var1.field_151327_b - 1, var1.field_151328_c)
            ? new BlockRailBase$Rail(this.field_150662_a, this.field_150660_b, var1.field_151329_a, var1.field_151327_b - 1, var1.field_151328_c)
            : null;
      }
   }

   private boolean func_150653_a(BlockRailBase$Rail var1) {
      for(int var2 = 0; var2 < this.field_150657_g.size(); ++var2) {
         ChunkPosition var3 = (ChunkPosition)this.field_150657_g.get(var2);
         if (var3.field_151329_a == var1.field_150661_c && var3.field_151328_c == var1.field_150659_e) {
            return true;
         }
      }

      return false;
   }

   private boolean func_150652_b(int var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.field_150657_g.size(); ++var4) {
         ChunkPosition var5 = (ChunkPosition)this.field_150657_g.get(var4);
         if (var5.field_151329_a == var1 && var5.field_151328_c == var3) {
            return true;
         }
      }

      return false;
   }

   protected int func_150650_a() {
      int var1 = 0;
      if (this.func_150646_a(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1)) {
         ++var1;
      }

      if (this.func_150646_a(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1)) {
         ++var1;
      }

      if (this.func_150646_a(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e)) {
         ++var1;
      }

      if (this.func_150646_a(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e)) {
         ++var1;
      }

      return var1;
   }

   private boolean func_150649_b(BlockRailBase$Rail var1) {
      if (this.func_150653_a(var1)) {
         return true;
      } else if (this.field_150657_g.size() == 2) {
         return false;
      } else {
         return this.field_150657_g.isEmpty() ? true : true;
      }
   }

   private void func_150645_c(BlockRailBase$Rail var1) {
      this.field_150657_g.add(new ChunkPosition(var1.field_150661_c, var1.field_150658_d, var1.field_150659_e));
      boolean var2 = this.func_150652_b(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1);
      boolean var3 = this.func_150652_b(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1);
      boolean var4 = this.func_150652_b(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e);
      boolean var5 = this.func_150652_b(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e);
      byte var6 = -1;
      if (var2 || var3) {
         var6 = 0;
      }

      if (var4 || var5) {
         var6 = 1;
      }

      if (!this.field_150656_f) {
         if (var3 && var5 && !var2 && !var4) {
            var6 = 6;
         }

         if (var3 && var4 && !var2 && !var5) {
            var6 = 7;
         }

         if (var2 && var4 && !var3 && !var5) {
            var6 = 8;
         }

         if (var2 && var5 && !var3 && !var4) {
            var6 = 9;
         }
      }

      if (var6 == 0) {
         if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c, this.field_150658_d + 1, this.field_150659_e - 1)) {
            var6 = 4;
         }

         if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c, this.field_150658_d + 1, this.field_150659_e + 1)) {
            var6 = 5;
         }
      }

      if (var6 == 1) {
         if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c + 1, this.field_150658_d + 1, this.field_150659_e)) {
            var6 = 2;
         }

         if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c - 1, this.field_150658_d + 1, this.field_150659_e)) {
            var6 = 3;
         }
      }

      if (var6 < 0) {
         var6 = 0;
      }

      int var7 = var6;
      if (this.field_150656_f) {
         var7 = this.field_150660_b.func_72805_g(this.field_150661_c, this.field_150658_d, this.field_150659_e) & 8 | var6;
      }

      this.field_150660_b.func_72921_c(this.field_150661_c, this.field_150658_d, this.field_150659_e, var7, 3);
   }

   private boolean func_150647_c(int var1, int var2, int var3) {
      BlockRailBase$Rail var4 = this.func_150654_a(new ChunkPosition(var1, var2, var3));
      if (var4 == null) {
         return false;
      } else {
         var4.func_150651_b();
         return var4.func_150649_b(this);
      }
   }

   public void func_150655_a(boolean var1, boolean var2) {
      boolean var3 = this.func_150647_c(this.field_150661_c, this.field_150658_d, this.field_150659_e - 1);
      boolean var4 = this.func_150647_c(this.field_150661_c, this.field_150658_d, this.field_150659_e + 1);
      boolean var5 = this.func_150647_c(this.field_150661_c - 1, this.field_150658_d, this.field_150659_e);
      boolean var6 = this.func_150647_c(this.field_150661_c + 1, this.field_150658_d, this.field_150659_e);
      byte var7 = -1;
      if ((var3 || var4) && !var5 && !var6) {
         var7 = 0;
      }

      if ((var5 || var6) && !var3 && !var4) {
         var7 = 1;
      }

      if (!this.field_150656_f) {
         if (var4 && var6 && !var3 && !var5) {
            var7 = 6;
         }

         if (var4 && var5 && !var3 && !var6) {
            var7 = 7;
         }

         if (var3 && var5 && !var4 && !var6) {
            var7 = 8;
         }

         if (var3 && var6 && !var4 && !var5) {
            var7 = 9;
         }
      }

      if (var7 == -1) {
         if (var3 || var4) {
            var7 = 0;
         }

         if (var5 || var6) {
            var7 = 1;
         }

         if (!this.field_150656_f) {
            if (var1) {
               if (var4 && var6) {
                  var7 = 6;
               }

               if (var5 && var4) {
                  var7 = 7;
               }

               if (var6 && var3) {
                  var7 = 9;
               }

               if (var3 && var5) {
                  var7 = 8;
               }
            } else {
               if (var3 && var5) {
                  var7 = 8;
               }

               if (var6 && var3) {
                  var7 = 9;
               }

               if (var5 && var4) {
                  var7 = 7;
               }

               if (var4 && var6) {
                  var7 = 6;
               }
            }
         }
      }

      if (var7 == 0) {
         if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c, this.field_150658_d + 1, this.field_150659_e - 1)) {
            var7 = 4;
         }

         if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c, this.field_150658_d + 1, this.field_150659_e + 1)) {
            var7 = 5;
         }
      }

      if (var7 == 1) {
         if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c + 1, this.field_150658_d + 1, this.field_150659_e)) {
            var7 = 2;
         }

         if (BlockRailBase.func_150049_b_(this.field_150660_b, this.field_150661_c - 1, this.field_150658_d + 1, this.field_150659_e)) {
            var7 = 3;
         }
      }

      if (var7 < 0) {
         var7 = 0;
      }

      this.func_150648_a(var7);
      int var8 = var7;
      if (this.field_150656_f) {
         var8 = this.field_150660_b.func_72805_g(this.field_150661_c, this.field_150658_d, this.field_150659_e) & 8 | var7;
      }

      if (var2 || this.field_150660_b.func_72805_g(this.field_150661_c, this.field_150658_d, this.field_150659_e) != var8) {
         this.field_150660_b.func_72921_c(this.field_150661_c, this.field_150658_d, this.field_150659_e, var8, 3);

         for(int var9 = 0; var9 < this.field_150657_g.size(); ++var9) {
            BlockRailBase$Rail var10 = this.func_150654_a((ChunkPosition)this.field_150657_g.get(var9));
            if (var10 != null) {
               var10.func_150651_b();
               if (var10.func_150649_b(this)) {
                  var10.func_150645_c(this);
               }
            }
         }
      }
   }
}
