package net.minecraft.client.multiplayer.chat;

public enum GuiMessageSource {
   PLAYER,
   SYSTEM_SERVER,
   SYSTEM_CLIENT;

   private GuiMessageSource() {
   }

   // $FF: synthetic method
   private static GuiMessageSource[] $values() {
      return new GuiMessageSource[]{PLAYER, SYSTEM_SERVER, SYSTEM_CLIENT};
   }
}
