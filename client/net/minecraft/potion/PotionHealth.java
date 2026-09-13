package net.minecraft.potion;

public class PotionHealth extends Potion {
   public PotionHealth(int var1, boolean var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean func_76403_b() {
      return true;
   }

   @Override
   public boolean func_76397_a(int var1, int var2) {
      return var1 >= 1;
   }
}
