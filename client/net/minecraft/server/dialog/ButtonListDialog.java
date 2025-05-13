package net.minecraft.server.dialog;

import com.mojang.serialization.MapCodec;

public interface ButtonListDialog extends Dialog {
   MapCodec<? extends ButtonListDialog> codec();

   int columns();
}
