package net.minecraft.world.dimension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.CheckerboardBiomeProviderSettings;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.EndGenSettings;
import net.minecraft.world.gen.FlatGenSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NetherGenSettings;
import net.minecraft.world.gen.OverworldGenSettings;

public class OverworldDimension extends Dimension {
   public OverworldDimension() {
      super();
   }

   public DimensionType func_186058_p() {
      return DimensionType.OVERWORLD;
   }

   public boolean func_186056_c(int var1, int var2) {
      return !this.field_76579_a.func_72916_c(var1, var2) && super.func_186056_c(var1, var2);
   }

   protected void func_76572_b() {
      this.field_191067_f = true;
   }

   public IChunkGenerator<? extends IChunkGenSettings> func_186060_c() {
      WorldType var1 = this.field_76579_a.func_72912_H().func_76067_t();
      ChunkGeneratorType var2 = ChunkGeneratorType.field_205489_f;
      ChunkGeneratorType var3 = ChunkGeneratorType.field_205488_e;
      ChunkGeneratorType var4 = ChunkGeneratorType.field_206912_c;
      ChunkGeneratorType var5 = ChunkGeneratorType.field_206913_d;
      ChunkGeneratorType var6 = ChunkGeneratorType.field_206911_b;
      BiomeProviderType var7 = BiomeProviderType.field_205461_c;
      BiomeProviderType var8 = BiomeProviderType.field_206859_d;
      BiomeProviderType var9 = BiomeProviderType.field_205460_b;
      if (var1 == WorldType.field_77138_c) {
         FlatGenSettings var24 = FlatGenSettings.func_210835_a(new Dynamic(NBTDynamicOps.field_210820_a, this.field_76579_a.func_72912_H().func_211027_A()));
         SingleBiomeProviderSettings var20 = ((SingleBiomeProviderSettings)var7.func_205458_a()).func_205436_a(var24.func_82648_a());
         return var2.create(this.field_76579_a, var7.func_205457_a(var20), var24);
      } else if (var1 == WorldType.field_180272_g) {
         SingleBiomeProviderSettings var22 = ((SingleBiomeProviderSettings)var7.func_205458_a()).func_205436_a(Biomes.field_76772_c);
         return var3.create(this.field_76579_a, var7.func_205457_a(var22), var3.func_205483_a());
      } else if (var1 != WorldType.field_205394_h) {
         OverworldGenSettings var21 = (OverworldGenSettings)var6.func_205483_a();
         OverworldBiomeProviderSettings var19 = ((OverworldBiomeProviderSettings)var8.func_205458_a()).func_205439_a(this.field_76579_a.func_72912_H()).func_205441_a(var21);
         return var6.create(this.field_76579_a, var8.func_205457_a(var19), var21);
      } else {
         BiomeProvider var10 = null;
         JsonElement var11 = (JsonElement)Dynamic.convert(NBTDynamicOps.field_210820_a, JsonOps.INSTANCE, this.field_76579_a.func_72912_H().func_211027_A());
         JsonObject var12 = var11.getAsJsonObject();
         if (var12.has("biome_source") && var12.getAsJsonObject("biome_source").has("type") && var12.getAsJsonObject("biome_source").has("options")) {
            ResourceLocation var13 = new ResourceLocation(var12.getAsJsonObject("biome_source").getAsJsonPrimitive("type").getAsString());
            JsonObject var14 = var12.getAsJsonObject("biome_source").getAsJsonObject("options");
            Biome[] var15 = new Biome[]{Biomes.field_76771_b};
            if (var14.has("biomes")) {
               JsonArray var16 = var14.getAsJsonArray("biomes");
               var15 = var16.size() > 0 ? new Biome[var16.size()] : new Biome[]{Biomes.field_76771_b};

               for(int var17 = 0; var17 < var16.size(); ++var17) {
                  Biome var18 = (Biome)IRegistry.field_212624_m.func_212608_b(new ResourceLocation(var16.get(var17).getAsString()));
                  var15[var17] = var18 != null ? var18 : Biomes.field_76771_b;
               }
            }

            if (BiomeProviderType.field_205461_c.func_206858_b().equals(var13)) {
               SingleBiomeProviderSettings var26 = ((SingleBiomeProviderSettings)var7.func_205458_a()).func_205436_a(var15[0]);
               var10 = var7.func_205457_a(var26);
            }

            if (BiomeProviderType.field_205460_b.func_206858_b().equals(var13)) {
               int var28 = var14.has("size") ? var14.getAsJsonPrimitive("size").getAsInt() : 2;
               CheckerboardBiomeProviderSettings var33 = ((CheckerboardBiomeProviderSettings)var9.func_205458_a()).func_206860_a(var15).func_206861_a(var28);
               var10 = var9.func_205457_a(var33);
            }

            if (BiomeProviderType.field_206859_d.func_206858_b().equals(var13)) {
               OverworldBiomeProviderSettings var30 = ((OverworldBiomeProviderSettings)var8.func_205458_a()).func_205441_a(new OverworldGenSettings()).func_205439_a(this.field_76579_a.func_72912_H());
               var10 = var8.func_205457_a(var30);
            }
         }

         if (var10 == null) {
            var10 = var7.func_205457_a(((SingleBiomeProviderSettings)var7.func_205458_a()).func_205436_a(Biomes.field_76771_b));
         }

         IBlockState var23 = Blocks.field_150348_b.func_176223_P();
         IBlockState var25 = Blocks.field_150355_j.func_176223_P();
         if (var12.has("chunk_generator") && var12.getAsJsonObject("chunk_generator").has("options")) {
            String var27;
            Block var31;
            if (var12.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_block")) {
               var27 = var12.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_block").getAsString();
               var31 = (Block)IRegistry.field_212618_g.func_82594_a(new ResourceLocation(var27));
               if (var31 != null) {
                  var23 = var31.func_176223_P();
               }
            }

            if (var12.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_fluid")) {
               var27 = var12.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_fluid").getAsString();
               var31 = (Block)IRegistry.field_212618_g.func_82594_a(new ResourceLocation(var27));
               if (var31 != null) {
                  var25 = var31.func_176223_P();
               }
            }
         }

         if (var12.has("chunk_generator") && var12.getAsJsonObject("chunk_generator").has("type")) {
            ResourceLocation var29 = new ResourceLocation(var12.getAsJsonObject("chunk_generator").getAsJsonPrimitive("type").getAsString());
            if (ChunkGeneratorType.field_206912_c.func_205482_c().equals(var29)) {
               NetherGenSettings var35 = (NetherGenSettings)var4.func_205483_a();
               var35.func_205535_a(var23);
               var35.func_205534_b(var25);
               return var4.create(this.field_76579_a, var10, var35);
            }

            if (ChunkGeneratorType.field_206913_d.func_205482_c().equals(var29)) {
               EndGenSettings var34 = (EndGenSettings)var5.func_205483_a();
               var34.func_205538_a(new BlockPos(0, 64, 0));
               var34.func_205535_a(var23);
               var34.func_205534_b(var25);
               return var5.create(this.field_76579_a, var10, var34);
            }
         }

         OverworldGenSettings var32 = (OverworldGenSettings)var6.func_205483_a();
         var32.func_205535_a(var23);
         var32.func_205534_b(var25);
         return var6.create(this.field_76579_a, var10, var32);
      }
   }

   @Nullable
   public BlockPos func_206920_a(ChunkPos var1, boolean var2) {
      for(int var3 = var1.func_180334_c(); var3 <= var1.func_180332_e(); ++var3) {
         for(int var4 = var1.func_180333_d(); var4 <= var1.func_180330_f(); ++var4) {
            BlockPos var5 = this.func_206921_a(var3, var4, var2);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   }

   @Nullable
   public BlockPos func_206921_a(int var1, int var2, boolean var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos(var1, 0, var2);
      Biome var5 = this.field_76579_a.func_180494_b(var4);
      IBlockState var6 = var5.func_203944_q().func_204108_a();
      if (var3 && !var6.func_177230_c().func_203417_a(BlockTags.field_205599_H)) {
         return null;
      } else {
         Chunk var7 = this.field_76579_a.func_72964_e(var1 >> 4, var2 >> 4);
         int var8 = var7.func_201576_a(Heightmap.Type.MOTION_BLOCKING, var1 & 15, var2 & 15);
         if (var8 < 0) {
            return null;
         } else if (var7.func_201576_a(Heightmap.Type.WORLD_SURFACE, var1 & 15, var2 & 15) > var7.func_201576_a(Heightmap.Type.OCEAN_FLOOR, var1 & 15, var2 & 15)) {
            return null;
         } else {
            for(int var9 = var8 + 1; var9 >= 0; --var9) {
               var4.func_181079_c(var1, var9, var2);
               IBlockState var10 = this.field_76579_a.func_180495_p(var4);
               if (!var10.func_204520_s().func_206888_e()) {
                  break;
               }

               if (var10.equals(var6)) {
                  return var4.func_177984_a().func_185334_h();
               }
            }

            return null;
         }
      }
   }

   public float func_76563_a(long var1, float var3) {
      int var4 = (int)(var1 % 24000L);
      float var5 = ((float)var4 + var3) / 24000.0F - 0.25F;
      if (var5 < 0.0F) {
         ++var5;
      }

      if (var5 > 1.0F) {
         --var5;
      }

      float var6 = var5;
      var5 = 1.0F - (float)((Math.cos((double)var5 * 3.141592653589793D) + 1.0D) / 2.0D);
      var5 = var6 + (var5 - var6) / 3.0F;
      return var5;
   }

   public boolean func_76569_d() {
      return true;
   }

   public Vec3d func_76562_b(float var1, float var2) {
      float var3 = MathHelper.func_76134_b(var1 * 6.2831855F) * 2.0F + 0.5F;
      var3 = MathHelper.func_76131_a(var3, 0.0F, 1.0F);
      float var4 = 0.7529412F;
      float var5 = 0.84705883F;
      float var6 = 1.0F;
      var4 *= var3 * 0.94F + 0.06F;
      var5 *= var3 * 0.94F + 0.06F;
      var6 *= var3 * 0.91F + 0.09F;
      return new Vec3d((double)var4, (double)var5, (double)var6);
   }

   public boolean func_76567_e() {
      return true;
   }

   public boolean func_76568_b(int var1, int var2) {
      return false;
   }
}
