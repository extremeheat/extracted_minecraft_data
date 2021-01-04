package net.minecraft.client;

import net.minecraft.network.chat.Component;

public class GuiMessage {
   private final int addedTime;
   private final Component message;
   private final int id;

   public GuiMessage(int var1, Component var2, int var3) {
      super();
      this.message = var2;
      this.addedTime = var1;
      this.id = var3;
   }

   public Component getMessage() {
      return this.message;
   }

   public int getAddedTime() {
      return this.addedTime;
   }

   public int getId() {
      return this.id;
   }
}
