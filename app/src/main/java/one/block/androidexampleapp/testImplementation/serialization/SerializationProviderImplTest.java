package one.block.androidexampleapp.testImplementation.serialization;

import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.util.encoders.Hex;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import one.block.eosiojava.error.serializationProvider.SerializationProviderError;
import one.block.eosiojavaabieosserializationprovider.AbiEosSerializationProviderImpl;

public class SerializationProviderImplTest extends AbiEosSerializationProviderImpl implements ISerializationProviderTest {
    /**
     * Create a new AbiEosSerializationProviderImpl serialization provider instance, initilizing a new context for the C++
     * library to work on automatically.
     *
     * @throws SerializationProviderError - An error is thrown if the context cannot be created.
     */
    public SerializationProviderImplTest() throws SerializationProviderError {
    }

    @Override
    public List<String> deserializeContextFreeData(String hex) throws DeserializeContextFreeDataError {
        return new ArrayList<String>();
    }

    @Override
    public String serializeContextFreeData(List<String> contextFreeData) throws SerializeContextFreeDataError {
        if (contextFreeData.size() == 0) {
            return "";
        }

        String finish = this.getHexContextFreeData(contextFreeData);
        //String test = testWorking(contextFreeData);

        return finish;
    }

    public String testWorking(List<String> contextFreeData) {
        byte[] bytes = new byte[this.getTotalBytes(contextFreeData)];
        int index = 0;
        bytes[index++] = Byte.parseByte(String.valueOf(contextFreeData.size()));
        for(String cfd : contextFreeData) {
            byte[] cfdBytes = cfd.getBytes();
            bytes[index++] = Byte.parseByte(String.valueOf(cfdBytes.length));
            for (int i = 0; i < cfdBytes.length; i++) {
                bytes[index++] = cfdBytes[i];
            }
        }

        return Hex.toHexString(Sha256Hash.hash(bytes));
    }

    // This does not work with data longer than 255 bytes
    public String getHexContextFreeData(List<String> contextFreeData) {
        if (contextFreeData.size() == 0) {
            return "";
        }

        ByteBuffer buffer = ByteBuffer.allocate(1 + this.getTotalBytes(contextFreeData));
        //ByteBuffer buffer = ByteBuffer.allocate(12);

        buffer.put(Byte.parseByte(String.valueOf(contextFreeData.size())));
        //buffer.putInt(contextFreeData.size());

        for(String cfd : contextFreeData) {
            byte[] cfdBytes = cfd.getBytes();
            //buffer.put(Byte.parseByte(String.valueOf(cfdBytes.length)));
            buffer.put((byte)0x80);
            buffer.put((byte)1);
            //buffer.putShort(cfdBytes.length);
            buffer.put(cfdBytes);
        }

        return Hex.toHexString(Sha256Hash.hash(buffer.array()));
    }

//    private byte[] GetPrefix(int length) {
//        byte[] bytes = new byte[2];
//        if (length > 127) {
//            bytes[0] = (byte)0x80;
//            int
//        }
//    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    private void PushInt32(int number, ByteBuffer buffer) {
        while (true) {
            if (number >>> 7 == 0) {
                buffer.put((byte)(0x80 | (number & 0x7f)));
                number = number >>> 7;
            } else {
                buffer.put((byte)number);
            }
        }
    }

    private Integer getTotalBytes(List<String> contextFreeData) {
        int bytes = 1;
        for(String cfd : contextFreeData) {
            bytes += 1 + cfd.getBytes().length;
        }
        return bytes;
    }
}
