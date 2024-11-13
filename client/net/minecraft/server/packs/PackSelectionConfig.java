package net.minecraft.server.packs;

import net.minecraft.server.packs.repository.Pack;

public record PackSelectionConfig(boolean required, Pack.Position defaultPosition, boolean fixedPosition) {
   public PackSelectionConfig(boolean var1, Pack.Position var2, boolean var3) {
      super();
      this.required = var1;
      this.defaultPosition = var2;
      this.fixedPosition = var3;
   }
}
