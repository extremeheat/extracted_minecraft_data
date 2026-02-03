package net.minecraft.util.worldupdate;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;

public class UpgradeStatusTranslator {
   private final Map<DataFixTypes, Messages> messages = (Map)Util.make(new EnumMap(DataFixTypes.class), (map) -> {
      map.put(DataFixTypes.CHUNK, UpgradeStatusTranslator.Messages.create("chunks"));
      map.put(DataFixTypes.ENTITY_CHUNK, UpgradeStatusTranslator.Messages.create("entities"));
      map.put(DataFixTypes.POI_CHUNK, UpgradeStatusTranslator.Messages.create("poi"));
   });
   private static final Component FAILED = Component.translatable("optimizeWorld.stage.failed");
   private static final Component COUNTING = Component.translatable("optimizeWorld.stage.counting");
   private static final Component UPGRADING = Component.translatable("optimizeWorld.stage.upgrading");

   public UpgradeStatusTranslator() {
      super();
   }

   public Component translate(final UpgradeProgress upgradeProgress) {
      UpgradeProgress.Status status = upgradeProgress.getStatus();
      if (status == UpgradeProgress.Status.FAILED) {
         return FAILED;
      } else if (status == UpgradeProgress.Status.COUNTING) {
         return COUNTING;
      } else {
         DataFixTypes dataFixType = upgradeProgress.getDataFixType();
         if (dataFixType == null) {
            return COUNTING;
         } else {
            Messages typeMessages = (Messages)this.messages.get(dataFixType);
            return typeMessages == null ? UPGRADING : typeMessages.forStatus(status);
         }
      }
   }

   public static record Messages(Component upgrading, Component finished) {
      public Messages {
         super();
      }

      public static Messages create(final String type) {
         return new Messages(Component.translatable("optimizeWorld.stage.upgrading." + type), Component.translatable("optimizeWorld.stage.finished." + type));
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
