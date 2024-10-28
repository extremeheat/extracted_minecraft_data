package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class LegacySinglePoolElement extends SinglePoolElement {
   public static final MapCodec<LegacySinglePoolElement> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(templateCodec(), processorsCodec(), projectionCodec(), overrideLiquidSettingsCodec()).apply(var0, LegacySinglePoolElement::new);
   });

   protected LegacySinglePoolElement(Either<ResourceLocation, StructureTemplate> var1, Holder<StructureProcessorList> var2, StructureTemplatePool.Projection var3, Optional<LiquidSettings> var4) {
      super(var1, var2, var3, var4);
   }

   protected StructurePlaceSettings getSettings(Rotation var1, BoundingBox var2, LiquidSettings var3, boolean var4) {
      StructurePlaceSettings var5 = super.getSettings(var1, var2, var3, var4);
      var5.popProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
      var5.addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
      return var5;
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.LEGACY;
   }

   public String toString() {
      return "LegacySingle[" + String.valueOf(this.template) + "]";
   }
}
