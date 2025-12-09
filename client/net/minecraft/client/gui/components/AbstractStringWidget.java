package net.minecraft.client.gui.components;

import java.util.function.Consumer;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.Nullable;

public abstract class AbstractStringWidget extends AbstractWidget {
   private @Nullable Consumer<Style> componentClickHandler = null;
   private final Font font;

   public AbstractStringWidget(int var1, int var2, int var3, int var4, Component var5, Font var6) {
      super(var1, var2, var3, var4, var5);
      this.font = var6;
   }

   public abstract void visitLines(ActiveTextCollector var1);

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      GuiGraphics.HoveredTextEffects var5;
      if (this.isHovered()) {
         if (this.componentClickHandler != null) {
            var5 = GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR;
         } else {
            var5 = GuiGraphics.HoveredTextEffects.TOOLTIP_ONLY;
         }
      } else {
         var5 = GuiGraphics.HoveredTextEffects.NONE;
      }

      this.visitLines(var1.textRendererForWidget(this, var5));
   }

   public void onClick(MouseButtonEvent var1, boolean var2) {
      if (this.componentClickHandler != null) {
         ActiveTextCollector.ClickableStyleFinder var3 = new ActiveTextCollector.ClickableStyleFinder(this.getFont(), (int)var1.x(), (int)var1.y());
         this.visitLines(var3);
         Style var4 = var3.result();
         if (var4 != null) {
            this.componentClickHandler.accept(var4);
            return;
         }
      }

      super.onClick(var1, var2);
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
   }

   protected final Font getFont() {
      return this.font;
   }

   public void setMessage(Component var1) {
      super.setMessage(var1);
      this.setWidth(this.getFont().width(var1.getVisualOrderText()));
   }

   public AbstractStringWidget setComponentClickHandler(@Nullable Consumer<Style> var1) {
      this.componentClickHandler = var1;
      return this;
   }
}
