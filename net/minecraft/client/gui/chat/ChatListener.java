package net.minecraft.client.gui.chat;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

public interface ChatListener {
   void handle(ChatType var1, Component var2);
}
