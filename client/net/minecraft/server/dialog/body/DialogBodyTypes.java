package net.minecraft.server.dialog.body;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class DialogBodyTypes {
   public DialogBodyTypes() {
      super();
   }

   public static MapCodec<? extends DialogBody> bootstrap(final Registry<MapCodec<? extends DialogBody>> registry) {
      Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("item"), ItemBody.MAP_CODEC);
      return (MapCodec)Registry.register(registry, (Identifier)Identifier.withDefaultNamespace("plain_message"), PlainMessage.MAP_CODEC);
   }
}
