package net.minecraft.network.chat;

public class TextComponent extends BaseComponent {
   public static final Component EMPTY = new TextComponent("");
   private final String text;

   public TextComponent(String var1) {
      super();
      this.text = var1;
   }

   public String getText() {
      return this.text;
   }

   public String getContents() {
      return this.text;
   }

   public TextComponent plainCopy() {
      return new TextComponent(this.text);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TextComponent)) {
         return false;
      } else {
         TextComponent var2 = (TextComponent)var1;
         return this.text.equals(var2.getText()) && super.equals(var1);
      }
   }

   public String toString() {
      String var10000 = this.text;
      return "TextComponent{text='" + var10000 + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
   }

   // $FF: synthetic method
   public BaseComponent plainCopy() {
      return this.plainCopy();
   }

   // $FF: synthetic method
   public MutableComponent plainCopy() {
      return this.plainCopy();
   }
}
