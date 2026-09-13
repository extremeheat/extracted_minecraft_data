package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class StructureVillagePieces$Start extends StructureVillagePieces$Well {
   public WorldChunkManager field_74929_a;
   public boolean field_74927_b;
   public int field_74928_c;
   public StructureVillagePieces$PieceWeight field_74926_d;
   public List field_74931_h;
   public List field_74932_i = new ArrayList();
   public List field_74930_j = new ArrayList();

   public StructureVillagePieces$Start() {
      super();
   }

   public StructureVillagePieces$Start(WorldChunkManager var1, int var2, Random var3, int var4, int var5, List var6, int var7) {
      super(null, 0, var3, var4, var5);
      this.field_74929_a = var1;
      this.field_74931_h = var6;
      this.field_74928_c = var7;
      BiomeGenBase var8 = var1.func_76935_a(var4, var5);
      this.field_74927_b = var8 == BiomeGenBase.field_76769_d || var8 == BiomeGenBase.field_76786_s;
   }

   public WorldChunkManager func_74925_d() {
      return this.field_74929_a;
   }
}
