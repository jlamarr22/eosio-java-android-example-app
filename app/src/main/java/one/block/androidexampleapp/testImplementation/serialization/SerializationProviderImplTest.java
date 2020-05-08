package one.block.androidexampleapp.testImplementation.serialization;

import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.util.encoders.Hex;

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

        return this.getHexContextFreeData(contextFreeData);
    }

    // This does not work with data longer than 255 bytes
    public String getHexContextFreeData(List<String> contextFreeData) {
        if (contextFreeData.size() == 0) {
            return "";
        }
        byte[] bytes = new byte[this.getTotalBytes(contextFreeData)];
        bytes[0] = Byte.parseByte(String.valueOf(contextFreeData.size()));
        int index = 1;
        for(String cfd : contextFreeData) {
            byte[] cfdBytes = cfd.getBytes();
            bytes[index] = Byte.parseByte(String.valueOf(cfdBytes.length));
            index++;
            for (int i = 0; i < cfdBytes.length; i++) {
                bytes[index] = cfdBytes[i];
                index++;
            }
        }

        return Hex.toHexString(Sha256Hash.hash(bytes));
    }

    private Integer getTotalBytes(List<String> contextFreeData) {
        int bytes = 1;
        for(String cfd : contextFreeData) {
            bytes += 1 + cfd.getBytes().length;
        }
        return bytes;
    }
}
