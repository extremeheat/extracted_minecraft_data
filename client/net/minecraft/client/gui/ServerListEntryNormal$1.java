package net.minecraft.client.gui;

import java.net.UnknownHostException;
import net.minecraft.util.EnumChatFormatting;

class ServerListEntryNormal$1 implements Runnable {
   ServerListEntryNormal$1(ServerListEntryNormal var1) {
      super();
      this.field_148521_a = var1;
   }

   @Override
   public void run() {
      try {
         ServerListEntryNormal.access$100(this.field_148521_a).func_146789_i().func_147224_a(ServerListEntryNormal.access$000(this.field_148521_a));
      } catch (UnknownHostException var2) {
         ServerListEntryNormal.access$000(this.field_148521_a).field_78844_e = -1L;
         ServerListEntryNormal.access$000(this.field_148521_a).field_78843_d = EnumChatFormatting.DARK_RED + "Can't resolve hostname";
      } catch (Exception var3) {
         ServerListEntryNormal.access$000(this.field_148521_a).field_78844_e = -1L;
         ServerListEntryNormal.access$000(this.field_148521_a).field_78843_d = EnumChatFormatting.DARK_RED + "Can't connect to server.";
      }
   }
}
