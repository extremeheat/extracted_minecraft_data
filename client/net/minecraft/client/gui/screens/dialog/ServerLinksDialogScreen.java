package net.minecraft.client.gui.screens.dialog;

import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.dialog.ClickAction;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.ServerLinksDialog;

public class ServerLinksDialogScreen extends ButtonListDialogScreen<ServerLinksDialog> {
   public ServerLinksDialogScreen(@Nullable Screen var1, ServerLinksDialog var2, DialogConnectionAccess var3) {
      super(var1, var2, var3);
   }

   protected Stream<ClickAction> createListActions(ServerLinksDialog var1, DialogConnectionAccess var2) {
      return var2.serverLinks().entries().stream().map((var1x) -> createDialogClickAction(var1, var1x));
   }

   private static ClickAction createDialogClickAction(ServerLinksDialog var0, ServerLinks.Entry var1) {
      return new ClickAction(new CommonButtonData(var1.displayName(), var0.buttonWidth()), Optional.of(new ClickEvent.OpenUrl(var1.link())));
   }
}
