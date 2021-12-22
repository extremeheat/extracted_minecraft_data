package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

public enum NarratorStatus {
   OFF(0, "options.narrator.off"),
   ALL(1, "options.narrator.all"),
   CHAT(2, "options.narrator.chat"),
   SYSTEM(3, "options.narrator.system");

   private static final NarratorStatus[] BY_ID = (NarratorStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(NarratorStatus::getId)).toArray((var0) -> {
      return new NarratorStatus[var0];
   });
   // $FF: renamed from: id int
   private final int field_483;
   private final Component name;

   private NarratorStatus(int var3, String var4) {
      this.field_483 = var3;
      this.name = new TranslatableComponent(var4);
   }

   public int getId() {
      return this.field_483;
   }

   public Component getName() {
      return this.name;
   }

   public static NarratorStatus byId(int var0) {
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }

   // $FF: synthetic method
   private static NarratorStatus[] $values() {
      return new NarratorStatus[]{OFF, ALL, CHAT, SYSTEM};
   }
}
