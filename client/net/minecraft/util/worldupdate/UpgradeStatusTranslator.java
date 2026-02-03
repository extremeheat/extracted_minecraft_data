package net.minecraft.util.worldupdate;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;

public class UpgradeStatusTranslator {
   private final Map<DataFixTypes, Messages> messages = (Map)Util.make(new EnumMap(DataFixTypes.class), (map) -> {
      map.put(DataFixTypes.CHUNK, new Messages(Component.translatable("optimizeWorld.stage.upgrading.chunks"), Component.translatable("optimizeWorld.stage.finished.chunks")));
      map.put(DataFixTypes.ENTITY_CHUNK, new Messages(Component.translatable("optimizeWorld.stage.upgrading.entities"), Component.translatable("optimizeWorld.stage.finished.entities")));
      map.put(DataFixTypes.POI_CHUNK, new Messages(Component.translatable("optimizeWorld.stage.upgrading.poi"), Component.translatable("optimizeWorld.stage.finished.poi")));
   });

   public UpgradeStatusTranslator() {
      super();
   }

   public Component translate(final UpgradeProgress upgradeProgress) {
      UpgradeProgress.Status status = upgradeProgress.getStatus();
      if (status == UpgradeProgress.Status.FAILED) {
         return Component.translatable("optimizeWorld.stage.failed");
      } else if (status == UpgradeProgress.Status.COUNTING) {
         return Component.translatable("optimizeWorld.stage.counting");
      } else {
         DataFixTypes dataFixType = upgradeProgress.getDataFixType();
         if (dataFixType == null) {
            return Component.translatable("optimizeWorld.stage.counting");
         } else {
            Messages typeMessages = (Messages)this.messages.get(dataFixType);
            return (Component)(typeMessages == null ? Component.translatable("optimizeWorld.stage.upgrading") : typeMessages.forStatus(status));
         }
      }
   }

   public static record Messages(Component upgrading, Component finished) {
      public Messages {
         super();
      }

      public Component forStatus(final UpgradeProgress.Status status) {
         Component var10000;
         switch (status) {
            case UPGRADING -> var10000 = this.upgrading;
            case FINISHED -> var10000 = this.finished;
            default -> throw new IllegalStateException("Invalid Status received: " + String.valueOf(status));
         }

         return var10000;
      }
   }
}
