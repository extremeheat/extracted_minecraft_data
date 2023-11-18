package net.minecraft.client.multiplayer;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class ClientAdvancements {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Minecraft minecraft;
   private final WorldSessionTelemetryManager telemetryManager;
   private final AdvancementList advancements = new AdvancementList();
   private final Map<Advancement, AdvancementProgress> progress = Maps.newHashMap();
   @Nullable
   private ClientAdvancements.Listener listener;
   @Nullable
   private Advancement selectedTab;

   public ClientAdvancements(Minecraft var1, WorldSessionTelemetryManager var2) {
      super();
      this.minecraft = var1;
      this.telemetryManager = var2;
   }

   public void update(ClientboundUpdateAdvancementsPacket var1) {
      if (var1.shouldReset()) {
         this.advancements.clear();
         this.progress.clear();
      }

      this.advancements.remove(var1.getRemoved());
      this.advancements.add(var1.getAdded());

      for(Entry var3 : var1.getProgress().entrySet()) {
         Advancement var4 = this.advancements.get((ResourceLocation)var3.getKey());
         if (var4 != null) {
            AdvancementProgress var5 = (AdvancementProgress)var3.getValue();
            var5.update(var4.getCriteria(), var4.getRequirements());
            this.progress.put(var4, var5);
            if (this.listener != null) {
               this.listener.onUpdateAdvancementProgress(var4, var5);
            }

            if (!var1.shouldReset() && var5.isDone()) {
               if (this.minecraft.level != null) {
                  this.telemetryManager.onAdvancementDone(this.minecraft.level, var4);
               }

               if (var4.getDisplay() != null && var4.getDisplay().shouldShowToast()) {
                  this.minecraft.getToasts().addToast(new AdvancementToast(var4));
               }
            }
         } else {
            LOGGER.warn("Server informed client about progress for unknown advancement {}", var3.getKey());
         }
      }
   }

   public AdvancementList getAdvancements() {
      return this.advancements;
   }

   public void setSelectedTab(@Nullable Advancement var1, boolean var2) {
      ClientPacketListener var3 = this.minecraft.getConnection();
      if (var3 != null && var1 != null && var2) {
         var3.send(ServerboundSeenAdvancementsPacket.openedTab(var1));
      }

      if (this.selectedTab != var1) {
         this.selectedTab = var1;
         if (this.listener != null) {
            this.listener.onSelectedTabChanged(var1);
         }
      }
   }

   public void setListener(@Nullable ClientAdvancements.Listener var1) {
      this.listener = var1;
      this.advancements.setListener(var1);
      if (var1 != null) {
         for(Entry var3 : this.progress.entrySet()) {
            var1.onUpdateAdvancementProgress((Advancement)var3.getKey(), (AdvancementProgress)var3.getValue());
         }

         var1.onSelectedTabChanged(this.selectedTab);
      }
   }

   public interface Listener extends AdvancementList.Listener {
      void onUpdateAdvancementProgress(Advancement var1, AdvancementProgress var2);

      void onSelectedTabChanged(@Nullable Advancement var1);
   }
}
