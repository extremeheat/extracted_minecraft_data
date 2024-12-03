package net.minecraft.client.gui.screens.worldselection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

class SwitchGrid {
   private static final int DEFAULT_SWITCH_BUTTON_WIDTH = 44;
   private final List<LabeledSwitch> switches;
   private final Layout layout;

   SwitchGrid(List<LabeledSwitch> var1, Layout var2) {
      super();
      this.switches = var1;
      this.layout = var2;
   }

   public Layout layout() {
      return this.layout;
   }

   public void refreshStates() {
      this.switches.forEach(LabeledSwitch::refreshState);
   }

   public static Builder builder(int var0) {
      return new Builder(var0);
   }

   public static class Builder {
      final int width;
      private final List<SwitchBuilder> switchBuilders = new ArrayList();
      int paddingLeft;
      int rowSpacing = 4;
      int rowCount;
      Optional<InfoUnderneathSettings> infoUnderneath = Optional.empty();

      public Builder(int var1) {
         super();
         this.width = var1;
      }

      void increaseRow() {
         ++this.rowCount;
      }

      public SwitchBuilder addSwitch(Component var1, BooleanSupplier var2, Consumer<Boolean> var3) {
         SwitchBuilder var4 = new SwitchBuilder(var1, var2, var3, 44);
         this.switchBuilders.add(var4);
         return var4;
      }

      public Builder withPaddingLeft(int var1) {
         this.paddingLeft = var1;
         return this;
      }

      public Builder withRowSpacing(int var1) {
         this.rowSpacing = var1;
         return this;
      }

      public SwitchGrid build() {
         GridLayout var1 = (new GridLayout()).rowSpacing(this.rowSpacing);
         var1.addChild(SpacerElement.width(this.width - 44), 0, 0);
         var1.addChild(SpacerElement.width(44), 0, 1);
         ArrayList var2 = new ArrayList();
         this.rowCount = 0;

         for(SwitchBuilder var4 : this.switchBuilders) {
            var2.add(var4.build(this, var1, 0));
         }

         var1.arrangeElements();
         SwitchGrid var5 = new SwitchGrid(var2, var1);
         var5.refreshStates();
         return var5;
      }

      public Builder withInfoUnderneath(int var1, boolean var2) {
         this.infoUnderneath = Optional.of(new InfoUnderneathSettings(var1, var2));
         return this;
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

      public SwitchBuilder withIsActiveCondition(BooleanSupplier var1) {
         this.isActiveCondition = var1;
         return this;
      }

      public SwitchBuilder withInfo(Component var1) {
         this.info = var1;
         return this;
      }

      LabeledSwitch build(Builder var1, GridLayout var2, int var3) {
         var1.increaseRow();
         StringWidget var4 = (new StringWidget(this.label, Minecraft.getInstance().font)).alignLeft();
         var2.addChild(var4, var1.rowCount, var3, var2.newCellSettings().align(0.0F, 0.5F).paddingLeft(var1.paddingLeft));
         Optional var5 = var1.infoUnderneath;
         CycleButton.Builder var6 = CycleButton.onOffBuilder(this.stateSupplier.getAsBoolean());
         var6.displayOnlyValue();
         boolean var7 = this.info != null && var5.isEmpty();
         if (var7) {
            Tooltip var8 = Tooltip.create(this.info);
            var6.withTooltip((var1x) -> var8);
         }

         if (this.info != null && !var7) {
            var6.withCustomNarration((var1x) -> CommonComponents.joinForNarration(this.label, var1x.createDefaultNarrationMessage(), this.info));
         } else {
            var6.withCustomNarration((var1x) -> CommonComponents.joinForNarration(this.label, var1x.createDefaultNarrationMessage()));
         }

         CycleButton var9 = var6.create(0, 0, this.buttonWidth, 20, Component.empty(), (var1x, var2x) -> this.onClicked.accept(var2x));
         if (this.isActiveCondition != null) {
            var9.active = this.isActiveCondition.getAsBoolean();
         }

         var2.addChild(var9, var1.rowCount, var3 + 1, var2.newCellSettings().alignHorizontallyRight());
         if (this.info != null) {
            var5.ifPresent((var4x) -> {
               MutableComponent var5 = this.info.copy().withStyle(ChatFormatting.GRAY);
               Font var6 = Minecraft.getInstance().font;
               MultiLineTextWidget var7 = new MultiLineTextWidget(var5, var6);
               var7.setMaxWidth(var1.width - var1.paddingLeft - this.buttonWidth);
               var7.setMaxRows(var4x.maxInfoRows());
               var1.increaseRow();
               int var10000;
               if (var4x.alwaysMaxHeight) {
                  Objects.requireNonNull(var6);
                  var10000 = 9 * var4x.maxInfoRows - var7.getHeight();
               } else {
                  var10000 = 0;
               }

               int var8 = var10000;
               var2.addChild(var7, var1.rowCount, var3, var2.newCellSettings().paddingTop(-var1.rowSpacing).paddingBottom(var8));
            });
         }

         return new LabeledSwitch(var9, this.stateSupplier, this.isActiveCondition);
      }
   }

   static record LabeledSwitch(CycleButton<Boolean> button, BooleanSupplier stateSupplier, @Nullable BooleanSupplier isActiveCondition) {
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

   static record InfoUnderneathSettings(int maxInfoRows, boolean alwaysMaxHeight) {
      final int maxInfoRows;
      final boolean alwaysMaxHeight;

      InfoUnderneathSettings(int var1, boolean var2) {
         super();
         this.maxInfoRows = var1;
         this.alwaysMaxHeight = var2;
      }
   }
}
