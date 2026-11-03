package net.minecraft.client.gui.components;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class Tooltip implements NarrationSupplier {
   private static final int MAX_WIDTH = 170;
   private final Component message;
   private @Nullable List<FormattedCharSequence> cachedTooltip;
   private @Nullable Language splitWithLanguage;
   private final @Nullable Identifier style;
   private final @Nullable Component narration;
   private final Optional<TooltipComponent> component;
   private final boolean extraSpaceAfterFirstLine;

   private Tooltip(final Component message, final @Nullable Component narration, final Optional<TooltipComponent> component, final @Nullable Identifier style) {
      this(message, narration, component, style, false);
   }

   private Tooltip(final Component message, final @Nullable Component narration, final Optional<TooltipComponent> component, final @Nullable Identifier style, final boolean extraSpaceAfterFirstLine) {
      super();
      this.message = message;
      this.narration = narration;
      this.component = component;
      this.style = style;
      this.extraSpaceAfterFirstLine = extraSpaceAfterFirstLine;
   }

   public static Tooltip create(final Component message) {
      return new Tooltip(message, message, Optional.empty(), (Identifier)null);
   }

   public static Tooltip create(final Component message, final @Nullable Component narration) {
      return new Tooltip(message, narration, Optional.empty(), (Identifier)null);
   }

   public static Tooltip create(final Component message, final Optional<TooltipComponent> component, final @Nullable Identifier style) {
      return new Tooltip(message, message, component, style);
   }

   public static Tooltip createForItem(final List<Component> lines, final ItemStack item) {
      Component message = CommonComponents.joinLines((Collection)lines);
      Component narration = lines.size() > 1 ? CommonComponents.joinLines((Collection)lines.subList(1, lines.size())) : null;
      return new Tooltip(message, narration, item.getTooltipImage(), (Identifier)item.get(DataComponents.TOOLTIP_STYLE), true);
   }

   public Optional<TooltipComponent> component() {
      return this.component;
   }

   public @Nullable Identifier style() {
      return this.style;
   }

   public boolean extraSpaceAfterFirstLine() {
      return this.extraSpaceAfterFirstLine;
   }

   public void updateNarration(final NarrationElementOutput output) {
      if (this.narration != null) {
         output.add(NarratedElementType.HINT, this.narration);
      }

   }

   public List<FormattedCharSequence> toCharSequence(final Minecraft minecraft) {
      Language currentLanguage = Language.getInstance();
      if (this.cachedTooltip == null || currentLanguage != this.splitWithLanguage) {
         this.cachedTooltip = splitTooltip(minecraft, this.message);
         this.splitWithLanguage = currentLanguage;
      }

      return this.cachedTooltip;
   }

   public static List<FormattedCharSequence> splitTooltip(final Minecraft minecraft, final Component message) {
      return minecraft.font.split(message, 170);
   }
}
