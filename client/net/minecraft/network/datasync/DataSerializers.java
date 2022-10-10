package net.minecraft.network.datasync;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;

public class DataSerializers {
   private static final IntIdentityHashBiMap<DataSerializer<?>> field_187204_n = new IntIdentityHashBiMap(16);
   public static final DataSerializer<Byte> field_187191_a = new DataSerializer<Byte>() {
      public void func_187160_a(PacketBuffer var1, Byte var2) {
         var1.writeByte(var2);
      }

      public Byte func_187159_a(PacketBuffer var1) {
         return var1.readByte();
      }

      public DataParameter<Byte> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public Byte func_192717_a(Byte var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<Integer> field_187192_b = new DataSerializer<Integer>() {
      public void func_187160_a(PacketBuffer var1, Integer var2) {
         var1.func_150787_b(var2);
      }

      public Integer func_187159_a(PacketBuffer var1) {
         return var1.func_150792_a();
      }

      public DataParameter<Integer> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public Integer func_192717_a(Integer var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<Float> field_187193_c = new DataSerializer<Float>() {
      public void func_187160_a(PacketBuffer var1, Float var2) {
         var1.writeFloat(var2);
      }

      public Float func_187159_a(PacketBuffer var1) {
         return var1.readFloat();
      }

      public DataParameter<Float> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public Float func_192717_a(Float var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<String> field_187194_d = new DataSerializer<String>() {
      public void func_187160_a(PacketBuffer var1, String var2) {
         var1.func_180714_a(var2);
      }

      public String func_187159_a(PacketBuffer var1) {
         return var1.func_150789_c(32767);
      }

      public DataParameter<String> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public String func_192717_a(String var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<ITextComponent> field_187195_e = new DataSerializer<ITextComponent>() {
      public void func_187160_a(PacketBuffer var1, ITextComponent var2) {
         var1.func_179256_a(var2);
      }

      public ITextComponent func_187159_a(PacketBuffer var1) {
         return var1.func_179258_d();
      }

      public DataParameter<ITextComponent> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public ITextComponent func_192717_a(ITextComponent var1) {
         return var1.func_212638_h();
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<Optional<ITextComponent>> field_200544_f = new DataSerializer<Optional<ITextComponent>>() {
      public void func_187160_a(PacketBuffer var1, Optional<ITextComponent> var2) {
         if (var2.isPresent()) {
            var1.writeBoolean(true);
            var1.func_179256_a((ITextComponent)var2.get());
         } else {
            var1.writeBoolean(false);
         }

      }

      public Optional<ITextComponent> func_187159_a(PacketBuffer var1) {
         return var1.readBoolean() ? Optional.of(var1.func_179258_d()) : Optional.empty();
      }

      public DataParameter<Optional<ITextComponent>> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public Optional<ITextComponent> func_192717_a(Optional<ITextComponent> var1) {
         return var1.isPresent() ? Optional.of(((ITextComponent)var1.get()).func_212638_h()) : Optional.empty();
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<ItemStack> field_187196_f = new DataSerializer<ItemStack>() {
      public void func_187160_a(PacketBuffer var1, ItemStack var2) {
         var1.func_150788_a(var2);
      }

      public ItemStack func_187159_a(PacketBuffer var1) {
         return var1.func_150791_c();
      }

      public DataParameter<ItemStack> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public ItemStack func_192717_a(ItemStack var1) {
         return var1.func_77946_l();
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<Optional<IBlockState>> field_187197_g = new DataSerializer<Optional<IBlockState>>() {
      public void func_187160_a(PacketBuffer var1, Optional<IBlockState> var2) {
         if (var2.isPresent()) {
            var1.func_150787_b(Block.func_196246_j((IBlockState)var2.get()));
         } else {
            var1.func_150787_b(0);
         }

      }

      public Optional<IBlockState> func_187159_a(PacketBuffer var1) {
         int var2 = var1.func_150792_a();
         return var2 == 0 ? Optional.empty() : Optional.of(Block.func_196257_b(var2));
      }

      public DataParameter<Optional<IBlockState>> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public Optional<IBlockState> func_192717_a(Optional<IBlockState> var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<Boolean> field_187198_h = new DataSerializer<Boolean>() {
      public void func_187160_a(PacketBuffer var1, Boolean var2) {
         var1.writeBoolean(var2);
      }

      public Boolean func_187159_a(PacketBuffer var1) {
         return var1.readBoolean();
      }

      public DataParameter<Boolean> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public Boolean func_192717_a(Boolean var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<IParticleData> field_198166_i = new DataSerializer<IParticleData>() {
      public void func_187160_a(PacketBuffer var1, IParticleData var2) {
         var1.func_150787_b(IRegistry.field_212632_u.func_148757_b(var2.func_197554_b()));
         var2.func_197553_a(var1);
      }

      public IParticleData func_187159_a(PacketBuffer var1) {
         return this.func_200543_a(var1, (ParticleType)IRegistry.field_212632_u.func_148754_a(var1.func_150792_a()));
      }

      private <T extends IParticleData> T func_200543_a(PacketBuffer var1, ParticleType<T> var2) {
         return var2.func_197571_g().func_197543_b(var2, var1);
      }

      public DataParameter<IParticleData> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public IParticleData func_192717_a(IParticleData var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<Rotations> field_187199_i = new DataSerializer<Rotations>() {
      public void func_187160_a(PacketBuffer var1, Rotations var2) {
         var1.writeFloat(var2.func_179415_b());
         var1.writeFloat(var2.func_179416_c());
         var1.writeFloat(var2.func_179413_d());
      }

      public Rotations func_187159_a(PacketBuffer var1) {
         return new Rotations(var1.readFloat(), var1.readFloat(), var1.readFloat());
      }

      public DataParameter<Rotations> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public Rotations func_192717_a(Rotations var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<BlockPos> field_187200_j = new DataSerializer<BlockPos>() {
      public void func_187160_a(PacketBuffer var1, BlockPos var2) {
         var1.func_179255_a(var2);
      }

      public BlockPos func_187159_a(PacketBuffer var1) {
         return var1.func_179259_c();
      }

      public DataParameter<BlockPos> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public BlockPos func_192717_a(BlockPos var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<Optional<BlockPos>> field_187201_k = new DataSerializer<Optional<BlockPos>>() {
      public void func_187160_a(PacketBuffer var1, Optional<BlockPos> var2) {
         var1.writeBoolean(var2.isPresent());
         if (var2.isPresent()) {
            var1.func_179255_a((BlockPos)var2.get());
         }

      }

      public Optional<BlockPos> func_187159_a(PacketBuffer var1) {
         return !var1.readBoolean() ? Optional.empty() : Optional.of(var1.func_179259_c());
      }

      public DataParameter<Optional<BlockPos>> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public Optional<BlockPos> func_192717_a(Optional<BlockPos> var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<EnumFacing> field_187202_l = new DataSerializer<EnumFacing>() {
      public void func_187160_a(PacketBuffer var1, EnumFacing var2) {
         var1.func_179249_a(var2);
      }

      public EnumFacing func_187159_a(PacketBuffer var1) {
         return (EnumFacing)var1.func_179257_a(EnumFacing.class);
      }

      public DataParameter<EnumFacing> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public EnumFacing func_192717_a(EnumFacing var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<Optional<UUID>> field_187203_m = new DataSerializer<Optional<UUID>>() {
      public void func_187160_a(PacketBuffer var1, Optional<UUID> var2) {
         var1.writeBoolean(var2.isPresent());
         if (var2.isPresent()) {
            var1.func_179252_a((UUID)var2.get());
         }

      }

      public Optional<UUID> func_187159_a(PacketBuffer var1) {
         return !var1.readBoolean() ? Optional.empty() : Optional.of(var1.func_179253_g());
      }

      public DataParameter<Optional<UUID>> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public Optional<UUID> func_192717_a(Optional<UUID> var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };
   public static final DataSerializer<NBTTagCompound> field_192734_n = new DataSerializer<NBTTagCompound>() {
      public void func_187160_a(PacketBuffer var1, NBTTagCompound var2) {
         var1.func_150786_a(var2);
      }

      public NBTTagCompound func_187159_a(PacketBuffer var1) {
         return var1.func_150793_b();
      }

      public DataParameter<NBTTagCompound> func_187161_a(int var1) {
         return new DataParameter(var1, this);
      }

      public NBTTagCompound func_192717_a(NBTTagCompound var1) {
         return var1.func_74737_b();
      }

      // $FF: synthetic method
      public Object func_187159_a(PacketBuffer var1) {
         return this.func_187159_a(var1);
      }
   };

   public static void func_187189_a(DataSerializer<?> var0) {
      field_187204_n.func_186808_c(var0);
   }

   @Nullable
   public static DataSerializer<?> func_187190_a(int var0) {
      return (DataSerializer)field_187204_n.func_186813_a(var0);
   }

   public static int func_187188_b(DataSerializer<?> var0) {
      return field_187204_n.func_186815_a(var0);
   }

   static {
      func_187189_a(field_187191_a);
      func_187189_a(field_187192_b);
      func_187189_a(field_187193_c);
      func_187189_a(field_187194_d);
      func_187189_a(field_187195_e);
      func_187189_a(field_200544_f);
      func_187189_a(field_187196_f);
      func_187189_a(field_187198_h);
      func_187189_a(field_187199_i);
      func_187189_a(field_187200_j);
      func_187189_a(field_187201_k);
      func_187189_a(field_187202_l);
      func_187189_a(field_187203_m);
      func_187189_a(field_187197_g);
      func_187189_a(field_192734_n);
      func_187189_a(field_198166_i);
   }
}
