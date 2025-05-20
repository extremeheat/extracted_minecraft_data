package net.minecraft.client.gui.screens.dialog.submit;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.dialog.submit.CommandTemplate;
import net.minecraft.server.dialog.submit.CustomForm;
import net.minecraft.server.dialog.submit.CustomSubmitMethod;
import net.minecraft.server.dialog.submit.CustomTemplate;
import net.minecraft.server.dialog.submit.ParsedTemplate;
import net.minecraft.server.dialog.submit.SubmitMethod;
import org.slf4j.Logger;

public class SubmitMethodHandlers {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<MapCodec<? extends SubmitMethod>, SubmitMethodHandler<?>> FACTORIES = new HashMap();

   public SubmitMethodHandlers() {
      super();
   }

   private static <T extends SubmitMethod> void register(MapCodec<T> var0, SubmitMethodHandler<? super T> var1) {
      FACTORIES.put(var0, var1);
   }

   @Nullable
   private static <T extends SubmitMethod> SubmitMethodHandler<T> get(T var0) {
      return (SubmitMethodHandler)FACTORIES.get(var0.mapCodec());
   }

   public static <T extends SubmitMethod> SubmitMethodHandler.Callback createCallback(T var0) {
      SubmitMethodHandler var1 = get(var0);
      if (var1 == null) {
         LOGGER.warn("Unrecognized submit action {}", var0);
         return SubmitMethodHandler.Callback.NOP;
      } else {
         return var1.createCallback(var0);
      }
   }

   public static void bootstrap() {
      register(CommandTemplate.MAP_CODEC, new SendCommand());
      SendCustom var0 = new SendCustom();
      register(CustomForm.MAP_CODEC, var0);
      register(CustomTemplate.MAP_CODEC, var0);
   }

   static class SendCommand implements SubmitMethodHandler<CommandTemplate> {
      SendCommand() {
         super();
      }

      public SubmitMethodHandler.Callback createCallback(CommandTemplate var1) {
         ParsedTemplate var2 = var1.template();
         return (var1x, var2x, var3) -> {
            String var4 = var2.instantiate(var2x);
            var1x.runCommand(var4, var3.previousScreen());
         };
      }

      // $FF: synthetic method
      public SubmitMethodHandler.Callback createCallback(final SubmitMethod var1) {
         return this.createCallback((CommandTemplate)var1);
      }
   }

   static class SendCustom implements SubmitMethodHandler<CustomSubmitMethod> {
      SendCustom() {
         super();
      }

      public SubmitMethodHandler.Callback createCallback(CustomSubmitMethod var1) {
         return (var1x, var2, var3) -> {
            String var4 = var1.payload(var2);
            var1x.sendCustomAction(var1.id(), Optional.of(var4));
         };
      }

      // $FF: synthetic method
      public SubmitMethodHandler.Callback createCallback(final SubmitMethod var1) {
         return this.createCallback((CustomSubmitMethod)var1);
      }
   }
}
