package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class BlendingDataNoValueHeightFix extends DataFix {
   public BlendingDataNoValueHeightFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("BlendingDataNoValueHeightFix", this.getInputSchema().getType(References.CHUNK), (chunk) -> chunk.update(DSL.remainderFinder(), BlendingDataNoValueHeightFix::fix));
   }

   private static Dynamic<?> fix(final Dynamic<?> chunk) {
      return chunk.update("blending_data", (blendingData) -> blendingData.update("heights", (heights) -> heights.createList(heights.asStream().map(BlendingDataNoValueHeightFix::fixHeightValue))));
   }

   private static Dynamic<?> fixHeightValue(final Dynamic<?> height) {
      double heightValue = height.asDouble(1.7976931348623157E308);
      return heightValue == 1.7976931348623157E308 ? height.createFloat(3.4028235E38F) : height.createFloat((float)heightValue);
   }
}
