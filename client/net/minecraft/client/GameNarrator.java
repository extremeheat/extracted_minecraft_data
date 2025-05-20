package net.minecraft.client;

import com.mojang.logging.LogUtils;
import com.mojang.text2speech.Narrator;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.main.SilentInitException;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
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

   public void sayChatQueued(Component var1) {
      if (this.getStatus().shouldNarrateChat()) {
         this.narrateNotInterruptingMessage(var1);
      }

   }

   public void saySystemChatQueued(Component var1) {
      if (this.getStatus().shouldNarrateSystemOrChat()) {
         this.narrateNotInterruptingMessage(var1);
      }

   }

   public void saySystemQueued(Component var1) {
      if (this.getStatus().shouldNarrateSystem()) {
         this.narrateNotInterruptingMessage(var1);
      }

   }

   private void narrateNotInterruptingMessage(Component var1) {
      String var2 = var1.getString();
      if (!var2.isEmpty()) {
         this.logNarratedMessage(var2);
         this.narrateMessage(var2, false);
      }

   }

   public void saySystemNow(Component var1) {
      this.saySystemNow(var1.getString());
   }

   public void saySystemNow(String var1) {
      if (this.getStatus().shouldNarrateSystem() && !var1.isEmpty()) {
         this.logNarratedMessage(var1);
         if (this.narrator.active()) {
            this.narrator.clear();
            this.narrateMessage(var1, true);
         }
      }

   }

   private void narrateMessage(String var1, boolean var2) {
      this.narrator.say(var1, var2, this.minecraft.options.getFinalSoundSourceVolume(SoundSource.VOICE));
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
      this.narrateMessage(Component.translatable("options.narrator").append(" : ").append(var1.getName()).getString(), true);
      ToastManager var2 = Minecraft.getInstance().getToastManager();
      if (this.narrator.active()) {
         if (var1 == NarratorStatus.OFF) {
            SystemToast.addOrUpdate(var2, SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.translatable("narrator.toast.disabled"), (Component)null);
         } else {
            SystemToast.addOrUpdate(var2, SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.translatable("narrator.toast.enabled"), var1.getName());
         }
      } else {
         SystemToast.addOrUpdate(var2, SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.translatable("narrator.toast.disabled"), Component.translatable("options.narrator.notavailable"));
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

   public void checkStatus(boolean var1) {
      if (var1 && !this.isActive() && !TinyFileDialogs.tinyfd_messageBox("Minecraft", "Failed to initialize text-to-speech library. Do you want to continue?\nIf this problem persists, please report it at bugs.mojang.com", "yesno", "error", true)) {
         throw new NarratorInitException("Narrator library is not active");
      }
   }

   static {
      NO_TITLE = CommonComponents.EMPTY;
      LOGGER = LogUtils.getLogger();
   }

   public static class NarratorInitException extends SilentInitException {
      public NarratorInitException(String var1) {
         super(var1);
      }
   }
}
