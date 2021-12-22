package net.minecraft.client.gui.chat;

import com.mojang.text2speech.Narrator;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NarratorChatListener implements ChatListener {
   public static final Component NO_TITLE;
   private static final Logger LOGGER;
   public static final NarratorChatListener INSTANCE;
   private final Narrator narrator = Narrator.getNarrator();

   public NarratorChatListener() {
      super();
   }

   public void handle(ChatType var1, Component var2, UUID var3) {
      NarratorStatus var4 = getStatus();
      if (var4 != NarratorStatus.OFF) {
         if (!this.narrator.active()) {
            this.logNarratedMessage(var2.getString());
         } else {
            if (var4 == NarratorStatus.ALL || var4 == NarratorStatus.CHAT && var1 == ChatType.CHAT || var4 == NarratorStatus.SYSTEM && var1 == ChatType.SYSTEM) {
               Object var5;
               if (var2 instanceof TranslatableComponent && "chat.type.text".equals(((TranslatableComponent)var2).getKey())) {
                  var5 = new TranslatableComponent("chat.type.text.narrate", ((TranslatableComponent)var2).getArgs());
               } else {
                  var5 = var2;
               }

               String var6 = ((Component)var5).getString();
               this.logNarratedMessage(var6);
               this.narrator.say(var6, var1.shouldInterrupt());
            }

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
      return Minecraft.getInstance().options.narratorStatus;
   }

   private void logNarratedMessage(String var1) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         LOGGER.debug("Narrating: {}", var1.replaceAll("\n", "\\\\n"));
      }

   }

   public void updateNarratorStatus(NarratorStatus var1) {
      this.clear();
      this.narrator.say((new TranslatableComponent("options.narrator")).append(" : ").append(var1.getName()).getString(), true);
      ToastComponent var2 = Minecraft.getInstance().getToasts();
      if (this.narrator.active()) {
         if (var1 == NarratorStatus.OFF) {
            SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.disabled"), (Component)null);
         } else {
            SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.enabled"), var1.getName());
         }
      } else {
         SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.disabled"), new TranslatableComponent("options.narrator.notavailable"));
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

   static {
      NO_TITLE = TextComponent.EMPTY;
      LOGGER = LogManager.getLogger();
      INSTANCE = new NarratorChatListener();
   }
}
