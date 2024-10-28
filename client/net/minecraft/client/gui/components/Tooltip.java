package net.minecraft.client.gui.components;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class Tooltip implements NarrationSupplier {
   private static final int MAX_WIDTH = 170;
   private final Component message;
   @Nullable
   private List<FormattedCharSequence> cachedTooltip;
   @Nullable
   private Language splitWithLanguage;
   @Nullable
   private final Component narration;

   private Tooltip(Component var1, @Nullable Component var2) {
      super();
      this.message = var1;
      this.narration = var2;
   }

   public static Tooltip create(Component var0, @Nullable Component var1) {
      return new Tooltip(var0, var1);
   }

   public static Tooltip create(Component var0) {
      return new Tooltip(var0, var0);
   }

   public void updateNarration(NarrationElementOutput var1) {
      if (this.narration != null) {
         var1.add(NarratedElementType.HINT, this.narration);
      }

   }

   public List<FormattedCharSequence> toCharSequence(Minecraft var1) {
      Language var2 = Language.getInstance();
      if (this.cachedTooltip == null || var2 != this.splitWithLanguage) {
         this.cachedTooltip = splitTooltip(var1, this.message);
         this.splitWithLanguage = var2;
      }

      return this.cachedTooltip;
   }

   public static List<FormattedCharSequence> splitTooltip(Minecraft var0, Component var1) {
      return var0.font.split(var1, 170);
   }
}
