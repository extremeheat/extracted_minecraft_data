package net.minecraft.client;

import com.mojang.logging.LogUtils;
import com.mojang.text2speech.Narrator;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class GameNarrator {
   public static final Component NO_TITLE;
   private static final Logger LOGGER;
   private final Minecraft minecraft;
   private final Narrator narrator = Narrator.getNarrator();

   public GameNarrator(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void sayChatNow(Supplier<Component> var1) {
      if (this.getStatus().shouldNarrateChat()) {
         String var2 = ((Component)var1.get()).getString();
         this.logNarratedMessage(var2);
         this.narrator.say(var2, false);
      }

   }

   public void sayNow(Component var1) {
      this.sayNow(var1.getString());
   }

   public void sayNow(String var1) {
      if (this.getStatus().shouldNarrateSystem() && !var1.isEmpty()) {
         this.logNarratedMessage(var1);
         if (this.narrator.active()) {
            this.narrator.clear();
            this.narrator.say(var1, true);
         }
      }

   }

   private NarratorStatus getStatus() {
      return (NarratorStatus)this.minecraft.options.narrator().get();
   }

   private void logNarratedMessage(String var1) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         LOGGER.debug("Narrating: {}", var1.replaceAll("\n", "\\\\n"));
      }

   }

   public void updateNarratorStatus(NarratorStatus var1) {
      this.clear();
      this.narrator.say(Component.translatable("options.narrator").append(" : ").append(var1.getName()).getString(), true);
      ToastComponent var2 = Minecraft.getInstance().getToasts();
      if (this.narrator.active()) {
         if (var1 == NarratorStatus.OFF) {
            SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, Component.translatable("narrator.toast.disabled"), (Component)null);
         } else {
            SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, Component.translatable("narrator.toast.enabled"), var1.getName());
         }
      } else {
         SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, Component.translatable("narrator.toast.disabled"), Component.translatable("options.narrator.notavailable"));
      }

   }

   public boolean isActive() {
      return this.narrator.active();
   }

   public void clear() {
      if (this.getStatus() != NarratorStatus.OFF && this.narrator.active()) {
         this.narrator.clear();
      }
   }

   public void destroy() {
      this.narrator.destroy();
   }

   static {
      NO_TITLE = CommonComponents.EMPTY;
      LOGGER = LogUtils.getLogger();
   }
}
