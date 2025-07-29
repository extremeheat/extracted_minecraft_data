package net.minecraft.server.packs.repository;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.util.InclusiveRange;

public enum PackCompatibility {
   TOO_OLD("old"),
   TOO_NEW("new"),
   UNKNOWN("unknown"),
   COMPATIBLE("compatible");

   public static final int UNKNOWN_VERSION = 2147483647;
   private final Component description;
   private final Component confirmation;

   private PackCompatibility(final String var3) {
      this.description = Component.translatable("pack.incompatible." + var3).withStyle(ChatFormatting.GRAY);
      this.confirmation = Component.translatable("pack.incompatible.confirm." + var3);
   }

   public boolean isCompatible() {
      return this == COMPATIBLE;
   }

   public static PackCompatibility forVersion(InclusiveRange<PackFormat> var0, PackFormat var1) {
      if (((PackFormat)var0.minInclusive()).major() == 2147483647) {
         return UNKNOWN;
      } else if (((PackFormat)var0.maxInclusive()).compareTo(var1) < 0) {
         return TOO_OLD;
      } else {
         return var1.compareTo((PackFormat)var0.minInclusive()) < 0 ? TOO_NEW : COMPATIBLE;
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
      return new PackCompatibility[]{TOO_OLD, TOO_NEW, UNKNOWN, COMPATIBLE};
   }
}
