package net.minecraft.server.packs.repository;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.InclusiveRange;

public enum PackCompatibility {
   TOO_OLD("old"),
   TOO_NEW("new"),
   COMPATIBLE("compatible");

   private final Component description;
   private final Component confirmation;

   private PackCompatibility(final String var3) {
      this.description = Component.translatable("pack.incompatible." + var3).withStyle(ChatFormatting.GRAY);
      this.confirmation = Component.translatable("pack.incompatible.confirm." + var3);
   }

   public boolean isCompatible() {
      return this == COMPATIBLE;
   }

   public static PackCompatibility forVersion(InclusiveRange<Integer> var0, int var1) {
      if ((Integer)var0.maxInclusive() < var1) {
         return TOO_OLD;
      } else {
         return var1 < (Integer)var0.minInclusive() ? TOO_NEW : COMPATIBLE;
      }
   }

   public Component getDescription() {
      return this.description;
   }

   public Component getConfirmation() {
      return this.confirmation;
   }

   // $FF: synthetic method
   private static PackCompatibility[] $values() {
      return new PackCompatibility[]{TOO_OLD, TOO_NEW, COMPATIBLE};
   }
}
