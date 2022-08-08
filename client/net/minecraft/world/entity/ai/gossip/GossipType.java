package net.minecraft.world.entity.ai.gossip;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public enum GossipType {
   MAJOR_NEGATIVE("major_negative", -5, 100, 10, 10),
   MINOR_NEGATIVE("minor_negative", -1, 200, 20, 20),
   MINOR_POSITIVE("minor_positive", 1, 200, 1, 5),
   MAJOR_POSITIVE("major_positive", 5, 100, 0, 100),
   TRADING("trading", 1, 25, 2, 20);

   public static final int REPUTATION_CHANGE_PER_EVENT = 25;
   public static final int REPUTATION_CHANGE_PER_EVERLASTING_MEMORY = 20;
   public static final int REPUTATION_CHANGE_PER_TRADE = 2;
   public final String id;
   public final int weight;
   public final int max;
   public final int decayPerDay;
   public final int decayPerTransfer;
   private static final Map<String, GossipType> BY_ID = (Map)Stream.of(values()).collect(ImmutableMap.toImmutableMap((var0) -> {
      return var0.id;
   }, Function.identity()));

   private GossipType(String var3, int var4, int var5, int var6, int var7) {
      this.id = var3;
      this.weight = var4;
      this.max = var5;
      this.decayPerDay = var6;
      this.decayPerTransfer = var7;
   }

   @Nullable
   public static GossipType byId(String var0) {
      return (GossipType)BY_ID.get(var0);
   }

   // $FF: synthetic method
   private static GossipType[] $values() {
      return new GossipType[]{MAJOR_NEGATIVE, MINOR_NEGATIVE, MINOR_POSITIVE, MAJOR_POSITIVE, TRADING};
   }
}
