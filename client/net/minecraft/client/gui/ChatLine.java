package net.minecraft.client.gui;

import net.minecraft.util.IChatComponent;

public class ChatLine {
   private final int field_74543_a;
   private final IChatComponent field_74541_b;
   private final int field_74542_c;

   public ChatLine(int var1, IChatComponent var2, int var3) {
      super();
      this.field_74541_b = var2;
      this.field_74543_a = var1;
      this.field_74542_c = var3;
   }

   public IChatComponent func_151461_a() {
      return this.field_74541_b;
   }

   public int func_74540_b() {
      return this.field_74543_a;
   }

   public int func_74539_c() {
      return this.field_74542_c;
   }
}
