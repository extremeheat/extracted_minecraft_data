package net.minecraft.client.gui.screens.dialog;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.ConfirmationDialog;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogListDialog;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.NoticeDialog;
import net.minecraft.server.dialog.ServerLinksDialog;
import org.jspecify.annotations.Nullable;

public class DialogScreens {
   private static final Map<MapCodec<? extends Dialog>, Factory<?>> FACTORIES = new HashMap();

   public DialogScreens() {
      super();
   }

   private static <T extends Dialog> void register(final MapCodec<T> type, final Factory<? super T> factory) {
      FACTORIES.put(type, factory);
   }

   public static <T extends Dialog> @Nullable DialogScreen<T> createFromData(final T dialog, final @Nullable Screen previousScreen, final DialogConnectionAccess connectionAccess) {
      Factory<T> factory = (Factory)FACTORIES.get(dialog.codec());
      return factory != null ? factory.create(previousScreen, dialog, connectionAccess) : null;
   }

   public static void bootstrap() {
      register(ConfirmationDialog.MAP_CODEC, SimpleDialogScreen::new);
      register(NoticeDialog.MAP_CODEC, SimpleDialogScreen::new);
      register(DialogListDialog.MAP_CODEC, DialogListDialogScreen::new);
      register(MultiActionDialog.MAP_CODEC, MultiButtonDialogScreen::new);
      register(ServerLinksDialog.MAP_CODEC, ServerLinksDialogScreen::new);
   }

   @FunctionalInterface
   public interface Factory<T extends Dialog> {
      DialogScreen<T> create(@Nullable Screen previousScreen, T data, DialogConnectionAccess connectionAccess);
   }
}
