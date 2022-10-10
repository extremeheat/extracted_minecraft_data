package net.minecraft.client.gui.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;

public class NormalChatListener implements IChatListener {
   private final Minecraft field_192581_a;

   public NormalChatListener(Minecraft var1) {
      super();
      this.field_192581_a = var1;
   }

   public void func_192576_a(ChatType var1, ITextComponent var2) {
      this.field_192581_a.field_71456_v.func_146158_b().func_146227_a(var2);
   }
}
