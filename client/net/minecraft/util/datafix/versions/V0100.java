package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.TypeReferences;

public class V0100 extends Schema {
   public V0100(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static TypeTemplate func_206605_a(Schema var0) {
      return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.field_211295_k.in(var0)), "HandItems", DSL.list(TypeReferences.field_211295_k.in(var0)));
   }

   protected static void func_206611_a(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return func_206605_a(var0);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      func_206611_a(var1, var2, "ArmorStand");
      func_206611_a(var1, var2, "Creeper");
      func_206611_a(var1, var2, "Skeleton");
      func_206611_a(var1, var2, "Spider");
      func_206611_a(var1, var2, "Giant");
      func_206611_a(var1, var2, "Zombie");
      func_206611_a(var1, var2, "Slime");
      func_206611_a(var1, var2, "Ghast");
      func_206611_a(var1, var2, "PigZombie");
      var1.register(var2, "Enderman", (var1x) -> {
         return DSL.optionalFields("carried", TypeReferences.field_211300_p.in(var1), func_206605_a(var1));
      });
      func_206611_a(var1, var2, "CaveSpider");
      func_206611_a(var1, var2, "Silverfish");
      func_206611_a(var1, var2, "Blaze");
      func_206611_a(var1, var2, "LavaSlime");
      func_206611_a(var1, var2, "EnderDragon");
      func_206611_a(var1, var2, "WitherBoss");
      func_206611_a(var1, var2, "Bat");
      func_206611_a(var1, var2, "Witch");
      func_206611_a(var1, var2, "Endermite");
      func_206611_a(var1, var2, "Guardian");
      func_206611_a(var1, var2, "Pig");
      func_206611_a(var1, var2, "Sheep");
      func_206611_a(var1, var2, "Cow");
      func_206611_a(var1, var2, "Chicken");
      func_206611_a(var1, var2, "Squid");
      func_206611_a(var1, var2, "Wolf");
      func_206611_a(var1, var2, "MushroomCow");
      func_206611_a(var1, var2, "SnowMan");
      func_206611_a(var1, var2, "Ozelot");
      func_206611_a(var1, var2, "VillagerGolem");
      var1.register(var2, "EntityHorse", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.field_211295_k.in(var1)), "ArmorItem", TypeReferences.field_211295_k.in(var1), "SaddleItem", TypeReferences.field_211295_k.in(var1), func_206605_a(var1));
      });
      func_206611_a(var1, var2, "Rabbit");
      var1.register(var2, "Villager", (var1x) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.field_211295_k.in(var1)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.field_211295_k.in(var1), "buyB", TypeReferences.field_211295_k.in(var1), "sell", TypeReferences.field_211295_k.in(var1)))), func_206605_a(var1));
      });
      func_206611_a(var1, var2, "Shulker");
      var1.registerSimple(var2, "AreaEffectCloud");
      var1.registerSimple(var2, "ShulkerBullet");
      return var2;
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, TypeReferences.field_211290_f, () -> {
         return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.field_211298_n.in(var1))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.field_211294_j.in(var1))), "palette", DSL.list(TypeReferences.field_211296_l.in(var1)));
      });
      var1.registerType(false, TypeReferences.field_211296_l, DSL::remainder);
   }
}
