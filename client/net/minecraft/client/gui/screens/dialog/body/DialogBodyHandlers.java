package net.minecraft.client.gui.screens.dialog.body;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.ItemDisplayWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.dialog.DialogScreen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Style;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.ItemBody;
import net.minecraft.server.dialog.body.PlainMessage;
import org.slf4j.Logger;

public class DialogBodyHandlers {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<MapCodec<? extends DialogBody>, DialogBodyHandler<?>> HANDLERS = new HashMap();

   public DialogBodyHandlers() {
      super();
   }

   private static <B extends DialogBody> void register(MapCodec<B> var0, DialogBodyHandler<? super B> var1) {
      HANDLERS.put(var0, var1);
   }

   @Nullable
   private static <B extends DialogBody> DialogBodyHandler<B> getHandler(B var0) {
      return (DialogBodyHandler)HANDLERS.get(var0.mapCodec());
   }

   @Nullable
   public static <B extends DialogBody> LayoutElement createBodyElement(DialogScreen<?> var0, B var1) {
      DialogBodyHandler var2 = getHandler(var1);
      if (var2 == null) {
         LOGGER.warn("Unrecognized dialog body {}", var1);
         return null;
      } else {
         return var2.createControls(var0, var1);
      }
   }

   public static void bootstrap() {
      register(PlainMessage.MAP_CODEC, new PlainMessageHandler());
      register(ItemBody.MAP_CODEC, new ItemHandler());
   }

   static void runActionOnParent(DialogScreen<?> var0, @Nullable Style var1) {
      if (var1 != null) {
         ClickEvent var2 = var1.getClickEvent();
         if (var2 != null) {
            var0.runAction(Optional.of(var2));
         }
      }

   }

   static class PlainMessageHandler implements DialogBodyHandler<PlainMessage> {
      PlainMessageHandler() {
         super();
      }

      public LayoutElement createControls(DialogScreen<?> var1, PlainMessage var2) {
         return FocusableTextWidget.builder(var2.contents(), var1.getFont()).maxWidth(var2.width()).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.NEVER).build().setCentered(true).setComponentClickHandler((var1x) -> DialogBodyHandlers.runActionOnParent(var1, var1x));
      }
   }

   static class ItemHandler implements DialogBodyHandler<ItemBody> {
      ItemHandler() {
         super();
      }

      public LayoutElement createControls(DialogScreen<?> var1, ItemBody var2) {
         if (var2.description().isPresent()) {
            PlainMessage var3 = (PlainMessage)var2.description().get();
            LinearLayout var4 = LinearLayout.horizontal().spacing(2);
            var4.defaultCellSetting().alignVerticallyMiddle();
            ItemDisplayWidget var5 = new ItemDisplayWidget(Minecraft.getInstance(), 0, 0, var2.width(), var2.height(), CommonComponents.EMPTY, var2.item(), var2.showDecorations(), var2.showTooltip());
            var4.addChild(var5);
            var4.addChild(FocusableTextWidget.builder(var3.contents(), var1.getFont()).maxWidth(var3.width()).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.NEVER).build().setComponentClickHandler((var1x) -> DialogBodyHandlers.runActionOnParent(var1, var1x)));
            return var4;
         } else {
            return new ItemDisplayWidget(Minecraft.getInstance(), 0, 0, var2.width(), var2.height(), var2.item().getHoverName(), var2.item(), var2.showDecorations(), var2.showTooltip());
         }
      }
   }
}
