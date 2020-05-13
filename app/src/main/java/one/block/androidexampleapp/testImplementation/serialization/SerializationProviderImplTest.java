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

        ByteBuffer buffer = ByteBuffer.allocate(this.getTotalBytes(contextFreeData));
        //ByteBuffer buffer = ByteBuffer.allocate(12);

        pushPrefix(buffer, contextFreeData.size());

        //buffer.put(Byte.parseByte(String.valueOf(contextFreeData.size())));
        //buffer.putInt(contextFreeData.size());

        for(String cfd : contextFreeData) {
            byte[] cfdBytes = cfd.getBytes();
//            buffer.put((byte)0x80);
//            buffer.put((byte)1);
            pushPrefix(buffer, cfdBytes.length);
            //buffer.putShort(cfdBytes.length);
            buffer.put(cfdBytes);
        }

        return Hex.toHexString(Sha256Hash.hash(buffer.array()));
    }

    private void pushPrefix(ByteBuffer buffer, int length) {
        while(true) {
            if (length >>> 7 == 0) {
                buffer.put((byte)length);
                break;
            } else {
                buffer.put((byte)(0x80 | (length & 0x7f)));
                length = length >>> 7;
            }
        }
    }

    private Integer getTotalBytes(List<String> contextFreeData) {
        int bytes = 1;
        for(String cfd : contextFreeData) {
            byte[] cfdBytes = cfd.getBytes();
            if (cfdBytes.length > 127) {
                bytes += 2 + cfdBytes.length;
            } else {
                bytes += 1 + cfdBytes.length;
            }
        }
        return bytes;
    }
}
