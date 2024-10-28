package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.RandomSource;

public class EntityZombieVillagerTypeFix extends NamedEntityFix {
   private static final int PROFESSION_MAX = 6;

   public EntityZombieVillagerTypeFix(Schema var1, boolean var2) {
      super(var1, var2, "EntityZombieVillagerTypeFix", References.ENTITY, "Zombie");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      if (var1.get("IsVillager").asBoolean(false)) {
         if (var1.get("ZombieType").result().isEmpty()) {
            int var2 = this.getVillagerProfession(var1.get("VillagerProfession").asInt(-1));
            if (var2 == -1) {
               var2 = this.getVillagerProfession(RandomSource.create().nextInt(6));
            }

            var1 = var1.set("ZombieType", var1.createInt(var2));
         }

         var1 = var1.remove("IsVillager");
      }

      return var1;
   }

   private int getVillagerProfession(int var1) {
      return var1 >= 0 && var1 < 6 ? var1 : -1;
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}
