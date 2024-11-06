package net.minecraft.client.gui.screens.telemetry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TelemetryEventWidget extends AbstractTextAreaWidget {
   private static final int HEADER_HORIZONTAL_PADDING = 32;
   private static final String TELEMETRY_REQUIRED_TRANSLATION_KEY = "telemetry.event.required";
   private static final String TELEMETRY_OPTIONAL_TRANSLATION_KEY = "telemetry.event.optional";
   private static final String TELEMETRY_OPTIONAL_DISABLED_TRANSLATION_KEY = "telemetry.event.optional.disabled";
   private static final Component PROPERTY_TITLE;
   private final Font font;
   private Content content;
   @Nullable
   private DoubleConsumer onScrolledListener;

   public TelemetryEventWidget(int var1, int var2, int var3, int var4, Font var5) {
      super(var1, var2, var3, var4, Component.empty());
      this.font = var5;
      this.content = this.buildContent(Minecraft.getInstance().telemetryOptInExtra());
   }

   public void onOptInChanged(boolean var1) {
      this.content = this.buildContent(var1);
      this.refreshScrollAmount();
   }

   public void updateLayout() {
      this.content = this.buildContent(Minecraft.getInstance().telemetryOptInExtra());
      this.refreshScrollAmount();
   }

   private Content buildContent(boolean var1) {
      ContentBuilder var2 = new ContentBuilder(this.containerWidth());
      ArrayList var3 = new ArrayList(TelemetryEventType.values());
      var3.sort(Comparator.comparing(TelemetryEventType::isOptIn));

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         TelemetryEventType var5 = (TelemetryEventType)var3.get(var4);
         boolean var6 = var5.isOptIn() && !var1;
         this.addEventType(var2, var5, var6);
         if (var4 < var3.size() - 1) {
            Objects.requireNonNull(this.font);
            var2.addSpacer(9);
         }
      }

      return var2.build();
   }

   public void setOnScrolledListener(@Nullable DoubleConsumer var1) {
      this.onScrolledListener = var1;
   }

   public void setScrollAmount(double var1) {
      super.setScrollAmount(var1);
      if (this.onScrolledListener != null) {
         this.onScrolledListener.accept(this.scrollAmount());
      }

   }

   protected int getInnerHeight() {
      return this.content.container().getHeight();
   }

   protected double scrollRate() {
      Objects.requireNonNull(this.font);
      return 9.0;
   }

   protected void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
      int var5 = this.getInnerTop();
      int var6 = this.getInnerLeft();
      var1.pose().pushPose();
      var1.pose().translate((double)var6, (double)var5, 0.0);
      this.content.container().visitWidgets((var4x) -> {
         var4x.render(var1, var2, var3, var4);
      });
      var1.pose().popPose();
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.content.narration());
   }

   private Component grayOutIfDisabled(Component var1, boolean var2) {
      return (Component)(var2 ? var1.copy().withStyle(ChatFormatting.GRAY) : var1);
   }

   private void addEventType(ContentBuilder var1, TelemetryEventType var2, boolean var3) {
      String var4 = var2.isOptIn() ? (var3 ? "telemetry.event.optional.disabled" : "telemetry.event.optional") : "telemetry.event.required";
      var1.addHeader(this.font, this.grayOutIfDisabled(Component.translatable(var4, var2.title()), var3));
      var1.addHeader(this.font, var2.description().withStyle(ChatFormatting.GRAY));
      Objects.requireNonNull(this.font);
      var1.addSpacer(9 / 2);
      var1.addLine(this.font, this.grayOutIfDisabled(PROPERTY_TITLE, var3), 2);
      this.addEventTypeProperties(var2, var1, var3);
   }

   private void addEventTypeProperties(TelemetryEventType var1, ContentBuilder var2, boolean var3) {
      Iterator var4 = var1.properties().iterator();

      while(var4.hasNext()) {
         TelemetryProperty var5 = (TelemetryProperty)var4.next();
         var2.addLine(this.font, this.grayOutIfDisabled(var5.title(), var3));
      }

   }

   private int containerWidth() {
      return this.width - this.totalInnerPadding();
   }

   static {
      PROPERTY_TITLE = Component.translatable("telemetry_info.property_title").withStyle(ChatFormatting.UNDERLINE);
   }

   private static record Content(Layout container, Component narration) {
      Content(Layout var1, Component var2) {
         super();
         this.container = var1;
         this.narration = var2;
      }

      public Layout container() {
         return this.container;
      }

      public Component narration() {
         return this.narration;
      }
   }

   private static class ContentBuilder {
      private final int width;
      private final LinearLayout layout;
      private final MutableComponent narration = Component.empty();

      public ContentBuilder(int var1) {
         super();
         this.width = var1;
         this.layout = LinearLayout.vertical();
         this.layout.defaultCellSetting().alignHorizontallyLeft();
         this.layout.addChild(SpacerElement.width(var1));
      }

      public void addLine(Font var1, Component var2) {
         this.addLine(var1, var2, 0);
      }

      public void addLine(Font var1, Component var2, int var3) {
         this.layout.addChild((new MultiLineTextWidget(var2, var1)).setMaxWidth(this.width), (Consumer)((var1x) -> {
            var1x.paddingBottom(var3);
         }));
         this.narration.append(var2).append("\n");
      }

      public void addHeader(Font var1, Component var2) {
         this.layout.addChild((new MultiLineTextWidget(var2, var1)).setMaxWidth(this.width - 64).setCentered(true), (Consumer)((var0) -> {
            var0.alignHorizontallyCenter().paddingHorizontal(32);
         }));
         this.narration.append(var2).append("\n");
      }

      public void addSpacer(int var1) {
         this.layout.addChild(SpacerElement.height(var1));
      }

      public Content build() {
         this.layout.arrangeElements();
         return new Content(this.layout, this.narration);
      }
   }
}
