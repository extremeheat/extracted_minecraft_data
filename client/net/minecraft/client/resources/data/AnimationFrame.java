package net.minecraft.client.resources.data;

public class AnimationFrame {
   private final int field_110499_a;
   private final int field_110498_b;

   public AnimationFrame(int var1) {
      this(var1, -1);
   }

   public AnimationFrame(int var1, int var2) {
      super();
      this.field_110499_a = var1;
      this.field_110498_b = var2;
   }

   public boolean func_110495_a() {
      return this.field_110498_b == -1;
   }

   public int func_110497_b() {
      return this.field_110498_b;
   }

   public int func_110496_c() {
      return this.field_110499_a;
   }
}
