package net.minecraft.server.packs.repository;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum PackCompatibility {
   TOO_OLD("old"),
   TOO_NEW("new"),
   COMPATIBLE("compatible");

   private final Component description;
   private final Component confirmation;

   private PackCompatibility(String var3) {
      this.description = new TranslatableComponent("resourcePack.incompatible." + var3, new Object[0]);
      this.confirmation = new TranslatableComponent("resourcePack.incompatible.confirm." + var3, new Object[0]);
   }

   public boolean isCompatible() {
      return this == COMPATIBLE;
   }

   public static PackCompatibility forFormat(int var0) {
      if (var0 < SharedConstants.getCurrentVersion().getPackVersion()) {
         return TOO_OLD;
      } else {
         return var0 > SharedConstants.getCurrentVersion().getPackVersion() ? TOO_NEW : COMPATIBLE;
      }
   }

   public Component getDescription() {
      return this.description;
   }

   public Component getConfirmation() {
      return this.confirmation;
   }
}
