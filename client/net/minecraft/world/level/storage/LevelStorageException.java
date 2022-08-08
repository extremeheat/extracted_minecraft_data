package net.minecraft.world.level.storage;

import net.minecraft.network.chat.Component;

public class LevelStorageException extends RuntimeException {
   private final Component messageComponent;

   public LevelStorageException(Component var1) {
      super(var1.getString());
      this.messageComponent = var1;
   }

   public Component getMessageComponent() {
      return this.messageComponent;
   }
}
