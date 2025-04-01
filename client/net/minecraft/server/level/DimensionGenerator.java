package net.minecraft.server.level;

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javax.annotation.CheckReturnValue;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TheGame;
import net.minecraft.server.packs.GeneratedMarkerMetadataSection;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.mines.MineSpawnStrategy;
import net.minecraft.world.level.mines.SpecialMine;
import net.minecraft.world.level.mines.WorldEffect;
import net.minecraft.world.level.mines.WorldEffects;
import net.minecraft.world.level.mines.WorldGenBuilder;
import net.minecraft.world.level.mines.WorldGenEffect;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.slf4j.Logger;

public class DimensionGenerator {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String LEVEL_PREFIX = "level";

   public DimensionGenerator() {
      super();
   }

   public static GeneratedDimension generateDimension(TheGame var0, List<WorldEffect> var1, Optional<SpecialMine> var2) {
      ServerLevelData var3 = var0.getWorldData().overworldData();
      int var4 = var3.incrementAndGetLevelCount();
      ResourceLocation var5 = ResourceLocation.withDefaultNamespace("level" + var4);
      Path var6 = var0.server().getWorldPath(LevelResource.DATAPACK_DIR).resolve(var5.getPath());
      MutableComponent var7 = Component.translatable("minecraftlike.pack_name", var4);
      RegistryOps var8 = var0.registryAccess().createSerializationContext(JsonOps.INSTANCE);
      ArrayList var9 = new ArrayList();
      var9.add(save(new DimensionPackMetadata(new PackMetadataSection(var7, 71, Optional.empty()), new GeneratedMarkerMetadataSection()), DimensionGenerator.DimensionPackMetadata.CODEC, var8, var6.resolve("pack.mcmeta")));
      Path var10 = var6.resolve("data").resolve(var5.getNamespace());
      Holder.Reference var11 = var0.registryAccess().getOrThrow(BuiltinDimensionTypes.GENERATED);
      WorldGenBuilder var12 = new WorldGenBuilder(var0.registryAccess());
      WorldEffects.componentsOfType(var1, WorldGenEffect.class).forEach((var1x) -> var1x.modifyWorld(var12));
      Optional var14 = var12.createDimensionType((DimensionType)var11.value());
      ResourceKey var13;
      if (var14.isEmpty()) {
         var13 = (ResourceKey)var11.unwrapKey().get();
      } else {
         DimensionType var15 = (DimensionType)var14.get();
         Path var16 = var10.resolve("dimension_type").resolve(var5.getPath() + ".json");
         var9.add(save(var15, DimensionType.DIRECT_CODEC, var8, var16));
         var13 = ResourceKey.create(Registries.DIMENSION_TYPE, var5);
      }

      for(WorldGenBuilder.ModifiedBiome var17 : var12.createModifiedBiomes(var0.registryAccess().lookupOrThrow(Registries.BIOME), var5.getPath())) {
         var9.add(save(var17.biome(), Biome.DIRECT_CODEC, var8, var10.resolve("worldgen").resolve("biome").resolve(var17.modified().location().getPath() + ".json")));
      }

      Path var20 = var10.resolve("dimension").resolve(var5.getPath() + ".json");
      var9.add(save(new FakeLevelStem(var13, Optional.empty(), var1, var2, var12.spawnStrategy()), DimensionGenerator.FakeLevelStem.CODEC, var8, var20));
      return new GeneratedDimension(ResourceKey.create(Registries.LEVEL_STEM, var5), () -> CompletableFuture.allOf((CompletableFuture[])var9.toArray(new CompletableFuture[0])).join());
   }

   @CheckReturnValue
   private static <T> CompletableFuture<?> save(T var0, Codec<T> var1, RegistryOps<JsonElement> var2, Path var3) {
      return CompletableFuture.runAsync(() -> {
         try {
            ByteArrayOutputStream var4 = new ByteArrayOutputStream();
            DataResult var5 = var1.encodeStart(var2, var0);
            if (var5.isError()) {
               LOGGER.error("Failed to encode entry {}: {}", var3, var5.error());
               return;
            }

            JsonWriter var6 = new JsonWriter(new OutputStreamWriter(var4, StandardCharsets.UTF_8));

            try {
               var6.setSerializeNulls(false);
               var6.setIndent("  ");
               GsonHelper.writeValue(var6, (JsonElement)var5.getOrThrow(), DataProvider.KEY_COMPARATOR);
            } catch (Throwable var10) {
               try {
                  var6.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            var6.close();
            FileUtil.createDirectoriesSafe(var3.getParent());
            Files.write(var3, var4.toByteArray(), new OpenOption[0]);
         } catch (IOException var11) {
            LOGGER.error("Failed to save file to {}", var3, var11);
         }

      }, Util.backgroundExecutor().forName("saveDimension"));
   }

   static record DimensionPackMetadata(PackMetadataSection pack, GeneratedMarkerMetadataSection generated) {
      public static final Codec<DimensionPackMetadata> CODEC = RecordCodecBuilder.create((var0) -> var0.group(PackMetadataSection.CODEC.fieldOf("pack").forGetter(DimensionPackMetadata::pack), GeneratedMarkerMetadataSection.CODEC.fieldOf("generated").forGetter(DimensionPackMetadata::generated)).apply(var0, DimensionPackMetadata::new));

      DimensionPackMetadata(PackMetadataSection var1, GeneratedMarkerMetadataSection var2) {
         super();
         this.pack = var1;
         this.generated = var2;
      }
   }

   static record FakeLevelStem(ResourceKey<DimensionType> type, Optional<ChunkGenerator> generator, List<WorldEffect> effects, Optional<SpecialMine> mine, MineSpawnStrategy spawn) {
      public static final Codec<FakeLevelStem> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ResourceKey.codec(Registries.DIMENSION_TYPE).fieldOf("type").forGetter(FakeLevelStem::type), ChunkGenerator.CODEC.optionalFieldOf("generator").forGetter(FakeLevelStem::generator), WorldEffect.CODEC.listOf().fieldOf("effects").forGetter(FakeLevelStem::effects), SpecialMine.CODEC.optionalFieldOf("mine").forGetter(FakeLevelStem::mine), MineSpawnStrategy.CODEC.fieldOf("spawn").forGetter(FakeLevelStem::spawn)).apply(var0, FakeLevelStem::new));

      FakeLevelStem(ResourceKey<DimensionType> var1, Optional<ChunkGenerator> var2, List<WorldEffect> var3, Optional<SpecialMine> var4, MineSpawnStrategy var5) {
         super();
         this.type = var1;
         this.generator = var2;
         this.effects = var3;
         this.mine = var4;
         this.spawn = var5;
      }
   }

   public static record GeneratedDimension(ResourceKey<LevelStem> id, Runnable synchronize) {
      public GeneratedDimension(ResourceKey<LevelStem> var1, Runnable var2) {
         super();
         this.id = var1;
         this.synchronize = var2;
      }
   }
}
