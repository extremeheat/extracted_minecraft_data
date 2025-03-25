package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class InlineBlockPosFormatFix extends DataFix {
   public InlineBlockPosFormatFix(Schema var1) {
      super(var1, false);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder var1 = this.entityFinder("minecraft:vex");
      OpticFinder var2 = this.entityFinder("minecraft:phantom");
      OpticFinder var3 = this.entityFinder("minecraft:turtle");
      List var4 = List.of(this.entityFinder("minecraft:item_frame"), this.entityFinder("minecraft:glow_item_frame"), this.entityFinder("minecraft:painting"), this.entityFinder("minecraft:leash_knot"));
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("InlineBlockPosFormatFix - player", this.getInputSchema().getType(References.PLAYER), (var1x) -> var1x.update(DSL.remainderFinder(), this::fixPlayer)), this.fixTypeEverywhereTyped("InlineBlockPosFormatFix - entity", this.getInputSchema().getType(References.ENTITY), (var5) -> {
         var5 = var5.update(DSL.remainderFinder(), this::fixLivingEntity).updateTyped(var1, (var1x) -> var1x.update(DSL.remainderFinder(), this::fixVex)).updateTyped(var2, (var1x) -> var1x.update(DSL.remainderFinder(), this::fixPhantom)).updateTyped(var3, (var1x) -> var1x.update(DSL.remainderFinder(), this::fixTurtle));

         for(OpticFinder var7 : var4) {
            var5 = var5.updateTyped(var7, (var1x) -> var1x.update(DSL.remainderFinder(), this::fixBlockAttached));
         }

         return var5;
      }));
   }

   private OpticFinder<?> entityFinder(String var1) {
      return DSL.namedChoice(var1, this.getInputSchema().getChoiceType(References.ENTITY, var1));
   }

   private Dynamic<?> fixPlayer(Dynamic<?> var1) {
      var1 = this.fixLivingEntity(var1);
      Optional var2 = var1.get("SpawnX").asNumber().result();
      Optional var3 = var1.get("SpawnY").asNumber().result();
      Optional var4 = var1.get("SpawnZ").asNumber().result();
      if (var2.isPresent() && var3.isPresent() && var4.isPresent()) {
         Dynamic var5 = var1.createMap(Map.of(var1.createString("pos"), ExtraDataFixUtils.createBlockPos(var1, ((Number)var2.get()).intValue(), ((Number)var3.get()).intValue(), ((Number)var4.get()).intValue())));
         var5 = Dynamic.copyField(var1, "SpawnAngle", var5, "angle");
         var5 = Dynamic.copyField(var1, "SpawnDimension", var5, "dimension");
         var5 = Dynamic.copyField(var1, "SpawnForced", var5, "forced");
         var1 = var1.remove("SpawnX").remove("SpawnY").remove("SpawnZ").remove("SpawnAngle").remove("SpawnDimension").remove("SpawnForced");
         var1 = var1.set("respawn", var5);
      }

      Optional var11 = var1.get("enteredNetherPosition").result();
      if (var11.isPresent()) {
         var1 = var1.remove("enteredNetherPosition").set("entered_nether_pos", var1.createList(Stream.of(var1.createDouble(((Dynamic)var11.get()).get("x").asDouble(0.0)), var1.createDouble(((Dynamic)var11.get()).get("y").asDouble(0.0)), var1.createDouble(((Dynamic)var11.get()).get("z").asDouble(0.0)))));
      }

      return var1;
   }

   private Dynamic<?> fixLivingEntity(Dynamic<?> var1) {
      return ExtraDataFixUtils.fixInlineBlockPos(var1, "SleepingX", "SleepingY", "SleepingZ", "sleeping_pos");
   }

   private Dynamic<?> fixVex(Dynamic<?> var1) {
      return ExtraDataFixUtils.fixInlineBlockPos(var1.renameField("LifeTicks", "life_ticks"), "BoundX", "BoundY", "BoundZ", "bound_pos");
   }

   private Dynamic<?> fixPhantom(Dynamic<?> var1) {
      return ExtraDataFixUtils.fixInlineBlockPos(var1.renameField("Size", "size"), "AX", "AY", "AZ", "anchor_pos");
   }

   private Dynamic<?> fixTurtle(Dynamic<?> var1) {
      var1 = var1.remove("TravelPosX").remove("TravelPosY").remove("TravelPosZ");
      var1 = ExtraDataFixUtils.fixInlineBlockPos(var1, "HomePosX", "HomePosY", "HomePosZ", "home_pos");
      return var1.renameField("HasEgg", "has_egg");
   }

   private Dynamic<?> fixBlockAttached(Dynamic<?> var1) {
      return ExtraDataFixUtils.fixInlineBlockPos(var1, "TileX", "TileY", "TileZ", "block_pos");
   }
}
