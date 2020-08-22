package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class VillagerData {
   private static final int[] NEXT_LEVEL_XP_THRESHOLDS = new int[]{0, 10, 70, 150, 250};
   private final VillagerType type;
   private final VillagerProfession profession;
   private final int level;

   public VillagerData(VillagerType var1, VillagerProfession var2, int var3) {
      this.type = var1;
      this.profession = var2;
      this.level = Math.max(1, var3);
   }

   public VillagerData(Dynamic var1) {
      this((VillagerType)Registry.VILLAGER_TYPE.get(ResourceLocation.tryParse(var1.get("type").asString(""))), (VillagerProfession)Registry.VILLAGER_PROFESSION.get(ResourceLocation.tryParse(var1.get("profession").asString(""))), var1.get("level").asInt(1));
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

   public Object serialize(DynamicOps var1) {
      return var1.createMap(ImmutableMap.of(var1.createString("type"), var1.createString(Registry.VILLAGER_TYPE.getKey(this.type).toString()), var1.createString("profession"), var1.createString(Registry.VILLAGER_PROFESSION.getKey(this.profession).toString()), var1.createString("level"), var1.createInt(this.level)));
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
