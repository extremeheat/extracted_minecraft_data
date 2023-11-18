package net.minecraft.client.gui.screens.worldselection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

class SwitchGrid {
   private static final int DEFAULT_SWITCH_BUTTON_WIDTH = 44;
   private final List<SwitchGrid.LabeledSwitch> switches;

   SwitchGrid(List<SwitchGrid.LabeledSwitch> var1) {
      super();
      this.switches = var1;
   }

   public void refreshStates() {
      this.switches.forEach(SwitchGrid.LabeledSwitch::refreshState);
   }

   public static SwitchGrid.Builder builder(int var0) {
      return new SwitchGrid.Builder(var0);
   }

   public static class Builder {
      final int width;
      private final List<SwitchGrid.SwitchBuilder> switchBuilders = new ArrayList<>();
      int paddingLeft;
      int rowSpacing = 4;
      int rowCount;
      Optional<SwitchGrid.InfoUnderneathSettings> infoUnderneath = Optional.empty();

      public Builder(int var1) {
         super();
         this.width = var1;
      }

      void increaseRow() {
         ++this.rowCount;
      }

      public SwitchGrid.SwitchBuilder addSwitch(Component var1, BooleanSupplier var2, Consumer<Boolean> var3) {
         SwitchGrid.SwitchBuilder var4 = new SwitchGrid.SwitchBuilder(var1, var2, var3, 44);
         this.switchBuilders.add(var4);
         return var4;
      }

      public SwitchGrid.Builder withPaddingLeft(int var1) {
         this.paddingLeft = var1;
         return this;
      }

      public SwitchGrid.Builder withRowSpacing(int var1) {
         this.rowSpacing = var1;
         return this;
      }

      public SwitchGrid build(Consumer<LayoutElement> var1) {
         GridLayout var2 = new GridLayout().rowSpacing(this.rowSpacing);
         var2.addChild(SpacerElement.width(this.width - 44), 0, 0);
         var2.addChild(SpacerElement.width(44), 0, 1);
         ArrayList var3 = new ArrayList();
         this.rowCount = 0;

         for(SwitchGrid.SwitchBuilder var5 : this.switchBuilders) {
            var3.add(var5.build(this, var2, 0));
         }

         var2.arrangeElements();
         var1.accept(var2);
         SwitchGrid var6 = new SwitchGrid(var3);
         var6.refreshStates();
         return var6;
      }

      public SwitchGrid.Builder withInfoUnderneath(int var1, boolean var2) {
         this.infoUnderneath = Optional.of(new SwitchGrid.InfoUnderneathSettings(var1, var2));
         return this;
      }
   }

   static record InfoUnderneathSettings(int a, boolean b) {
      final int maxInfoRows;
      final boolean alwaysMaxHeight;

      InfoUnderneathSettings(int var1, boolean var2) {
         super();
         this.maxInfoRows = var1;
         this.alwaysMaxHeight = var2;
      }
   }

   static record LabeledSwitch(CycleButton<Boolean> a, BooleanSupplier b, @Nullable BooleanSupplier c) {
      private final CycleButton<Boolean> button;
      private final BooleanSupplier stateSupplier;
      @Nullable
      private final BooleanSupplier isActiveCondition;

      LabeledSwitch(CycleButton<Boolean> var1, BooleanSupplier var2, @Nullable BooleanSupplier var3) {
         super();
         this.button = var1;
         this.stateSupplier = var2;
         this.isActiveCondition = var3;
      }

      public void refreshState() {
         this.button.setValue(this.stateSupplier.getAsBoolean());
         if (this.isActiveCondition != null) {
            this.button.active = this.isActiveCondition.getAsBoolean();
         }
      }
   }

   public static class SwitchBuilder {
      private final Component label;
      private final BooleanSupplier stateSupplier;
      private final Consumer<Boolean> onClicked;
      @Nullable
      private Component info;
      @Nullable
      private BooleanSupplier isActiveCondition;
      private final int buttonWidth;

      SwitchBuilder(Component var1, BooleanSupplier var2, Consumer<Boolean> var3, int var4) {
         super();
         this.label = var1;
         this.stateSupplier = var2;
         this.onClicked = var3;
         this.buttonWidth = var4;
      }

      public SwitchGrid.SwitchBuilder withIsActiveCondition(BooleanSupplier var1) {
         this.isActiveCondition = var1;
         return this;
      }

      public SwitchGrid.SwitchBuilder withInfo(Component var1) {
         this.info = var1;
         return this;
      }

      SwitchGrid.LabeledSwitch build(SwitchGrid.Builder var1, GridLayout var2, int var3) {
         var1.increaseRow();
         StringWidget var4 = new StringWidget(this.label, Minecraft.getInstance().font).alignLeft();
         var2.addChild(var4, var1.rowCount, var3, var2.newCellSettings().align(0.0F, 0.5F).paddingLeft(var1.paddingLeft));
         Optional var5 = var1.infoUnderneath;
         CycleButton.Builder var6 = CycleButton.onOffBuilder(this.stateSupplier.getAsBoolean());
         var6.displayOnlyValue();
         boolean var7 = this.info != null && var5.isEmpty();
         if (var7) {
            Tooltip var8 = Tooltip.create(this.info);
            var6.withTooltip(var1x -> var8);
         }

         if (this.info != null && !var7) {
            var6.withCustomNarration(var1x -> CommonComponents.joinForNarration(this.label, var1x.createDefaultNarrationMessage(), this.info));
         } else {
            var6.withCustomNarration(var1x -> CommonComponents.joinForNarration(this.label, var1x.createDefaultNarrationMessage()));
         }

         CycleButton var9 = var6.create(0, 0, this.buttonWidth, 20, Component.empty(), (var1x, var2x) -> this.onClicked.accept(var2x));
         if (this.isActiveCondition != null) {
            var9.active = this.isActiveCondition.getAsBoolean();
         }

         var2.addChild(var9, var1.rowCount, var3 + 1, var2.newCellSettings().alignHorizontallyRight());
         if (this.info != null) {
            var5.ifPresent(var4x -> {
               MutableComponent var5x = this.info.copy().withStyle(ChatFormatting.GRAY);
               Font var6x = Minecraft.getInstance().font;
               MultiLineTextWidget var7x = new MultiLineTextWidget(var5x, var6x);
               var7x.setMaxWidth(var1.width - var1.paddingLeft - this.buttonWidth);
               var7x.setMaxRows(var4x.maxInfoRows());
               var1.increaseRow();
               int var8x = var4x.alwaysMaxHeight ? 9 * var4x.maxInfoRows - var7x.getHeight() : 0;
               var2.addChild(var7x, var1.rowCount, var3, var2.newCellSettings().paddingTop(-var1.rowSpacing).paddingBottom(var8x));
            });
         }

         return new SwitchGrid.LabeledSwitch(var9, this.stateSupplier, this.isActiveCondition);
      }
   }
}
