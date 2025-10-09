package net.minecraft.client.gui.screens.dialog.input;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dialog.action.Action;
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
         Object var5;
         final Supplier var6;
         if (var1.multiline().isPresent()) {
            TextInput.MultilineOptions var7 = (TextInput.MultilineOptions)var1.multiline().get();
            int var8 = (Integer)var7.height().orElseGet(() -> {
               int var2 = (Integer)var7.maxLines().orElse(4);
               Objects.requireNonNull(var4);
               return Math.min(9 * var2 + 8, 512);
            });
            MultiLineEditBox var9 = MultiLineEditBox.builder().build(var4, var1.width(), var8, CommonComponents.EMPTY);
            var9.setCharacterLimit(var1.maxLength());
            Optional var10000 = var7.maxLines();
            Objects.requireNonNull(var9);
            var10000.ifPresent(var9::setLineLimit);
            var9.setValue(var1.initial());
            var5 = var9;
            Objects.requireNonNull(var9);
            var6 = var9::getValue;
         } else {
            EditBox var10 = new EditBox(var4, var1.width(), 20, var1.label());
            var10.setMaxLength(var1.maxLength());
            var10.setValue(var1.initial());
            var5 = var10;
            Objects.requireNonNull(var10);
            var6 = var10::getValue;
         }

         Object var11 = var1.labelVisible() ? CommonLayouts.labeledElement(var4, (LayoutElement)var5, var1.label()) : var5;
         var3.accept((LayoutElement)var11, new Action.ValueGetter() {
            public String asTemplateSubstitution() {
               return StringTag.escapeWithoutQuotes((String)var6.get());
            }

            public Tag asTag() {
               return StringTag.valueOf((String)var6.get());
            }
         });
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
         SingleOptionInput.Entry var4 = (SingleOptionInput.Entry)var1.initial().orElse((SingleOptionInput.Entry)var1.entries().getFirst());
         CycleButton.Builder var5 = CycleButton.builder(SingleOptionInput.Entry::displayOrDefault, var4).withValues(var1.entries()).displayOnlyValue(!var1.labelVisible());
         CycleButton var6 = var5.create(0, 0, var1.width(), 20, var1.label());
         var3.accept(var6, Action.ValueGetter.of((Supplier)(() -> ((SingleOptionInput.Entry)var6.getValue()).id())));
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

      public void addControl(final BooleanInput var1, Screen var2, InputControlHandler.Output var3) {
         Font var4 = var2.getFont();
         final Checkbox var5 = Checkbox.builder(var1.label(), var4).selected(var1.initial()).build();
         var3.accept(var5, new Action.ValueGetter() {
            public String asTemplateSubstitution() {
               return var5.selected() ? var1.onTrue() : var1.onFalse();
            }

            public Tag asTag() {
               return ByteTag.valueOf(var5.selected());
            }
         });
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
         float var4 = var1.rangeInfo().initialSliderValue();
         final SliderImpl var5 = new SliderImpl(var1, (double)var4);
         var3.accept(var5, new Action.ValueGetter() {
            public String asTemplateSubstitution() {
               return var5.stringValueToSend();
            }

            public Tag asTag() {
               return FloatTag.valueOf(var5.floatValueToSend());
            }
         });
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

         public String stringValueToSend() {
            return sliderValueToString(this.input, this.value);
         }

         public float floatValueToSend() {
            return scaledValue(this.input, this.value);
         }

         private static float scaledValue(NumberRangeInput var0, double var1) {
            return var0.rangeInfo().computeScaledValue((float)var1);
         }

         private static String sliderValueToString(NumberRangeInput var0, double var1) {
            return valueToString(scaledValue(var0, var1));
         }

         private static Component computeMessage(NumberRangeInput var0, double var1) {
            return var0.computeLabel(sliderValueToString(var0, var1));
         }

         private static String valueToString(float var0) {
            int var1 = (int)var0;
            return (float)var1 == var0 ? Integer.toString(var1) : Float.toString(var0);
         }
      }
   }
}
