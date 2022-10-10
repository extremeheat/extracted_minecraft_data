package net.minecraft.pathfinding;

public class PathHeap {
   private PathPoint[] field_75852_a = new PathPoint[128];
   private int field_75851_b;

   public PathHeap() {
      super();
   }

   public PathPoint func_75849_a(PathPoint var1) {
      if (var1.field_75835_d >= 0) {
         throw new IllegalStateException("OW KNOWS!");
      } else {
         if (this.field_75851_b == this.field_75852_a.length) {
            PathPoint[] var2 = new PathPoint[this.field_75851_b << 1];
            System.arraycopy(this.field_75852_a, 0, var2, 0, this.field_75851_b);
            this.field_75852_a = var2;
         }

         this.field_75852_a[this.field_75851_b] = var1;
         var1.field_75835_d = this.field_75851_b;
         this.func_75847_a(this.field_75851_b++);
         return var1;
      }
   }

   public void func_75848_a() {
      this.field_75851_b = 0;
   }

   public PathPoint func_75844_c() {
      PathPoint var1 = this.field_75852_a[0];
      this.field_75852_a[0] = this.field_75852_a[--this.field_75851_b];
      this.field_75852_a[this.field_75851_b] = null;
      if (this.field_75851_b > 0) {
         this.func_75846_b(0);
      }

      var1.field_75835_d = -1;
      return var1;
   }

   public void func_75850_a(PathPoint var1, float var2) {
      float var3 = var1.field_75834_g;
      var1.field_75834_g = var2;
      if (var2 < var3) {
         this.func_75847_a(var1.field_75835_d);
      } else {
         this.func_75846_b(var1.field_75835_d);
      }

   }

   private void func_75847_a(int var1) {
      PathPoint var2 = this.field_75852_a[var1];

      int var4;
      for(float var3 = var2.field_75834_g; var1 > 0; var1 = var4) {
         var4 = var1 - 1 >> 1;
         PathPoint var5 = this.field_75852_a[var4];
         if (var3 >= var5.field_75834_g) {
            break;
         }

         this.field_75852_a[var1] = var5;
         var5.field_75835_d = var1;
      }

      this.field_75852_a[var1] = var2;
      var2.field_75835_d = var1;
   }

   private void func_75846_b(int var1) {
      PathPoint var2 = this.field_75852_a[var1];
      float var3 = var2.field_75834_g;

      while(true) {
         int var4 = 1 + (var1 << 1);
         int var5 = var4 + 1;
         if (var4 >= this.field_75851_b) {
            break;
         }

         PathPoint var6 = this.field_75852_a[var4];
         float var7 = var6.field_75834_g;
         PathPoint var8;
         float var9;
         if (var5 >= this.field_75851_b) {
            var8 = null;
            var9 = 1.0F / 0.0;
         } else {
            var8 = this.field_75852_a[var5];
            var9 = var8.field_75834_g;
         }

         if (var7 < var9) {
            if (var7 >= var3) {
               break;
            }

            this.field_75852_a[var1] = var6;
            var6.field_75835_d = var1;
            var1 = var4;
         } else {
            if (var9 >= var3) {
               break;
            }

            this.field_75852_a[var1] = var8;
            var8.field_75835_d = var1;
            var1 = var5;
         }
      }

      this.field_75852_a[var1] = var2;
      var2.field_75835_d = var1;
   }

   public boolean func_75845_e() {
      return this.field_75851_b == 0;
   }
}
