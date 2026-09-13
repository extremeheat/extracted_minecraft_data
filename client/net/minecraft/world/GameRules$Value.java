package net.minecraft.world;

class GameRules$Value {
   private String field_82762_a;
   private boolean field_82760_b;
   private int field_82761_c;
   private double field_82759_d;

   public GameRules$Value(String var1) {
      super();
      this.func_82757_a(var1);
   }

   public void func_82757_a(String var1) {
      this.field_82762_a = var1;
      this.field_82760_b = Boolean.parseBoolean(var1);

      try {
         this.field_82761_c = Integer.parseInt(var1);
      } catch (NumberFormatException var4) {
      }

      try {
         this.field_82759_d = Double.parseDouble(var1);
      } catch (NumberFormatException var3) {
      }
   }

   public String func_82756_a() {
      return this.field_82762_a;
   }

   public boolean func_82758_b() {
      return this.field_82760_b;
   }
}
