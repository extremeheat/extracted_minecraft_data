package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class EntityZombieVillagerTypeFix extends NamedEntityFix {
   private static final int PROFESSION_MAX = 4;

   public EntityZombieVillagerTypeFix(final Schema outputSchema, final boolean changesType) {
      super(outputSchema, changesType, "EntityZombieVillagerTypeFix", References.ENTITY, "Zombie");
   }

   public Dynamic<?> fixTag(Dynamic<?> input) {
      if (input.get("IsVillager").asBoolean(false)) {
         if (input.get("ZombieType").result().isEmpty()) {
            int profession = this.getVillagerProfession(input.get("VillagerProfession").asInt(0));
            input = input.set("ZombieType", input.createInt(profession + 1));
         }

         input = input.remove("IsVillager");
      }

      return input;
   }

   private int getVillagerProfession(final int profession) {
      return profession >= 0 && profession <= 4 ? profession : -1;
   }

   protected Typed<?> fix(final Typed<?> entity) {
      return entity.update(DSL.remainderFinder(), this::fixTag);
   }
}
