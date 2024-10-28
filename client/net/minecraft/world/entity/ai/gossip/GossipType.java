package net.minecraft.world.entity.ai.gossip;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum GossipType implements StringRepresentable {
   MAJOR_NEGATIVE("major_negative", -5, 100, 10, 10),
   MINOR_NEGATIVE("minor_negative", -1, 200, 20, 20),
   MINOR_POSITIVE("minor_positive", 1, 25, 1, 5),
   MAJOR_POSITIVE("major_positive", 5, 20, 0, 20),
   TRADING("trading", 1, 25, 2, 20);

   public static final int REPUTATION_CHANGE_PER_EVENT = 25;
   public static final int REPUTATION_CHANGE_PER_EVERLASTING_MEMORY = 20;
   public static final int REPUTATION_CHANGE_PER_TRADE = 2;
   public final String id;
   public final int weight;
   public final int max;
   public final int decayPerDay;
   public final int decayPerTransfer;
   public static final Codec<GossipType> CODEC = StringRepresentable.fromEnum(GossipType::values);

   private GossipType(final String var3, final int var4, final int var5, final int var6, final int var7) {
      this.id = var3;
      this.weight = var4;
      this.max = var5;
      this.decayPerDay = var6;
      this.decayPerTransfer = var7;
   }

   public String getSerializedName() {
      return this.id;
   }

   // $FF: synthetic method
   private static GossipType[] $values() {
      return new GossipType[]{MAJOR_NEGATIVE, MINOR_NEGATIVE, MINOR_POSITIVE, MAJOR_POSITIVE, TRADING};
   }
}
