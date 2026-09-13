package net.minecraft.util;

public class ChatComponentText extends ChatComponentStyle {
   private final String field_150267_b;

   public ChatComponentText(String var1) {
      super();
      this.field_150267_b = var1;
   }

   public String func_150265_g() {
      return this.field_150267_b;
   }

   @Override
   public String func_150261_e() {
      return this.field_150267_b;
   }

   public ChatComponentText func_150259_f() {
      ChatComponentText var1 = new ChatComponentText(this.field_150267_b);
      var1.func_150255_a(this.func_150256_b().func_150232_l());

      for(IChatComponent var3 : this.func_150253_a()) {
         var1.func_150257_a(var3.func_150259_f());
      }

      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ChatComponentText)) {
         return false;
      } else {
         ChatComponentText var2 = (ChatComponentText)var1;
         return this.field_150267_b.equals(var2.func_150265_g()) && super.equals(var1);
      }
   }

   @Override
   public String toString() {
      return "TextComponent{text='" + this.field_150267_b + '\'' + ", siblings=" + this.field_150264_a + ", style=" + this.func_150256_b() + '}';
   }
}
