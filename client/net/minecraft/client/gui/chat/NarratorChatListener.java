package net.minecraft.client.gui.chat;

import com.mojang.text2speech.Narrator;
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
   public static final Component NO_TITLE = new TextComponent("");
   private static final Logger LOGGER = LogManager.getLogger();
   public static final NarratorChatListener INSTANCE = new NarratorChatListener();
   private final Narrator narrator = Narrator.getNarrator();

   public NarratorChatListener() {
      super();
   }

   public void handle(ChatType var1, Component var2) {
      NarratorStatus var3 = getStatus();
      if (var3 != NarratorStatus.OFF && this.narrator.active()) {
         if (var3 == NarratorStatus.ALL || var3 == NarratorStatus.CHAT && var1 == ChatType.CHAT || var3 == NarratorStatus.SYSTEM && var1 == ChatType.SYSTEM) {
            Object var4;
            if (var2 instanceof TranslatableComponent && "chat.type.text".equals(((TranslatableComponent)var2).getKey())) {
               var4 = new TranslatableComponent("chat.type.text.narrate", ((TranslatableComponent)var2).getArgs());
            } else {
               var4 = var2;
            }

            this.doSay(var1.shouldInterrupt(), ((Component)var4).getString());
         }

      }
   }

   public void sayNow(String var1) {
      NarratorStatus var2 = getStatus();
      if (this.narrator.active() && var2 != NarratorStatus.OFF && var2 != NarratorStatus.CHAT && !var1.isEmpty()) {
         this.narrator.clear();
         this.doSay(true, var1);
      }

   }

   private static NarratorStatus getStatus() {
      return Minecraft.getInstance().options.narratorStatus;
   }

   private void doSay(boolean var1, String var2) {
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         LOGGER.debug("Narrating: {}", var2);
      }

      this.narrator.say(var2, var1);
   }

   public void updateNarratorStatus(NarratorStatus var1) {
      this.clear();
      this.narrator.say((new TranslatableComponent("options.narrator", new Object[0])).getString() + " : " + (new TranslatableComponent(var1.getKey(), new Object[0])).getString(), true);
      ToastComponent var2 = Minecraft.getInstance().getToasts();
      if (this.narrator.active()) {
         if (var1 == NarratorStatus.OFF) {
            SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.disabled", new Object[0]), (Component)null);
         } else {
            SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.enabled", new Object[0]), new TranslatableComponent(var1.getKey(), new Object[0]));
         }
      } else {
         SystemToast.addOrUpdate(var2, SystemToast.SystemToastIds.NARRATOR_TOGGLE, new TranslatableComponent("narrator.toast.disabled", new Object[0]), new TranslatableComponent("options.narrator.notavailable", new Object[0]));
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
