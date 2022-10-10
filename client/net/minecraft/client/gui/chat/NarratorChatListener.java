package net.minecraft.client.gui.chat;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class NarratorChatListener implements IChatListener {
   public static final NarratorChatListener field_193643_a = new NarratorChatListener();
   private final Narrator field_192580_a = Narrator.getNarrator();

   public NarratorChatListener() {
      super();
   }

   public void func_192576_a(ChatType var1, ITextComponent var2) {
      int var3 = Minecraft.func_71410_x().field_71474_y.field_192571_R;
      if (var3 != 0 && this.field_192580_a.active()) {
         if (var3 == 1 || var3 == 2 && var1 == ChatType.CHAT || var3 == 3 && var1 == ChatType.SYSTEM) {
            if (var2 instanceof TextComponentTranslation && "chat.type.text".equals(((TextComponentTranslation)var2).func_150268_i())) {
               this.field_192580_a.say((new TextComponentTranslation("chat.type.text.narrate", ((TextComponentTranslation)var2).func_150271_j())).getString());
            } else {
               this.field_192580_a.say(var2.getString());
            }
         }

      }
   }

   public void func_193641_a(int var1) {
      this.field_192580_a.clear();
      this.field_192580_a.say((new TextComponentTranslation("options.narrator", new Object[0])).getString() + " : " + (new TextComponentTranslation(GameSettings.field_193632_b[var1], new Object[0])).getString());
      GuiToast var2 = Minecraft.func_71410_x().func_193033_an();
      if (this.field_192580_a.active()) {
         if (var1 == 0) {
            SystemToast.func_193657_a(var2, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.disabled", new Object[0]), (ITextComponent)null);
         } else {
            SystemToast.func_193657_a(var2, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.enabled", new Object[0]), new TextComponentTranslation(GameSettings.field_193632_b[var1], new Object[0]));
         }
      } else {
         SystemToast.func_193657_a(var2, SystemToast.Type.NARRATOR_TOGGLE, new TextComponentTranslation("narrator.toast.disabled", new Object[0]), new TextComponentTranslation("options.narrator.notavailable", new Object[0]));
      }

   }

   public boolean func_193640_a() {
      return this.field_192580_a.active();
   }

   public void func_193642_b() {
      this.field_192580_a.clear();
   }
}
