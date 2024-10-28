package com.mojang.realmsclient.util.task;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.WorldGenerationInfo;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

public class CreateSnapshotRealmTask extends LongRunningTask {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.snapshot.creating");
   private final long parentId;
   private final WorldGenerationInfo generationInfo;
   private final String name;
   private final String description;
   private final RealmsMainScreen realmsMainScreen;
   @Nullable
   private RealmCreationTask creationTask;
   @Nullable
   private ResettingGeneratedWorldTask generateWorldTask;

   public CreateSnapshotRealmTask(RealmsMainScreen var1, long var2, WorldGenerationInfo var4, String var5, String var6) {
      super();
      this.parentId = var2;
      this.generationInfo = var4;
      this.name = var5;
      this.description = var6;
      this.realmsMainScreen = var1;
   }

   public void run() {
      RealmsClient var1 = RealmsClient.create();

      try {
         RealmsServer var2 = var1.createSnapshotRealm(this.parentId);
         this.creationTask = new RealmCreationTask(var2.id, this.name, this.description);
         this.generateWorldTask = new ResettingGeneratedWorldTask(this.generationInfo, var2.id, RealmsResetWorldScreen.CREATE_WORLD_RESET_TASK_TITLE, () -> {
            Minecraft.getInstance().execute(() -> {
               RealmsMainScreen.play(var2, this.realmsMainScreen, true);
            });
         });
         if (this.aborted()) {
            return;
         }

         this.creationTask.run();
         if (this.aborted()) {
            return;
         }

         this.generateWorldTask.run();
      } catch (RealmsServiceException var3) {
         LOGGER.error("Couldn't create snapshot world", var3);
         this.error(var3);
      } catch (Exception var4) {
         LOGGER.error("Couldn't create snapshot world", var4);
         this.error(var4);
      }

   }

   public Component getTitle() {
      return TITLE;
   }

   public void abortTask() {
      super.abortTask();
      if (this.creationTask != null) {
         this.creationTask.abortTask();
      }

      if (this.generateWorldTask != null) {
         this.generateWorldTask.abortTask();
      }

   }
}
