package net.minecraft.client.multiplayer.chat;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;

public enum ChatPreviewStatus implements OptionEnum {
   OFF(0, "options.off"),
   LIVE(1, "options.chatPreview.live"),
   CONFIRM(2, "options.chatPreview.confirm");

   private static final ChatPreviewStatus[] BY_ID = (ChatPreviewStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(ChatPreviewStatus::getId)).toArray((var0) -> {
      return new ChatPreviewStatus[var0];
   });
   private final int id;
   private final String key;

   private ChatPreviewStatus(int var3, String var4) {
      this.id = var3;
      this.key = var4;
   }

   public String getKey() {
      return this.key;
   }

   public int getId() {
      return this.id;
   }

   public static ChatPreviewStatus byId(int var0) {
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }

   // $FF: synthetic method
   private static ChatPreviewStatus[] $values() {
      return new ChatPreviewStatus[]{OFF, LIVE, CONFIRM};
   }
}
