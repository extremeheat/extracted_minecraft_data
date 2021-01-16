package io.netty.handler.codec.stomp;

import io.netty.handler.codec.Headers;
import io.netty.util.AsciiString;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public interface StompHeaders extends Headers<CharSequence, CharSequence, StompHeaders> {
   AsciiString ACCEPT_VERSION = AsciiString.cached("accept-version");
   AsciiString HOST = AsciiString.cached("host");
   AsciiString LOGIN = AsciiString.cached("login");
   AsciiString PASSCODE = AsciiString.cached("passcode");
   AsciiString HEART_BEAT = AsciiString.cached("heart-beat");
   AsciiString VERSION = AsciiString.cached("version");
   AsciiString SESSION = AsciiString.cached("session");
   AsciiString SERVER = AsciiString.cached("server");
   AsciiString DESTINATION = AsciiString.cached("destination");
   AsciiString ID = AsciiString.cached("id");
   AsciiString ACK = AsciiString.cached("ack");
   AsciiString TRANSACTION = AsciiString.cached("transaction");
   AsciiString RECEIPT = AsciiString.cached("receipt");
   AsciiString MESSAGE_ID = AsciiString.cached("message-id");
   AsciiString SUBSCRIPTION = AsciiString.cached("subscription");
   AsciiString RECEIPT_ID = AsciiString.cached("receipt-id");
   AsciiString MESSAGE = AsciiString.cached("message");
   AsciiString CONTENT_LENGTH = AsciiString.cached("content-length");
   AsciiString CONTENT_TYPE = AsciiString.cached("content-type");

   String getAsString(CharSequence var1);

   List<String> getAllAsString(CharSequence var1);

   Iterator<Entry<String, String>> iteratorAsString();

   boolean contains(CharSequence var1, CharSequence var2, boolean var3);
}
