package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class BlockPosFormatAndRenamesFix extends DataFix {
   private static final List<String> PATROLLING_MOBS = List.of("minecraft:witch", "minecraft:ravager", "minecraft:pillager", "minecraft:illusioner", "minecraft:evoker", "minecraft:vindicator");

   public BlockPosFormatAndRenamesFix(Schema var1) {
      super(var1, false);
   }

   private Typed<?> fixFields(Typed<?> var1, Map<String, String> var2) {
      return var1.update(DSL.remainderFinder(), (var1x) -> {
         Map.Entry var3;
         for(Iterator var2x = var2.entrySet().iterator(); var2x.hasNext(); var1x = var1x.renameAndFixField((String)var3.getKey(), (String)var3.getValue(), ExtraDataFixUtils::fixBlockPos)) {
            var3 = (Map.Entry)var2x.next();
         }

         return var1x;
      });
   }

   private <T> Dynamic<T> fixMapSavedData(Dynamic<T> var1) {
      return var1.update("frames", (var0) -> {
         return var0.createList(var0.asStream().map((var0x) -> {
            var0x = var0x.renameAndFixField("Pos", "pos", ExtraDataFixUtils::fixBlockPos);
            var0x = var0x.renameField("Rotation", "rotation");
            var0x = var0x.renameField("EntityId", "entity_id");
            return var0x;
         }));
      }).update("banners", (var0) -> {
         return var0.createList(var0.asStream().map((var0x) -> {
            var0x = var0x.renameField("Pos", "pos");
            var0x = var0x.renameField("Color", "color");
            var0x = var0x.renameField("Name", "name");
            return var0x;
         }));
      });
   }

   public TypeRewriteRule makeRule() {
      ArrayList var1 = new ArrayList();
      this.addEntityRules(var1);
      this.addBlockEntityRules(var1);
      var1.add(this.fixTypeEverywhereTyped("BlockPos format for map frames", this.getInputSchema().getType(References.SAVED_DATA_MAP_DATA), (var1x) -> {
         return var1x.update(DSL.remainderFinder(), (var1) -> {
            return var1.update("data", this::fixMapSavedData);
         });
      }));
      Type var2 = this.getInputSchema().getType(References.ITEM_STACK);
      var1.add(this.fixTypeEverywhereTyped("BlockPos format for compass target", var2, ItemStackTagFix.createFixer(var2, "minecraft:compass"::equals, (var0) -> {
         return var0.update("LodestonePos", ExtraDataFixUtils::fixBlockPos);
      })));
      return TypeRewriteRule.seq(var1);
   }

   private void addEntityRules(List<TypeRewriteRule> var1) {
      var1.add(this.createEntityFixer(References.ENTITY, "minecraft:bee", Map.of("HivePos", "hive_pos", "FlowerPos", "flower_pos")));
      var1.add(this.createEntityFixer(References.ENTITY, "minecraft:end_crystal", Map.of("BeamTarget", "beam_target")));
      var1.add(this.createEntityFixer(References.ENTITY, "minecraft:wandering_trader", Map.of("WanderTarget", "wander_target")));
      Iterator var2 = PATROLLING_MOBS.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.add(this.createEntityFixer(References.ENTITY, var3, Map.of("PatrolTarget", "patrol_target")));
      }

      var1.add(this.fixTypeEverywhereTyped("BlockPos format in Leash for mobs", this.getInputSchema().getType(References.ENTITY), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            return var0x.renameAndFixField("Leash", "leash", ExtraDataFixUtils::fixBlockPos);
         });
      }));
   }

   private void addBlockEntityRules(List<TypeRewriteRule> var1) {
      var1.add(this.createEntityFixer(References.BLOCK_ENTITY, "minecraft:beehive", Map.of("FlowerPos", "flower_pos")));
      var1.add(this.createEntityFixer(References.BLOCK_ENTITY, "minecraft:end_gateway", Map.of("ExitPortal", "exit_portal")));
   }

   private TypeRewriteRule createEntityFixer(DSL.TypeReference var1, String var2, Map<String, String> var3) {
      String var4 = "BlockPos format in " + String.valueOf(var3.keySet()) + " for " + var2 + " (" + var1.typeName() + ")";
      OpticFinder var5 = DSL.namedChoice(var2, this.getInputSchema().getChoiceType(var1, var2));
      return this.fixTypeEverywhereTyped(var4, this.getInputSchema().getType(var1), (var3x) -> {
         return var3x.updateTyped(var5, (var2) -> {
            return this.fixFields(var2, var3);
         });
      });
   }
}
