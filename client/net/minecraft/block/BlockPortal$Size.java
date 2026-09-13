package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class BlockPortal$Size {
   private final World field_150867_a;
   private final int field_150865_b;
   private final int field_150866_c;
   private final int field_150863_d;
   private int field_150864_e = 0;
   private ChunkCoordinates field_150861_f;
   private int field_150862_g;
   private int field_150868_h;

   public BlockPortal$Size(World var1, int var2, int var3, int var4, int var5) {
      super();
      this.field_150867_a = var1;
      this.field_150865_b = var5;
      this.field_150863_d = BlockPortal.field_150001_a[var5][0];
      this.field_150866_c = BlockPortal.field_150001_a[var5][1];
      int var6 = var3;

      while(var3 > var6 - 21 && var3 > 0 && this.func_150857_a(var1.func_147439_a(var2, var3 - 1, var4))) {
         --var3;
      }

      int var7 = this.func_150853_a(var2, var3, var4, this.field_150863_d) - 1;
      if (var7 >= 0) {
         this.field_150861_f = new ChunkCoordinates(
            var2 + var7 * Direction.field_71583_a[this.field_150863_d], var3, var4 + var7 * Direction.field_71581_b[this.field_150863_d]
         );
         this.field_150868_h = this.func_150853_a(
            this.field_150861_f.field_71574_a, this.field_150861_f.field_71572_b, this.field_150861_f.field_71573_c, this.field_150866_c
         );
         if (this.field_150868_h < 2 || this.field_150868_h > 21) {
            this.field_150861_f = null;
            this.field_150868_h = 0;
         }
      }

      if (this.field_150861_f != null) {
         this.field_150862_g = this.func_150858_a();
      }
   }

   protected int func_150853_a(int var1, int var2, int var3, int var4) {
      int var6 = Direction.field_71583_a[var4];
      int var7 = Direction.field_71581_b[var4];

      int var5;
      for(var5 = 0; var5 < 22; ++var5) {
         Block var8 = this.field_150867_a.func_147439_a(var1 + var6 * var5, var2, var3 + var7 * var5);
         if (!this.func_150857_a(var8)) {
            break;
         }

         Block var9 = this.field_150867_a.func_147439_a(var1 + var6 * var5, var2 - 1, var3 + var7 * var5);
         if (var9 != Blocks.field_150343_Z) {
            break;
         }
      }

      Block var10 = this.field_150867_a.func_147439_a(var1 + var6 * var5, var2, var3 + var7 * var5);
      return var10 == Blocks.field_150343_Z ? var5 : 0;
   }

   protected int func_150858_a() {
      label56:
      for(this.field_150862_g = 0; this.field_150862_g < 21; ++this.field_150862_g) {
         int var1 = this.field_150861_f.field_71572_b + this.field_150862_g;

         for(int var2 = 0; var2 < this.field_150868_h; ++var2) {
            int var3 = this.field_150861_f.field_71574_a + var2 * Direction.field_71583_a[BlockPortal.field_150001_a[this.field_150865_b][1]];
            int var4 = this.field_150861_f.field_71573_c + var2 * Direction.field_71581_b[BlockPortal.field_150001_a[this.field_150865_b][1]];
            Block var5 = this.field_150867_a.func_147439_a(var3, var1, var4);
            if (!this.func_150857_a(var5)) {
               break label56;
            }

            if (var5 == Blocks.field_150427_aO) {
               ++this.field_150864_e;
            }

            if (var2 == 0) {
               var5 = this.field_150867_a
                  .func_147439_a(
                     var3 + Direction.field_71583_a[BlockPortal.field_150001_a[this.field_150865_b][0]],
                     var1,
                     var4 + Direction.field_71581_b[BlockPortal.field_150001_a[this.field_150865_b][0]]
                  );
               if (var5 != Blocks.field_150343_Z) {
                  break label56;
               }
            } else if (var2 == this.field_150868_h - 1) {
               var5 = this.field_150867_a
                  .func_147439_a(
                     var3 + Direction.field_71583_a[BlockPortal.field_150001_a[this.field_150865_b][1]],
                     var1,
                     var4 + Direction.field_71581_b[BlockPortal.field_150001_a[this.field_150865_b][1]]
                  );
               if (var5 != Blocks.field_150343_Z) {
                  break label56;
               }
            }
         }
      }

      for(int var6 = 0; var6 < this.field_150868_h; ++var6) {
         int var7 = this.field_150861_f.field_71574_a + var6 * Direction.field_71583_a[BlockPortal.field_150001_a[this.field_150865_b][1]];
         int var8 = this.field_150861_f.field_71572_b + this.field_150862_g;
         int var9 = this.field_150861_f.field_71573_c + var6 * Direction.field_71581_b[BlockPortal.field_150001_a[this.field_150865_b][1]];
         if (this.field_150867_a.func_147439_a(var7, var8, var9) != Blocks.field_150343_Z) {
            this.field_150862_g = 0;
            break;
         }
      }

      if (this.field_150862_g <= 21 && this.field_150862_g >= 3) {
         return this.field_150862_g;
      } else {
         this.field_150861_f = null;
         this.field_150868_h = 0;
         this.field_150862_g = 0;
         return 0;
      }
   }

   protected boolean func_150857_a(Block var1) {
      return var1.field_149764_J == Material.field_151579_a || var1 == Blocks.field_150480_ab || var1 == Blocks.field_150427_aO;
   }

   public boolean func_150860_b() {
      return this.field_150861_f != null && this.field_150868_h >= 2 && this.field_150868_h <= 21 && this.field_150862_g >= 3 && this.field_150862_g <= 21;
   }

   public void func_150859_c() {
      for(int var1 = 0; var1 < this.field_150868_h; ++var1) {
         int var2 = this.field_150861_f.field_71574_a + Direction.field_71583_a[this.field_150866_c] * var1;
         int var3 = this.field_150861_f.field_71573_c + Direction.field_71581_b[this.field_150866_c] * var1;

         for(int var4 = 0; var4 < this.field_150862_g; ++var4) {
            int var5 = this.field_150861_f.field_71572_b + var4;
            this.field_150867_a.func_147465_d(var2, var5, var3, Blocks.field_150427_aO, this.field_150865_b, 2);
         }
      }
   }
}
