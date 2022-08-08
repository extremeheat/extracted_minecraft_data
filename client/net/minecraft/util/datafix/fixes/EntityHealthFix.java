package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;

public class EntityHealthFix extends DataFix {
   private static final Set<String> ENTITIES = Sets.newHashSet(new String[]{"ArmorStand", "Bat", "Blaze", "CaveSpider", "Chicken", "Cow", "Creeper", "EnderDragon", "Enderman", "Endermite", "EntityHorse", "Ghast", "Giant", "Guardian", "LavaSlime", "MushroomCow", "Ozelot", "Pig", "PigZombie", "Rabbit", "Sheep", "Shulker", "Silverfish", "Skeleton", "Slime", "SnowMan", "Spider", "Squid", "Villager", "VillagerGolem", "Witch", "WitherBoss", "Wolf", "Zombie"});

   public EntityHealthFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      Optional var3 = var1.get("HealF").asNumber().result();
      Optional var4 = var1.get("Health").asNumber().result();
      float var2;
      if (var3.isPresent()) {
         var2 = ((Number)var3.get()).floatValue();
         var1 = var1.remove("HealF");
      } else {
         if (!var4.isPresent()) {
            return var1;
         }

         var2 = ((Number)var4.get()).floatValue();
      }

      return var1.set("Health", var1.createFloat(var2));
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityHealthFix", this.getInputSchema().getType(References.ENTITY), (var1) -> {
         return var1.update(DSL.remainderFinder(), this::fixTag);
      });
   }
}
