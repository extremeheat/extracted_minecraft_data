package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.biome.BiomeGenBase$SpawnListEntry;

public class MapGenNetherBridge extends MapGenStructure {
   private List field_75060_e = new ArrayList();

   public MapGenNetherBridge() {
      super();
      this.field_75060_e.add(new BiomeGenBase$SpawnListEntry(EntityBlaze.class, 10, 2, 3));
      this.field_75060_e.add(new BiomeGenBase$SpawnListEntry(EntityPigZombie.class, 5, 4, 4));
      this.field_75060_e.add(new BiomeGenBase$SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
      this.field_75060_e.add(new BiomeGenBase$SpawnListEntry(EntityMagmaCube.class, 3, 4, 4));
   }

   @Override
   public String func_143025_a() {
      return "Fortress";
   }

   public List func_75059_a() {
      return this.field_75060_e;
   }

   @Override
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

   @Override
   protected StructureStart func_75049_b(int var1, int var2) {
      return new MapGenNetherBridge$Start(this.field_75039_c, this.field_75038_b, var1, var2);
   }
}
