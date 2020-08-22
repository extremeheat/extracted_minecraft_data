package net.minecraft.commands;

import net.minecraft.network.chat.Component;

public interface CommandSource {
   CommandSource NULL = new CommandSource() {
      public void sendMessage(Component var1) {
      }

      public boolean acceptsSuccess() {
         return false;
      }

      public boolean acceptsFailure() {
         return false;
      }

      public boolean shouldInformAdmins() {
         return false;
      }
   };

   void sendMessage(Component var1);

   boolean acceptsSuccess();

   boolean acceptsFailure();

   boolean shouldInformAdmins();
}
