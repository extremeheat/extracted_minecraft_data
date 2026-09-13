package net.minecraft.client.renderer.texture;

public class Stitcher$Holder implements Comparable {
   private final TextureAtlasSprite field_98151_a;
   private final int field_94204_c;
   private final int field_94201_d;
   private final int field_147968_d;
   private boolean field_94202_e;
   private float field_94205_a = 1.0F;

   public Stitcher$Holder(TextureAtlasSprite var1, int var2) {
      super();
      this.field_98151_a = var1;
      this.field_94204_c = var1.func_94211_a();
      this.field_94201_d = var1.func_94216_b();
      this.field_147968_d = var2;
      this.field_94202_e = Stitcher.access$000(this.field_94201_d, var2) > Stitcher.access$000(this.field_94204_c, var2);
   }

   public TextureAtlasSprite func_98150_a() {
      return this.field_98151_a;
   }

   public int func_94197_a() {
      return this.field_94202_e
         ? Stitcher.access$000((int)((float)this.field_94201_d * this.field_94205_a), this.field_147968_d)
         : Stitcher.access$000((int)((float)this.field_94204_c * this.field_94205_a), this.field_147968_d);
   }

   public int func_94199_b() {
      return this.field_94202_e
         ? Stitcher.access$000((int)((float)this.field_94204_c * this.field_94205_a), this.field_147968_d)
         : Stitcher.access$000((int)((float)this.field_94201_d * this.field_94205_a), this.field_147968_d);
   }

   public void func_94194_d() {
      this.field_94202_e = !this.field_94202_e;
   }

   public boolean func_94195_e() {
      return this.field_94202_e;
   }

   public void func_94196_a(int var1) {
      if (this.field_94204_c > var1 && this.field_94201_d > var1) {
         this.field_94205_a = (float)var1 / (float)Math.min(this.field_94204_c, this.field_94201_d);
      }
   }

   @Override
   public String toString() {
      return "Holder{width=" + this.field_94204_c + ", height=" + this.field_94201_d + '}';
   }

   public int compareTo(Stitcher$Holder var1) {
      int var2;
      if (this.func_94199_b() == var1.func_94199_b()) {
         if (this.func_94197_a() == var1.func_94197_a()) {
            if (this.field_98151_a.func_94215_i() == null) {
               return var1.field_98151_a.func_94215_i() == null ? 0 : -1;
            }

            return this.field_98151_a.func_94215_i().compareTo(var1.field_98151_a.func_94215_i());
         }

         var2 = this.func_94197_a() < var1.func_94197_a() ? 1 : -1;
      } else {
         var2 = this.func_94199_b() < var1.func_94199_b() ? 1 : -1;
      }

      return var2;
   }
}
