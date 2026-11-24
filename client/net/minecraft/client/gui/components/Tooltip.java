package net.minecraft.client.gui.components;

import java.util.ArrayList;
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
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;

public class Tooltip implements NarrationSupplier {
   private static final int MAX_WIDTH = 170;
   private final @Nullable Component title;
   private final List<Component> messages;
   private @Nullable List<FormattedCharSequence> cachedTooltip;
   private int cachedTitleLineCount;
   private @Nullable Language splitWithLanguage;
   private final @Nullable Identifier style;
   private final @Nullable Component narration;
   private final Optional<TooltipComponent> component;
   private final boolean extraSpaceAfterTitle;
   private final boolean besideCursor;
   private final boolean splitLines;

   private Tooltip(final Component message, final @Nullable Component narration, final Optional<TooltipComponent> component, final @Nullable Identifier style) {
      this((Component)null, List.of(message), narration, component, style, false, false, true);
   }

   private Tooltip(final @Nullable Component title, final List<Component> messages, final @Nullable Component narration, final Optional<TooltipComponent> component, final @Nullable Identifier style, final boolean extraSpaceAfterTitle, final boolean besideCursor, final boolean splitLines) {
      super();
      this.title = title;
      this.messages = messages;
      this.narration = narration;
      this.component = component;
      this.style = style;
      this.extraSpaceAfterTitle = extraSpaceAfterTitle;
      this.besideCursor = besideCursor;
      this.splitLines = splitLines;
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
      Component narration = lines.size() > 1 ? CommonComponents.joinLines((Collection)lines.subList(1, lines.size())) : null;
      return createForItem(lines, narration, item);
   }

   public static Tooltip createForItem(final List<Component> lines, final @Nullable Component narration, final ItemStack item) {
      Component title = lines.isEmpty() ? null : (Component)lines.getFirst();
      List<Component> messages = lines.isEmpty() ? List.of() : lines.subList(1, lines.size());
      Identifier tooltipStyle = (Identifier)item.get(DataComponents.TOOLTIP_STYLE);
      TooltipDisplay tooltipDisplay = (TooltipDisplay)item.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
      return new Tooltip(title, messages, narration, item.getTooltipImage(), tooltipStyle, true, true, tooltipDisplay.splitLines());
   }

   public Optional<TooltipComponent> component() {
      return this.component;
   }

   public @Nullable Identifier style() {
      return this.style;
   }

   public int linesBeforeExtraSpace(final Minecraft minecraft) {
      if (!this.extraSpaceAfterTitle) {
         return 0;
      } else {
         this.toCharSequence(minecraft);
         return this.cachedTitleLineCount;
      }
   }

   public boolean besideCursor() {
      return this.besideCursor;
   }

   public void updateNarration(final NarrationElementOutput output) {
      if (this.narration != null) {
         output.add(NarratedElementType.HINT, this.narration);
      }

   }

   public List<FormattedCharSequence> toCharSequence(final Minecraft minecraft) {
      Language currentLanguage = Language.getInstance();
      if (this.cachedTooltip == null || currentLanguage != this.splitWithLanguage) {
         List<FormattedCharSequence> lines = new ArrayList();
         if (this.splitLines) {
            if (this.title != null) {
               lines.addAll(splitTooltip(minecraft, this.title));
            }

            this.cachedTitleLineCount = lines.size();
            lines.addAll(splitTooltip(minecraft, CommonComponents.joinLines((Collection)this.messages)));
         } else {
            if (this.title != null) {
               lines.add(this.title.getVisualOrderText());
            }

            this.cachedTitleLineCount = 1;

            for(Component message : this.messages) {
               lines.add(message.getVisualOrderText());
            }
         }

         this.cachedTooltip = lines;
         this.splitWithLanguage = currentLanguage;
      }

      return this.cachedTooltip;
   }

   public static List<FormattedCharSequence> splitTooltip(final Minecraft minecraft, final Component message) {
      return minecraft.font.split(message, 170);
   }
}
