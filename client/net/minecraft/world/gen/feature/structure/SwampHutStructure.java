package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;

public class SwampHutStructure extends ScatteredStructure<SwampHutConfig> {
   private static final List<Biome.SpawnListEntry> field_202384_d;

   public SwampHutStructure() {
      super();
   }

   protected String func_143025_a() {
      return "Swamp_Hut";
   }

   public int func_202367_b() {
      return 3;
   }

   protected StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5) {
      Biome var6 = var2.func_202090_b().func_180300_a(new BlockPos((var4 << 4) + 9, 0, (var5 << 4) + 9), Biomes.field_76772_c);
      return new SwampHutStructure.Start(var1, var3, var4, var5, var6);
   }

   protected int func_202382_c() {
      return 14357620;
   }

   public List<Biome.SpawnListEntry> func_202279_e() {
      return field_202384_d;
   }

   public boolean func_202383_b(IWorld var1, BlockPos var2) {
      StructureStart var3 = this.func_202364_a(var1, var2);
      if (var3 != field_202376_c && var3 instanceof SwampHutStructure.Start && !var3.func_186161_c().isEmpty()) {
         StructurePiece var4 = (StructurePiece)var3.func_186161_c().get(0);
         return var4 instanceof SwampHutPiece;
      } else {
         return false;
      }
   }

   static {
      field_202384_d = Lists.newArrayList(new Biome.SpawnListEntry[]{new Biome.SpawnListEntry(EntityType.field_200759_ay, 1, 1, 1)});
   }

   public static class Start extends StructureStart {
      public Start() {
         super();
      }

      public Start(IWorld var1, SharedSeedRandom var2, int var3, int var4, Biome var5) {
         super(var3, var4, var5, var2, var1.func_72905_C());
         SwampHutPiece var6 = new SwampHutPiece(var2, var3 * 16, var4 * 16);
         this.field_75075_a.add(var6);
         this.func_202500_a(var1);
      }
   }
}
