package net.minecraft.client.sounds;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;

public class JOrbisAudioStream implements FloatSampleSource {
   private static final int BUFSIZE = 8192;
   private static final int PAGEOUT_RECAPTURE = -1;
   private static final int PAGEOUT_NEED_MORE_DATA = 0;
   private static final int PAGEOUT_OK = 1;
   private static final int PACKETOUT_ERROR = -1;
   private static final int PACKETOUT_NEED_MORE_DATA = 0;
   private static final int PACKETOUT_OK = 1;
   private final SyncState syncState = new SyncState();
   private final Page page = new Page();
   private final StreamState streamState = new StreamState();
   private final Packet packet = new Packet();
   private final Info info = new Info();
   private final DspState dspState = new DspState();
   private final Block block;
   private final AudioFormat audioFormat;
   private final InputStream input;
   private long samplesWritten;
   private long totalSamplesInStream;

   public JOrbisAudioStream(InputStream var1) throws IOException {
      super();
      this.block = new Block(this.dspState);
      this.totalSamplesInStream = 9223372036854775807L;
      this.input = var1;
      Comment var2 = new Comment();
      Page var3 = this.readPage();
      if (var3 == null) {
         throw new IOException("Invalid Ogg file - can't find first page");
      } else {
         Packet var4 = this.readIdentificationPacket(var3);
         if (isError(this.info.synthesis_headerin(var2, var4))) {
            throw new IOException("Invalid Ogg identification packet");
         } else {
            for(int var5 = 0; var5 < 2; ++var5) {
               var4 = this.readPacket();
               if (var4 == null) {
                  throw new IOException("Unexpected end of Ogg stream");
               }

               if (isError(this.info.synthesis_headerin(var2, var4))) {
                  throw new IOException("Invalid Ogg header packet " + var5);
               }
            }

            this.dspState.synthesis_init(this.info);
            this.block.init(this.dspState);
            this.audioFormat = new AudioFormat((float)this.info.rate, 16, this.info.channels, true, false);
         }
      }
   }

   private static boolean isError(int var0) {
      return var0 < 0;
   }

   public AudioFormat getFormat() {
      return this.audioFormat;
   }

   private boolean readToBuffer() throws IOException {
      int var1 = this.syncState.buffer(8192);
      byte[] var2 = this.syncState.data;
      int var3 = this.input.read(var2, var1, 8192);
      if (var3 == -1) {
         return false;
      } else {
         this.syncState.wrote(var3);
         return true;
      }
   }

   @Nullable
   private Page readPage() throws IOException {
      while(true) {
         int var1 = this.syncState.pageout(this.page);
         switch (var1) {
            case -1:
               throw new IllegalStateException("Corrupt or missing data in bitstream");
            case 0:
               if (this.readToBuffer()) {
                  break;
               }

               return null;
            case 1:
               if (this.page.eos() != 0) {
                  this.totalSamplesInStream = this.page.granulepos();
               }

               return this.page;
            default:
               throw new IllegalStateException("Unknown page decode result: " + var1);
         }
      }
   }

   private Packet readIdentificationPacket(Page var1) throws IOException {
      this.streamState.init(var1.serialno());
      if (isError(this.streamState.pagein(var1))) {
         throw new IOException("Failed to parse page");
      } else {
         int var2 = this.streamState.packetout(this.packet);
         if (var2 != 1) {
            throw new IOException("Failed to read identification packet: " + var2);
         } else {
            return this.packet;
         }
      }
   }

   @Nullable
   private Packet readPacket() throws IOException {
      while(true) {
         int var1 = this.streamState.packetout(this.packet);
         switch (var1) {
            case -1:
               throw new IOException("Failed to parse packet");
            case 0:
               Page var2 = this.readPage();
               if (var2 == null) {
                  return null;
               }

               if (!isError(this.streamState.pagein(var2))) {
                  break;
               }

               throw new IOException("Failed to parse page");
            case 1:
               return this.packet;
            default:
               throw new IllegalStateException("Unknown packet decode result: " + var1);
         }
      }
   }

   private long getSamplesToWrite(int var1) {
      long var2 = this.samplesWritten + (long)var1;
      long var4;
      if (var2 > this.totalSamplesInStream) {
         var4 = this.totalSamplesInStream - this.samplesWritten;
         this.samplesWritten = this.totalSamplesInStream;
      } else {
         this.samplesWritten = var2;
         var4 = (long)var1;
      }

      return var4;
   }

   public boolean readChunk(FloatConsumer var1) throws IOException {
      float[][][] var2 = new float[1][][];
      int[] var3 = new int[this.info.channels];
      Packet var4 = this.readPacket();
      if (var4 == null) {
         return false;
      } else if (isError(this.block.synthesis(var4))) {
         throw new IOException("Can't decode audio packet");
      } else {
         this.dspState.synthesis_blockin(this.block);

         int var5;
         for(; (var5 = this.dspState.synthesis_pcmout(var2, var3)) > 0; this.dspState.synthesis_read(var5)) {
            float[][] var6 = var2[0];
            long var7 = this.getSamplesToWrite(var5);
            switch (this.info.channels) {
               case 1:
                  copyMono(var6[0], var3[0], var7, var1);
                  break;
               case 2:
                  copyStereo(var6[0], var3[0], var6[1], var3[1], var7, var1);
                  break;
               default:
                  copyAnyChannels(var6, this.info.channels, var3, var7, var1);
            }
         }

         return true;
      }
   }

   private static void copyAnyChannels(float[][] var0, int var1, int[] var2, long var3, FloatConsumer var5) {
      for(int var6 = 0; (long)var6 < var3; ++var6) {
         for(int var7 = 0; var7 < var1; ++var7) {
            int var8 = var2[var7];
            float var9 = var0[var7][var8 + var6];
            var5.accept(var9);
         }
      }

   }

   private static void copyMono(float[] var0, int var1, long var2, FloatConsumer var4) {
      for(int var5 = var1; (long)var5 < (long)var1 + var2; ++var5) {
         var4.accept(var0[var5]);
      }

   }

   private static void copyStereo(float[] var0, int var1, float[] var2, int var3, long var4, FloatConsumer var6) {
      for(int var7 = 0; (long)var7 < var4; ++var7) {
         var6.accept(var0[var1 + var7]);
         var6.accept(var2[var3 + var7]);
      }

   }

   public void close() throws IOException {
      this.input.close();
   }
}
