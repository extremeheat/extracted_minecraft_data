package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenNetherBridge extends MapGenStructure {
   private List<BiomeGenBase.SpawnListEntry> field_75060_e = Lists.newArrayList();

   public MapGenNetherBridge() {
      super();
      this.field_75060_e.add(new BiomeGenBase.SpawnListEntry(EntityBlaze.class, 10, 2, 3));
      this.field_75060_e.add(new BiomeGenBase.SpawnListEntry(EntityPigZombie.class, 5, 4, 4));
      this.field_75060_e.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
      this.field_75060_e.add(new BiomeGenBase.SpawnListEntry(EntityMagmaCube.class, 3, 4, 4));
   }

   public String func_143025_a() {
      return "Fortress";
   }

   public List<BiomeGenBase.SpawnListEntry> func_75059_a() {
      return this.field_75060_e;
   }

   protected boolean func_75047_a(int var1, int var2) {
      int var3 = var1 >> 4;
      int var4 = var2 >> 4;
      this.field_75038_b.setSeed((long)(var3 ^ var4 << 4) ^ this.field_75039_c.func_72905_C());
      this.field_75038_b.nextInt();
      if (this.field_75038_b.nextInt(3) != 0) {
         return false;
      } else if (var1 != (var3 << 4) + 4 + this.field_75038_b.nextInt(8)) {
         return false;
      } else {
         return var2 == (var4 << 4) + 4 + this.field_75038_b.nextInt(8);
      }
   }

   protected StructureStart func_75049_b(int var1, int var2) {
      return new MapGenNetherBridge.Start(this.field_75039_c, this.field_75038_b, var1, var2);
   }

   public static class Start extends StructureStart {
      public Start() {
         super();
      }

      public Start(World var1, Random var2, int var3, int var4) {
         super(var3, var4);
         StructureNetherBridgePieces.Start var5 = new StructureNetherBridgePieces.Start(var2, (var3 << 4) + 2, (var4 << 4) + 2);
         this.field_75075_a.add(var5);
         var5.func_74861_a(var5, this.field_75075_a, var2);
         List var6 = var5.field_74967_d;

         while(!var6.isEmpty()) {
            int var7 = var2.nextInt(var6.size());
            StructureComponent var8 = (StructureComponent)var6.remove(var7);
            var8.func_74861_a(var5, this.field_75075_a, var2);
         }

         this.func_75072_c();
         this.func_75070_a(var1, var2, 48, 70);
      }
   }
}
