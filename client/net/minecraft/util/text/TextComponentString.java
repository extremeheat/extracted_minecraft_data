package net.minecraft.util.text;

public class TextComponentString extends TextComponentBase {
   private final String field_150267_b;

   public TextComponentString(String var1) {
      super();
      this.field_150267_b = var1;
   }

   public String func_150265_g() {
      return this.field_150267_b;
   }

   public String func_150261_e() {
      return this.field_150267_b;
   }

   public TextComponentString func_150259_f() {
      return new TextComponentString(this.field_150267_b);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TextComponentString)) {
         return false;
      } else {
         TextComponentString var2 = (TextComponentString)var1;
         return this.field_150267_b.equals(var2.func_150265_g()) && super.equals(var1);
      }
   }

   public String toString() {
      return "TextComponent{text='" + this.field_150267_b + '\'' + ", siblings=" + this.field_150264_a + ", style=" + this.func_150256_b() + '}';
   }

   // $FF: synthetic method
   public ITextComponent func_150259_f() {
      return this.func_150259_f();
   }
}
