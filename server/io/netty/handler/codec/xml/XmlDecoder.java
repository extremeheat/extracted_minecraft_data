package io.netty.handler.codec.xml;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import javax.xml.stream.XMLStreamException;

public class XmlDecoder extends ByteToMessageDecoder {
   private static final AsyncXMLInputFactory XML_INPUT_FACTORY = new InputFactoryImpl();
   private static final XmlDocumentEnd XML_DOCUMENT_END;
   private final AsyncXMLStreamReader<AsyncByteArrayFeeder> streamReader;
   private final AsyncByteArrayFeeder streamFeeder;

   public XmlDecoder() {
      super();
      this.streamReader = XML_INPUT_FACTORY.createAsyncForByteArray();
      this.streamFeeder = (AsyncByteArrayFeeder)this.streamReader.getInputFeeder();
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      byte[] var4 = new byte[var2.readableBytes()];
      var2.readBytes(var4);

      try {
         this.streamFeeder.feedInput(var4, 0, var4.length);
      } catch (XMLStreamException var10) {
         var2.skipBytes(var2.readableBytes());
         throw var10;
      }

      while(true) {
         while(!this.streamFeeder.needMoreInput()) {
            int var5 = this.streamReader.next();
            switch(var5) {
            case 1:
               XmlElementStart var6 = new XmlElementStart(this.streamReader.getLocalName(), this.streamReader.getName().getNamespaceURI(), this.streamReader.getPrefix());

               int var11;
               for(var11 = 0; var11 < this.streamReader.getAttributeCount(); ++var11) {
                  XmlAttribute var12 = new XmlAttribute(this.streamReader.getAttributeType(var11), this.streamReader.getAttributeLocalName(var11), this.streamReader.getAttributePrefix(var11), this.streamReader.getAttributeNamespace(var11), this.streamReader.getAttributeValue(var11));
                  var6.attributes().add(var12);
               }

               for(var11 = 0; var11 < this.streamReader.getNamespaceCount(); ++var11) {
                  XmlNamespace var13 = new XmlNamespace(this.streamReader.getNamespacePrefix(var11), this.streamReader.getNamespaceURI(var11));
                  var6.namespaces().add(var13);
               }

               var3.add(var6);
               break;
            case 2:
               XmlElementEnd var7 = new XmlElementEnd(this.streamReader.getLocalName(), this.streamReader.getName().getNamespaceURI(), this.streamReader.getPrefix());

               for(int var8 = 0; var8 < this.streamReader.getNamespaceCount(); ++var8) {
                  XmlNamespace var9 = new XmlNamespace(this.streamReader.getNamespacePrefix(var8), this.streamReader.getNamespaceURI(var8));
                  var7.namespaces().add(var9);
               }

               var3.add(var7);
               break;
            case 3:
               var3.add(new XmlProcessingInstruction(this.streamReader.getPIData(), this.streamReader.getPITarget()));
               break;
            case 4:
               var3.add(new XmlCharacters(this.streamReader.getText()));
               break;
            case 5:
               var3.add(new XmlComment(this.streamReader.getText()));
               break;
            case 6:
               var3.add(new XmlSpace(this.streamReader.getText()));
               break;
            case 7:
               var3.add(new XmlDocumentStart(this.streamReader.getEncoding(), this.streamReader.getVersion(), this.streamReader.isStandalone(), this.streamReader.getCharacterEncodingScheme()));
               break;
            case 8:
               var3.add(XML_DOCUMENT_END);
               break;
            case 9:
               var3.add(new XmlEntityReference(this.streamReader.getLocalName(), this.streamReader.getText()));
            case 10:
            default:
               break;
            case 11:
               var3.add(new XmlDTD(this.streamReader.getText()));
               break;
            case 12:
               var3.add(new XmlCdata(this.streamReader.getText()));
            }
         }

         return;
      }
   }

   static {
      XML_DOCUMENT_END = XmlDocumentEnd.INSTANCE;
   }
}
