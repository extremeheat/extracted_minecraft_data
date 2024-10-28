package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ItemSpawnEggFix extends DataFix {
   private static final String[] ID_TO_ENTITY = (String[])DataFixUtils.make(new String[256], (var0) -> {
      var0[1] = "Item";
      var0[2] = "XPOrb";
      var0[7] = "ThrownEgg";
      var0[8] = "LeashKnot";
      var0[9] = "Painting";
      var0[10] = "Arrow";
      var0[11] = "Snowball";
      var0[12] = "Fireball";
      var0[13] = "SmallFireball";
      var0[14] = "ThrownEnderpearl";
      var0[15] = "EyeOfEnderSignal";
      var0[16] = "ThrownPotion";
      var0[17] = "ThrownExpBottle";
      var0[18] = "ItemFrame";
      var0[19] = "WitherSkull";
      var0[20] = "PrimedTnt";
      var0[21] = "FallingSand";
      var0[22] = "FireworksRocketEntity";
      var0[23] = "TippedArrow";
      var0[24] = "SpectralArrow";
      var0[25] = "ShulkerBullet";
      var0[26] = "DragonFireball";
      var0[30] = "ArmorStand";
      var0[41] = "Boat";
      var0[42] = "MinecartRideable";
      var0[43] = "MinecartChest";
      var0[44] = "MinecartFurnace";
      var0[45] = "MinecartTNT";
      var0[46] = "MinecartHopper";
      var0[47] = "MinecartSpawner";
      var0[40] = "MinecartCommandBlock";
      var0[50] = "Creeper";
      var0[51] = "Skeleton";
      var0[52] = "Spider";
      var0[53] = "Giant";
      var0[54] = "Zombie";
      var0[55] = "Slime";
      var0[56] = "Ghast";
      var0[57] = "PigZombie";
      var0[58] = "Enderman";
      var0[59] = "CaveSpider";
      var0[60] = "Silverfish";
      var0[61] = "Blaze";
      var0[62] = "LavaSlime";
      var0[63] = "EnderDragon";
      var0[64] = "WitherBoss";
      var0[65] = "Bat";
      var0[66] = "Witch";
      var0[67] = "Endermite";
      var0[68] = "Guardian";
      var0[69] = "Shulker";
      var0[90] = "Pig";
      var0[91] = "Sheep";
      var0[92] = "Cow";
      var0[93] = "Chicken";
      var0[94] = "Squid";
      var0[95] = "Wolf";
      var0[96] = "MushroomCow";
      var0[97] = "SnowMan";
      var0[98] = "Ozelot";
      var0[99] = "VillagerGolem";
      var0[100] = "EntityHorse";
      var0[101] = "Rabbit";
      var0[120] = "Villager";
      var0[200] = "EnderCrystal";
   });

   public ItemSpawnEggFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Schema var1 = this.getInputSchema();
      Type var2 = var1.getType(References.ITEM_STACK);
      OpticFinder var3 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder var4 = DSL.fieldFinder("id", DSL.string());
      OpticFinder var5 = var2.findField("tag");
      OpticFinder var6 = var5.type().findField("EntityTag");
      OpticFinder var7 = DSL.typeFinder(var1.getTypeRaw(References.ENTITY));
      Type var8 = this.getOutputSchema().getTypeRaw(References.ENTITY);
      return this.fixTypeEverywhereTyped("ItemSpawnEggFix", var2, (var6x) -> {
         Optional var7x = var6x.getOptional(var3);
         if (var7x.isPresent() && Objects.equals(((Pair)var7x.get()).getSecond(), "minecraft:spawn_egg")) {
            Dynamic var8x = (Dynamic)var6x.get(DSL.remainderFinder());
            short var9 = var8x.get("Damage").asShort((short)0);
            Optional var10 = var6x.getOptionalTyped(var5);
            Optional var11 = var10.flatMap((var1) -> {
               return var1.getOptionalTyped(var6);
            });
            Optional var12 = var11.flatMap((var1) -> {
               return var1.getOptionalTyped(var7);
            });
            Optional var13 = var12.flatMap((var1) -> {
               return var1.getOptional(var4);
            });
            Typed var14 = var6x;
            String var15 = ID_TO_ENTITY[var9 & 255];
            if (var15 != null && (var13.isEmpty() || !Objects.equals(var13.get(), var15))) {
               Typed var16 = var6x.getOrCreateTyped(var5);
               Typed var17 = var16.getOrCreateTyped(var6);
               Typed var18 = var17.getOrCreateTyped(var7);
               Typed var20 = Util.writeAndReadTypedOrThrow(var18, var8, (var2) -> {
                  return var2.set("id", var8x.createString(var15));
               });
               var14 = var6x.set(var5, var16.set(var6, var17.set(var7, var20)));
            }

            if (var9 != 0) {
               var8x = var8x.set("Damage", var8x.createShort((short)0));
               var14 = var14.set(DSL.remainderFinder(), var8x);
            }

            return var14;
         } else {
            return var6x;
         }
      });
   }
}
