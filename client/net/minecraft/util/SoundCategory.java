package net.minecraft.util;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SoundCategory {
   MASTER("master"),
   MUSIC("music"),
   RECORDS("record"),
   WEATHER("weather"),
   BLOCKS("block"),
   HOSTILE("hostile"),
   NEUTRAL("neutral"),
   PLAYERS("player"),
   AMBIENT("ambient"),
   VOICE("voice");

   private static final Map<String, SoundCategory> field_187961_k = (Map)Arrays.stream(values()).collect(Collectors.toMap(SoundCategory::func_187948_a, Function.identity()));
   private final String field_187962_l;

   private SoundCategory(String var3) {
      this.field_187962_l = var3;
   }

   public String func_187948_a() {
      return this.field_187962_l;
   }
}
