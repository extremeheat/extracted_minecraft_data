package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

public class CycleOption<T> extends Option {
   private final CycleOption.OptionSetter<T> setter;
   private final Function<Options, T> getter;
   private final Supplier<CycleButton.Builder<T>> buttonSetup;
   private Function<Minecraft, CycleButton.TooltipSupplier<T>> tooltip = (var0) -> {
      return (var0x) -> {
         return ImmutableList.of();
      };
   };

   private CycleOption(String var1, Function<Options, T> var2, CycleOption.OptionSetter<T> var3, Supplier<CycleButton.Builder<T>> var4) {
      super(var1);
      this.getter = var2;
      this.setter = var3;
      this.buttonSetup = var4;
   }

   public static <T> CycleOption<T> create(String var0, List<T> var1, Function<T, Component> var2, Function<Options, T> var3, CycleOption.OptionSetter<T> var4) {
      return new CycleOption(var0, var3, var4, () -> {
         return CycleButton.builder(var2).withValues(var1);
      });
   }

   public static <T> CycleOption<T> create(String var0, Supplier<List<T>> var1, Function<T, Component> var2, Function<Options, T> var3, CycleOption.OptionSetter<T> var4) {
      return new CycleOption(var0, var3, var4, () -> {
         return CycleButton.builder(var2).withValues((List)var1.get());
      });
   }

   public static <T> CycleOption<T> create(String var0, List<T> var1, List<T> var2, BooleanSupplier var3, Function<T, Component> var4, Function<Options, T> var5, CycleOption.OptionSetter<T> var6) {
      return new CycleOption(var0, var5, var6, () -> {
         return CycleButton.builder(var4).withValues(var3, var1, var2);
      });
   }

   public static <T> CycleOption<T> create(String var0, T[] var1, Function<T, Component> var2, Function<Options, T> var3, CycleOption.OptionSetter<T> var4) {
      return new CycleOption(var0, var3, var4, () -> {
         return CycleButton.builder(var2).withValues(var1);
      });
   }

   public static CycleOption<Boolean> createBinaryOption(String var0, Component var1, Component var2, Function<Options, Boolean> var3, CycleOption.OptionSetter<Boolean> var4) {
      return new CycleOption(var0, var3, var4, () -> {
         return CycleButton.booleanBuilder(var1, var2);
      });
   }

   public static CycleOption<Boolean> createOnOff(String var0, Function<Options, Boolean> var1, CycleOption.OptionSetter<Boolean> var2) {
      return new CycleOption(var0, var1, var2, CycleButton::onOffBuilder);
   }

   public static CycleOption<Boolean> createOnOff(String var0, Component var1, Function<Options, Boolean> var2, CycleOption.OptionSetter<Boolean> var3) {
      return createOnOff(var0, var2, var3).setTooltip((var1x) -> {
         List var2 = var1x.font.split(var1, 200);
         return (var1xx) -> {
            return var2;
         };
      });
   }

   public CycleOption<T> setTooltip(Function<Minecraft, CycleButton.TooltipSupplier<T>> var1) {
      this.tooltip = var1;
      return this;
   }

   public AbstractWidget createButton(Options var1, int var2, int var3, int var4) {
      CycleButton.TooltipSupplier var5 = (CycleButton.TooltipSupplier)this.tooltip.apply(Minecraft.getInstance());
      return ((CycleButton.Builder)this.buttonSetup.get()).withTooltip(var5).withInitialValue(this.getter.apply(var1)).create(var2, var3, var4, 20, this.getCaption(), (var2x, var3x) -> {
         this.setter.accept(var1, this, var3x);
         var1.save();
      });
   }

   @FunctionalInterface
   public interface OptionSetter<T> {
      void accept(Options var1, Option var2, T var3);
   }
}
