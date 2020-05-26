package one.block.androidexampleapp.testImplementation.serialization;

import org.bitcoinj.core.Sha256Hash;
import org.bouncycastle.util.encoders.Hex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import one.block.eosiojava.error.serializationProvider.SerializationProviderError;
import one.block.eosiojava.error.serializationProvider.SerializeError;
import one.block.eosiojava.models.AbiEosSerializationObject;
import one.block.eosiojavaabieosserializationprovider.AbiEosSerializationProviderImpl;
import one.block.eosiojavaabieosserializationprovider.AbieosContextNullError;

public class SerializationProviderImplTest extends AbiEosSerializationProviderImpl implements ISerializationProviderTest {
    private ByteBuffer context = this.create();
    /**
     * Create a new AbiEosSerializationProviderImpl serialization provider instance, initilizing a new context for the C++
     * library to work on automatically.
     *
     * @throws SerializationProviderError - An error is thrown if the context cannot be created.
     */
    public SerializationProviderImplTest() throws SerializationProviderError {
    }

    public List<String> deserializeContextFreeData(String hex) throws DeserializeContextFreeDataError {
        return new ArrayList<String>();
    }

    public String serializeContextFreeData(List<String> contextFreeData) throws SerializeContextFreeDataError {
        if (contextFreeData.size() == 0) {
            return "";
        }

        return this.getHexContextFreeData(contextFreeData);
    }

    @Override
    public void serialize(@NotNull AbiEosSerializationObject serializationObject) throws SerializeError {
        try {
            this.refreshContext();
            if (serializationObject.getJson().isEmpty()) {
                throw new SerializeError("No content to serialize.");
            } else {
                long contract64 = this.stringToName64(serializationObject.getContract());
                if (serializationObject.getAbi().isEmpty()) {
                    throw new SerializeError(String.format("serialize -- No ABI provided for %s %s", serializationObject.getContract() == null ? serializationObject.getContract() : "", serializationObject.getName()));
                } else {
                    boolean result = this.setAbi(this.context, contract64, serializationObject.getAbi());
                    String typeStr;
                    String err;
                    if (!result) {
                        typeStr = this.error();
                        err = String.format("Json to hex == Unable to set ABI. %s", typeStr == null ? "" : typeStr);
                        throw new SerializeError(err);
                    } else {
                        typeStr = serializationObject.getType() == null ? this.getType(serializationObject.getName(), contract64) : serializationObject.getType();
                        String hex;
                        if (typeStr == null) {
                            err = this.error();
                            hex = String.format("Unable to find type for action %s. %s", serializationObject.getName(), err == null ? "" : err);
                            throw new SerializeError(hex);
                        } else {
                            boolean jsonToBinResult = this.jsonToBin(this.context, contract64, typeStr, serializationObject.getJson(), true);
                            if (!jsonToBinResult) {
                                hex = this.error();
                                String errMsg = String.format("Unable to pack json to bin. %s", hex == null ? "" : hex);
                                throw new SerializeError(errMsg);
                            } else {
                                hex = this.getBinHex(this.context);
                                if (hex == null) {
                                    throw new SerializeError("Unable to convert binary to hex.");
                                } else {
                                    serializationObject.setHex(hex);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SerializationProviderError var9) {
            throw new SerializeError(var9);
        }
    }

    @Override
    public long stringToName64(@Nullable String str) throws SerializationProviderError {
        if (null == this.context) {
            throw new AbieosContextNullError("Null context!  Has destroyContext() already been called?");
        } else {
            return this.stringToName(this.context, str);
        }
    }

    @Nullable
    public String error() throws SerializationProviderError {
        if (null == this.context) {
            throw new AbieosContextNullError("Null context!  Has destroyContext() already been called?");
        } else {
            return this.getError(this.context);
        }
    }

    private void refreshContext() throws SerializationProviderError {
        this.destroyContext();
        this.context = this.create();
        if (null == this.context) {
            throw new AbieosContextNullError("Could not create abieos context.");
        }
    }

    @Nullable
    private String getType(@NotNull String action, long contract) throws SerializationProviderError {
        long action64 = this.stringToName64(action);
        return this.getTypeForAction(this.context, contract, action64);
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
