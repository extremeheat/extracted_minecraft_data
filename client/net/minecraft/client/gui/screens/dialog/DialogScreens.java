package net.minecraft.client.gui.screens.dialog;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.ConfirmationDialog;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogListDialog;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.NoticeDialog;
import net.minecraft.server.dialog.ServerLinksDialog;

public class DialogScreens {
   private static final Map<MapCodec<? extends Dialog>, Factory<?>> FACTORIES = new HashMap();

   public DialogScreens() {
      super();
   }

   private static <T extends Dialog> void register(MapCodec<T> var0, Factory<? super T> var1) {
      FACTORIES.put(var0, var1);
   }

   @Nullable
   public static <T extends Dialog> DialogScreen<T> createFromData(T var0, @Nullable Screen var1, DialogConnectionAccess var2) {
      Factory var3 = (Factory)FACTORIES.get(var0.codec());
      return var3 != null ? var3.create(var1, var0, var2) : null;
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
      DialogScreen<T> create(@Nullable Screen var1, T var2, DialogConnectionAccess var3);
   }
}
