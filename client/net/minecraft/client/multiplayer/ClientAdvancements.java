package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.DisplayInfo;
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
   private final AdvancementTree tree = new AdvancementTree();
   private final Map<AdvancementHolder, AdvancementProgress> progress = new Object2ObjectOpenHashMap();
   @Nullable
   private Listener listener;
   @Nullable
   private AdvancementHolder selectedTab;

   public ClientAdvancements(Minecraft var1, WorldSessionTelemetryManager var2) {
      super();
      this.minecraft = var1;
      this.telemetryManager = var2;
   }

   public void update(ClientboundUpdateAdvancementsPacket var1) {
      if (var1.shouldReset()) {
         this.tree.clear();
         this.progress.clear();
      }

      this.tree.remove(var1.getRemoved());
      this.tree.addAll(var1.getAdded());
      Iterator var2 = var1.getProgress().entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         AdvancementNode var4 = this.tree.get((ResourceLocation)var3.getKey());
         if (var4 != null) {
            AdvancementProgress var5 = (AdvancementProgress)var3.getValue();
            var5.update(var4.advancement().requirements());
            this.progress.put(var4.holder(), var5);
            if (this.listener != null) {
               this.listener.onUpdateAdvancementProgress(var4, var5);
            }

            if (!var1.shouldReset() && var5.isDone()) {
               if (this.minecraft.level != null) {
                  this.telemetryManager.onAdvancementDone(this.minecraft.level, var4.holder());
               }

               Optional var6 = var4.advancement().display();
               if (var6.isPresent() && ((DisplayInfo)var6.get()).shouldShowToast()) {
                  this.minecraft.getToasts().addToast(new AdvancementToast(var4.holder()));
               }
            }
         } else {
            LOGGER.warn("Server informed client about progress for unknown advancement {}", var3.getKey());
         }
      }

   }

   public AdvancementTree getTree() {
      return this.tree;
   }

   public void setSelectedTab(@Nullable AdvancementHolder var1, boolean var2) {
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

   public void setListener(@Nullable Listener var1) {
      this.listener = var1;
      this.tree.setListener(var1);
      if (var1 != null) {
         this.progress.forEach((var2, var3) -> {
            AdvancementNode var4 = this.tree.get(var2);
            if (var4 != null) {
               var1.onUpdateAdvancementProgress(var4, var3);
            }

         });
         var1.onSelectedTabChanged(this.selectedTab);
      }

   }

   @Nullable
   public AdvancementHolder get(ResourceLocation var1) {
      AdvancementNode var2 = this.tree.get(var1);
      return var2 != null ? var2.holder() : null;
   }

   public interface Listener extends AdvancementTree.Listener {
      void onUpdateAdvancementProgress(AdvancementNode var1, AdvancementProgress var2);

      void onSelectedTabChanged(@Nullable AdvancementHolder var1);
   }
}
