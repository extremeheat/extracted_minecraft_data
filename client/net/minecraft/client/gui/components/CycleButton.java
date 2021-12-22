package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class CycleButton<T> extends AbstractButton implements TooltipAccessor {
   static final BooleanSupplier DEFAULT_ALT_LIST_SELECTOR = Screen::hasAltDown;
   private static final List<Boolean> BOOLEAN_OPTIONS;
   private final Component name;
   private int index;
   private T value;
   private final CycleButton.ValueListSupplier<T> values;
   private final Function<T, Component> valueStringifier;
   private final Function<CycleButton<T>, MutableComponent> narrationProvider;
   private final CycleButton.OnValueChange<T> onValueChange;
   private final CycleButton.TooltipSupplier<T> tooltipSupplier;
   private final boolean displayOnlyValue;

   CycleButton(int var1, int var2, int var3, int var4, Component var5, Component var6, int var7, T var8, CycleButton.ValueListSupplier<T> var9, Function<T, Component> var10, Function<CycleButton<T>, MutableComponent> var11, CycleButton.OnValueChange<T> var12, CycleButton.TooltipSupplier<T> var13, boolean var14) {
      super(var1, var2, var3, var4, var5);
      this.name = var6;
      this.index = var7;
      this.value = var8;
      this.values = var9;
      this.valueStringifier = var10;
      this.narrationProvider = var11;
      this.onValueChange = var12;
      this.tooltipSupplier = var13;
      this.displayOnlyValue = var14;
   }

   public void onPress() {
      if (Screen.hasShiftDown()) {
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
      return var2.get(Mth.positiveModulo(this.index + var1, var2.size()));
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (var5 > 0.0D) {
         this.cycleValue(-1);
      } else if (var5 < 0.0D) {
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

   private void updateValue(T var1) {
      Component var2 = this.createLabelForValue(var1);
      this.setMessage(var2);
      this.value = var1;
   }

   private Component createLabelForValue(T var1) {
      return (Component)(this.displayOnlyValue ? (Component)this.valueStringifier.apply(var1) : this.createFullName(var1));
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

   public void updateNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
      if (this.active) {
         Object var2 = this.getCycledValue(1);
         Component var3 = this.createLabelForValue(var2);
         if (this.isFocused()) {
            var1.add(NarratedElementType.USAGE, (Component)(new TranslatableComponent("narration.cycle_button.usage.focused", new Object[]{var3})));
         } else {
            var1.add(NarratedElementType.USAGE, (Component)(new TranslatableComponent("narration.cycle_button.usage.hovered", new Object[]{var3})));
         }
      }

   }

   public MutableComponent createDefaultNarrationMessage() {
      return wrapDefaultNarrationMessage((Component)(this.displayOnlyValue ? this.createFullName(this.value) : this.getMessage()));
   }

   public List<FormattedCharSequence> getTooltip() {
      return (List)this.tooltipSupplier.apply(this.value);
   }

   public static <T> CycleButton.Builder<T> builder(Function<T, Component> var0) {
      return new CycleButton.Builder(var0);
   }

   public static CycleButton.Builder<Boolean> booleanBuilder(Component var0, Component var1) {
      return (new CycleButton.Builder((var2) -> {
         return var2 ? var0 : var1;
      })).withValues(BOOLEAN_OPTIONS);
   }

   public static CycleButton.Builder<Boolean> onOffBuilder() {
      return (new CycleButton.Builder((var0) -> {
         return var0 ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
      })).withValues(BOOLEAN_OPTIONS);
   }

   public static CycleButton.Builder<Boolean> onOffBuilder(boolean var0) {
      return onOffBuilder().withInitialValue(var0);
   }

   static {
      BOOLEAN_OPTIONS = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
   }

   private interface ValueListSupplier<T> {
      List<T> getSelectedList();

      List<T> getDefaultList();

      static <T> CycleButton.ValueListSupplier<T> create(List<T> var0) {
         final ImmutableList var1 = ImmutableList.copyOf(var0);
         return new CycleButton.ValueListSupplier<T>() {
            public List<T> getSelectedList() {
               return var1;
            }

            public List<T> getDefaultList() {
               return var1;
            }
         };
      }

      static <T> CycleButton.ValueListSupplier<T> create(final BooleanSupplier var0, List<T> var1, List<T> var2) {
         final ImmutableList var3 = ImmutableList.copyOf(var1);
         final ImmutableList var4 = ImmutableList.copyOf(var2);
         return new CycleButton.ValueListSupplier<T>() {
            public List<T> getSelectedList() {
               return var0.getAsBoolean() ? var4 : var3;
            }

            public List<T> getDefaultList() {
               return var3;
            }
         };
      }
   }

   public interface OnValueChange<T> {
      void onValueChange(CycleButton var1, T var2);
   }

   @FunctionalInterface
   public interface TooltipSupplier<T> extends Function<T, List<FormattedCharSequence>> {
   }

   public static class Builder<T> {
      private int initialIndex;
      @Nullable
      private T initialValue;
      private final Function<T, Component> valueStringifier;
      private CycleButton.TooltipSupplier<T> tooltipSupplier = (var0) -> {
         return ImmutableList.of();
      };
      private Function<CycleButton<T>, MutableComponent> narrationProvider = CycleButton::createDefaultNarrationMessage;
      private CycleButton.ValueListSupplier<T> values = CycleButton.ValueListSupplier.create(ImmutableList.of());
      private boolean displayOnlyValue;

      public Builder(Function<T, Component> var1) {
         super();
         this.valueStringifier = var1;
      }

      public CycleButton.Builder<T> withValues(List<T> var1) {
         this.values = CycleButton.ValueListSupplier.create(var1);
         return this;
      }

      @SafeVarargs
      public final CycleButton.Builder<T> withValues(T... var1) {
         return this.withValues((List)ImmutableList.copyOf(var1));
      }

      public CycleButton.Builder<T> withValues(List<T> var1, List<T> var2) {
         this.values = CycleButton.ValueListSupplier.create(CycleButton.DEFAULT_ALT_LIST_SELECTOR, var1, var2);
         return this;
      }

      public CycleButton.Builder<T> withValues(BooleanSupplier var1, List<T> var2, List<T> var3) {
         this.values = CycleButton.ValueListSupplier.create(var1, var2, var3);
         return this;
      }

      public CycleButton.Builder<T> withTooltip(CycleButton.TooltipSupplier<T> var1) {
         this.tooltipSupplier = var1;
         return this;
      }

      public CycleButton.Builder<T> withInitialValue(T var1) {
         this.initialValue = var1;
         int var2 = this.values.getDefaultList().indexOf(var1);
         if (var2 != -1) {
            this.initialIndex = var2;
         }

         return this;
      }

      public CycleButton.Builder<T> withCustomNarration(Function<CycleButton<T>, MutableComponent> var1) {
         this.narrationProvider = var1;
         return this;
      }

      public CycleButton.Builder<T> displayOnlyValue() {
         this.displayOnlyValue = true;
         return this;
      }

      public CycleButton<T> create(int var1, int var2, int var3, int var4, Component var5) {
         return this.create(var1, var2, var3, var4, var5, (var0, var1x) -> {
         });
      }

      public CycleButton<T> create(int var1, int var2, int var3, int var4, Component var5, CycleButton.OnValueChange<T> var6) {
         List var7 = this.values.getDefaultList();
         if (var7.isEmpty()) {
            throw new IllegalStateException("No values for cycle button");
         } else {
            Object var8 = this.initialValue != null ? this.initialValue : var7.get(this.initialIndex);
            Component var9 = (Component)this.valueStringifier.apply(var8);
            Object var10 = this.displayOnlyValue ? var9 : CommonComponents.optionNameValue(var5, var9);
            return new CycleButton(var1, var2, var3, var4, (Component)var10, var5, this.initialIndex, var8, this.values, this.valueStringifier, this.narrationProvider, var6, this.tooltipSupplier, this.displayOnlyValue);
         }
      }
   }
}
