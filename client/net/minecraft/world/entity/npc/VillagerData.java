package net.minecraft.world.entity.npc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;

public class VillagerData {
   public static final int MIN_VILLAGER_LEVEL = 1;
   public static final int MAX_VILLAGER_LEVEL = 5;
   private static final int[] NEXT_LEVEL_XP_THRESHOLDS = new int[]{0, 10, 70, 150, 250};
   public static final Codec<VillagerData> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Registry.VILLAGER_TYPE.byNameCodec().fieldOf("type").orElseGet(() -> {
         return VillagerType.PLAINS;
      }).forGetter((var0x) -> {
         return var0x.type;
      }), Registry.VILLAGER_PROFESSION.byNameCodec().fieldOf("profession").orElseGet(() -> {
         return VillagerProfession.NONE;
      }).forGetter((var0x) -> {
         return var0x.profession;
      }), Codec.INT.fieldOf("level").orElse(1).forGetter((var0x) -> {
         return var0x.level;
      })).apply(var0, VillagerData::new);
   });
   private final VillagerType type;
   private final VillagerProfession profession;
   private final int level;

   public VillagerData(VillagerType var1, VillagerProfession var2, int var3) {
      super();
      this.type = var1;
      this.profession = var2;
      this.level = Math.max(1, var3);
   }

   public VillagerType getType() {
      return this.type;
   }

   public VillagerProfession getProfession() {
      return this.profession;
   }

   public int getLevel() {
      return this.level;
   }

   public VillagerData setType(VillagerType var1) {
      return new VillagerData(var1, this.profession, this.level);
   }

   public VillagerData setProfession(VillagerProfession var1) {
      return new VillagerData(this.type, var1, this.level);
   }

   public VillagerData setLevel(int var1) {
      return new VillagerData(this.type, this.profession, var1);
   }

   public static int getMinXpPerLevel(int var0) {
      return canLevelUp(var0) ? NEXT_LEVEL_XP_THRESHOLDS[var0 - 1] : 0;
   }

   public static int getMaxXpPerLevel(int var0) {
      return canLevelUp(var0) ? NEXT_LEVEL_XP_THRESHOLDS[var0] : 0;
   }

   public static boolean canLevelUp(int var0) {
      return var0 >= 1 && var0 < 5;
   }
}
