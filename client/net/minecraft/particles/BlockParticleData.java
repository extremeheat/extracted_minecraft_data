package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class BlockParticleData implements IParticleData {
   public static final IParticleData.IDeserializer<BlockParticleData> field_197585_a = new IParticleData.IDeserializer<BlockParticleData>() {
      public BlockParticleData func_197544_b(ParticleType<BlockParticleData> var1, StringReader var2) throws CommandSyntaxException {
         var2.expect(' ');
         return new BlockParticleData(var1, (new BlockStateParser(var2, false)).func_197243_a(false).func_197249_b());
      }

      public BlockParticleData func_197543_b(ParticleType<BlockParticleData> var1, PacketBuffer var2) {
         return new BlockParticleData(var1, (IBlockState)Block.field_176229_d.func_148745_a(var2.func_150792_a()));
      }

      // $FF: synthetic method
      public IParticleData func_197543_b(ParticleType var1, PacketBuffer var2) {
         return this.func_197543_b(var1, var2);
      }

      // $FF: synthetic method
      public IParticleData func_197544_b(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         return this.func_197544_b(var1, var2);
      }
   };
   private final ParticleType<BlockParticleData> field_197586_b;
   private final IBlockState field_197587_c;

   public BlockParticleData(ParticleType<BlockParticleData> var1, IBlockState var2) {
      super();
      this.field_197586_b = var1;
      this.field_197587_c = var2;
   }

   public void func_197553_a(PacketBuffer var1) {
      var1.func_150787_b(Block.field_176229_d.func_148747_b(this.field_197587_c));
   }

   public String func_197555_a() {
      return this.func_197554_b().func_197570_d() + " " + BlockStateParser.func_197247_a(this.field_197587_c, (NBTTagCompound)null);
   }

   public ParticleType<BlockParticleData> func_197554_b() {
      return this.field_197586_b;
   }

   public IBlockState func_197584_c() {
      return this.field_197587_c;
   }
}
