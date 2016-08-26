package mdl.sinlov.android.websokcet;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * web socket heartbeat parser
 * <pre>
 *     sinlov
 *
 *     /\__/\
 *    /`    '\
 *  ≈≈≈ 0  0 ≈≈≈ Hello world!
 *    \  --  /
 *   /        \
 *  /          \
 * |            |
 *  \  ||  ||  /
 *   \_oo__oo_/≡≡≡≡≡≡≡≡o
 *
 * </pre>
 * Created by "sinlov" on 16/8/25.
 */
/*package*/ class HeartbeatParser {
    private static final String TAG = "HeartbeatParser";

    private WebSocketClient client;

    private boolean isMasking = true;

    private int mStage;

    private boolean mFinal;
    private boolean mMasked;
    private int mOpcode;
    private int mLengthSize;
    private int mLength;
    private int mMode;

    private byte[] mMask = new byte[0];
    private byte[] mPayload = new byte[0];

    private boolean mClosed = false;

    private ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();

    private static final int BYTE = 255;
    private static final int FIN = 128;
    private static final int MASK = 128;
    private static final int RSV1 = 64;
    private static final int RSV2 = 32;
    private static final int RSV3 = 16;
    private static final int OPCODE = 15;
    private static final int LENGTH = 127;

    private static final int MODE_TEXT = 1;
    private static final int MODE_BINARY = 2;

    private static final int OP_CONTINUATION = 0;
    private static final int OP_TEXT = 1;
    private static final int OP_BINARY = 2;
    private static final int OP_CLOSE = 8;
    private static final int OP_PING = 9;
    private static final int OP_PONG = 10;

    private static final List<Integer> OPCODES = Arrays.asList(
            OP_CONTINUATION,
            OP_TEXT,
            OP_BINARY,
            OP_CLOSE,
            OP_PING,
            OP_PONG
    );

    private static final List<Integer> FRAGMENTED_OPCODES = Arrays.asList(
            OP_CONTINUATION, OP_TEXT, OP_BINARY
    );

    public HeartbeatParser(WebSocketClient client) {
        this.client = client;
    }

    private static byte[] mask(byte[] payload, byte[] mask, int offset) {
        if (mask.length == 0) return payload;

        for (int i = 0; i < payload.length - offset; i++) {
            payload[offset + i] = (byte) (payload[offset + i] ^ mask[i % 4]);
        }
        return payload;
    }

    public void start(HappyDataInputStream stream) throws IOException {
        while (true) {
            if (stream.available() == -1) break;
            switch (mStage) {
                case 0:
                    parseOpcode(stream.readByte());
                    break;
                case 1:
                    parseLength(stream.readByte());
                    break;
                case 2:
                    parseExtendedLength(stream.readBytes(mLengthSize));
                    break;
                case 3:
                    mMask = stream.readBytes(4);
                    mStage = 4;
                    break;
                case 4:
                    mPayload = stream.readBytes(mLength);
                    emitFrame();
                    mStage = 0;
                    break;
            }
        }
        client.msgError(WebSocketClient.MSG_DISCONNECT, WebSocketListener.DISCONNECT_EOF,
                WebSocketListener.DISCONNECT_EOF, null);
    }

    private void parseOpcode(byte data) throws ProtocolError {
        boolean rsv1 = (data & RSV1) == RSV1;
        boolean rsv2 = (data & RSV2) == RSV2;
        boolean rsv3 = (data & RSV3) == RSV3;

        if (rsv1 || rsv2 || rsv3) {
            throw new ProtocolError("RSV not zero");
        }

        mFinal = (data & FIN) == FIN;
        mOpcode = (data & OPCODE);
        mMask = new byte[0];
        mPayload = new byte[0];

        if (!OPCODES.contains(mOpcode)) {
            throw new ProtocolError("Bad opcode");
        }

        if (!FRAGMENTED_OPCODES.contains(mOpcode) && !mFinal) {
            throw new ProtocolError("Expected non-final packet");
        }

        mStage = 1;
    }

    private void parseLength(byte data) {
        mMasked = (data & MASK) == MASK;
        mLength = (data & LENGTH);

        if (mLength >= 0 && mLength <= 125) {
            mStage = mMasked ? 3 : 4;
        } else {
            mLengthSize = (mLength == 126) ? 2 : 8;
            mStage = 2;
        }
    }

    private void parseExtendedLength(byte[] buffer) throws ProtocolError {
        mLength = getInteger(buffer);
        mStage = mMasked ? 3 : 4;
    }

    public byte[] frame(String data) {
        return frame(data, OP_TEXT, -1);
    }

    public byte[] frame(byte[] data) {
        return frame(data, OP_BINARY, -1);
    }

    private byte[] frame(byte[] data, int opcode, int errorCode) {
        return frame((Object) data, opcode, errorCode);
    }

    private byte[] frame(String data, int opcode, int errorCode) {
        return frame((Object) data, opcode, errorCode);
    }

    private byte[] frame(Object data, int opcode, int errorCode) {
        if (mClosed) return null;

        if (WebSocketEngine.DEBUG) {
            Log.d(TAG, "Creating frame for: " + data + " op: " + opcode + " err: " + errorCode);
        }

        byte[] buffer = (data instanceof String) ? decode((String) data) : (byte[]) data;
        int insert = (errorCode > 0) ? 2 : 0;
        int length = buffer.length + insert;
        int header = (length <= 125) ? 2 : (length <= 65535 ? 4 : 10);
        int offset = header + (isMasking ? 4 : 0);
        int masked = isMasking ? MASK : 0;
        byte[] frame = new byte[length + offset];

        frame[0] = (byte) ((byte) FIN | (byte) opcode);

        if (length <= 125) {
            frame[1] = (byte) (masked | length);
        } else if (length <= 65535) {
            frame[1] = (byte) (masked | 126);
            frame[2] = (byte) Math.floor(length / 256);
            frame[3] = (byte) (length & BYTE);
        } else {
            frame[1] = (byte) (masked | 127);
            frame[2] = (byte) (((int) Math.floor(length / Math.pow(2, 56))) & BYTE);
            frame[3] = (byte) (((int) Math.floor(length / Math.pow(2, 48))) & BYTE);
            frame[4] = (byte) (((int) Math.floor(length / Math.pow(2, 40))) & BYTE);
            frame[5] = (byte) (((int) Math.floor(length / Math.pow(2, 32))) & BYTE);
            frame[6] = (byte) (((int) Math.floor(length / Math.pow(2, 24))) & BYTE);
            frame[7] = (byte) (((int) Math.floor(length / Math.pow(2, 16))) & BYTE);
            frame[8] = (byte) (((int) Math.floor(length / Math.pow(2, 8))) & BYTE);
            frame[9] = (byte) (length & BYTE);
        }

        if (errorCode > 0) {
            frame[offset] = (byte) (((int) Math.floor(errorCode / 256)) & BYTE);
            frame[offset + 1] = (byte) (errorCode & BYTE);
        }
        System.arraycopy(buffer, 0, frame, offset + insert, buffer.length);

        if (isMasking) {
            byte[] mask = {
                    (byte) Math.floor(Math.random() * 256), (byte) Math.floor(Math.random() * 256),
                    (byte) Math.floor(Math.random() * 256), (byte) Math.floor(Math.random() * 256)
            };
            System.arraycopy(mask, 0, frame, header, mask.length);
            mask(frame, mask, offset);
        }

        return frame;
    }

    public void ping(String message) {
        client.send(frame(message, OP_PING, -1));
    }

    public void close(int code, String reason) {
        if (mClosed) return;
        client.send(frame(reason, OP_CLOSE, code));
        mClosed = true;
    }

    private void emitFrame() throws IOException {
        byte[] payload = mask(mPayload, mMask, 0);
        int opcode = mOpcode;

        if (opcode == OP_CONTINUATION) {
            if (mMode == 0) {
                throw new ProtocolError("Mode was not set.");
            }
            mBuffer.write(payload);
            if (mFinal) {
                byte[] message = mBuffer.toByteArray();
                if (mMode == MODE_TEXT) {
                    client.msgContent(WebSocketClient.MSG_ON_MESSAGE_STRING, encode(message));
                } else {
                    client.msgContent(WebSocketClient.MSG_ON_MESSAGE_BYTE, message);
                }
                reset();
            }

        } else if (opcode == OP_TEXT) {
            if (mFinal) {
                String messageText = encode(payload);
                client.msgContent(WebSocketClient.MSG_ON_MESSAGE_STRING, messageText);
            } else {
                mMode = MODE_TEXT;
                mBuffer.write(payload);
            }

        } else if (opcode == OP_BINARY) {
            if (mFinal) {
                client.msgContent(WebSocketClient.MSG_ON_MESSAGE_BYTE, payload);
            } else {
                mMode = MODE_BINARY;
                mBuffer.write(payload);
            }

        } else if (opcode == OP_CLOSE) {
            int code = (payload.length >= 2) ? 256 * payload[0] + payload[1] : 0;
            String reason = (payload.length > 2) ? encode(slice(payload, 2)) : null;
            if (WebSocketEngine.DEBUG) {
                Log.d(TAG, "Got close op! " + code + " " + reason);
            }
            client.msgError(WebSocketClient.MSG_DISCONNECT, WebSocketListener.GOT_CLOSE_OPT,
                    WebSocketListener.GOT_CLOSE_OPT, new Exception(reason));

        } else if (opcode == OP_PING) {
            if (payload.length > 125) {
                throw new ProtocolError("Ping payload too large");
            }
            if (WebSocketEngine.DEBUG) {
                Log.d(TAG, "Sending pong!!");
            }
            client.sendFrame(frame(payload, OP_PONG, -1));

        } else if (opcode == OP_PONG) {
            String message = encode(payload);
            // FIXME: Fire callback...
            if (WebSocketEngine.DEBUG) {
                Log.d(TAG, "Got pong! " + message);
            }
        }
    }

    private void reset() {
        mMode = 0;
        mBuffer.reset();
    }

    private String encode(byte[] buffer) {
        try {
            return new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] decode(String string) {
        try {
            return (string).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private int getInteger(byte[] bytes) throws ProtocolError {
        long i = byteArrayToLong(bytes, 0, bytes.length);
        if (i < 0 || i > Integer.MAX_VALUE) {
            throw new ProtocolError("Bad integer: " + i);
        }
        return (int) i;
    }

    private byte[] slice(byte[] array, int start) {
        return Arrays.copyOfRange(array, start, array.length);
    }

    public static class ProtocolError extends IOException {
        public ProtocolError(String detailMessage) {
            super(detailMessage);
        }
    }

    private static long byteArrayToLong(byte[] b, int offset, int length) {
        if (b.length < length)
            throw new IllegalArgumentException("length must be less than or equal to b.length");

        long value = 0;
        for (int i = 0; i < length; i++) {
            int shift = (length - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    public static class HappyDataInputStream extends DataInputStream {
        public HappyDataInputStream(InputStream in) {
            super(in);
        }

        public byte[] readBytes(int length) throws IOException {
            byte[] buffer = new byte[length];
            readFully(buffer);
            return buffer;
        }
    }

}
