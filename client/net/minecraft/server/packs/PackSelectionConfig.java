package net.minecraft.server.packs;

import net.minecraft.server.packs.repository.Pack;

public record PackSelectionConfig(boolean required, Pack.Position defaultPosition, boolean fixedPosition) {
   public PackSelectionConfig(boolean required, Pack.Position defaultPosition, boolean fixedPosition) {
      super();
      this.required = required;
      this.defaultPosition = defaultPosition;
      this.fixedPosition = fixedPosition;
   }

   public boolean required() {
      return this.required;
   }

   public Pack.Position defaultPosition() {
      return this.defaultPosition;
   }

   public boolean fixedPosition() {
      return this.fixedPosition;
   }
}
