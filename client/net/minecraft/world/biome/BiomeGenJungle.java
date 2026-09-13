package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMelon;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenJungle extends BiomeGenBase {
   private boolean field_150614_aC;

   public BiomeGenJungle(int var1, boolean var2) {
      super(var1);
      this.field_150614_aC = var2;
      if (var2) {
         this.field_76760_I.field_76832_z = 2;
      } else {
         this.field_76760_I.field_76832_z = 50;
      }

      this.field_76760_I.field_76803_B = 25;
      this.field_76760_I.field_76802_A = 4;
      if (!var2) {
         this.field_76761_J.add(new BiomeGenBase$SpawnListEntry(EntityOcelot.class, 2, 1, 1));
      }

      this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityChicken.class, 10, 4, 4));
   }

   @Override
   public WorldGenAbstractTree func_150567_a(Random var1) {
      if (var1.nextInt(10) == 0) {
         return this.field_76758_O;
      } else if (var1.nextInt(2) == 0) {
         return new WorldGenShrub(3, 0);
      } else {
         return (WorldGenAbstractTree)(!this.field_150614_aC && var1.nextInt(3) == 0
            ? new WorldGenMegaJungle(false, 10, 20, 3, 3)
            : new WorldGenTrees(false, 4 + var1.nextInt(7), 3, 3, true));
      }
   }

   @Override
   public WorldGenerator func_76730_b(Random var1) {
      return var1.nextInt(4) == 0 ? new WorldGenTallGrass(Blocks.field_150329_H, 2) : new WorldGenTallGrass(Blocks.field_150329_H, 1);
   }

   @Override
   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      super.func_76728_a(var1, var2, var3, var4);
      int var5 = var3 + var2.nextInt(16) + 8;
      int var6 = var4 + var2.nextInt(16) + 8;
      int var7 = var2.nextInt(var1.func_72976_f(var5, var6) * 2);
      new WorldGenMelon().func_76484_a(var1, var2, var5, var7, var6);
      WorldGenVines var10 = new WorldGenVines();

      for(int var11 = 0; var11 < 50; ++var11) {
         var7 = var3 + var2.nextInt(16) + 8;
         short var8 = 128;
         int var9 = var4 + var2.nextInt(16) + 8;
         var10.func_76484_a(var1, var2, var7, var8, var9);
      }
   }
}
