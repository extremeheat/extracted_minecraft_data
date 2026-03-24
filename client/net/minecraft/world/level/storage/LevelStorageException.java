package net.minecraft.world.level.storage;

import net.minecraft.network.chat.Component;

public class LevelStorageException extends RuntimeException {
   private final Component messageComponent;

   public LevelStorageException(final Component message) {
      super(message.getString());
      this.messageComponent = message;
   }

   public Component getMessageComponent() {
      return this.messageComponent;
   }
}
