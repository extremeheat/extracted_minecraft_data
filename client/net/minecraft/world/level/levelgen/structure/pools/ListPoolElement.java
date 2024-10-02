package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class ListPoolElement extends StructurePoolElement {
   public static final MapCodec<ListPoolElement> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(StructurePoolElement.CODEC.listOf().fieldOf("elements").forGetter(var0x -> var0x.elements), projectionCodec())
            .apply(var0, ListPoolElement::new)
   );
   private final List<StructurePoolElement> elements;

   public ListPoolElement(List<StructurePoolElement> var1, StructureTemplatePool.Projection var2) {
      super(var2);
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Elements are empty");
      } else {
         this.elements = var1;
         this.setProjectionOnEachElement(var2);
      }
   }

   @Override
   public Vec3i getSize(StructureTemplateManager var1, Rotation var2) {
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;

      for (StructurePoolElement var7 : this.elements) {
         Vec3i var8 = var7.getSize(var1, var2);
         var3 = Math.max(var3, var8.getX());
         var4 = Math.max(var4, var8.getY());
         var5 = Math.max(var5, var8.getZ());
      }

      return new Vec3i(var3, var4, var5);
   }

   @Override
   public List<StructureTemplate.JigsawBlockInfo> getShuffledJigsawBlocks(StructureTemplateManager var1, BlockPos var2, Rotation var3, RandomSource var4) {
      return this.elements.get(0).getShuffledJigsawBlocks(var1, var2, var3, var4);
   }

   @Override
   public BoundingBox getBoundingBox(StructureTemplateManager var1, BlockPos var2, Rotation var3) {
      Stream var4 = this.elements.stream().filter(var0 -> var0 != EmptyPoolElement.INSTANCE).map(var3x -> var3x.getBoundingBox(var1, var2, var3));
      return BoundingBox.encapsulatingBoxes(var4::iterator).orElseThrow(() -> new IllegalStateException("Unable to calculate boundingbox for ListPoolElement"));
   }

   @Override
   public boolean place(
      StructureTemplateManager var1,
      WorldGenLevel var2,
      StructureManager var3,
      ChunkGenerator var4,
      BlockPos var5,
      BlockPos var6,
      Rotation var7,
      BoundingBox var8,
      RandomSource var9,
      LiquidSettings var10,
      boolean var11
   ) {
      for (StructurePoolElement var13 : this.elements) {
         if (!var13.place(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.LIST;
   }

   @Override
   public StructurePoolElement setProjection(StructureTemplatePool.Projection var1) {
      super.setProjection(var1);
      this.setProjectionOnEachElement(var1);
      return this;
   }

   @Override
   public String toString() {
      return "List[" + this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
   }

   private void setProjectionOnEachElement(StructureTemplatePool.Projection var1) {
      this.elements.forEach(var1x -> var1x.setProjection(var1));
   }
}
