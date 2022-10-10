package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Random;
import net.minecraft.util.datafix.TypeReferences;

public class ZombieProfToType extends NamedEntityFix {
   private static final Random field_190049_a = new Random();

   public ZombieProfToType(Schema var1, boolean var2) {
      super(var1, var2, "EntityZombieVillagerTypeFix", TypeReferences.field_211299_o, "Zombie");
   }

   public Dynamic<?> func_209656_a(Dynamic<?> var1) {
      if (var1.getBoolean("IsVillager")) {
         if (!var1.get("ZombieType").isPresent()) {
            int var2 = this.func_191277_a(var1.getInt("VillagerProfession", -1));
            if (var2 == -1) {
               var2 = this.func_191277_a(field_190049_a.nextInt(6));
            }

            var1 = var1.set("ZombieType", var1.createInt(var2));
         }

         var1 = var1.remove("IsVillager");
      }

      return var1;
   }

   private int func_191277_a(int var1) {
      return var1 >= 0 && var1 < 6 ? var1 : -1;
   }

   protected Typed<?> func_207419_a(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::func_209656_a);
   }
}
