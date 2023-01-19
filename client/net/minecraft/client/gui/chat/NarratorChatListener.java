package net.minecraft.client.gui.chat;

import com.mojang.logging.LogUtils;
import com.mojang.text2speech.Narrator;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class NarratorChatListener implements ChatListener {
   public static final Component NO_TITLE = CommonComponents.EMPTY;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final NarratorChatListener INSTANCE = new NarratorChatListener();
   private final Narrator narrator = Narrator.getNarrator();

   public NarratorChatListener() {
      super();
   }

   @Override
   public void handle(ChatType var1, Component var2, @Nullable ChatSender var3) {
      NarratorStatus var4 = getStatus();
      if (var4 != NarratorStatus.OFF) {
         if (!this.narrator.active()) {
            this.logNarratedMessage(var2.getString());
         } else {
            var1.narration().ifPresent(var4x -> {
               if (var4.shouldNarrate(var4x.priority())) {
                  Component var5 = var4x.decorate(var2, var3);
                  String var6 = var5.getString();
                  this.logNarratedMessage(var6);
                  this.narrator.say(var6, var4x.priority().interrupts());
               }
            });
         }
      }
   }

   public void sayNow(Component var1) {
      this.sayNow(var1.getString());
   }

   public void sayNow(String var1) {
      NarratorStatus var2 = getStatus();
      if (var2 != NarratorStatus.OFF && var2 != NarratorStatus.CHAT && !var1.isEmpty()) {
         this.logNarratedMessage(var1);
         if (this.narrator.active()) {
            this.narrator.clear();
            this.narrator.say(var1, true);
         }
      }
   }

   private static NarratorStatus getStatus() {
      return Minecraft.getInstance().options.narrator().get();
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
            SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, Component.translatable("narrator.toast.disabled"), null);
         } else {
            SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, Component.translatable("narrator.toast.enabled"), var1.getName());
         }
      } else {
         SystemToast.addOrUpdate(
            var2,
            SystemToast.SystemToastIds.NARRATOR_TOGGLE,
            Component.translatable("narrator.toast.disabled"),
            Component.translatable("options.narrator.notavailable")
         );
      }
   }

   public boolean isActive() {
      return this.narrator.active();
   }

   public void clear() {
      if (getStatus() != NarratorStatus.OFF && this.narrator.active()) {
         this.narrator.clear();
      }
   }

   public void destroy() {
      this.narrator.destroy();
   }
}
