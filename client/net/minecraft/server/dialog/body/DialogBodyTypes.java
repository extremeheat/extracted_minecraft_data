package net.minecraft.server.dialog.body;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class DialogBodyTypes {
   public DialogBodyTypes() {
      super();
   }

   public static MapCodec<? extends DialogBody> bootstrap(Registry<MapCodec<? extends DialogBody>> var0) {
      Registry.register(var0, (Identifier)Identifier.withDefaultNamespace("item"), ItemBody.MAP_CODEC);
      return (MapCodec)Registry.register(var0, (Identifier)Identifier.withDefaultNamespace("plain_message"), PlainMessage.MAP_CODEC);
   }
}
