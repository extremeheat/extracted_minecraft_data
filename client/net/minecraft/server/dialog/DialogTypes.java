package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class DialogTypes {
   public DialogTypes() {
      super();
   }

   public static MapCodec<? extends Dialog> bootstrap(final Registry<MapCodec<? extends Dialog>> registry) {
      Registry.register(registry, (String)"notice", NoticeDialog.MAP_CODEC);
      Registry.register(registry, (String)"server_links", ServerLinksDialog.MAP_CODEC);
      Registry.register(registry, (String)"dialog_list", DialogListDialog.MAP_CODEC);
      Registry.register(registry, (String)"multi_action", MultiActionDialog.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (String)"confirmation", ConfirmationDialog.MAP_CODEC);
   }
}
