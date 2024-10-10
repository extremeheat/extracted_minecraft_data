package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

public class MultiNoiseBiomeSourceParameterList {
   public static final Codec<MultiNoiseBiomeSourceParameterList> DIRECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               MultiNoiseBiomeSourceParameterList.Preset.CODEC.fieldOf("preset").forGetter(var0x -> var0x.preset), RegistryOps.retrieveGetter(Registries.BIOME)
            )
            .apply(var0, MultiNoiseBiomeSourceParameterList::new)
   );
   public static final Codec<Holder<MultiNoiseBiomeSourceParameterList>> CODEC = RegistryFileCodec.create(
      Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, DIRECT_CODEC
   );
   private final MultiNoiseBiomeSourceParameterList.Preset preset;
   private final Climate.ParameterList<Holder<Biome>> parameters;

   public MultiNoiseBiomeSourceParameterList(MultiNoiseBiomeSourceParameterList.Preset var1, HolderGetter<Biome> var2) {
      super();
      this.preset = var1;
      this.parameters = var1.provider.apply(var2::getOrThrow);
   }

   public Climate.ParameterList<Holder<Biome>> parameters() {
      return this.parameters;
   }

   public static Map<MultiNoiseBiomeSourceParameterList.Preset, Climate.ParameterList<ResourceKey<Biome>>> knownPresets() {
      return MultiNoiseBiomeSourceParameterList.Preset.BY_NAME
         .values()
         .stream()
         .collect(Collectors.toMap(var0 -> (MultiNoiseBiomeSourceParameterList.Preset)var0, var0 -> var0.provider().apply(var0x -> var0x)));
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
