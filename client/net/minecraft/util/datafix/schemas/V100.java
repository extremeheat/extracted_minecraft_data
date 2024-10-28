package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V100 extends Schema {
   public V100(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static TypeTemplate equipment(Schema var0) {
      return DSL.optionalFields("ArmorItems", DSL.list(References.ITEM_STACK.in(var0)), "HandItems", DSL.list(References.ITEM_STACK.in(var0)), "body_armor_item", References.ITEM_STACK.in(var0));
   }

   protected static void registerMob(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return equipment(var0);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      registerMob(var1, var2, "ArmorStand");
      registerMob(var1, var2, "Creeper");
      registerMob(var1, var2, "Skeleton");
      registerMob(var1, var2, "Spider");
      registerMob(var1, var2, "Giant");
      registerMob(var1, var2, "Zombie");
      registerMob(var1, var2, "Slime");
      registerMob(var1, var2, "Ghast");
      registerMob(var1, var2, "PigZombie");
      var1.register(var2, "Enderman", (var1x) -> {
         return DSL.optionalFields("carried", References.BLOCK_NAME.in(var1), equipment(var1));
      });
      registerMob(var1, var2, "CaveSpider");
      registerMob(var1, var2, "Silverfish");
      registerMob(var1, var2, "Blaze");
      registerMob(var1, var2, "LavaSlime");
      registerMob(var1, var2, "EnderDragon");
      registerMob(var1, var2, "WitherBoss");
      registerMob(var1, var2, "Bat");
      registerMob(var1, var2, "Witch");
      registerMob(var1, var2, "Endermite");
      registerMob(var1, var2, "Guardian");
      registerMob(var1, var2, "Pig");
      registerMob(var1, var2, "Sheep");
      registerMob(var1, var2, "Cow");
      registerMob(var1, var2, "Chicken");
      registerMob(var1, var2, "Squid");
      registerMob(var1, var2, "Wolf");
      registerMob(var1, var2, "MushroomCow");
      registerMob(var1, var2, "SnowMan");
      registerMob(var1, var2, "Ozelot");
      registerMob(var1, var2, "VillagerGolem");
      var1.register(var2, "EntityHorse", (var1x) -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var1)), "ArmorItem", References.ITEM_STACK.in(var1), "SaddleItem", References.ITEM_STACK.in(var1), equipment(var1));
      });
      registerMob(var1, var2, "Rabbit");
      var1.register(var2, "Villager", (var1x) -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(var1)), "Offers", DSL.optionalFields("Recipes", DSL.list(References.VILLAGER_TRADE.in(var1))), equipment(var1));
      });
      registerMob(var1, var2, "Shulker");
      var1.register(var2, "AreaEffectCloud", (var1x) -> {
         return DSL.optionalFields("Particle", References.PARTICLE.in(var1));
      });
      var1.registerSimple(var2, "ShulkerBullet");
      return var2;
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, References.STRUCTURE, () -> {
         return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", References.ENTITY_TREE.in(var1))), "blocks", DSL.list(DSL.optionalFields("nbt", References.BLOCK_ENTITY.in(var1))), "palette", DSL.list(References.BLOCK_STATE.in(var1)));
      });
      var1.registerType(false, References.BLOCK_STATE, DSL::remainder);
      var1.registerType(false, References.FLAT_BLOCK_STATE, DSL::remainder);
   }
}
