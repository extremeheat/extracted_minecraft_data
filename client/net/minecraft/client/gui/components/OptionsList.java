package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class OptionsList extends ContainerObjectSelectionList<AbstractEntry> {
   private static final int BIG_BUTTON_WIDTH = 310;
   private static final int DEFAULT_ITEM_HEIGHT = 25;
   private final OptionsSubScreen screen;

   public OptionsList(final Minecraft minecraft, final int width, final OptionsSubScreen screen) {
      super(minecraft, width, screen.layout.getContentHeight(), screen.layout.getHeaderHeight(), 25);
      this.centerListVertically = false;
      this.screen = screen;
   }

   public void addBig(final OptionInstance<?> option) {
      this.addEntry(OptionsList.Entry.big(this.minecraft.options, option, this.screen));
   }

   public void addSmall(final OptionInstance<?>... options) {
      for(int i = 0; i < options.length; i += 2) {
         OptionInstance<?> secondOption = i < options.length - 1 ? options[i + 1] : null;
         this.addEntry(OptionsList.Entry.small(this.minecraft.options, options[i], secondOption, this.screen));
      }

   }

   public void addSmall(final List<AbstractWidget> widgets) {
      for(int i = 0; i < widgets.size(); i += 2) {
         this.addSmall((AbstractWidget)widgets.get(i), i < widgets.size() - 1 ? (AbstractWidget)widgets.get(i + 1) : null);
      }

   }

   public void addSmall(final AbstractWidget firstOption, final @Nullable AbstractWidget secondOption) {
      this.addEntry(OptionsList.Entry.small(firstOption, secondOption, this.screen));
   }

   public void addSmall(final AbstractWidget firstOption, final OptionInstance<?> firstOptionInstance, final @Nullable AbstractWidget secondOption) {
      this.addEntry(OptionsList.Entry.small((AbstractWidget)firstOption, firstOptionInstance, secondOption, (Screen)this.screen));
   }

   public void addHeader(final Component text) {
      Objects.requireNonNull(this.minecraft.font);
      int lineHeight = 9;
      int paddingTop = this.children().isEmpty() ? 0 : lineHeight * 2;
      this.addEntry(new HeaderEntry(this.screen, text, paddingTop), paddingTop + lineHeight + 4);
   }

   public int getRowWidth() {
      return 310;
   }

   public @Nullable AbstractWidget findOption(final OptionInstance<?> option) {
      for(AbstractEntry child : this.children()) {
         if (child instanceof Entry entry) {
            AbstractWidget widgetForOption = entry.findOption(option);
            if (widgetForOption != null) {
               return widgetForOption;
            }
         }
      }

      return null;
   }

   public void applyUnsavedChanges() {
      for(AbstractEntry child : this.children()) {
         if (child instanceof Entry entry) {
            for(OptionInstanceWidget optionInstanceWidget : entry.children) {
               if (optionInstanceWidget.optionInstance() != null) {
                  AbstractWidget var7 = optionInstanceWidget.widget();
                  if (var7 instanceof OptionInstance.OptionInstanceSliderButton) {
                     OptionInstance.OptionInstanceSliderButton<?> optionSlider = (OptionInstance.OptionInstanceSliderButton)var7;
                     optionSlider.applyUnsavedValue();
                  }
               }
            }
         }
      }

   }

   public void resetOption(final OptionInstance<?> option) {
      for(AbstractEntry child : this.children()) {
         if (child instanceof Entry entry) {
            for(OptionInstanceWidget optionInstanceWidget : entry.children) {
               if (optionInstanceWidget.optionInstance() == option) {
                  AbstractWidget var8 = optionInstanceWidget.widget();
                  if (var8 instanceof ResettableOptionWidget) {
                     ResettableOptionWidget resettableOptionWidget = (ResettableOptionWidget)var8;
                     resettableOptionWidget.resetValue();
                     return;
                  }
               }
            }
         }
      }

   }

   protected abstract static class AbstractEntry extends ContainerObjectSelectionList.Entry<AbstractEntry> {
      protected AbstractEntry() {
         super();
      }
   }

   protected static class HeaderEntry extends AbstractEntry {
      private final Screen screen;
      private final int paddingTop;
      private final StringWidget widget;

      protected HeaderEntry(final Screen screen, final Component text, final int paddingTop) {
         super();
         this.screen = screen;
         this.paddingTop = paddingTop;
         this.widget = new StringWidget(text, screen.getFont());
      }

      public List<? extends NarratableEntry> narratables() {
         return List.of(this.widget);
      }

      public void renderContent(final GuiGraphics graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         this.widget.setPosition(this.screen.width / 2 - 155, this.getContentY() + this.paddingTop);
         this.widget.render(graphics, mouseX, mouseY, a);
      }

      public List<? extends GuiEventListener> children() {
         return List.of(this.widget);
      }
   }

   protected static class Entry extends AbstractEntry {
      private final List<OptionInstanceWidget> children;
      private final Screen screen;
      private static final int X_OFFSET = 160;

      private Entry(final List<OptionInstanceWidget> widgets, final Screen screen) {
         super();
         this.children = widgets;
         this.screen = screen;
      }

      public static Entry big(final Options options, final OptionInstance<?> optionInstance, final Screen screen) {
         return new Entry(List.of(new OptionInstanceWidget(optionInstance.createButton(options, 0, 0, 310), optionInstance)), screen);
      }

      public static Entry small(final AbstractWidget leftWidget, final @Nullable AbstractWidget rightWidget, final Screen screen) {
         return rightWidget == null ? new Entry(List.of(new OptionInstanceWidget(leftWidget)), screen) : new Entry(List.of(new OptionInstanceWidget(leftWidget), new OptionInstanceWidget(rightWidget)), screen);
      }

      public static Entry small(final AbstractWidget leftWidget, final OptionInstance<?> leftWidgetOptionInstance, final @Nullable AbstractWidget rightWidget, final Screen screen) {
         return rightWidget == null ? new Entry(List.of(new OptionInstanceWidget(leftWidget, leftWidgetOptionInstance)), screen) : new Entry(List.of(new OptionInstanceWidget(leftWidget, leftWidgetOptionInstance), new OptionInstanceWidget(rightWidget)), screen);
      }

      public static Entry small(final Options options, final OptionInstance<?> optionA, final @Nullable OptionInstance<?> optionB, final OptionsSubScreen screen) {
         AbstractWidget buttonA = optionA.createButton(options);
         return optionB == null ? new Entry(List.of(new OptionInstanceWidget(buttonA, optionA)), screen) : new Entry(List.of(new OptionInstanceWidget(buttonA, optionA), new OptionInstanceWidget(optionB.createButton(options), optionB)), screen);
      }

      public void renderContent(final GuiGraphics graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         int xOffset = 0;
         int x = this.screen.width / 2 - 155;

         for(OptionInstanceWidget optionInstanceWidget : this.children) {
            optionInstanceWidget.widget().setPosition(x + xOffset, this.getContentY());
            optionInstanceWidget.widget().render(graphics, mouseX, mouseY, a);
            xOffset += 160;
         }

      }

      public List<? extends GuiEventListener> children() {
         return Lists.transform(this.children, OptionInstanceWidget::widget);
      }

      public List<? extends NarratableEntry> narratables() {
         return Lists.transform(this.children, OptionInstanceWidget::widget);
      }

      public @Nullable AbstractWidget findOption(final OptionInstance<?> option) {
         for(OptionInstanceWidget child : this.children) {
            if (child.optionInstance == option) {
               return child.widget();
            }
         }

         return null;
      }
   }

   public static record OptionInstanceWidget(AbstractWidget widget, @Nullable OptionInstance<?> optionInstance) {
      public OptionInstanceWidget(final AbstractWidget widget) {
         this(widget, (OptionInstance)null);
      }

      public OptionInstanceWidget {
         super();
      }
   }
}
