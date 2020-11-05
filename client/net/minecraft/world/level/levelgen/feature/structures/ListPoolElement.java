package net.minecraft.world.level.levelgen.feature.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class ListPoolElement extends StructurePoolElement {
   public static final Codec<ListPoolElement> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(StructurePoolElement.CODEC.listOf().fieldOf("elements").forGetter((var0x) -> {
         return var0x.elements;
      }), projectionCodec()).apply(var0, ListPoolElement::new);
   });
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

   public List<StructureTemplate.StructureBlockInfo> getShuffledJigsawBlocks(StructureManager var1, BlockPos var2, Rotation var3, Random var4) {
      return ((StructurePoolElement)this.elements.get(0)).getShuffledJigsawBlocks(var1, var2, var3, var4);
   }

   public BoundingBox getBoundingBox(StructureManager var1, BlockPos var2, Rotation var3) {
      BoundingBox var4 = BoundingBox.getUnknownBox();
      Iterator var5 = this.elements.iterator();

      while(var5.hasNext()) {
         StructurePoolElement var6 = (StructurePoolElement)var5.next();
         BoundingBox var7 = var6.getBoundingBox(var1, var2, var3);
         var4.expand(var7);
      }

      return var4;
   }

   public boolean place(StructureManager var1, WorldGenLevel var2, StructureFeatureManager var3, ChunkGenerator var4, BlockPos var5, BlockPos var6, Rotation var7, BoundingBox var8, Random var9, boolean var10) {
      Iterator var11 = this.elements.iterator();

      StructurePoolElement var12;
      do {
         if (!var11.hasNext()) {
            return true;
         }

         var12 = (StructurePoolElement)var11.next();
      } while(var12.place(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10));

      return false;
   }

   public StructurePoolElementType<?> getType() {
      return StructurePoolElementType.LIST;
   }

   public StructurePoolElement setProjection(StructureTemplatePool.Projection var1) {
      super.setProjection(var1);
      this.setProjectionOnEachElement(var1);
      return this;
   }

   public String toString() {
      return "List[" + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
   }

   private void setProjectionOnEachElement(StructureTemplatePool.Projection var1) {
      this.elements.forEach((var1x) -> {
         var1x.setProjection(var1);
      });
   }
}
