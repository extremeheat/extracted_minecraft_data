package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class DialogTypes {
   public DialogTypes() {
      super();
   }

   public static MapCodec<? extends Dialog> bootstrap(Registry<MapCodec<? extends Dialog>> var0) {
      Registry.register(var0, (String)"notice", NoticeDialog.MAP_CODEC);
      Registry.register(var0, (String)"server_links", ServerLinksDialog.MAP_CODEC);
      Registry.register(var0, (String)"dialog_list", DialogListDialog.MAP_CODEC);
      Registry.register(var0, (String)"multi_action", MultiActionDialog.MAP_CODEC);
      return (MapCodec)Registry.register(var0, (String)"confirmation", ConfirmationDialog.MAP_CODEC);
   }
}
