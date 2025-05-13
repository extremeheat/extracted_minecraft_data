package net.minecraft.client.gui.screens.dialog.input;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dialog.input.BooleanInput;
import net.minecraft.server.dialog.input.InputControl;
import net.minecraft.server.dialog.input.NumberRangeInput;
import net.minecraft.server.dialog.input.SingleOptionInput;
import net.minecraft.server.dialog.input.TextInput;
import org.slf4j.Logger;

public class InputControlHandlers {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<MapCodec<? extends InputControl>, InputControlHandler<?>> HANDLERS = new HashMap();

   public InputControlHandlers() {
      super();
   }

   private static <T extends InputControl> void register(MapCodec<T> var0, InputControlHandler<? super T> var1) {
      HANDLERS.put(var0, var1);
   }

   @Nullable
   private static <T extends InputControl> InputControlHandler<T> get(T var0) {
      return (InputControlHandler)HANDLERS.get(var0.mapCodec());
   }

   public static <T extends InputControl> void createHandler(T var0, Screen var1, InputControlHandler.Output var2) {
      InputControlHandler var3 = get(var0);
      if (var3 == null) {
         LOGGER.warn("Unrecognized input control {}", var0);
      } else {
         var3.addControl(var0, var1, var2);
      }
   }

   public static void bootstrap() {
      register(TextInput.MAP_CODEC, new TextInputHandler());
      register(SingleOptionInput.MAP_CODEC, new SingleOptionHandler());
      register(BooleanInput.MAP_CODEC, new BooleanHandler());
      register(NumberRangeInput.MAP_CODEC, new NumberRangeHandler());
   }

   static class TextInputHandler implements InputControlHandler<TextInput> {
      TextInputHandler() {
         super();
      }

      public void addControl(TextInput var1, Screen var2, InputControlHandler.Output var3) {
         Font var4 = var2.getFont();
         EditBox var5 = new EditBox(var4, var1.width(), 20, var1.label());
         var5.setValue(var1.initial());
         Object var6 = var1.labelVisible() ? CommonLayouts.labeledElement(var4, var5, var1.label()) : var5;
         Objects.requireNonNull(var5);
         var3.accept((LayoutElement)var6, var5::getValue);
      }

      // $FF: synthetic method
      public void addControl(final InputControl var1, final Screen var2, final InputControlHandler.Output var3) {
         this.addControl((TextInput)var1, var2, var3);
      }
   }

   static class SingleOptionHandler implements InputControlHandler<SingleOptionInput> {
      SingleOptionHandler() {
         super();
      }

      public void addControl(SingleOptionInput var1, Screen var2, InputControlHandler.Output var3) {
         CycleButton.Builder var4 = CycleButton.builder(SingleOptionInput.Entry::displayOrDefault).withValues(var1.entries()).displayOnlyValue(!var1.labelVisible());
         Optional var5 = var1.initial();
         if (var5.isPresent()) {
            var4 = var4.withInitialValue((SingleOptionInput.Entry)var5.get());
         }

         CycleButton var6 = var4.create(0, 0, var1.width(), 20, var1.label());
         var3.accept(var6, () -> ((SingleOptionInput.Entry)var6.getValue()).id());
      }

      // $FF: synthetic method
      public void addControl(final InputControl var1, final Screen var2, final InputControlHandler.Output var3) {
         this.addControl((SingleOptionInput)var1, var2, var3);
      }
   }

   static class BooleanHandler implements InputControlHandler<BooleanInput> {
      BooleanHandler() {
         super();
      }

      public void addControl(BooleanInput var1, Screen var2, InputControlHandler.Output var3) {
         Font var4 = var2.getFont();
         Checkbox var5 = Checkbox.builder(var1.label(), var4).selected(var1.initial()).build();
         var3.accept(var5, () -> var5.selected() ? var1.onTrue() : var1.onFalse());
      }

      // $FF: synthetic method
      public void addControl(final InputControl var1, final Screen var2, final InputControlHandler.Output var3) {
         this.addControl((BooleanInput)var1, var2, var3);
      }
   }

   static class NumberRangeHandler implements InputControlHandler<NumberRangeInput> {
      NumberRangeHandler() {
         super();
      }

      public void addControl(NumberRangeInput var1, Screen var2, InputControlHandler.Output var3) {
         double var4 = var1.rangeInfo().initialSliderValue();
         SliderImpl var6 = new SliderImpl(var1, var4);
         Objects.requireNonNull(var6);
         var3.accept(var6, var6::valueToSend);
      }

      // $FF: synthetic method
      public void addControl(final InputControl var1, final Screen var2, final InputControlHandler.Output var3) {
         this.addControl((NumberRangeInput)var1, var2, var3);
      }

      static class SliderImpl extends AbstractSliderButton {
         private final NumberRangeInput input;

         SliderImpl(NumberRangeInput var1, double var2) {
            super(0, 0, var1.width(), 20, computeMessage(var1, var2), var2);
            this.input = var1;
         }

         protected void updateMessage() {
            this.setMessage(computeMessage(this.input, this.value));
         }

         protected void applyValue() {
         }

         public String valueToSend() {
            return sliderValueToString(this.input, this.value);
         }

         private static String sliderValueToString(NumberRangeInput var0, double var1) {
            return valueToString(var0.rangeInfo().computeScaledValue(var1));
         }

         private static Component computeMessage(NumberRangeInput var0, double var1) {
            return var0.computeLabel(sliderValueToString(var0, var1));
         }

         private static String valueToString(double var0) {
            long var2 = (long)var0;
            return (double)var2 == var0 ? Long.toString(var2) : Double.toString(var0);
         }
      }
   }
}
