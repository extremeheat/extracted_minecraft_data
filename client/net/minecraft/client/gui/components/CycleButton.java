package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class CycleButton<T> extends AbstractButton implements ResettableOptionWidget {
   public static final BooleanSupplier DEFAULT_ALT_LIST_SELECTOR = () -> Minecraft.getInstance().hasAltDown();
   private static final List<Boolean> BOOLEAN_OPTIONS;
   private final Supplier<T> defaultValueSupplier;
   private final Component name;
   private int index;
   private T value;
   private final ValueListSupplier<T> values;
   private final Function<T, Component> valueStringifier;
   private final Function<CycleButton<T>, MutableComponent> narrationProvider;
   private final OnValueChange<T> onValueChange;
   private final DisplayState displayState;
   private final OptionInstance.TooltipSupplier<T> tooltipSupplier;
   private final SpriteSupplier<T> spriteSupplier;

   CycleButton(int var1, int var2, int var3, int var4, Component var5, Component var6, int var7, T var8, Supplier<T> var9, ValueListSupplier<T> var10, Function<T, Component> var11, Function<CycleButton<T>, MutableComponent> var12, OnValueChange<T> var13, OptionInstance.TooltipSupplier<T> var14, DisplayState var15, SpriteSupplier<T> var16) {
      super(var1, var2, var3, var4, var5);
      this.name = var6;
      this.index = var7;
      this.defaultValueSupplier = var9;
      this.value = var8;
      this.values = var10;
      this.valueStringifier = var11;
      this.narrationProvider = var12;
      this.onValueChange = var13;
      this.displayState = var15;
      this.tooltipSupplier = var14;
      this.spriteSupplier = var16;
      this.updateTooltip();
   }

   protected void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
      Identifier var5 = this.spriteSupplier.apply(this, this.getValue());
      if (var5 != null) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, var5, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      } else {
         this.renderDefaultSprite(var1);
      }

      if (this.displayState != CycleButton.DisplayState.HIDE) {
         this.renderDefaultLabel(var1.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
      }

   }

   private void updateTooltip() {
      this.setTooltip(this.tooltipSupplier.apply(this.value));
   }

   public void onPress(InputWithModifiers var1) {
      if (var1.hasShiftDown()) {
         this.cycleValue(-1);
      } else {
         this.cycleValue(1);
      }

   }

   private void cycleValue(int var1) {
      List var2 = this.values.getSelectedList();
      this.index = Mth.positiveModulo(this.index + var1, var2.size());
      Object var3 = var2.get(this.index);
      this.updateValue(var3);
      this.onValueChange.onValueChange(this, var3);
   }

   private T getCycledValue(int var1) {
      List var2 = this.values.getSelectedList();
      return (T)var2.get(Mth.positiveModulo(this.index + var1, var2.size()));
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (var7 > 0.0) {
         this.cycleValue(-1);
      } else if (var7 < 0.0) {
         this.cycleValue(1);
      }

      return true;
   }

   public void setValue(T var1) {
      List var2 = this.values.getSelectedList();
      int var3 = var2.indexOf(var1);
      if (var3 != -1) {
         this.index = var3;
      }

      this.updateValue(var1);
   }

   public void resetValue() {
      this.setValue(this.defaultValueSupplier.get());
   }

   private void updateValue(T var1) {
      Component var2 = this.createLabelForValue(var1);
      this.setMessage(var2);
      this.value = var1;
      this.updateTooltip();
   }

   private Component createLabelForValue(T var1) {
      return (Component)(this.displayState == CycleButton.DisplayState.VALUE ? (Component)this.valueStringifier.apply(var1) : this.createFullName(var1));
   }

   private MutableComponent createFullName(T var1) {
      return CommonComponents.optionNameValue(this.name, (Component)this.valueStringifier.apply(var1));
   }

   public T getValue() {
      return this.value;
   }

   protected MutableComponent createNarrationMessage() {
      return (MutableComponent)this.narrationProvider.apply(this);
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
      if (this.active) {
         Object var2 = this.getCycledValue(1);
         Component var3 = this.createLabelForValue(var2);
         if (this.isFocused()) {
            var1.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.cycle_button.usage.focused", var3));
         } else {
            var1.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.cycle_button.usage.hovered", var3));
         }
      }

   }

   public MutableComponent createDefaultNarrationMessage() {
      return wrapDefaultNarrationMessage((Component)(this.displayState == CycleButton.DisplayState.VALUE ? this.createFullName(this.value) : this.getMessage()));
   }

   public static <T> Builder<T> builder(Function<T, Component> var0, Supplier<T> var1) {
      return new Builder<T>(var0, var1);
   }

   public static <T> Builder<T> builder(Function<T, Component> var0, T var1) {
      return new Builder<T>(var0, () -> var1);
   }

   public static Builder<Boolean> booleanBuilder(Component var0, Component var1, boolean var2) {
      return (new Builder((var2x) -> var2x == Boolean.TRUE ? var0 : var1, () -> var2)).withValues(BOOLEAN_OPTIONS);
   }

   public static Builder<Boolean> onOffBuilder(boolean var0) {
      return (new Builder((var0x) -> var0x == Boolean.TRUE ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF, () -> var0)).withValues(BOOLEAN_OPTIONS);
   }

   static {
      BOOLEAN_OPTIONS = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
   }

   public static class Builder<T> {
      private final Supplier<T> defaultValueSupplier;
      private final Function<T, Component> valueStringifier;
      private OptionInstance.TooltipSupplier<T> tooltipSupplier = (var0) -> null;
      private SpriteSupplier<T> spriteSupplier = (var0, var1x) -> null;
      private Function<CycleButton<T>, MutableComponent> narrationProvider = CycleButton::createDefaultNarrationMessage;
      private ValueListSupplier<T> values = CycleButton.ValueListSupplier.<T>create(ImmutableList.of());
      private DisplayState displayState;

      public Builder(Function<T, Component> var1, Supplier<T> var2) {
         super();
         this.displayState = CycleButton.DisplayState.NAME_AND_VALUE;
         this.valueStringifier = var1;
         this.defaultValueSupplier = var2;
      }

      public Builder<T> withValues(Collection<T> var1) {
         return this.withValues(CycleButton.ValueListSupplier.create(var1));
      }

      @SafeVarargs
      public final Builder<T> withValues(T... var1) {
         return this.withValues(ImmutableList.copyOf(var1));
      }

      public Builder<T> withValues(List<T> var1, List<T> var2) {
         return this.withValues(CycleButton.ValueListSupplier.create(CycleButton.DEFAULT_ALT_LIST_SELECTOR, var1, var2));
      }

      public Builder<T> withValues(BooleanSupplier var1, List<T> var2, List<T> var3) {
         return this.withValues(CycleButton.ValueListSupplier.create(var1, var2, var3));
      }

      public Builder<T> withValues(ValueListSupplier<T> var1) {
         this.values = var1;
         return this;
      }

      public Builder<T> withTooltip(OptionInstance.TooltipSupplier<T> var1) {
         this.tooltipSupplier = var1;
         return this;
      }

      public Builder<T> withCustomNarration(Function<CycleButton<T>, MutableComponent> var1) {
         this.narrationProvider = var1;
         return this;
      }

      public Builder<T> withSprite(SpriteSupplier<T> var1) {
         this.spriteSupplier = var1;
         return this;
      }

      public Builder<T> displayState(DisplayState var1) {
         this.displayState = var1;
         return this;
      }

      public Builder<T> displayOnlyValue() {
         return this.displayState(CycleButton.DisplayState.VALUE);
      }

      public CycleButton<T> create(Component var1, OnValueChange<T> var2) {
         return this.create(0, 0, 150, 20, var1, var2);
      }

      public CycleButton<T> create(int var1, int var2, int var3, int var4, Component var5) {
         return this.create(var1, var2, var3, var4, var5, (var0, var1x) -> {
         });
      }

      public CycleButton<T> create(int var1, int var2, int var3, int var4, Component var5, OnValueChange<T> var6) {
         List var7 = this.values.getDefaultList();
         if (var7.isEmpty()) {
            throw new IllegalStateException("No values for cycle button");
         } else {
            Object var8 = this.defaultValueSupplier.get();
            int var9 = var7.indexOf(var8);
            Component var10 = (Component)this.valueStringifier.apply(var8);
            Object var11 = this.displayState == CycleButton.DisplayState.VALUE ? var10 : CommonComponents.optionNameValue(var5, var10);
            return new CycleButton<T>(var1, var2, var3, var4, (Component)var11, var5, var9, var8, this.defaultValueSupplier, this.values, this.valueStringifier, this.narrationProvider, var6, this.tooltipSupplier, this.displayState, this.spriteSupplier);
         }
      }
   }

   public interface ValueListSupplier<T> {
      List<T> getSelectedList();

      List<T> getDefaultList();

      static <T> ValueListSupplier<T> create(Collection<T> var0) {
         final ImmutableList var1 = ImmutableList.copyOf(var0);
         return new ValueListSupplier<T>() {
            public List<T> getSelectedList() {
               return var1;
            }

            public List<T> getDefaultList() {
               return var1;
            }
         };
      }

      static <T> ValueListSupplier<T> create(final BooleanSupplier var0, List<T> var1, List<T> var2) {
         final ImmutableList var3 = ImmutableList.copyOf(var1);
         final ImmutableList var4 = ImmutableList.copyOf(var2);
         return new ValueListSupplier<T>() {
            public List<T> getSelectedList() {
               return var0.getAsBoolean() ? var4 : var3;
            }

            public List<T> getDefaultList() {
               return var3;
            }
         };
      }
   }

   public static enum DisplayState {
      NAME_AND_VALUE,
      VALUE,
      HIDE;

      private DisplayState() {
      }

      // $FF: synthetic method
      private static DisplayState[] $values() {
         return new DisplayState[]{NAME_AND_VALUE, VALUE, HIDE};
      }
   }

   @FunctionalInterface
   public interface OnValueChange<T> {
      void onValueChange(CycleButton<T> var1, T var2);
   }

   @FunctionalInterface
   public interface SpriteSupplier<T> {
      @Nullable Identifier apply(CycleButton<T> var1, T var2);
   }
}
