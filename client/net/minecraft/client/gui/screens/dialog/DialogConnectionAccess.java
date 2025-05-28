package net.minecraft.client.gui.screens.dialog;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.dialog.Dialog;

public interface DialogConnectionAccess {
   void disconnect(Component var1);

   void runCommand(String var1, @Nullable Screen var2);

   void openDialog(Holder<Dialog> var1, @Nullable Screen var2);

   void sendCustomAction(ResourceLocation var1, Optional<Tag> var2);

   ServerLinks serverLinks();
}
