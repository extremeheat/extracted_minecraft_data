package net.minecraft.client.stream;

class TwitchStream$1$1 extends Thread {
   TwitchStream$1$1(TwitchStream$1 var1, String var2) {
      super(var2);
      this.field_153082_a = var1;
   }

   @Override
   public void run() {
      this.field_153082_a.field_153084_b.func_152923_i();
   }
}
