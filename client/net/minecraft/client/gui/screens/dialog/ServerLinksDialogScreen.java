package net.minecraft.client.gui.screens.dialog;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.ServerLinksDialog;
import net.minecraft.server.dialog.action.StaticAction;
import org.jspecify.annotations.Nullable;

public class ServerLinksDialogScreen extends ButtonListDialogScreen<ServerLinksDialog> {
   public ServerLinksDialogScreen(@Nullable Screen var1, ServerLinksDialog var2, DialogConnectionAccess var3) {
      super(var1, var2, var3);
   }

   protected Stream<ActionButton> createListActions(ServerLinksDialog var1, DialogConnectionAccess var2) {
      return var2.serverLinks().entries().stream().map((var1x) -> createDialogClickAction(var1, var1x));
   }

   private static ActionButton createDialogClickAction(ServerLinksDialog var0, ServerLinks.Entry var1) {
      return new ActionButton(new CommonButtonData(var1.displayName(), var0.buttonWidth()), Optional.of(new StaticAction(new ClickEvent.OpenUrl(var1.link()))));
   }
}
